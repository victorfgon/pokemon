package com.highlightservice.highlightservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class HighlightService {
    private static final Logger logger = LoggerFactory.getLogger(HighlightService.class);

    private final RabbitTemplate rabbitTemplate;
    private final Queue pokemonQueue;

    public HighlightService(RabbitTemplate rabbitTemplate, Queue pokemonQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.pokemonQueue = pokemonQueue;
    }

    private final Set<String> consumedPokemons = new HashSet<>();

    public List<HighlightedPokemon> getHighlightedPokemons(String query, SortType sort) {
        try {
            consumedPokemons.clear();
            List<String> pokemons = consumePokemonsFromQueue();
            List<HighlightedPokemon> result = new ArrayList<>();

            for (String pokemon : pokemons) {
                if (!consumedPokemons.contains(pokemon)) {
                    boolean isMatching = true;

                    if (query != null && !query.isEmpty()) {
                        isMatching = pokemon.toLowerCase().contains(query.toLowerCase());
                    }

                    if (isMatching) {
                        if(query != null && !query.isEmpty()) {
                            String highlightedName = highlightSubstring(pokemon, query);
                            result.add(new HighlightedPokemon(pokemon, highlightedName));
                        }else{
                            result.add(new HighlightedPokemon(pokemon, pokemon));
                        }
                        consumedPokemons.add(pokemon);
                    }
                }
            }

            quickSort(result, sort);
            return result;
        } catch (Exception e) {
            logger.error("An error occurred while getting highlighted Pokémon names", e);
            throw new HighlightServiceException("Error while getting highlighted Pokémon names", e);
        }
    }



    private void quickSort(List<HighlightedPokemon> list, SortType sort) {
        if (list.size() <= 1) {
            return;
        }

        HighlightedPokemon pivot = list.get(list.size() / 2);
        List<HighlightedPokemon> equal = new ArrayList<>();
        List<HighlightedPokemon> smaller = new ArrayList<>();
        List<HighlightedPokemon> larger = new ArrayList<>();

        for (HighlightedPokemon pokemon : list) {
            int cmp = compare(pokemon, pivot, sort);
            if (cmp < 0) {
                smaller.add(pokemon);
            } else if (cmp > 0) {
                larger.add(pokemon);
            } else {
                equal.add(pokemon);
            }
        }

        quickSort(smaller, sort);
        quickSort(larger, sort);

        list.clear();
        list.addAll(smaller);
        list.addAll(equal);
        list.addAll(larger);
    }

    private int compare(HighlightedPokemon a, HighlightedPokemon b, SortType sort) {
        String aName = a.getName();
        String bName = b.getName();

        if (sort == SortType.ALPHABETICAL) {
            return aName.compareToIgnoreCase(bName);
        } else if (sort == SortType.LENGTH) {
            return Integer.compare(aName.length(), bName.length());
        } else {
            return 0;
        }
    }

    private String highlightSubstring(String text, String query) {
        int index = text.toLowerCase().indexOf(query.toLowerCase());
        if (index >= 0) {
            return text.substring(0, index)
                    + "<pre>" + text.substring(index, index + query.length()) + "</pre>"
                    + text.substring(index + query.length());
        } else {
            return text;
        }
    }

    private List<String> consumePokemonsFromQueue() {
        List<String> pokemons = new ArrayList<>();
        while (true) {
            String pokemon = (String) rabbitTemplate.receiveAndConvert(pokemonQueue.getName());
            if (pokemon == null) {
                break;
            }
            pokemons.add(pokemon);
        }
        return pokemons;
    }
}
