package com.pokemonservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PokemonService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonService.class);
    private static final String POKE_API_BASE_URL = "https://pokeapi.co/api/v2/";

    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final Queue pokemonQueue;

    private final Map<String, List<String>> cache = new HashMap<>();

    public PokemonService(RestTemplate restTemplate, RabbitTemplate rabbitTemplate, Queue pokemonQueue) {
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.pokemonQueue = pokemonQueue;
    }

    public List<String> getPokemons(String query, SortType sort) {
        logger.info("Fetching Pokémon data with query: {}, sort: {}", query, sort);

        try {
            List<String> pokemonNames;
            List<String> cachedResult = cache.get(generateCacheKey(query, sort));
            if (cachedResult != null) {
                pokemonNames = cachedResult;
            } else {
                pokemonNames = fetchAndFilterPokemonNames(query, sort);
                cache.put(generateCacheKey(query, sort), pokemonNames);
            }
            for (String pokemon : pokemonNames) {
                rabbitTemplate.convertAndSend(pokemonQueue.getName(), pokemon);
            }
            logger.info("Fetched {} Pokémon names", pokemonNames.size());
            return pokemonNames;
        } catch (HttpClientErrorException e) {
            String errorMessage = "Failed to fetch Pokémon data. HTTP status: " + e.getStatusCode();
            logger.error(errorMessage, e);
            throw new PokemonServiceException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = "An unexpected error occurred while fetching Pokémon data";
            logger.error(errorMessage, e);
            throw new PokemonServiceException(errorMessage, e);
        }
    }

    private String generateCacheKey(String query, SortType sort) {
        return query + "_" + sort;
    }

    private List<String> fetchAndFilterPokemonNames(String query, SortType sort) {
        String url = POKE_API_BASE_URL + "pokemon?limit=1500"; // Adjust limit as needed

        ResponseEntity<PokemonListResponse> response = restTemplate.getForEntity(url, PokemonListResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            List<String> names = Objects.requireNonNull(response.getBody()).getResults()
                    .stream()
                    .map(PokemonName::getName)
                    .collect(Collectors.toList());

            if (query != null && !query.isEmpty()) {
                names = names.stream()
                        .filter(name -> name != null && name.toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
            }

            if (sort != null) {
                    quickSort(names, sort);
            }

            return names;
        } else {
            throw new PokemonServiceException("Failed to fetch Pokémon data. HTTP status: " + response.getStatusCodeValue());
        }
    }

    private void quickSort(List<String> list, SortType sort) {
        // Base case: If the list has 1 or 0 elements, it's already sorted.
        if (list.size() <= 1) {
            return;
        }

        // Choose a pivot element (in this case, the middle element).
        String pivot = list.get(list.size() / 2);
        List<String> equal = new ArrayList<>();
        List<String> smaller = new ArrayList<>();
        List<String> larger = new ArrayList<>();

        // Partition the list into three sublists: smaller, equal, and larger.
        for (String str : list) {
            int cmp = compare(str, pivot, sort);
            if (cmp < 0) {
                smaller.add(str);
            } else if (cmp > 0) {
                larger.add(str);
            } else {
                equal.add(str);
            }
        }

        // Recursively apply quickSort to the smaller and larger sublists.
        quickSort(smaller, sort);
        quickSort(larger, sort);

        // Reconstruct the list by combining the smaller, equal, and larger sublists.
        list.clear();
        list.addAll(smaller);
        list.addAll(equal);
        list.addAll(larger);
    }

    private int compare(String a, String b, SortType sort) {
        // Compare function based on the specified sorting criterion.
        if (sort == SortType.ALPHABETICAL) {
            // Compare alphabetically (case-insensitive).
            return a.compareToIgnoreCase(b);
        } else if (sort == SortType.LENGTH) {
            // Compare by length of strings.
            return Integer.compare(a.length(), b.length());
        } else {
            // Default case: No comparison (considered equal).
            return 0;
        }
    }

}
