package com.highlightservice.highlightservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HighlightServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Queue pokemonQueue;

    @InjectMocks
    private HighlightService highlightService;

    @Test
    void testGetHighlightedPokemons_Alphabetical() {
        when(rabbitTemplate.receiveAndConvert(pokemonQueue.getName()))
                .thenReturn("bulbasaur","pikachu", "pichu", null);

        List<HighlightedPokemon> highlightedPokemons = highlightService.getHighlightedPokemons("pi", SortType.ALPHABETICAL);

        assertEquals(2, highlightedPokemons.size());
        assertEquals("<pre>pi</pre>chu", highlightedPokemons.get(0).getHighlight());
        assertEquals("<pre>pi</pre>kachu", highlightedPokemons.get(1).getHighlight());
    }

    @Test
    void testGetPokemons_Length() {
        when(rabbitTemplate.receiveAndConvert(pokemonQueue.getName()))
                .thenReturn("pikachu", "pichu", "bulbasaur", null);

        List<HighlightedPokemon> highlightedPokemons = highlightService.getHighlightedPokemons("", SortType.LENGTH);

        assertEquals(3, highlightedPokemons.size());
        assertEquals("pichu", highlightedPokemons.get(0).getHighlight());
        assertEquals("pikachu", highlightedPokemons.get(1).getHighlight());
        assertEquals("bulbasaur", highlightedPokemons.get(2).getHighlight());
    }


}
