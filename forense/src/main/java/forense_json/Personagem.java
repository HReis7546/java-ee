package forense_json;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import com.fasterxml.jackson.core.type.TypeReference;


@Path("/personagem")
public class Personagem {

    private String nome;
    private String especie;
    private String comidaFavorita;
    
    public String getNome() {
        return nome;
    }
    public String getEspecie() {
        return especie;
    }
    public String getComidaFavorita() {
        return comidaFavorita;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setEspecie(String especie) {
        this.especie = especie;
    }
    public void setComidaFavorita(String comidaFavorita) {
        this.comidaFavorita = comidaFavorita;
    }

    public Personagem() {
    }

    public Personagem(String nome, String especie, String comidaFavorita) {
        this.nome = nome;
        this.especie = especie;
        this.comidaFavorita = comidaFavorita;
    }

    private static List<Personagem> listaPersonagens = new ArrayList<>();

    @Path("/criar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPostPersonagem(
            @QueryParam("nome") String nomeParam,
            @QueryParam("especie") String especieParam,
            @QueryParam("comidaFavorita") String comidaFavoritaParam) throws IOException {

    if (nomeParam != null) {
        try {
            if (nomeParam.trim().length() <=3 ) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O parâmetro nome tem de ter mais de 3 caracteres.")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("O parâmetro nome tem de ser texto.")
                    .build();
        }
    }
    if (especieParam != null) {
        try {
            if (especieParam.trim().length() <=5 ) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O parâmetro especie tem de ter mais de 5 caracteres.")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("O parâmetro especie tem de ser texto.")
                    .build();
        }
    }
    if (comidaFavoritaParam != null) {
        try {
            if (comidaFavoritaParam.trim().length() <=5 ) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O parâmetro comidaFavorita tem de ter mais de 5 caracteres.")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("O parâmetro comidaFavorita tem de ser texto.")
                    .build();
        }
    }

    Personagem personagem = new Personagem(
                nomeParam, 
                especieParam, 
                comidaFavoritaParam);

    listaPersonagens.add(personagem);

    return Response.status(Response.Status.CREATED)
            .entity(personagem)
            .build();
    }
    
    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetPersonagens() {
        
        return Response.ok(listaPersonagens).build();
    }

    @Path("/apagar")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response doDeletePersonagens() {
        listaPersonagens.clear();
        return Response.ok("Todos os personagens foram apagados.").build();
    }
}