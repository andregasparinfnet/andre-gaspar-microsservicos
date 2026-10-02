# Sistema de Gestão de Perícias

Aplicação acadêmica em Java e Spring Boot para gerenciar peritos judiciais, nomeações, honorários e atividades periciais. Os dados de demonstração são fictícios.

## Disciplina

**Arquiteturas Avançadas de Software com Microsserviços e Spring Framework**

## Etapa atual

**Etapa 2 — Separação e Comunicação entre Serviços**

A solução possui duas aplicações Spring Boot independentes:

| Aplicação | Responsabilidade | Porta local |
| --- | --- | --- |
| Aplicação principal | Nomeações, honorários, atividades e consulta de feriados | `8080` |
| Perito Service | Cadastro e consulta de peritos | `8081` |

O cadastro de peritos, escolhido como candidato na Etapa 1, foi extraído para `perito-service/`. A aplicação principal conserva o identificador `peritoId` em cada nomeação e consulta o serviço de peritos via HTTP quando precisa validar esse cadastro.

## Arquitetura

```text
Cliente HTTP
    ↓
Aplicação principal (8080)
    ├── Nomeação: Controller → Service → Repository → banco principal
    ├── Atividade: Controller → Service → Repository → banco principal
    ├── Calendário → BrasilAPI
    └── NomeacaoPericialService → PeritoClient (OpenFeign)
                                  ↓ HTTP
                             Perito Service (8081)
                                  ↓
                             Controller → Service → Repository → banco de peritos
```

As aplicações possuem bancos H2 separados. A aplicação principal não acessa o repository nem as tabelas do Perito Service. A associação com o perito é representada por um `Long peritoId`, sem relacionamento JPA entre bancos.

## Responsabilidades e dependências

### Nomeação

Gerencia número do processo, datas, prazo, status, honorários, atividades associadas e identificador do perito. O `NomeacaoPericialService` consulta `PeritoClient` ao incluir ou alterar uma nomeação. O controller cuida da interface HTTP e não implementa a chamada ao serviço remoto.

### Atividade

Gerencia as atividades vinculadas a uma nomeação. A comunicação com o módulo de nomeações permanece interna à aplicação principal.

### Calendário

Consulta feriados nacionais por meio de `BrasilApiClient`, utilizando OpenFeign para acessar a BrasilAPI.

### Perito Service

Mantém o cadastro dos peritos, incluindo nome e e-mail, e oferece operações de inclusão, consulta, alteração e exclusão. Possui projeto Spring Boot, API, entidade, service, repository, inicialização e banco próprios.

## API do Perito Service

Base local: `http://localhost:8081`

| Método | Endpoint | Operação |
| --- | --- | --- |
| `GET` | `/api/peritos` | Listar peritos |
| `GET` | `/api/peritos/{id}` | Consultar perito por ID |
| `POST` | `/api/peritos` | Incluir perito |
| `PUT` | `/api/peritos/{id}` | Alterar perito |
| `DELETE` | `/api/peritos/{id}` | Excluir perito |

A API utiliza `PeritoRequest` e `PeritoResponse`. A aplicação principal recebe os dados remotos em `PeritoResumoResponse`; entidades JPA não são compartilhadas como contrato HTTP.

Documentação do serviço:

- OpenAPI: `http://localhost:8081/v3/api-docs`
- Swagger UI: `http://localhost:8081/swagger-ui/index.html`

## Comunicação entre as aplicações

O cliente Feign da aplicação principal consulta:

```http
GET /api/peritos/{id}
```

Seu endereço é externalizado em `src/main/resources/application.properties`:

```properties
servicos.perito.url=${PERITO_SERVICE_URL:http://localhost:8081}
```

Fluxo da inclusão de uma nomeação:

1. O cliente chama `POST /api/nomeacoes?peritoId={id}` na aplicação principal.
2. `NomeacaoPericialService` solicita ao `PeritoClient` a consulta do perito.
3. Se o perito existe, a aplicação principal grava a nomeação em seu próprio banco.
4. Se o perito não existe, retorna `404 Not Found`.
5. Se a comunicação falha, retorna `503 Service Unavailable`.

