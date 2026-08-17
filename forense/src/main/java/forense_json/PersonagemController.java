package forense_json;

import java.util.List;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;

@Path("/personagem")
public class PersonagemController {
    
    @Inject
    private PersonagemService service;


    @Path("/criar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPostPersonagem(
            @QueryParam("nome") String nomeParam,
            @QueryParam("especie") String especieParam,
            @QueryParam("comidaFavorita") String comidaFavoritaParam) {
                try {
                    Personagem personagem = service.criarPersonagem(nomeParam, especieParam, comidaFavoritaParam);
                    return Response.status(Response.Status.CREATED).entity(personagem).build();
                } catch (IllegalArgumentException e) {
                    return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
                }
            }       

    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetPersonagens() {
        List<Personagem> listaPersonagens = service.listarTodos();
        return Response.ok(listaPersonagens).build();
    }

    @Path("/apagarTodos")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response doDeletePersonagens() {
        service.apagarTodos();
        return Response.ok("Todos os personagens foram apagados.").build();
    }

    @Path("/apagar")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response doDeleteId(@QueryParam("id") int id) {
        if (service.apagarId(id)) {
            return Response.ok("Personagem com id " + id + " foi apagado.").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Personagem com id " + id + " não encontrado.").build();
        }
    }

}
