# Sistema de Gestão de Perícias

Aplicação acadêmica desenvolvida em Java e Spring Boot para o gerenciamento de peritos judiciais, nomeações, honorários e atividades periciais.

Este repositório utiliza como ponto de partida o projeto desenvolvido na disciplina anterior e demonstra sua evolução para o estudo de organização arquitetural, comunicação entre módulos e, posteriormente, microsserviços.

Todos os dados utilizados são fictícios e destinados exclusivamente à demonstração acadêmica.

## Disciplina

**Arquiteturas Avançadas de Software com Microsserviços e Spring Framework**

## Etapa atual

**Etapa 1 — Organização Arquitetural da Aplicação**

Nesta etapa, a solução continua sendo uma única aplicação Spring Boot. O código foi reorganizado por funcionalidades do domínio para tornar suas responsabilidades, dependências e fronteiras mais claras antes da separação de qualquer funcionalidade em um serviço independente.

A aplicação ainda não representa uma arquitetura distribuída nem utiliza microsserviços. Ela constitui um monólito modular organizado por domínio.

## Arquitetura da aplicação

Fluxo principal:

```text
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
Banco de dados H2
```

Organização interna:

```text
Aplicação Spring Boot
├── perito
├── nomeacao
├── atividade
├── calendario
├── shared
└── bootstrap
```

As responsabilidades estão distribuídas da seguinte maneira:

* os controllers realizam a comunicação HTTP;
* os services concentram as regras e operações da aplicação;
* os repositories realizam o acesso aos dados;
* os controllers não acessam diretamente os repositories;
* as dependências são recebidas por injeção de construtor;
* o tratamento das exceções da API é centralizado.

## Módulos da aplicação

### Perito

Responsável pelo cadastro e pela manutenção das informações dos peritos judiciais.

O módulo contém dados como nome e e-mail do profissional, além do relacionamento entre o perito e suas nomeações.

Principais componentes:

* `Pessoa`;
* `Perito`;
* `PeritoController`;
* `PeritoService`;
* `PeritoRepository`;
* `PeritoLoader`.

### Nomeação

Responsável pelo gerenciamento das nomeações periciais recebidas pelo perito.

O módulo controla informações como:

* número do processo;
* data da nomeação;
* data-limite;
* prazo em dias;
* status;
* honorários;
* perito responsável;
* atividades relacionadas.

Principais componentes:

* `NomeacaoPericial`;
* `HonorariosPericiais`;
* `StatusNomeacao`;
* `NomeacaoPericialController`;
* `NomeacaoPericialService`;
* `NomeacaoPericialRepository`;
* `NomeacaoLoader`.

### Atividade

Responsável pelas atividades necessárias à execução de uma perícia.

Cada atividade pode possuir:

* descrição;
* prazo;
* quantidade estimada de horas;
* situação de conclusão;
* nomeação à qual pertence.

Principais componentes:

* `AtividadePericial`;
* `AtividadePericialController`;
* `AtividadePericialService`;
* `AtividadePericialRepository`;
* `AtividadeLoader`.

### Calendário

Responsável pela consulta de feriados nacionais utilizados como apoio ao planejamento dos prazos periciais.

O módulo utiliza OpenFeign para consumir a BrasilAPI.

Principais componentes:

* `FeriadoNacional`;
* `FeriadoNacionalController`;
* `FeriadoNacionalService`;
* `BrasilApiClient`.

### Componentes compartilhados

O pacote `shared` reúne recursos utilizados por diferentes módulos da aplicação.

Principais componentes:

* `Identificavel`;
* `CrudService`;
* `BaseCrudService`;
* `DadosInvalidosException`;
* `EntidadeJaExistenteException`;
* `EntidadeNaoEncontradaException`;
* `ErroApi`;
* `TratadorGlobalExcecoes`;
* `OpenApiConfig`.

### Inicialização

O pacote `bootstrap` contém a rotina responsável pela carga dos dados fictícios utilizados na demonstração da aplicação.

Principal componente:

* `InicializadorAplicacao`.

## Dependências entre os módulos

A análise da aplicação permitiu identificar as seguintes dependências principais:

### Nomeação → Perito

Uma nomeação precisa consultar o módulo de peritos para localizar o profissional responsável e estabelecer a associação entre os objetos.

A comunicação ocorre por meio do seguinte fluxo:

```text
NomeacaoPericialService
    ↓
PeritoService
    ↓
PeritoRepository
```

