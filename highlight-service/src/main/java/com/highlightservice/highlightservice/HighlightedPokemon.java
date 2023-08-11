package com.highlightservice.highlightservice;

public class HighlightedPokemon {
    private final String name;
    private final String highlight;

    public HighlightedPokemon(String name, String highlight) {
        this.name = name;
        this.highlight = highlight;
    }

    public String getName() {
        return name;
    }

    public String getHighlight() {
        return highlight;
    }
}
