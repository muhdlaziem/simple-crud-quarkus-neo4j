package org.github.muhdlaziem.ogm.entity;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.json.bind.annotation.JsonbTransient;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Type {

    @Id
    public String name;

    @Relationship(value = "IS_TYPE", direction = Relationship.INCOMING)
    @JsonbTransient
    public Set<Pokemon> pokemonSet = new HashSet<>();

    @Override
    public String toString() {
        return "Type{" +
                "type='" + name + '\'' +
                ", pokemonSet=" + pokemonSet +
                '}';
    }
}
