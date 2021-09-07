package org.github.muhdlaziem.ogm.api;

import org.github.muhdlaziem.ogm.entity.Origin;
import org.github.muhdlaziem.ogm.entity.Pokemon;
import org.github.muhdlaziem.ogm.entity.Type;
import org.neo4j.ogm.cypher.query.SortOrder;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class Pokemons {

    @Inject
    SessionFactory sessionFactory;

    public Pokemon getPokemon(String name) {
        Session session = sessionFactory.openSession();
        return session.load(Pokemon.class, name);
    }

    public List<Pokemon> getPokemons(){
        Session session = sessionFactory.openSession();

        return new ArrayList<>(session.loadAll(Pokemon.class, new SortOrder("name"), 1));
    }

    public List<Pokemon> getPokemonSpecificType(String type) {
        Session session = sessionFactory.openSession();

        Iterable<Pokemon> result = session.query(Pokemon.class, "MATCH (p: Pokemon)-[:IS_TYPE]->(:Type {name: $type})\n" +
                        "MATCH (p)-[isFrom:IS_FROM]->(region)\n" +
                        "MATCH (p)-[isType:IS_TYPE]->(type)\n" +
                        "RETURN p, collect(isFrom), collect(region), collect(isType), collect(type)\n" +
                        "ORDER by p.name;",
                Map.of("type", type));

        return resultList(result);
    }

    public List<Pokemon> getPokemonSpecificRegion(String region) {
        Session session = sessionFactory.openSession();

        Iterable<Pokemon> result = session.query(Pokemon.class, "MATCH (p: Pokemon)-[:IS_FROM]->(:Origin {name: $region})\n" +
                        "MATCH (p)-[isFrom:IS_FROM]->(region)\n" +
                        "MATCH (p)-[isType:IS_TYPE]->(type)\n" +
                        "RETURN p, collect(isFrom), collect(region), collect(isType), collect(type)\n" +
                        "ORDER by p.name;",
                Map.of("region", region));

        return resultList(result);
    }

    public UUID createPokemon(String name, String origin, Set<String> types) {
        Session session = sessionFactory.openSession();
        return runInTransaction(() -> {

            Pokemon pokemon = new Pokemon();
            pokemon.name = name;

            verifyNameNotExist(name, session);

            addOrigins(origin, session, pokemon);
            addTypes(types, session, pokemon);

            session.save(pokemon);

        }, session);
    }

    public UUID updateType(String name, Set<String> types) {
        Session session = sessionFactory.openSession();

        return runInTransaction(() -> {
            Pokemon pokemon = session.load(Pokemon.class, name);
            if (pokemon == null)
                throw new IllegalArgumentException("Could not find bean with name " + name);

            pokemon.types.clear();
            addTypes(types, session, pokemon);
            session.save(pokemon);
        }, session);
    }

    public UUID deletePokemon(String name) {
        Session session = sessionFactory.openSession();
        return runInTransaction(() -> session.delete(session.load(Pokemon.class, name)), session);
    }

    private void verifyNameNotExist(String name, Session session) {
        Pokemon existing = session.load(Pokemon.class, name);

        if ( existing != null)
            throw new IllegalArgumentException("Pokemon with name already exists!");
    }

    private  void addOrigins(String origin, Session session, Pokemon pokemon) {
        Origin existingOrigin = session.load(Origin.class, origin);

        if (existingOrigin == null) {
            throw  new IllegalArgumentException("Origin with name " + origin + " not found!");
        }
        pokemon.origin = existingOrigin;
    }

    private void addTypes(Set<String> types, Session session, Pokemon pokemon){
        types.forEach((type) -> {
            Type existingType = session.load(Type.class, type);

            if(existingType == null) {
                throw new IllegalArgumentException("Origin with name " + type + " not found!");
            }

            pokemon.types.add(existingType);
        });
    }

    private List<Pokemon> resultList(Iterable<Pokemon> result) {
        ArrayList<Pokemon> pokemons = new ArrayList<>();
        result.forEach(pokemons::add);
        return pokemons;
    }

    private UUID runInTransaction(Runnable runnable, Session session) {
        Transaction transaction = session.beginTransaction();
        try {
            UUID actionId = UUID.randomUUID();
            runnable.run();
            session.query("CREATE (:Action {actionId: $actionId, timestamp: TIMESTAMP()})", Map.of("actionId", actionId));
            transaction.commit();
            transaction.close();
            return actionId;
        } catch (RuntimeException e) {
            System.err.println("Could not execute transaction: " + e);
            transaction.rollback();
            throw e;
        }
    }
}
