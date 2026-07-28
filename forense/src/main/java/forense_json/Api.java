package forense_json;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Api {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        int totalRequests = 20;
        int countAlive = 0;
        int countDead = 0;
        int countUnknown = 0;
        for (int i = 1; i <= totalRequests; i++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://rickandmortyapi.com/api/character/" + i))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonString = response.body();

            ObjectMapper mapper = new ObjectMapper();

            try {
                JsonNode jsonNode = mapper.readTree(jsonString);
                int id = jsonNode.get("id").asInt();
                String name = jsonNode.get("name").asText();

                if (jsonString.contains("Alive")) {
                    System.out.println("ID: " + id + " Name: " + name + " is Alive.");
                    countAlive++;
                } else if (jsonString.contains("Dead")) {
                    if (jsonString.contains("Alien")) {
                        System.out.println("[PERIGO] Um Alien foi encontrado morto com o ID " + id + "!");

                        nomeEpisodio(jsonNode);

                    }
                    System.out.println("ID: " + id + " Name: " + name + " is Dead.");
                    countDead++;
                } else {
                    System.out.println("ID: " + id + " Name: " + name + " is unknown.");
                    countUnknown++;
                }

            } catch (Exception e) {
                System.out.println("Exception generica");
            }

        }

        System.out.println("Total de personagens vivos: " + countAlive);
        System.out.println("Total de personagens mortos: " + countDead);
        System.out.println("Total de personagens desconhecido: " + countUnknown);
    }

    public static void nomeEpisodio(JsonNode jsonNode) throws Exception {
        String primeiroEpisodio = jsonNode.get("episode").get(0).asText();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(primeiroEpisodio))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonString = response.body();

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode jsonNodeEpisode = mapper.readTree(jsonString);
            String nameEpisode = jsonNodeEpisode.get("name").asText();
            System.out.println("[ALERTA FORENSE] O último registo do alien morto foi no episódio: " + nameEpisode);
        } catch (Exception e) {
            System.out.println("Exception generica Episodio");
        }

    }
}