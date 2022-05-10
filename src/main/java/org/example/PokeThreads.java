package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


// Get list of pokemon from pokemon API
// Split into 20 threads
// Write all details back to main map
public class PokeThreads {
    public static final String pokemonUrl = "https://pokeapi.co/api/v2/pokemon";
    public static final String imageUrl = "https://img.pokemondb.net/artwork/large";

    public static String getImageUrl (String name) {
        return imageUrl + "/" + name + ".jpg";
    }

    public static String getDetailUrl (String name) {
        return pokemonUrl + "/" + name;
    }

    public static Map<String, String> pokeDetails = new HashMap<>();

    public static synchronized void putPokeDetails (String key, String value) {
        pokeDetails.put(key, value);
    }

    /*
    public static Callable<HttpResponse> getDetail (HttpClient c, String name) throws URISyntaxException, IOException, InterruptedException {
        return new Callable<HttpResponse> = () -> {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(getDetailUrl(name)))
                    .version(HttpClient.Version.HTTP_2)
                    .GET()
                    .build();
        }
                (req, HttpResponse.BodyHandlers.ofString());
    }
    */

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        ObjectMapper objectMapper = new ObjectMapper();

        HttpClient c = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(pokemonUrl))
                .header("Accept","application/json")
                .version(HttpClient.Version.HTTP_2)
                .GET()
                .build();

        HttpResponse res = c.send(req, HttpResponse.BodyHandlers.ofString());

        PokemonList pokemonList = objectMapper.readValue(res.body().toString(), PokemonList.class);

        pokemonList.getResults()
                .stream()
                .forEach(pokemon -> {
                    String name = pokemon.getName();
                    executorService.submit(() -> {
                        System.out.println(name + ": " + Thread.currentThread().getId());
                        try {
                            HttpRequest cReq = HttpRequest.newBuilder()
                                    .uri(new URI(getDetailUrl(name)))
                                    .header("Accept", "application/json")
                                    .version(HttpClient.Version.HTTP_2)
                                    .GET()
                                    .build();
                            HttpResponse cRes = c.send(cReq, HttpResponse.BodyHandlers.ofString());
                            putPokeDetails(name, cRes.toString());
                        } catch (Exception e) {
                            // NOT THE WAY TO DO THIS
                        }
                    });
                });
        System.out.println(pokeDetails);
        Thread.sleep(20000);
        System.out.println(pokeDetails);

        executorService.shutdown();
    }
}