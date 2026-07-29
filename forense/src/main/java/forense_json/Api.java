package forense_json;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/census")
public class Api extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int offset = 1;
        int limit = 20;
        boolean showAlerts = true;

        String offsetParam = req.getParameter("offset");
        String limitParam = req.getParameter("limit");
        String showAlertsParam = req.getParameter("showAlerts");

        if (offsetParam != null && !offsetParam.trim().isEmpty()) {
            try {
                offset = Integer.parseInt(offsetParam.trim());
                if (offset < 1) {
                    sendBadRequest(resp, "O parâmetro offset deve ser um número inteiro superior a 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                sendBadRequest(resp, "O parâmetro offset não pode ser texto.");
                return;
            }
        }
        if (limitParam != null && !limitParam.trim().isEmpty()) {
            try {
                limit = Integer.parseInt(limitParam);
                if (limit < 1 || limit > 50) {
                    sendBadRequest(resp, "O parâmetro limit deve ser um número inteiro entre 1 e 50.");
                    return;
                }
            } catch (NumberFormatException e) {
                sendBadRequest(resp, "O parâmetro limit não pode ser texto.");
                return;
            }
        }
        if (showAlertsParam != null && !showAlertsParam.trim().isEmpty()) {
            String validAlertsParam = showAlertsParam.trim().toLowerCase();
            if (!validAlertsParam.equals("true") && !validAlertsParam.equals("false")) {
                sendBadRequest(resp, "O parâmetro showAlerts deve ser true ou false.");
                return;
            }
            showAlerts = Boolean.parseBoolean(showAlertsParam);
        }

        HttpClient client = HttpClient.newHttpClient();

        int countAlive = 0;
        int countDead = 0;
        int countUnknown = 0;

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
                        resp.getWriter().write("ID: " + id + " Name: " + name + " is Alive.\n");
                        countAlive++;
                    } else if (jsonString.contains("Dead")) {
                        if (jsonString.contains("Alien")) {
                            if (showAlerts) {
                                resp.getWriter().write("[PERIGO] Um Alien foi encontrado morto com o ID " + id + "!\n");
                                getNomeEpisodio(jsonNode, resp);
                            }
                        }
                        resp.getWriter().write("ID: " + id + " Name: " + name + " is Dead.\n");
                        countDead++;
                    } else {
                        resp.getWriter().write("ID: " + id + " Name: " + name + " is unknown.\n");
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

        resp.getWriter().write("Total de personagens vivos: " + countAlive + "\n");
        resp.getWriter().write("Total de personagens mortos: " + countDead + "\n");
        resp.getWriter().write("Total de personagens desconhecido: " + countUnknown + "\n");

    }

    public static void getNomeEpisodio(JsonNode jsonNode, HttpServletResponse resp) throws Exception {
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
            resp.getWriter()
                    .write("[ALERTA FORENSE] O último registo do alien morto foi no episódio: " + nameEpisode + "\n");
        } catch (Exception e) {
            System.out.println("Exception generica Episodio");
        }

    }

    private void sendBadRequest(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(
                "{\n" +
                        "  \"status\": 400,\n" +
                        "  \"error\": \"Bad Request\",\n" +
                        "  \"message\": \"" + message + "\"\n"
                        + "}");
    }
}