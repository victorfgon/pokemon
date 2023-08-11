package com.pokemonservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PokemonListResponse {

    @JsonProperty("results")
    private List<PokemonName> results;

    public List<PokemonName> getResults() {
        return results;
    }

    public void setResults(List<PokemonName> results) {
        this.results = results;
    }
}

class PokemonName {
    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}