O `NomeacaoPericialService` não acessa diretamente o `PeritoRepository`. Essa comunicação por meio do service preserva a responsabilidade do módulo de peritos e reduz o acoplamento indevido.

### Atividade → Nomeação

Uma atividade precisa consultar o módulo de nomeações para ser vinculada à perícia correspondente.

A comunicação ocorre por meio do seguinte fluxo:

```text
AtividadePericialService
    ↓
NomeacaoPericialService
    ↓
NomeacaoPericialRepository
```

O módulo de atividades não acessa diretamente o repository pertencente ao módulo de nomeações.

### Calendário → BrasilAPI

O módulo de calendário depende de um serviço externo para consultar os feriados nacionais.

O fluxo é:

```text
FeriadoNacionalController
    ↓
FeriadoNacionalService
    ↓
BrasilApiClient
    ↓
OpenFeign
    ↓
BrasilAPI
```
## Evolução realizada na Aula 2

Na Aula 2, foi analisada a comunicação entre os módulos internos da aplicação.

A dependência entre nomeações e peritos respeita as responsabilidades de cada módulo:

```text
NomeacaoPericialController
    ↓
NomeacaoPericialService
    ↓
PeritoService
    ↓
PeritoRepository

```

O `NomeacaoPericialService` não acessa diretamente o `PeritoRepository`. O repository permanece como detalhe interno do módulo responsável pelos peritos.

Também foram introduzidos DTOs de resposta para controlar os dados transportados pela API:

- `NomeacaoResumoResponse`;
- `PeritoResumoResponse`.

O `PeritoResumoResponse` está localizado no contexto de nomeação porque representa somente a visão de perito necessária para compor a resposta desse módulo.

O endpoint abaixo demonstra esse contrato:

```http
GET /api/nomeacoes/resumos
```

A resposta contém informações resumidas da nomeação e do perito responsável, sem expor automaticamente honorários, atividades ou a coleção de nomeações mantida pela entidade `Perito`.

Exemplo:

```json
[
  {
    "id": 1,
    "numeroProcesso": "0000001-00.2026.8.00.0001",
    "dataNomeacao": "2026-08-20",
    "dataLimite": "2026-08-25",
    "prazoEmDias": 5,
    "status": "ACEITA",
    "perito": {
      "id": 1,
      "nome": "Perito Academico",
      "email": "perito@exemplo.com"
    }
  }
]
```

Nesta aula, a aplicação continua sendo um monólito modular executado na mesma JVM, porta e processo. Ainda não existe comunicação de rede entre os módulos.

A chamada interna entre `NomeacaoPericialService` e `PeritoService` será substituída por uma comunicação HTTP somente quando o serviço de peritos for extraído na Aula 3.


## Candidato a serviço independente

O módulo **Perito** foi escolhido como candidato a serviço independente para a próxima etapa.

### Responsabilidade

O módulo é responsável por manter os dados cadastrais dos peritos e disponibilizar operações para:

* inclusão;
* consulta;
* alteração;
* exclusão;
* verificação de e-mail já cadastrado.

### Justificativa da escolha

O cadastro de peritos possui uma responsabilidade própria e claramente identificável dentro do domínio.

Essa funcionalidade pode evoluir independentemente das nomeações e atividades periciais. Em uma solução maior, o mesmo cadastro também poderia ser utilizado por outros contextos, como distribuição de nomeações, controle de especialidades ou comunicação com profissionais.

### Dependências atuais

Atualmente, o módulo de nomeações depende do módulo de peritos para:

* verificar a existência do profissional;
* recuperar seus dados;
* associá-lo a uma nomeação pericial.

Na arquitetura atual, essa operação é realizada por uma chamada interna entre classes Java:

```text
NomeacaoPericialService → PeritoService
```

Em uma etapa posterior, essa chamada poderá ser substituída pela comunicação HTTP entre duas aplicações Spring Boot independentes.

### Impactos esperados da separação

A separação introduzirá novas preocupações arquiteturais, como:

* criação de contratos de comunicação;
* utilização de DTOs;
* configuração do endereço remoto;
* comunicação pela rede;
* tratamento da indisponibilidade do serviço;
* responsabilidade independente sobre os dados;
* possibilidade de falhas que não existem em chamadas locais.

Por esses motivos, o módulo permanece integrado à aplicação durante a Etapa 1.