Na alteração, a ausência de `peritoId` no corpo preserva o perito já associado. Se outro identificador for informado, ele é validado no Perito Service antes de atualizar a nomeação.

O `503` apresenta a mensagem `Serviço de peritos temporariamente indisponível`, sem expor detalhes internos da falha de rede.

## Endpoints principais da aplicação principal

Base local: `http://localhost:8080`

| Método | Endpoint | Operação |
| --- | --- | --- |
| `GET` | `/api/nomeacoes` | Listar nomeações |
| `GET` | `/api/nomeacoes/{id}` | Consultar nomeação |
| `POST` | `/api/nomeacoes?peritoId={id}` | Incluir nomeação com validação remota do perito |
| `PUT` | `/api/nomeacoes/{id}` | Alterar nomeação |
| `DELETE` | `/api/nomeacoes/{id}` | Excluir nomeação |
| `GET` | `/api/nomeacoes/status/{status}` | Filtrar por status |
| `GET` | `/api/nomeacoes/ordenadas-por-prazo` | Ordenar pelo prazo |
| `GET` | `/api/nomeacoes/processo?numeroProcesso={numero}` | Buscar pelo processo |
| `GET` | `/api/nomeacoes/resumos` | Obter resumos com `peritoId` |
| `GET` | `/api/atividades` | Listar atividades |
| `POST` | `/api/atividades?nomeacaoId={id}` | Incluir atividade |
| `GET` | `/api/feriados/{ano}` | Consultar feriados |

As operações restantes de consulta, alteração e exclusão de atividades também estão documentadas na API principal.

Documentação da aplicação principal:

- OpenAPI: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

As rotas `/api/peritos` pertencem à aplicação da porta `8081`; não existem mais na aplicação principal.

## Bancos e carga inicial

A aplicação principal persiste nomeações e atividades em seu banco H2. O Perito Service persiste os peritos em outro banco H2. Nenhuma aplicação consulta diretamente o banco da outra.

Os arquivos de demonstração também pertencem aos respectivos projetos:

- Aplicação principal: `src/main/resources/dados/nomeacoes.txt` e `atividades.txt`.
- Perito Service: `perito-service/src/main/resources/dados/peritos.txt`.

A carga inicial da aplicação principal utiliza o identificador do perito registrado nos dados de demonstração. Seu funcionamento na inicialização não depende de uma consulta remota. A validação HTTP ocorre nas operações de inclusão e alteração de nomeações.

## Execução local

Requisitos: Java 21 e Maven Wrapper dos projetos.

Terminal 1, serviço de peritos:

```bash
cd perito-service
./mvnw spring-boot:run
```

Terminal 2, raiz do repositório:

```bash
./mvnw spring-boot:run
```

Se for necessário configurar outro endereço para o serviço:

```bash
PERITO_SERVICE_URL=http://localhost:8081 ./mvnw spring-boot:run
```

Cada projeto deve ser iniciado a partir de seu próprio diretório para utilizar a configuração e o banco H2 correspondentes.

## Validação da comunicação

Com os dois processos em execução:

```bash
curl -i http://localhost:8081/api/peritos/1
curl -i http://localhost:8081/v3/api-docs
curl -i http://localhost:8080/api/nomeacoes
```

Exemplo de criação pela aplicação principal, usando um número de processo ainda não cadastrado:

```bash
curl -i -X POST 'http://localhost:8080/api/nomeacoes?peritoId=1' \
  -H 'Content-Type: application/json' \
  -d '{
    "numeroProcesso": "ETAPA2-EXEMPLO-001",
    "dataNomeacao": "2026-10-02",
    "prazoEmDias": 15,
    "status": "RECEBIDA",
    "honorarios": {
      "valorProposto": 1000.00,
      "valorFixado": 0.00,
      "valorRecebido": 0.00,
      "depositado": false
    }
  }'
```

Para testar a indisponibilidade, interrompa somente o Perito Service, confirme que a porta `8081` não aceita conexões e tente incluir outra nomeação com um número de processo diferente. A aplicação principal deve responder `503` e continuar executando.

