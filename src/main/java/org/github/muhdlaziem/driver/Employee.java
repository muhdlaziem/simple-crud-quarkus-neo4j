package org.github.muhdlaziem.driver;

//CREATE (Laziem: Employee {name:'Laziem', title: 'Junior AI Engineer', started_in: 2020})

import org.neo4j.driver.types.Node;

public class Employee {
    public Long id;
    public String name;
    public Long started_in;
    public String title;


    public Employee() {
        // This is needed for the REST-Easy JSON Binding
    }

    public Employee(String name) {
        this.name = name;
    }

    public Employee(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Employee(Long id, String name, Long started_in, String title) {
        this.id = id;
        this.name = name;
        this.started_in = started_in;
        this.title = title;
    }

    public Employee(String name, Long started_in, String title) {
        this.name = name;
        this.started_in = started_in;
        this.title = title;
    }

    public Employee(String name, String title) {
        this.name = name;
        this.title = title;
    }
    public static Employee from(Node node) {
        return new Employee(node.id(), node.get("name").asString(), node.get("started_in").asLong(), node.get("title").asString());
    }

}