## Decisão arquitetural da Etapa 1

A aplicação permanece como um monólito modular.

A organização por domínio permite reconhecer as responsabilidades e dependências existentes antes de distribuir a solução. Essa análise evita a criação de microsserviços apenas por exigência tecnológica, sem uma justificativa relacionada ao negócio.

A separação do módulo candidato será avaliada na etapa seguinte, considerando seus benefícios e os custos adicionais provocados pela comunicação distribuída.

## Tecnologias

* Java 21;
* Spring Boot;
* Spring MVC;
* Spring Data JPA;
* Hibernate;
* H2 Database;
* Bean Validation;
* Spring Cloud OpenFeign;
* BrasilAPI;
* Springdoc OpenAPI;
* Swagger UI;
* Maven;
* JUnit;
* MockMvc;
* Postman;
* Git e GitHub.

## Modelo de negócio

As principais classes do modelo são:

* `Pessoa`: superclasse que reúne os dados pessoais;
* `Perito`: representa o perito judicial;
* `NomeacaoPericial`: representa uma nomeação recebida;
* `AtividadePericial`: representa uma atividade vinculada à nomeação;
* `HonorariosPericiais`: representa os honorários da perícia;
* `StatusNomeacao`: enumeração dos estados possíveis de uma nomeação.

Relacionamentos principais:

```text
Pessoa
   ↑
 Perito
   │
   │ 1
   │
   │ N
NomeacaoPericial
   │
   │ 1
   │
   │ N
AtividadePericial
```

Cada nomeação pertence a um perito, e um perito pode possuir várias nomeações.

Cada atividade pertence a uma nomeação, e uma nomeação pode possuir várias atividades.

Os honorários periciais pertencem a uma nomeação.

## Persistência com Spring Data JPA

As classes persistentes utilizam anotações JPA.

`Pessoa` é mapeada com `@MappedSuperclass`.

Os identificadores das entidades utilizam:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
```

Entidades persistentes:

* `Perito`;
* `NomeacaoPericial`;
* `AtividadePericial`.

Os relacionamentos utilizam:

* `@OneToMany`;
* `@ManyToOne`;
* `@JoinColumn`.

`HonorariosPericiais` utiliza `@Embeddable` e é incorporado à nomeação por meio de `@Embedded`.

## Repositories

Cada entidade persistente possui seu próprio repository:

* `PeritoRepository`;
* `NomeacaoPericialRepository`;
* `AtividadePericialRepository`.

Todos estendem `JpaRepository`.

O Spring Data JPA disponibiliza operações básicas como:

* `save`;
* `findById`;
* `findAll`;
* `deleteById`;
* `existsById`;
* `count`.

## Camada de serviço

Os controllers não acessam os repositories diretamente.

O fluxo adotado é:

```text
Controller → Service → Repository → Banco de dados
```

A classe genérica `BaseCrudService<T extends Identificavel>` centraliza operações CRUD comuns utilizando `JpaRepository<T, Long>`.

Services principais:

* `PeritoService`;
* `NomeacaoPericialService`;
* `AtividadePericialService`;
* `FeriadoNacionalService`.

Cada service recebe suas dependências por meio de injeção de construtor.

## Consultas adicionais com Spring Data

A aplicação possui consultas derivadas coerentes com as necessidades do domínio.

Exemplos:

```java
findByStatus(StatusNomeacao status)
findAllByOrderByDataLimiteAsc()
findByNumeroProcesso(String numeroProcesso)
findByConcluidaOrderByPrazoAsc(boolean concluida)
existsByEmail(String email)
existsByEmailAndIdNot(String email, Long id)
existsByNumeroProcesso(String numeroProcesso)
existsByNumeroProcessoAndIdNot(String numeroProcesso, Long id)
```

Esses métodos demonstram:

* busca;
* filtragem;
* ordenação;
* verificação de duplicidade.

## Banco de dados H2

A aplicação utiliza um banco H2 persistido em arquivo.

Configuração principal:

```properties
spring.datasource.url=jdbc:h2:file:./data/andre-gaspar-microsservicos
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

