package com.pokemonservice;

public class PokemonServiceException extends RuntimeException {
    public PokemonServiceException(String message) {
        super(message);
    }

    public PokemonServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
