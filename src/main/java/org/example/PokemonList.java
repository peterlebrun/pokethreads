package org.example;

import java.util.List;

public class PokemonList {
    private Integer count;
    private String next;
    private String previous;
    private List<PokemonListResult> results;

    public Integer getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<PokemonListResult> getResults() {
        return results;
    }

    private void setCount(Integer count) {
        this.count = count;
    }

    private void setNext(String next) {
        this.next = next;
    }

    private void setPrevious(String previous) {
        this.previous = previous;
    }

    private void setResults(List<PokemonListResult> results) {
        this.results = results;
    }
}
