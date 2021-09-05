# Neo4j Notes

## What is Cypher?

* Cypher is a graph query language that is used to query the Neo4j Database. Just like you use SQL to query a MySQL database, you would use Cypher to query the Neo4j Database.

* A simple cypher query can look something like this

  ```cypher
  Match (m:Movie) where m.released > 2000 RETURN m limit 5
  ```

* **Expected Result**: The above query will return all the movies that were released after the year 2000 limiting the result to 5 items.

![5 movies](http://guides.neo4j.com/sandbox/movies/img/5-movies.svg)

* A query to return the count of movies released after the year 2005.

  ```cypher
  Match (m:Movie) where m.released > 2005 RETURN count(m)
  ```



## Nodes and Relationships

Nodes and Relationships are the basic building blocks of a graph database.

### Nodes

Nodes represent entities. A node in graph database is similar to a row in a relational database. In the picture below we can see 2 kinds of nodes - `Person` and `Movie`. In writing a cypher query, a node is enclosed between a parenthesis — like `(p:Person)` where `p` is a variable and `Person` is the type of node it is referring to.

### Relationship

![schema](http://guides.neo4j.com/sandbox/movies/img/schema.svg)

Two nodes can be connected with a relationship. In the above image `ACTED_IN`, `REVIEWED`, `PRODUCED`, `WROTE` and `DIRECTED` are all relationships connecting the corresponding types of nodes.

In writing a cypher query, relationships are enclosed in square brackets - like `[w:WORKS_FOR]` where `w` is a variable and `WORKS_FOR` is the type of relationship it is referring to.

Two nodes can be connected with more than one relationships.

```cypher
MATCH (p:Person)-[d:DIRECTED]-(m:Movie) where m.released > 2010 RETURN p,d,m
```

**Expected Result**: The above query will return all Person nodes who directed a movie that was released after 2010.

![movies after 2010](http://guides.neo4j.com/sandbox/movies/img/movies-after-2010.svg)

## Labels

Labels is a name or identifer of a Node or a Relationship. In the image below `Movie` and `Person` are Node labels and `ACTED_IN`, `REVIEWED`, etc are Relationship labels.

![schema](http://guides.neo4j.com/sandbox/movies/img/schema.svg)

In writing a cypher query, Labels are prefixed with a colon - like `:Person` or `:ACTED_IN`. You can assign the node label to a variable by prefixing the syntax with the variable name. Like `(p:Person)` means `p` variable denoted `Person` labeled nodes.

Labels are used when you want to perform operations only on a specific types of Nodes. Like

```cypher
MATCH (p:Person) RETURN p limit 20
```

will return only `Person` Nodes (limiting to 20 items) while

```cypher
MATCH (n) RETURN n limit 20
```

will return all kinds of nodes (limiting to 20 items).

## Properties

Properties are name-value pairs that are used to add attributes to nodes and relationships.

To return specific properties of a node you can write -

```cypher
MATCH (m:Movie) return m.title, m.released
```



![movies properties](http://guides.neo4j.com/sandbox/movies/img/movies-properties.jpg)

**Expected Result** - This will return Movie nodes but with only the `title` and `released` properties.

## Create a Node

`Create` clause can be used to create a new node or a relationship.

```cypher
Create (p:Person {name: 'Muhammad Laziem Shafie'}) RETURN p
```

The above statement will create a new `Person` node with property `name` having value `John Doe`.

## Finding Nodes with **Match** and **Where** Clause

`Match` clause is used to find nodes that match a particular pattern. This is the primary way of getting data from a Neo4j database.

In most cases, a `Match` is used along with certain conditions to narrow down the result.

```cypher
Match (p:Person {name: 'Tom Hanks'}) RETURN p
```

This is one way of doing it. Although you can only do basic string match based filtering this way (without using `WHERE` clause).

Another way would be to use a `WHERE` clause which allows for more complex filtering including `>`, `<`, `Starts With`, `Ends With`, etc

```cypher
Match (p:Person) where p.name = "Tom Hanks" return p
```

Both of the above queries will return the same results.

You can read more about Where clause and list of all filters here - https://neo4j.com/docs/cypher-manual/4.1/clauses/where/

## Merge Clause

The `Merge` clause is used to either

1. match the existing nodes and bind them or
2. create new node(s) and bind them

It is a combination of `Match` and `Create` and additionally allows to specify additional actions if the data was matched or created.

```cypher
MERGE (p:Person {name: 'John Doe'})
ON MATCH SET p.lastLoggedInAt = timestamp()
ON CREATE SET p.createdAt = timestamp()
Return p
```

The above statement will create the Person node if it does not exist. If the node already exists, then it will set the property `lastLoggedInAt` to the current timestamp. If node did not exist and was newly created instead, then it will set the `createdAt` property to the current timestamp.

```cypher
MERGE (m:movie {title: 'Greyhound'})
ON MATCH SET m.lastUpdatedAt = timestamp()
ON CREATE SET m.released = "2020", m.lastUpdatedAt = timestamp()
Return m
```

## Create a Relationship

A Relationship connects 2 nodes.

```cypher
MATCH (p:Person), (m:Movie)
WHERE p.name = "Tom Hanks" and m.title = "Cloud Atlas"
CREATE (p)-[w:WATCHED]->(m)
RETURN type(w)
```

The above statement will create a relationship `:WATCHED` between the existing `Person` and `Movie` nodes and return the type of relationship (i.e `WATCHED`).

Check:

```cypher
MATCH (p:Person)-[w:WATCHED]-(m:Movie) where m.title ="Cloud Atlas" RETURN p,w,m
```

## Relationship Types

In Neo4j, there can be 2 kinds of relationships - **incoming** and **outgoing**.

![relationship types](http://guides.neo4j.com/sandbox/movies/img/relationship-types.svg)

In the above picture, the Tom Hanks node is said to have an outgoing relationship while Forrest Gump node is said to have an incoming relationship.

Relationships always have a direction. However, you only have to pay attention to the direction where it is useful.

To denote an outgoing or an incoming relationship in cypher, we use `→` or `←`.

Example -

```cypher
MATCH (p:Person)-[r:ACTED_IN]->(m:Movie) RETURN p,r,m
```

In the above query Person has an outgoing relationship and movie has an incoming relationship.

Although, in the case of the movies dataset, the direction of the relationship is not that important and even without denoting the direction in the query, it will return the same result. So the query -

```cypher
MATCH (p:Person)-[r:ACTED_IN]-(m:Movie) RETURN p,r,m
```

will return the same result as the above one.

## Advance Cypher queries

Let’s look at some questions that you can answer with cypher queries.

1. **Finding who directed Cloud Atlas movie**

   ```cypher
   MATCH (m:Movie {title: 'Cloud Atlas'})<-[d:DIRECTED]-(p:Person) return p.name
   ```

2. **Finding all people who have co-acted with Tom Hanks in any movie**

   ```cypher
   MATCH (tom:Person {name: "Tom Hanks"})-[:ACTED_IN]->(:Movie)<-[:ACTED_IN]-(p:Person) return p.name
   ```

3. **Finding all people related to the movie Cloud Atlas in any way**

   ```cypher
   MATCH (p:Person)-[relatedTo]-(m:Movie {title: "Cloud Atlas"}) return p.name, type(relatedTo)
   ```

   In the above query we only used the variable `relatedTo` which will try to find all the relationships between any `Person` node and the movie node "Cloud Atlas"

4. Finding Movies and Actors that are 3 hops away from Kevin Bacon.

   ```cypher
   MATCH (p:Person {name: 'Kevin Bacon'})-[*1..3]-(hollywood) return DISTINCT p, hollywood
   ```

Note: in the above query, `hollywood` refers to any node in the database (in this case `Person` and `Movie` nodes)
