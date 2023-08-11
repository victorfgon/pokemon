package com.pokemonservice;

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
@Api(tags = "Pokemon Controller")
public class PokemonController {
    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("api/v1/pokemons")
    @ApiOperation(value = "Get a list of Pokémon names", response = Map.class)
    public ResponseEntity<Map<String, List<String>>> getPokemons(
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

        List<String> pokemonNames = pokemonService.getPokemons(query, sortType);

        Map<String, List<String>> response = new HashMap<>();
        response.put("result", pokemonNames);

        return ResponseEntity.ok(response);
    }

}





