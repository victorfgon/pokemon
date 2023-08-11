package com.highlightservice.highlightservice;

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
class HighlightControllerTest {

    @Mock
    private HighlightService highlightService;

    @InjectMocks
    private HighlightController highlightController;

    @Test
    void testGetHighlightedPokemons() {
        HighlightedPokemon highlightedPokemon = new HighlightedPokemon("pikachu", "<pre>pi</pre>kachu");
        when(highlightService.getHighlightedPokemons("pi", SortType.ALPHABETICAL))
                .thenReturn(Collections.singletonList(highlightedPokemon));

        ResponseEntity<Map<String, List<HighlightedPokemon>>> responseEntity = highlightController.getHighlightedPokemons("pi", "ALPHABETICAL");

        assertEquals(200, responseEntity.getStatusCodeValue());
        List<HighlightedPokemon> result = Objects.requireNonNull(responseEntity.getBody()).get("result");
        assertEquals(1, result.size());
        assertEquals("pikachu", result.get(0).getName());
        assertEquals("<pre>pi</pre>kachu", result.get(0).getHighlight());
    }
}
