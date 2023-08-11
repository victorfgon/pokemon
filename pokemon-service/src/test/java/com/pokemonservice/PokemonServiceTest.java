package com.pokemonservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokemonServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Queue pokemonQueue;

    @InjectMocks
    private PokemonService pokemonService;

    @Test
    void testGetPokemonsAlphabetical_Success() {
        PokemonListResponse pokemonListResponse = new PokemonListResponse();

        PokemonName pikachuName = new PokemonName();
        pikachuName.setName("pikachu");

        PokemonName pichuName = new PokemonName();
        pichuName.setName("pichu");

        PokemonName bulbasaurName = new PokemonName();
        bulbasaurName.setName("bulbasaur");

        pokemonListResponse.setResults(Arrays.asList(pikachuName, pichuName, bulbasaurName));

        when(restTemplate.getForEntity(anyString(), eq(PokemonListResponse.class)))
                .thenReturn(new ResponseEntity<>(pokemonListResponse, HttpStatus.OK));

        List<String> pokemonNames = pokemonService.getPokemons("pi", SortType.ALPHABETICAL);

        assertEquals(2, pokemonNames.size());
        assertEquals("pichu", pokemonNames.get(0));
        assertEquals("pikachu", pokemonNames.get(1));
    }

    @Test
    void testGetPokemonsLength_Success() {
        PokemonListResponse pokemonListResponse = new PokemonListResponse();

        PokemonName pikachuName = new PokemonName();
        pikachuName.setName("pikachu");

        PokemonName pichuName = new PokemonName();
        pichuName.setName("pichu");

        PokemonName bulbasaurName = new PokemonName();
        bulbasaurName.setName("bulbasaur");

        pokemonListResponse.setResults(Arrays.asList(pikachuName, pichuName, bulbasaurName));

        when(restTemplate.getForEntity(anyString(), eq(PokemonListResponse.class)))
                .thenReturn(new ResponseEntity<>(pokemonListResponse, HttpStatus.OK));

        List<String> pokemonNames = pokemonService.getPokemons(null, SortType.LENGTH);

        assertEquals(3, pokemonNames.size());
        assertEquals("pichu", pokemonNames.get(0));
        assertEquals("pikachu", pokemonNames.get(1));
        assertEquals("bulbasaur", pokemonNames.get(2));
    }


    @Test
    void testGetPokemons_Failure() {
        when(restTemplate.getForEntity(anyString(), eq(PokemonListResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThrows(PokemonServiceException.class,
                () -> pokemonService.getPokemons("bulb", SortType.ALPHABETICAL));
    }
}
