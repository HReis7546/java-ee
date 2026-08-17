package forense_json;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersonagemRepository {

    private List<Personagem> listaPersonagens = new ArrayList<>();

    public Personagem guardar(Personagem personagem) {
        listaPersonagens.add(personagem);
        return personagem;
    }

    public List<Personagem> listarTodos() {
        return new ArrayList<>(listaPersonagens);
    }

    public void apagarTodos() {
        listaPersonagens.clear();
    }

    public boolean apagarId(int id) {
        return listaPersonagens.removeIf(p -> p.getId() == id);
    }
}
