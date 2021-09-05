package org.github.muhdlaziem.ogm.entity;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import javax.json.bind.annotation.JsonbTransient;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Pokemon {

    @Id
    public String name;

    @Relationship(value = "IS_FROM")
    public Origin origin;

    @Relationship(value = "IS_TYPE")
    public Set<Type> types = new HashSet<>();

    @Override
    public String toString() {
        return "Pokemon{" +
                "name='" + name + '\'' +
                ", origin=" + origin +
                ", types=" + types +
                '}';
    }
}
