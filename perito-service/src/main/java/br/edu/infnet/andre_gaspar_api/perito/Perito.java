package br.edu.infnet.andre_gaspar_api.perito;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "peritos")
public class Perito extends Pessoa {

    protected Perito() {
        super();
    }

    public Perito(Long id, String nome, String email) {
        super(id, nome, email);
    }

    public Perito(String nome, String email) {
        super(nome, email);
    }

    public void atualizarDados(Perito dados) {
        atualizarDadosPessoais(
                dados.getNome(),
                dados.getEmail()
        );
    }

    @Override
    public String toString() {
        return "Perito{" +
                "id=" + getId() +
                ", nome='" + getNome() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}
