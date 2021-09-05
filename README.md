# simple-crud-quarkus-neo4j Project

## How to use?

1. Run docker container for neo4j by running `./run-neo4j.sh`
2. Init Pokemon DB by running `cypher-shell -u neo4j -p test --file data_init.cypher -a "neo4j://localhost:7687"`
   1. If you don't have `cypher-shell`, alternatively open Neo4j DashBoard: `http://localhost:7474` and then copy and paste the content of `data_init.cypher` file
3. Run Quarkus in Dev mode `./mvnw compile quarkus:dev`
4. Start accessing the REST API.

## Example
```bash
# Get all Pokemons
curl "localhost:8080/ogm/pokemons/"

# Get all Pokemons that have Dark type
curl "localhost:8080/ogm/pokemons/?type=Dark"

# Get all Pokemons that originally from Galar region
curl "localhost:8080/ogm/pokemons/?region=Galar" 

# Create Pokemon Lucario
curl -v -X "POST" "http://localhost:8080/ogm/pokemons/" \
-H 'Content-Type: application/json; charset=utf-8' \
-d $'{"name": "Lucario", "region": "Sinnoh", "types": ["Steel","Fighting"]}'

# Update type Lucario to only fighting
curl -v -X "PATCH" "http://localhost:8080/ogm/pokemons/Lucario" \
-H 'Content-Type: application/json; charset=utf-8' \
-d $'{"types": ["Fighting"]}'

# Delete Lucario node
curl -v -X "DELETE" "localhost:8080/ogm/pokemons/Lucario"
```

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/simple-crud-quarkus-neo4j-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

## Provided Code

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)
