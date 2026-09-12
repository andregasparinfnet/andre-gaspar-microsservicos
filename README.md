# André Gaspar API

Aplicação acadêmica desenvolvida em Java com Spring Boot para gerenciamento de peritos judiciais, nomeações e atividades periciais.

Todos os dados utilizados são fictícios e destinados exclusivamente à demonstração acadêmica.

## Etapa atual

**Etapa 4 — APIs REST e Persistência com Spring Data**

Nesta etapa, a aplicação evoluiu do armazenamento temporário em Map para persistência em banco de dados utilizando Spring Data JPA.

Arquitetura atual:

    Cliente HTTP
        ↓
    Controller
        ↓
    Service
        ↓
    Repository
        ↓
    Spring Data JPA / Hibernate
        ↓
    Banco H2

A implementação baseada em Map das etapas anteriores permanece preservada no histórico Git por meio da tag `etapa-3`.

## Tecnologias

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- H2 Database
- Bean Validation
- Spring Cloud OpenFeign
- BrasilAPI
- Springdoc OpenAPI
- Swagger UI
- Postman
- JUnit
- MockMvc
- Maven
- Git

## Modelo de negócio

Principais classes:

- `Pessoa`: superclasse dos dados pessoais;
- `Perito`: representa o perito judicial;
- `NomeacaoPericial`: representa uma nomeação;
- `AtividadePericial`: representa uma atividade vinculada à nomeação;
- `HonorariosPericiais`: representa os honorários;
- `StatusNomeacao`: enum dos estados da nomeação.

Relacionamentos principais:

    Pessoa
       ↑
     Perito
       |
       | 1
       |
       | N
    NomeacaoPericial
       |
       | 1
       |
       | N
    AtividadePericial

`HonorariosPericiais` pertence à `NomeacaoPericial`.

## Persistência JPA

As classes persistentes utilizam as anotações JPA adequadas.

`Pessoa` é mapeada com `@MappedSuperclass`.

Os identificadores utilizam:

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

Entidades persistentes:

- `Perito`;
- `NomeacaoPericial`;
- `AtividadePericial`.

Os relacionamentos utilizam:

- `@OneToMany`;
- `@ManyToOne`;
- `@JoinColumn`.

`HonorariosPericiais` utiliza `@Embeddable` e é incorporado à nomeação através de `@Embedded`.

## Repositories

As entidades persistentes possuem repositories próprios:

- `PeritoRepository`;
- `NomeacaoPericialRepository`;
- `AtividadePericialRepository`.

Todos estendem `JpaRepository`.

O Spring Data disponibiliza operações como:

- `save`;
- `findById`;
- `findAll`;
- `deleteById`;
- `existsById`;
- `count`.

## Camada de serviço

Os Controllers não acessam os repositories diretamente.

Fluxo da aplicação:

    Controller → Service → Repository → Banco

A classe `BaseCrudService<T extends Identificavel>` centraliza as operações CRUD comuns utilizando `JpaRepository<T, Long>`.

Services:

- `PeritoService`;
- `NomeacaoPericialService`;
- `AtividadePericialService`.

Cada Service recebe seu Repository através de injeção por construtor.

## Consultas personalizadas

O projeto utiliza consultas derivadas do Spring Data.

Exemplos:

    findByStatus(StatusNomeacao status)
    findAllByOrderByDataLimiteAsc()
    findByNumeroProcesso(String numeroProcesso)
    findByConcluidaOrderByPrazoAsc(boolean concluida)
    existsByEmail(String email)
    existsByNumeroProcesso(String numeroProcesso)

Essas operações demonstram busca, filtragem e ordenação executadas pelo Spring Data JPA e pelo banco.

## Banco de dados H2

A aplicação utiliza banco H2 persistido em arquivo.

Configuração:

    spring.datasource.url=jdbc:h2:file:./data/andre-gaspar-api
    spring.datasource.driver-class-name=org.h2.Driver
    spring.datasource.username=sa
    spring.datasource.password=
    spring.jpa.hibernate.ddl-auto=update

Console H2:

    http://localhost:8080/h2-console

JDBC URL:

    jdbc:h2:file:./data/andre-gaspar-api

Usuário:

    sa

Senha: vazia.

Os dados permanecem persistidos após a aplicação ser encerrada e iniciada novamente.

## Loaders e arquivos-texto

Dados fictícios:

    src/main/resources/dados/peritos.txt
    src/main/resources/dados/nomeacoes.txt
    src/main/resources/dados/atividades.txt

Loaders:

- `PeritoLoader`;
- `NomeacaoLoader`;
- `AtividadeLoader`.

Os Maps existentes nos loaders são utilizados apenas como estruturas auxiliares para correlacionar os identificadores dos arquivos-texto com os objetos persistidos.

Eles não funcionam como mecanismo de persistência da aplicação.

A persistência final é realizada pelos Services através dos Repositories.

O `InicializadorAplicacao` verifica se o banco já contém registros antes da carga inicial, evitando duplicidades após uma reinicialização.

## Bean Validation

São utilizadas validações como:

