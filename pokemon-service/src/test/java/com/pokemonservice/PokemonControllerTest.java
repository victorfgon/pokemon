package com.pokemonservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokemonControllerTest {

    @Mock
    private PokemonService pokemonService;

    @InjectMocks
    private PokemonController pokemonController;

    @Test
    void testGetPokemons() {
        when(pokemonService.getPokemons("pi", SortType.ALPHABETICAL))
                .thenReturn(Collections.singletonList("pikachu"));

        ResponseEntity<Map<String, List<String>>> responseEntity = pokemonController.getPokemons("pi", "ALPHABETICAL");

        assertEquals(200, responseEntity.getStatusCodeValue());
        List<String> result = Objects.requireNonNull(responseEntity.getBody()).get("result");
        assertEquals(1, result.size());
        assertEquals("pikachu", result.get(0));
    }
}
