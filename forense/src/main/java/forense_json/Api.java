package forense_json;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/census")
public class Api {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGet(
            @QueryParam("offset") String offsetParam,
            @QueryParam("limit") String limitParam,
            @QueryParam("showAlerts") String showAlertsParam) throws IOException {

        int offset = 1;
        int limit = 20;
        Boolean showAlerts = true;

        if (offsetParam != null && !offsetParam.trim().isEmpty()) {
            try {
                offset = Integer.parseInt(offsetParam.trim());
                if (offset < 1) {
                    return Response.status(Response.Status.BAD_REQUEST)
                                    .entity("O parâmetro offset deve ser um número inteiro superior a 0.")
                                    .build();
                }
            } catch (NumberFormatException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                                .entity("O parâmetro offset não pode ser texto.")
                                .build();
            }
        }
        if (limitParam != null && !limitParam.trim().isEmpty()) {
            try {
                limit = Integer.parseInt(limitParam);
                if (limit < 1 || limit > 50) {
                return Response.status(Response.Status.BAD_REQUEST)
                                .entity("O parâmetro limit deve ser um número inteiro entre 1 e 50.")
                                .build();
                }
            } catch (NumberFormatException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                                .entity("O parâmetro limit não pode ser texto.")
                                .build();
            }
        }
        if (showAlertsParam != null && !showAlertsParam.trim().isEmpty()) {
            String validAlertsParam = showAlertsParam.trim().toLowerCase();
            if (!validAlertsParam.equals("true") && !validAlertsParam.equals("false")) {
                return Response.status(Response.Status.BAD_REQUEST)
                                .entity("O parâmetro showAlerts deve ser true ou false.")
                                .build();
            }
            showAlerts = Boolean.parseBoolean(showAlertsParam);
        }

        HttpClient client = HttpClient.newHttpClient();

        int countAlive = 0;
        int countDead = 0;
        int countUnknown = 0;

        String message = "";
        for (int i = offset; i < offset + limit; i++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://rickandmortyapi.com/api/character/" + i))
                    .GET()
                    .build();

            boolean success = false;
            int tentativas = 0;

            while (!success) {
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String jsonString = response.body();

                    ObjectMapper mapper = new ObjectMapper();

                    JsonNode jsonNode = mapper.readTree(jsonString);
                    int id = jsonNode.get("id").asInt();
                    String name = jsonNode.get("name").asText();


                    if (jsonString.contains("Alive")) {
                        message += "ID: " + id + " Name: " + name + " is Alive.\n";
                        countAlive++;
                    } else if (jsonString.contains("Dead")) {
                        if (jsonString.contains("Alien")) {
                            if (showAlerts) {
                                message += "[PERIGO] Um Alien foi encontrado morto com o ID " + id + "!\n";
                                getNomeEpisodio(jsonNode, message);
                            }
                        }
                        message += "ID: " + id + " Name: " + name + " is Dead.\n";
                        countDead++;
                    } else {
                        message += "ID: " + id + " Name: " + name + " is unknown.\n";
                        countUnknown++;
                    }
                    success = true;

                } catch (Exception e) {
                    tentativas++;
                    System.out.println("Exception no id " + i + ". Tentativa " + tentativas);
                    try {
                        Thread.sleep(500); // Aguarda 500ms antes de tentar novamente
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

        }

        message += "Total de personagens vivos: " + countAlive + "\n";
        message += "Total de personagens mortos: " + countDead + "\n";
        message += "Total de personagens desconhecido: " + countUnknown + "\n";

        return Response.status(Response.Status.OK)
                        .entity(message)
                        .build();
    }

    public static void getNomeEpisodio(JsonNode jsonNode, String message) throws Exception {
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
            message += "[ALERTA FORENSE] O último registo do alien morto foi no episódio: " + nameEpisode + "\n";
        } catch (Exception e) {
            System.out.println("Exception generica Episodio");
        }

    }
}