- `@NotBlank`;
- `@NotNull`;
- `@Size`;
- `@Min`;
- `@Positive`;
- `@PositiveOrZero`;
- `@Email`;
- `@Valid`.

Os Controllers utilizam `@Valid @RequestBody` para ativar a validação.

## Tratamento de erros

A classe `TratadorGlobalExcecoes` utiliza `@RestControllerAdvice`.

Principais respostas HTTP:

- `200 OK`;
- `201 Created`;
- `204 No Content`;
- `400 Bad Request`;
- `404 Not Found`;
- `409 Conflict`.

Os erros são retornados de forma padronizada através de `ErroApi`.

## Serialização JSON

Para evitar referências circulares nos relacionamentos são utilizadas anotações Jackson como:

- `@JsonIgnore`;
- `@JsonProperty(access = JsonProperty.Access.READ_ONLY)`.

Assim, o JSON pode apresentar peritos, nomeações e atividades sem recursão infinita.

## Endpoints REST

### Peritos

| Método | Endpoint |
| --- | --- |
| GET | `/api/peritos` |
| GET | `/api/peritos/{id}` |
| POST | `/api/peritos` |
| PUT | `/api/peritos/{id}` |
| DELETE | `/api/peritos/{id}` |

### Nomeações

| Método | Endpoint |
| --- | --- |
| GET | `/api/nomeacoes` |
| GET | `/api/nomeacoes/{id}` |
| POST | `/api/nomeacoes?peritoId={id}` |
| PUT | `/api/nomeacoes/{id}` |
| DELETE | `/api/nomeacoes/{id}` |
| GET | `/api/nomeacoes/status/{status}` |
| GET | `/api/nomeacoes/ordenadas-por-prazo` |
| GET | `/api/nomeacoes/processo?numeroProcesso={numero}` |

### Atividades

| Método | Endpoint |
| --- | --- |
| GET | `/api/atividades` |
| GET | `/api/atividades/{id}` |
| POST | `/api/atividades?nomeacaoId={id}` |
| PUT | `/api/atividades/{id}` |
| DELETE | `/api/atividades/{id}` |
| GET | `/api/atividades/filtro?concluida=false` |

## Integração externa com OpenFeign

O projeto utiliza OpenFeign para consumir a BrasilAPI.

O suporte é habilitado por `@EnableFeignClients`.

O cliente utiliza `@FeignClient`.

Configuração:

    integracoes.brasil-api.url=https://brasilapi.com.br

Fluxo:

    Cliente
       ↓
    FeriadoNacionalController
       ↓
    FeriadoNacionalService
       ↓
    BrasilApiClient
       ↓
    OpenFeign
       ↓
    BrasilAPI

Endpoint local:

    GET /api/feriados/{ano}

Exemplo:

    GET /api/feriados/2026

## Swagger e OpenAPI

Swagger UI:

    http://localhost:8080/swagger-ui/index.html

OpenAPI:

    http://localhost:8080/v3/api-docs

Os Controllers utilizam anotações como:

- `@Tag`;
- `@Operation`;
- `@Parameter`;
- `@ApiResponse`;
- `@ApiResponses`.

## Postman

Collection atual:

    postman/andre-gaspar-api.postman_collection.json

Ela contém requisições para:

- CRUD;
- filtros;
- ordenação;
- consultas personalizadas;
- erros HTTP;
- integração BrasilAPI/OpenFeign.

A collection da Etapa 3 permanece preservada separadamente.

## Testes automatizados

Executar:

    ./mvnw clean test

Resultado final obtido na Etapa 4:

    Tests run: 17
    Failures: 0
    Errors: 0
    Skipped: 0

    BUILD SUCCESS

Os testes utilizam banco H2 separado em memória:

    jdbc:h2:mem:andre-gaspar-api-test

Isso evita alterações no banco persistente da aplicação.

## Como executar

No Linux ou WSL:

    chmod +x mvnw
    ./mvnw spring-boot:run

Aplicação:

    http://localhost:8080

Principais URLs:

    http://localhost:8080/api/peritos
    http://localhost:8080/api/nomeacoes
    http://localhost:8080/api/atividades
    http://localhost:8080/api/feriados/2026
    http://localhost:8080/swagger-ui/index.html
    http://localhost:8080/h2-console

## Evolução do projeto

    Etapa 1
    Orientação a Objetos
            ↓
    Etapa 2
    Collections + Map + Service
            ↓
    Etapa 3
    REST + Controller + Service + Map
            ↓
    Etapa 4
    REST + Controller + Service + Repository + H2

Tags acadêmicas do projeto:

- `etapa-1`;
- `etapa-2`;
- `etapa-3`;
- `etapa-4`.

Cada tag preserva o estado correspondente à respectiva etapa da disciplina.

## Uso de inteligência artificial

O ChatGPT, da OpenAI, foi utilizado como ferramenta de apoio ao esclarecimento de dúvidas, configuração de frameworks, depuração, documentação e revisão da qualidade do código.

A modelagem, a implementação, a execução, os testes e a validação da aplicação foram acompanhados e compreendidos pelo aluno, responsável pelo resultado final.
