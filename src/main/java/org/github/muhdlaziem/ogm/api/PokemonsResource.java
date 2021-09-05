package org.github.muhdlaziem.ogm.api;

import org.github.muhdlaziem.ogm.entity.Pokemon;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/ogm/pokemons")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class PokemonsResource {

    @Inject
    Pokemons pokemons;

    @GET
    public Response beans(@QueryParam("type") @DefaultValue("") String type, @QueryParam("region") @DefaultValue("") String region) {
        try {
            if (!type.isBlank())
                return Response.ok(pokemons.getPokemonSpecificType(type)).build();
            else if (!region.isBlank())
                return Response.ok(pokemons.getPokemonSpecificRegion(region)).build();
            return Response.ok(pokemons.getPokemons()).build();
        }
        catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("{name}")
    public Response bean(@PathParam("name") String name) {
        Pokemon pokemon = pokemons.getPokemon(name);
        if (pokemon == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(pokemon).build();
    }

    @POST
    public Response create(@Valid @NotNull JsonObject json) {
        String name = json.getString("name");

        String origin = json.getString("region");

        Set<String> types = json.getJsonArray("types")
                .getValuesAs(JsonString.class).stream()
                .map(JsonString::getString)
                .collect(Collectors.toSet());
        try {
            UUID actionId = pokemons.createPokemon(name, origin,types);
            return Response.noContent().header("Action-Id", actionId).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @PATCH
    @Path("{name}")
    public Response updateType(@PathParam("name") String name, JsonObject json) {

        Set<String> types = json.getJsonArray("types")
                .getValuesAs(JsonString.class).stream()
                .map(JsonString::getString)
                .collect(Collectors.toSet());

        UUID actionId = pokemons.updateType(name, types);

        return Response.noContent().header("Action-Id", actionId).build();
    }

    @DELETE
    @Path("{name}")
    public Response deleteBean(@PathParam("name") String name) {
        UUID actionId = pokemons.deletePokemon(name);
        return Response.noContent().header("Action-Id", actionId).build();
    }
}
