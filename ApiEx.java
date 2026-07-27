import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiEx{
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        int totalRequests = 20;
        String characterStatus = "";
        for (int id = 1; id <= totalRequests; id++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://rickandmortyapi.com/api/character/" + id))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            String name = body.split("\"name\":\"")[1].split("\"")[0];

            if (body.contains("Alive")) {
                characterStatus += "ID: " + id + " Name: " + name + " is Alive.\n";
            } else if (body.contains("Dead")) {
                if (body.contains("Alien")) {
                    System.out.println("[PERIGO] Um Alien foi encontrado morto com o ID " + id + "!");
                }
                characterStatus += "ID: " + id + " Name: " + name + " is Dead.\n";
            } else {
                characterStatus += "ID: " + id + " Name: " + name + " is unknown.\n";
            }
        }
        System.out.println(characterStatus);

    }
}