# Changelog

Todas as alterações relevantes deste projeto serão documentadas neste arquivo.

O formato é baseado no [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/), e o versionamento segue o [Semantic Versioning](https://semver.org/lang/pt-BR/).

## [Não publicado]

Alterações em desenvolvimento para a próxima versão da aplicação.

## [0.4.0] — 2026-08-31

**Marco acadêmico:** `etapa-4`

**Competência:** APIs REST e Persistência com Spring Data

### Adicionado

* Persistência das entidades com Spring Data JPA e Hibernate.
* `PeritoRepository`, `NomeacaoPericialRepository` e
  `AtividadePericialRepository`, estendendo `JpaRepository`.
* Banco de dados H2 persistido em arquivo para execução da aplicação.
* Banco H2 em memória separado para execução dos testes automatizados.
* Mapeamentos JPA com `@Entity`, `@MappedSuperclass`, `@Embeddable`,
  `@Embedded`, `@OneToMany` e `@ManyToOne`.
* Identificadores gerados automaticamente com `GenerationType.IDENTITY`.
* Consultas derivadas do Spring Data para filtragem, busca e ordenação.
* Bean Validation nas entidades e corpos das requisições REST.
* Integração externa com a BrasilAPI utilizando Spring Cloud OpenFeign.
* Cliente `BrasilApiClient`, DTO `FeriadoNacional`, Service e Controller
  para consulta de feriados nacionais.
* Endpoint `GET /api/feriados/{ano}`.
* Testes automatizados da integração e das regras da Etapa 4.
* Collection Postman atualizada para demonstração da versão final.

### Alterado

* Arquitetura evoluída de
  `Cliente HTTP → Controller → Service → Map`
  para
  `Cliente HTTP → Controller → Service → Repository → Banco de Dados`.
* `BaseCrudService` refatorado para utilizar `JpaRepository` em vez de Map.
* Services refatorados para injeção dos respectivos Repositories.
* Loaders adaptados para persistir os dados iniciais através dos Services.
* Inicializador adaptado para evitar nova carga quando o banco já contém
  registros.
* Modelos de domínio adaptados para persistência JPA.
* Relacionamentos entre peritos, nomeações e atividades persistidos no banco.
* Serialização JSON configurada para evitar referências circulares.
* Tratamento global de erros ampliado para respostas HTTP padronizadas.
* Documentação OpenAPI atualizada para a versão `0.4.0`.
* Versão Maven atualizada para `0.4.0-SNAPSHOT`.
* README atualizado para a Etapa 4.

### Validado

* Persistência de peritos, nomeações e atividades no banco H2.
* Manutenção dos dados após reinicialização da aplicação.
* CRUD REST utilizando Spring Data JPA.
* Consulta de nomeações por status.
* Ordenação de nomeações por prazo.
* Busca de nomeação pelo número do processo.
* Filtragem de atividades por situação e ordenação por prazo.
* Restrições de unicidade de e-mail e número processual.
* Bean Validation e respostas `400 Bad Request`.
* Respostas `404 Not Found` e `409 Conflict`.
* Integração externa com BrasilAPI por OpenFeign.
* Documentação Swagger UI e contrato `/v3/api-docs`.
* Execução de 17 testes com zero falhas e zero erros.
* Compilação finalizada com `BUILD SUCCESS`.

## [0.3.0] — 2026-08-30

**Marco acadêmico:** `etapa-3`

**Competência:** API REST com Spring Boot

### Adicionado

* Controllers REST para peritos, nomeações e atividades periciais.
* Endpoints `GET`, `POST`, `PUT` e `DELETE` para os três contextos de
  negócio.
* Respostas HTTP com os códigos `200`, `201`, `204`, `400`, `404` e `409`.
* Classe `TratadorGlobalExcecoes` para tratamento centralizado das
  exceções da API.
* Estrutura padronizada de erros por meio do record `ErroApi`.
* Documentação OpenAPI gerada com Springdoc.
* Interface Swagger UI para documentação e testes interativos da API.
* Classe `OpenApiConfig` com título, descrição e versão da documentação.
* Anotações OpenAPI nos Controllers para descrever operações, parâmetros
  e respostas.
* Testes de integração dos endpoints REST com MockMvc.
* Teste da geração e do conteúdo da documentação OpenAPI.
* Coleção do Postman com requisições organizadas para demonstração e
    teste dos endpoints REST.
* Testes automatizados no Postman para validar códigos HTTP, formato JSON
  e conteúdo das respostas.

### Alterado

* Services registrados como componentes do Spring com `@Service`.
* Inicializador refatorado para receber os Services por injeção de
  dependência pelo construtor.
* Mesmas instâncias dos Services compartilhadas entre Loaders e
  Controllers.
* Aplicação evoluída da arquitetura `Service → Map` para
  `Cliente HTTP → Controller → Service → Map`.
* Versão do projeto atualizada para `0.3.0`.
* Documentação atualizada para a Etapa 3.

### Validado

* Operações REST de listagem, obtenção por identificador, inclusão,
  alteração e exclusão.
* Endpoints dos três contextos de negócio.
* Retornos de sucesso `200 OK`, `201 Created` e `204 No Content`.
* Retornos de erro `400 Bad Request`, `404 Not Found` e `409 Conflict`.
* Estrutura padronizada das respostas de erro.
* Disponibilização da documentação no Swagger UI e em `/v3/api-docs`.
* Execução de dez testes com zero falhas e zero erros.
* Compilação finalizada com `BUILD SUCCESS`.
* Execução da coleção do Postman com dez requisições, 29 testes aprovados
    e zero erros.

## [0.2.0] — 2026-08-28

**Marco acadêmico:** `etapa-2`

**Competência:** Estruturas de Dados e Camada de Serviço

### Adicionado

* Interface genérica `CrudService<T, ID>` com operações CRUD.
* Interface `Identificavel` para estabelecer o contrato de obtenção do
  identificador dos objetos.
* Classe abstrata genérica `BaseCrudService<T extends Identificavel>` para
  centralizar o armazenamento em Map e as operações CRUD.
* Classe `PeritoService` para gerenciamento dos peritos em memória.
* Classe `NomeacaoPericialService` para gerenciamento das nomeações.
* Classe `AtividadePericialService` para gerenciamento das atividades.
* Estruturas `LinkedHashMap` utilizando o identificador como chave e o
  objeto correspondente como valor.
* Operações para incluir, alterar, excluir, obter por identificador e listar
  objetos.
* Exceção `DadosInvalidosException`.
* Exceção `EntidadeJaExistenteException`.
* Exceção `EntidadeNaoEncontradaException`.
* Consulta de nomeações por status utilizando `filter`.
* Ordenação de nomeações por prazo utilizando `sorted`.
* Busca de nomeação pelo número do processo utilizando `findFirst`.
* Transformação das nomeações em números processuais utilizando `map`.
* Demonstração das consultas com Streams no console.
* Testes unitários do serviço de nomeações.
* Teste integrado da leitura dos arquivos e carga nos Services.

### Alterado

* Services específicos refatorados para herdar o CRUD do
  `BaseCrudService`, reduzindo duplicação de código.
* Validações próprias de peritos, nomeações e atividades preservadas nos
  respectivos Services.
* Loaders integrados à camada de serviço.
* Dados lidos dos arquivos-texto armazenados nos Maps.
* Busca de peritos e nomeações realizada pelos respectivos Services.
* Relacionamentos um-para-muitos preservados durante a carga dos dados.
* Inicializador atualizado para demonstrar a arquitetura da Etapa 2.
* Saída do console atualizada com consultas baseadas em Streams.
* Documentação atualizada para a Etapa 2.
* Versão do projeto atualizada para `0.2.0`.

### Validado

* Armazenamento de um perito, duas nomeações e quatro atividades nos Maps.
* Relacionamento entre o perito e suas duas nomeações.
* Relacionamento entre as nomeações e suas respectivas atividades.
* Operações de inclusão, alteração, exclusão, consulta e listagem.
* Tratamento de identificador duplicado.
* Tratamento de entidade inexistente.
* Filtragem, ordenação, busca e transformação de coleções.
* Leitura dos três arquivos-texto.
* Execução de seis testes com zero falhas e zero erros.
* Compilação finalizada com `BUILD SUCCESS`.

## [0.1.0] — 2026-08-27

**Marco acadêmico:** `etapa-1`

**Competência:** Orientação a Objetos Avançada

### Adicionado

* Projeto Java com Spring Boot e Maven.
* Modelo de negócio para gerenciamento de nomeações periciais.
* Classe abstrata `Pessoa`.
* Classe `Perito`, utilizando herança por meio de `extends Pessoa`.
* Classe `NomeacaoPericial` para representar uma nomeação recebida pelo perito.
* Classe `AtividadePericial` para representar tarefas e prazos vinculados à nomeação.
* Classe `HonorariosPericiais` para controle dos valores propostos, fixados, depositados e recebidos.
* Enum `StatusNomeacao` para representar os possíveis estados de uma nomeação.
* Relacionamento um-para-muitos entre `Perito` e `NomeacaoPericial`.
* Relacionamento um-para-muitos entre `NomeacaoPericial` e `AtividadePericial`.
* Relacionamento entre `NomeacaoPericial` e `HonorariosPericiais`.
* Atributos dos tipos `String`, `int`, `double`, `boolean`, `LocalDate` e `BigDecimal`.
* Comportamentos para aceitar, recusar e alterar o status das nomeações.
* Comportamentos para concluir, reabrir e verificar o atraso das atividades.
* Comportamentos para registrar a fixação, o depósito e o recebimento dos honorários.
* Implementação do método `toString()` nas classes do modelo.
* Arquivo `peritos.txt` com dados fictícios para carga inicial.
* Arquivo `nomeacoes.txt` com dados fictícios e relacionamento com o perito.
* Arquivo `atividades.txt` com dados fictícios e relacionamento com as nomeações.
* Classe `PeritoLoader` para leitura do arquivo de peritos e criação dos objetos.
* Classe `NomeacaoLoader` para leitura das nomeações e associação ao respectivo perito.
* Classe `AtividadeLoader` para leitura das atividades e associação às respectivas nomeações.
* Classe `InicializadorAplicacao`, utilizando `CommandLineRunner`.
* Rotina de inicialização para leitura dos arquivos, criação dos objetos e estabelecimento dos relacionamentos.
* Apresentação das informações dos objetos no console.
* Resumo da quantidade de peritos, nomeações e atividades carregadas.
* Teste de inicialização do contexto do Spring Boot.
* Documentação da Etapa 1 no arquivo `README.md`.
* Diagrama de classes em Mermaid.
* Instruções para teste e execução no Linux e WSL.
* Documentação dos limites da Etapa 1 e da evolução planejada para as etapas seguintes.

### Alterado

* Versão do projeto definida como `0.1.0`, seguindo Semantic Versioning.
* Permissão de execução do Maven Wrapper para permitir o uso de `./mvnw`.
* Configuração do projeto com suporte ao Spring Boot DevTools.
* Classe principal da aplicação com configuração de Logger.
* Finais de linha dos arquivos normalizados para o padrão Linux.

### Removido

* Arquivo `mvnw.cmd:Zone.Identifier`, criado pelo Windows e sem utilidade para o projeto.

### Validado

* Inicialização da aplicação com Spring Boot.
* Leitura dos três arquivos-texto.
* Carregamento de um perito fictício.
* Associação de duas nomeações ao perito.
* Associação de quatro atividades às respectivas nomeações.
* Representação textual dos objetos no console.
* Relacionamentos um-para-muitos em memória.
* Herança entre `Perito` e `Pessoa`.
* Execução dos testes com zero falhas e zero erros.
* Compilação finalizada com `BUILD SUCCESS`.

[Não publicado]: https://github.com/andregasparinfnet/andre-gaspar-api/compare/etapa-4...HEAD
[0.4.0]: https://github.com/andregasparinfnet/andre-gaspar-api/tree/etapa-4
[0.3.0]: https://github.com/andregasparinfnet/andre-gaspar-api/tree/etapa-3
[0.2.0]: https://github.com/andregasparinfnet/andre-gaspar-api/tree/etapa-2
[0.1.0]: https://github.com/andregasparinfnet/andre-gaspar-api/tree/etapa-1