Console H2:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:file:./data/andre-gaspar-microsservicos
```

Usuário:

```text
sa
```

A senha permanece vazia no ambiente acadêmico local.

Os dados permanecem persistidos depois que a aplicação é encerrada e iniciada novamente.

## Loaders e arquivos de dados

Os dados fictícios utilizados na inicialização estão nos arquivos:

```text
src/main/resources/dados/peritos.txt
src/main/resources/dados/nomeacoes.txt
src/main/resources/dados/atividades.txt
```

Loaders responsáveis pela leitura:

* `PeritoLoader`;
* `NomeacaoLoader`;
* `AtividadeLoader`.

Os mapas existentes nos loaders são utilizados somente como estruturas auxiliares para relacionar os identificadores dos arquivos-texto aos objetos persistidos.

Eles não funcionam como mecanismo principal de persistência. A gravação definitiva é realizada pelos services por meio dos repositories.

O `InicializadorAplicacao` verifica se o banco já possui registros antes de executar a carga inicial, evitando duplicidades durante novas inicializações.

## Validação dos dados

A aplicação utiliza Bean Validation para validar os dados recebidos pela API.

Entre as anotações utilizadas estão:

* `@NotBlank`;
* `@NotNull`;
* `@Size`;
* `@Min`;
* `@Positive`;
* `@PositiveOrZero`;
* `@Email`;
* `@Valid`.

As operações de inclusão e alteração utilizam `@Valid @RequestBody`.

Requisições inválidas produzem respostas HTTP adequadas antes da execução das regras de negócio.

## Tratamento de exceções

O tratamento das exceções está centralizado na classe `TratadorGlobalExcecoes`, anotada com `@RestControllerAdvice`.

São tratadas situações como:

* dados inválidos;
* falhas de Bean Validation;
* entidade não encontrada;
* entidade já existente;
* respostas representadas por `ResponseStatusException`.

A estrutura `ErroApi` padroniza as informações retornadas ao cliente e evita a exposição de detalhes internos da aplicação.

Principais respostas HTTP:

* `200 OK`;
* `201 Created`;
* `204 No Content`;
* `400 Bad Request`;
* `404 Not Found`;
* `409 Conflict`.

## Serialização JSON

Para evitar referências circulares nos relacionamentos, a aplicação utiliza mecanismos de controle da serialização, incluindo:

* `@JsonIgnore`;
* `@JsonProperty(access = JsonProperty.Access.READ_ONLY)`.

Dessa forma, os dados de peritos, nomeações e atividades podem ser retornados sem recursão infinita.

## Endpoints REST

### Peritos

| Método | Endpoint            | Finalidade              |
| ------ | ------------------- | ----------------------- |
| GET    | `/api/peritos`      | Listar todos os peritos |
| GET    | `/api/peritos/{id}` | Consultar um perito     |
| POST   | `/api/peritos`      | Incluir um perito       |
| PUT    | `/api/peritos/{id}` | Alterar um perito       |
| DELETE | `/api/peritos/{id}` | Excluir um perito       |

### Nomeações

| Método | Endpoint                                          | Finalidade                                   |
|--------|---------------------------------------------------|----------------------------------------------|
| GET    | `/api/nomeacoes`                                  | Listar todas as nomeações                    |
| GET    | `/api/nomeacoes/{id}`                             | Consultar uma nomeação                       |
| POST   | `/api/nomeacoes?peritoId={id}`                    | Incluir uma nomeação                         |
| PUT    | `/api/nomeacoes/{id}`                             | Alterar uma nomeação                         |
| DELETE | `/api/nomeacoes/{id}`                             | Excluir uma nomeação                         |
| GET    | `/api/nomeacoes/status/{status}`                  | Filtrar por status                           |
| GET    | `/api/nomeacoes/ordenadas-por-prazo`              | Ordenar pelo prazo                           |
| GET    | `/api/nomeacoes/processo?numeroProcesso={numero}` | Buscar pelo processo                         |
| GET    | `/api/nomeacoes/resumos`                          | Listar nomeações utilizando DTOs de resposta |

### Atividades

| Método | Endpoint                                 | Finalidade                 |
| ------ | ---------------------------------------- | -------------------------- |
| GET    | `/api/atividades`                        | Listar todas as atividades |
| GET    | `/api/atividades/{id}`                   | Consultar uma atividade    |
| POST   | `/api/atividades?nomeacaoId={id}`        | Incluir uma atividade      |
| PUT    | `/api/atividades/{id}`                   | Alterar uma atividade      |
| DELETE | `/api/atividades/{id}`                   | Excluir uma atividade      |
| GET    | `/api/atividades/filtro?concluida=false` | Filtrar por conclusão      |

### Calendário

| Método | Endpoint              | Finalidade                   |
| ------ | --------------------- | ---------------------------- |
| GET    | `/api/feriados/{ano}` | Consultar feriados nacionais |

## Integração externa

A aplicação utiliza OpenFeign para consumir a BrasilAPI.

O suporte ao OpenFeign é habilitado por `@EnableFeignClients`, enquanto o cliente remoto utiliza `@FeignClient`.

Configuração:

```properties
integracoes.brasil-api.url=https://brasilapi.com.br
```

Fluxo:

```text
Cliente HTTP
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
```

Exemplo de consulta:

```http
GET /api/feriados/2026
```

## Documentação OpenAPI e Swagger

A API é documentada utilizando Springdoc OpenAPI.

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

Documento OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

Os controllers utilizam anotações como:

* `@Tag`;
* `@Operation`;
* `@Parameter`;
* `@ApiResponse`;
* `@ApiResponses`.

A documentação permite identificar os recursos, métodos HTTP, parâmetros, estruturas recebidas e possíveis respostas da API.

## Postman

As coleções de requisições herdadas do projeto anterior permanecem no diretório:

```text
postman/
```

Elas contêm exemplos para:

* operações CRUD;
* consultas;
* filtros;
* ordenação;
* validações;
* respostas de erro;
* integração com a BrasilAPI.

As coleções poderão ser atualizadas conforme a evolução da nova disciplina.

## Testes automatizados

Para executar todos os testes:

```bash
./mvnw clean test
```

Resultado verificado na Etapa 1:

```text
Tests run: 18
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Durante os testes, a aplicação utiliza um banco H2 separado em memória:

