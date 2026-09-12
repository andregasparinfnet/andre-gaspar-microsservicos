package br.edu.infnet.andre_gaspar_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@MappedSuperclass
public abstract class Pessoa implements Identificavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(
            min = 3,
            max = 120,
            message = "O nome deve possuir entre 3 e 120 caracteres"
    )
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve possuir um formato válido")
    @Size(
            max = 160,
            message = "O e-mail deve possuir no máximo 160 caracteres"
    )
    @Column(nullable = false, unique = true, length = 160)
    private String email;

    protected Pessoa() {
    }

    protected Pessoa(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    protected Pessoa(String nome, String email) {
        this(null, nome, email);
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    protected void atualizarDadosPessoais(
            String nome,
            String email
    ) {
        this.nome = nome;
        this.email = email;
    }

    public void alterarEmail(String novoEmail) {
        this.email = novoEmail;
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