## Resultados observados na Etapa 2

| Cenário | Resultado |
| --- | --- |
| Listagem de nomeações na aplicação principal | `200 OK` |
| Consulta isolada de `/api/peritos/1` | `200 OK`, com ID, nome e e-mail |
| OpenAPI do Perito Service | `200 OK` |
| Inclusão com `peritoId=1` e serviço disponível | `201 Created` |
| Inclusão com `peritoId=999999` e serviço disponível | `404 Not Found` |
| Inclusão com a porta `8081` indisponível | `503 Service Unavailable`, com mensagem controlada |

Os cenários de rede foram executados manualmente com `curl`. As inclusões que responderam `201` permanecem no banco H2 local até serem excluídas.

## Testes automatizados

Os projetos possuem suítes Maven separadas:

```bash
./mvnw clean test
```

```bash
cd perito-service
./mvnw clean test
```

Na verificação realizada durante esta etapa, a aplicação principal executou **17 testes sem falhas** e o Perito Service executou **4 testes sem falhas**. Os testes da aplicação principal simulam o cliente Feign quando necessário. O funcionamento com ambos os serviços ativos e a falha de comunicação foram comprovados adicionalmente por chamadas HTTP reais.

## Reflexão arquitetural

**Qual funcionalidade foi separada?** O cadastro e a consulta de peritos, identificados como candidatos na Etapa 1.

**Por que essa funcionalidade?** O cadastro representa uma responsabilidade reconhecível no domínio e pode atender outras funcionalidades além das nomeações.

**O que ficou mais complexo?** A aplicação passou a depender de contrato HTTP, DTOs, configuração do endereço remoto, execução de dois processos e tratamento das falhas de rede.

**O que ocorre quando o serviço está indisponível?** A inclusão e a alteração de nomeações que precisam validar o perito não podem concluir a operação e retornam `503`. As consultas locais que não exigem essa validação podem continuar respondendo.

**O serviço precisa permanecer independente?** O cadastro poderia continuar como módulo do monólito. A separação oferece autonomia de evolução, mas acrescenta dependências operacionais. Sua permanência como serviço deve considerar esses custos e as necessidades reais da solução.

## Histórico das etapas

A tag `etapa-1` preserva a versão anterior à separação, quando Perito, Nomeação e Atividade eram módulos da mesma aplicação Spring Boot. Nesse marco foram demonstradas a organização por domínio, as camadas Controller, Service e Repository, validação, tratamento de exceções, consultas Spring Data, OpenAPI e análise das dependências.

Na Etapa 2, a chamada interna `NomeacaoPericialService → PeritoService` foi substituída por `NomeacaoPericialService → PeritoClient → HTTP → Perito Service`. A tag `etapa-2` será registrada somente após a revisão final desta etapa.

As etapas posteriores abordarão configuração Cloud Native, bancos relacionais, containers, mensageria e processamento Batch conforme o enunciado da disciplina.

## Tecnologias

Java 21, Spring Boot, Spring MVC, Spring Data JPA, H2, Bean Validation, Spring Cloud OpenFeign, Springdoc OpenAPI, Swagger UI, BrasilAPI, Maven, JUnit, MockMvc, `curl` e Git.

## Origem do projeto

O ponto de partida foi o projeto desenvolvido na disciplina anterior: [andre-gaspar-api](https://github.com/andregasparinfnet/andre-gaspar-api). Este repositório possui histórico e tags próprios.

## Uso de inteligência artificial

O ChatGPT (Codex), da OpenAI, foi utilizado como apoio à interpretação dos requisitos, análise arquitetural, implementação, diagnóstico de erros, comandos de teste e revisão da documentação. As sugestões foram avaliadas e verificadas pelo aluno, responsável pelo código e pela entrega.

**Referência:** OPENAI. *ChatGPT (Codex)*. Ferramenta de inteligência artificial generativa. Disponível em: <https://chatgpt.com/>. Acesso em: 2 out. 2026.

## Autor

**André Gonçalves Gaspar**

Projeto acadêmico desenvolvido para o Instituto Infnet.
