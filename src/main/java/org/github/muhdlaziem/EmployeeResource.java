package org.github.muhdlaziem;

import java.net.URI;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;
import org.neo4j.driver.async.AsyncSession;
import org.neo4j.driver.exceptions.NoSuchRecordException;

@Path("employees")
public class EmployeeResource {
    /*
        https://quarkus.io/guides/neo4j
     */

    @Inject
    Driver driver;

    @GET
    public CompletionStage<Response> get() {
        /*
            curl -v localhost:8080/employees/
         */
        AsyncSession session = driver.asyncSession(); // <1> Open a new, asynchronous session with Neo4j
        return session
                .runAsync("MATCH (f:Employee) RETURN f ORDER BY f.name")// <2> execute a query. This is a Cypher statement.
                .thenCompose(cursor -> // <3> Retrieve a cursor, list the results and create Fruits.
                        cursor.listAsync(record -> Employee.from(record.get("f").asNode()))
                )
                .thenCompose(employees -> // <4> Close the session after processing
                        session.closeAsync().thenApply(signal -> employees)
                )
                .thenApply(Response::ok) // <5> Create a JAX-RS response
                .thenApply(Response.ResponseBuilder::build);
    }

    @POST
    public CompletionStage<Response> create(Employee employee) {

        /*
            curl -v -X "POST" "http://localhost:8080/employees" \                                                                                                         ✔
              -H 'Content-Type: application/json; charset=utf-8' \
              -d $'{"name": "Myname", "title": "Data Science Intern", "started_in": "2020"}'
         */
        AsyncSession session = driver.asyncSession();
        return session
                .writeTransactionAsync(tx -> tx
                        .runAsync("CREATE (f:Employee {name: $name, title: $title, started_in: $started_in}) RETURN f", Values.parameters(
                                "name",employee.name,
                                "title",employee.title,
                                "started_in",employee.started_in))
                        .thenCompose(fn -> fn.singleAsync())
                )
                .thenApply(record -> Employee.from(record.get("f").asNode()))
                .thenCompose(persistedEmployee -> session.closeAsync().thenApply(signal -> persistedEmployee))
                .thenApply(persistedEmployee -> Response
                        .created(URI.create("/employees/" + persistedEmployee.id))
                        .build()
                );
    }

    @GET
    @Path("{id}")
    public CompletionStage<Response> getSingle(@PathParam("id") Long id) {
        /*
            curl -v localhost:8080/employees/2
         */
        AsyncSession session = driver.asyncSession();
        return session
                .readTransactionAsync(tx -> tx
                        .runAsync("MATCH (f:Employee) WHERE id(f) = $id RETURN f", Values.parameters("id", id))
                        .thenCompose(fn -> fn.singleAsync())
                )
                .handle((record, exception) -> {
                    if(exception != null) {
                        Throwable source = exception;
                        if(exception instanceof CompletionException) {
                            source = ((CompletionException)exception).getCause();
                        }
                        Status status = Status.INTERNAL_SERVER_ERROR;
                        if(source instanceof NoSuchRecordException) {
                            status = Status.NOT_FOUND;
                        }
                        return Response.status(status).build();
                    } else  {
                        return Response.ok(Employee.from(record.get("f").asNode())).build();
                    }
                })
                .thenCompose(response -> session.closeAsync().thenApply(signal -> response));
    }

    @DELETE
    @Path("{id}")
    public CompletionStage<Response> delete(@PathParam("id") Long id) {
        /*
            curl -v -X DELETE localhost:8080/employees/2
        */

        AsyncSession session = driver.asyncSession();
        return session
                .writeTransactionAsync(tx -> tx
                        .runAsync("MATCH (f:Employee) WHERE id(f) = $id DELETE f", Values.parameters("id", id))
                        .thenCompose(fn -> fn.consumeAsync()) // <1> There is no result for us, only a summary of the query executed.
                )
                .thenCompose(response -> session.closeAsync())
                .thenApply(signal -> Response.noContent().build());
    }

    @PATCH
    @Path("{id}")
    public CompletionStage<Response> updateName(@PathParam("id") Long id, Employee employee) {
        /*
         curl -v -X "PATCH" "http://localhost:8080/employees/80" \
          -H 'Content-Type: application/json; charset=utf-8' \
          -d $'{"name": "Ahmad", "title": "Software Engineer"}'
         */
        System.out.println(id+" "+employee.name + " "+ employee.title);
        AsyncSession session = driver.asyncSession();
        return session
                .writeTransactionAsync(tx -> tx
                        .runAsync("MATCH (f:Employee) WHERE id(f) = $id SET f.name = $name, f.title = $title RETURN f", Values.parameters(
                                "id", id,
                                "name", employee.name,
                                "title", employee.title))
                        .thenCompose(fn -> fn.singleAsync())
                )
                .handle((record, exception) -> {
                    if(exception != null) {
                        Throwable source = exception;
                        if(exception instanceof CompletionException) {
                            source = ((CompletionException)exception).getCause();
                        }
                        Status status = Status.INTERNAL_SERVER_ERROR;
                        if(source instanceof NoSuchRecordException) {
                            status = Status.NOT_FOUND;
                        }
                        return Response.status(status).build();
                    } else  {
                        return Response.ok(Employee.from(record.get("f").asNode())).build();
                    }
                })
                .thenCompose(response -> session.closeAsync().thenApply(signal -> response));
    }
}
