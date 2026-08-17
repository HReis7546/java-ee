package forense_json;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PersonagemService {

    @Inject
    private PersonagemRepository repository;
    private static final String COMIDA_PADRAO = "DefaultFood";

    public Personagem criarPersonagem(String nome, String especie, String comidaFavorita) {
        Integer id = validarId();
        validarNome(nome);
        validarEspecie(especie);
        if (comidaFavorita == null || comidaFavorita.trim().isEmpty()) {
            comidaFavorita = COMIDA_PADRAO;
        } else {
            validarComida(comidaFavorita);
        }
        Personagem personagem = new Personagem(id, nome, especie, comidaFavorita);
        return repository.guardar(personagem);
    }

    public List<Personagem> listarTodos() {
        return repository.listarTodos();
    }

    public void apagarTodos() {
        repository.apagarTodos();
    }

    public boolean apagarId(int id) {
        return repository.apagarId(id);
    }

    private Integer validarId() {
        int tamanho = repository.listarTodos().size();
        if (tamanho == 0) {
            return 1;
        }
        return tamanho + 1;
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().length() <= 3) {
            throw new IllegalArgumentException("O parâmetro nome tem de ter mais de 3 caracteres.");
        }
    }

    private void validarEspecie(String especie) {
        if (especie == null || especie.trim().length() <= 3) {
            throw new IllegalArgumentException("O parâmetro especie tem de ter mais de 3 caracteres.");
        }
    }

    private String validarComida(String comidaFavorita) {
        if (comidaFavorita == null || comidaFavorita.trim().length() <= 3) {
            throw new IllegalArgumentException("O parâmetro comidaFavorita tem de ter mais de 3 caracteres.");
        }
        return comidaFavorita.trim();
    }
}
