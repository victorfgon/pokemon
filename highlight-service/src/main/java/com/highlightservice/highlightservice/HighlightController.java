package com.highlightservice.highlightservice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "Highlight Controller")
public class HighlightController {
    private final HighlightService highlightService;

    public HighlightController(HighlightService highlightService) {
        this.highlightService = highlightService;
    }

    @GetMapping("api/v1/pokemons/highlight")
    @ApiOperation(value = "Get a list of highlighted Pokémon names", response = Map.class)
    public ResponseEntity<Map<String, List<HighlightedPokemon>>> getHighlightedPokemons(
            @ApiParam(value = "Search query to filter Pokémon names")
            @RequestParam(required = false) String query,

            @ApiParam(value = "Sort type for Pokémon names (ALPHABETICAL or LENGTH)")
            @RequestParam(required = false) String sort
    ) {
        SortType sortType = SortType.ALPHABETICAL;

        if (sort != null && !sort.isEmpty()) {
            try {
                sortType = SortType.valueOf(sort.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        List<HighlightedPokemon> highlightedPokemons = highlightService.getHighlightedPokemons(query, sortType);

        Map<String, List<HighlightedPokemon>> response = new HashMap<>();
        response.put("result", highlightedPokemons);

        return ResponseEntity.ok(response);
    }
}