```text
jdbc:h2:mem:andre-gaspar-microsservicos-test
```

Isso impede que os testes alterem o banco persistente utilizado durante a execução normal.

Os avisos relacionados ao carregamento dinâmico do agente Mockito não representam falhas dos testes.

## Como executar

### Requisitos

* Java 21;
* Git;
* terminal Bash, Linux ou WSL.

### Clonar o repositório

```bash
git clone git@github.com:andregasparinfnet/andre-gaspar-microsservicos.git
cd andre-gaspar-microsservicos
```

### Executar os testes

```bash
./mvnw clean test
```

### Executar a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em:

```text
http://localhost:8080
```

Principais endereços:

```text
http://localhost:8080/api/peritos
http://localhost:8080/api/nomeacoes
http://localhost:8080/api/atividades
http://localhost:8080/api/feriados/2026
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/v3/api-docs
http://localhost:8080/h2-console
```

## Evolução nesta disciplina

A evolução será preservada no histórico do Git por meio das seguintes tags:

* `etapa-1`: organização arquitetural da aplicação;
* `etapa-2`: separação e comunicação entre serviços;
* `etapa-3`: configuração e execução cloud native;
* `etapa-4`: comunicação assíncrona e processamento em lote.

A tag `etapa-1` representa a aplicação organizada e validada antes da separação do primeiro serviço independente.

## Histórico de origem

O código inicial deste repositório foi baseado no projeto desenvolvido na disciplina anterior:

[andre-gaspar-api](https://github.com/andregasparinfnet/andre-gaspar-api)

O novo repositório possui histórico próprio para evitar conflito entre as tags das duas disciplinas.

## Uso de inteligência artificial

O ChatGPT, da OpenAI, foi utilizado como ferramenta de apoio durante o desenvolvimento desta atividade.

A ferramenta auxiliou nas seguintes tarefas:

* interpretação dos requisitos acadêmicos;
* planejamento da evolução arquitetural;
* revisão da organização dos pacotes;
* elaboração de comandos Git e Bash;
* identificação e correção de imports;
* análise de erros de compilação;
* revisão da documentação;
* apoio à execução e interpretação dos testes.

As sugestões produzidas pela ferramenta foram analisadas, executadas e verificadas pelo aluno. A responsabilidade pela implementação, compreensão do código, validação dos resultados e conteúdo entregue permanece sendo do autor do projeto.

### Referência da ferramenta de IA

OPENAI. **ChatGPT**. Ferramenta de inteligência artificial generativa utilizada como apoio à organização arquitetural, documentação e revisão do projeto. Disponível em: https://chatgpt.com/. Acesso em: 13 set. 2026.

## Autor

**André Gonçalves Gaspar**

Projeto acadêmico desenvolvido para o Instituto Infnet.
