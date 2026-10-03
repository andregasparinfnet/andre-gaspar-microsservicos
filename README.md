# Sistema de Gestão de Perícias

Aplicação acadêmica em Java e Spring Boot para gerenciar peritos judiciais, nomeações, honorários e atividades periciais. Os dados de demonstração são fictícios.

## Disciplina

**Arquiteturas Avançadas de Software com Microsserviços e Spring Framework**

## Etapa atual

**Etapa 4 — Comunicação Assíncrona e Processamento em Lote**

A solução possui duas aplicações Spring Boot independentes, um Config Server, RabbitMQ e dois bancos PostgreSQL. O Docker Compose coordena a execução local:

| Aplicação | Responsabilidade | Porta local |
| --- | --- | --- |
| Aplicação principal | Nomeações, honorários, atividades e consulta de feriados | `8080` |
| Perito Service | Cadastro e consulta de peritos | `8081` |
| Config Server | Configuração centralizada | `8888` |

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

Na execução pelo Compose, cada aplicação utiliza seu próprio banco PostgreSQL. A aplicação principal não acessa o repository nem as tabelas do Perito Service. A associação é representada por `Long peritoId`, sem relacionamento JPA entre bancos. Os testes automatizados usam H2 em memória.

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

Seu endereço é fornecido pelo Config Server conforme o profile ativo:

~~~properties
# dev
servicos.perito.url=http://localhost:8081

# prod, na rede do Docker Compose
servicos.perito.url=http://perito-service:8081
~~~

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

A execução pelo Compose utiliza dois PostgreSQL 17: `db-nomeacoes` para nomeações e atividades, e `db-peritos` para o cadastro de peritos. Cada aplicação recebe a URL JDBC e as credenciais do próprio banco por variáveis de ambiente. Os volumes `dados-nomeacoes` e `dados-peritos` preservam os registros ao recriar os containers. Os arquivos H2 da etapa anterior não são migrados automaticamente.

Os dados fictícios permanecem em `src/main/resources/dados/nomeacoes.txt` e `src/main/resources/dados/atividades.txt` na aplicação principal e em `perito-service/src/main/resources/dados/peritos.txt` no serviço de peritos. A carga inicial usa `peritoId` sem chamada remota; a validação HTTP acontece na inclusão e alteração de nomeações.

## Configuração externa e profiles

As aplicações possuem `application-dev.properties` e `application-prod.properties`. O Compose seleciona `prod` por `SPRING_PROFILES_ACTIVE`. Os testes automatizados utilizam H2 em memória e desabilitam o cliente Config Server.

O projeto `config-server/` usa Spring Cloud Config Server com backend `native`. Os arquivos em `config-server/src/main/resources/config/` fornecem as portas e a URL do Perito Service: `http://localhost:8081` em `dev` e `http://perito-service:8081` em `prod`. Cada cliente recebe a URL do Config Server em `CONFIG_SERVER_URL`.

| Variável | Uso |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | Seleciona `dev` ou `prod` |
| `CONFIG_SERVER_URL` | Endereço do Config Server |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Conexão da aplicação ao seu próprio banco |
| `NOMEACOES_DB_PASSWORD`, `PERITOS_DB_PASSWORD` | Senhas dos bancos no Compose |
| `BRASIL_API_URL` | Endereço da integração externa de feriados |
| `RABBITMQ_USER`, `RABBITMQ_PASSWORD` | Credenciais locais do broker no Compose |
| `BATCH_ATIVIDADES_ARQUIVO` | Fonte CSV da importação; usa o arquivo incluído na aplicação por padrão |

As senhas locais ficam em `.env`, ignorado pelo Git; `.env.example` documenta os nomes das variáveis. No profile `prod`, as credenciais e URLs JDBC devem ser fornecidas externamente.

## Execução local

Requisitos: Docker Engine e plugin Docker Compose. Na primeira execução, copie `.env.example` para `.env` e substitua as três senhas de exemplo por senhas locais distintas. O arquivo `.env` é ignorado pelo Git. Depois execute na raiz do repositório:

~~~bash
sudo docker compose config --quiet
sudo docker compose up --build -d
sudo docker compose ps
~~~

A composição inicia a aplicação principal (`8080`), o Perito Service (`8081`), o Config Server (`8888`), o RabbitMQ e os dois bancos. Ela aguarda as verificações de disponibilidade do Config Server e dos PostgreSQL. Dentro dos containers, os endereços são `config-server:8888`, `perito-service:8081`, `db-nomeacoes:5432` e `db-peritos:5432`.

Para encerrar e preservar os volumes:

~~~bash
sudo docker compose down
~~~

As portas `5433` e `5434` permitem acessar os respectivos bancos a partir do host. O profile `dev` prevê PostgreSQL nessas portas, Config Server local e `DB_PASSWORD` informado externamente quando as aplicações forem executadas com Java 21 e Maven Wrapper fora do Compose.

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

Os cenários de rede foram executados manualmente com `curl`. Na Etapa 2, essas inclusões eram armazenadas no H2 local. Na Etapa 3, a execução pelo Compose utiliza PostgreSQL.

## Testes automatizados

Os projetos possuem suítes Maven separadas:

```bash
./mvnw clean test
```

```bash
cd perito-service
./mvnw clean test
```

Na verificação da Etapa 2, a aplicação principal executou **17 testes sem falhas** e o Perito Service executou **4 testes sem falhas**. Os testes da aplicação principal simulam o cliente Feign quando necessário. O funcionamento com ambos os serviços ativos e a falha de comunicação foram comprovados adicionalmente por chamadas HTTP reais.

## Reflexão arquitetural

**Qual funcionalidade foi separada?** O cadastro e a consulta de peritos, identificados como candidatos na Etapa 1.

**Por que essa funcionalidade?** O cadastro representa uma responsabilidade reconhecível no domínio e pode atender outras funcionalidades além das nomeações.

**O que ficou mais complexo?** A aplicação passou a depender de contrato HTTP, DTOs, configuração do endereço remoto, execução de dois processos e tratamento das falhas de rede.

**O que ocorre quando o serviço está indisponível?** A inclusão e a alteração de nomeações que precisam validar o perito não podem concluir a operação e retornam `503`. As consultas locais que não exigem essa validação podem continuar respondendo.

**O serviço precisa permanecer independente?** O cadastro poderia continuar como módulo do monólito. A separação oferece autonomia de evolução, mas acrescenta dependências operacionais. Sua permanência como serviço deve considerar esses custos e as necessidades reais da solução.

## Resultados observados na Etapa 3

| Cenário | Resultado observado |
| --- | --- |
| Construção das três imagens Docker | Concluída |
| Config Server e dois PostgreSQL | Containers saudáveis |
| `GET /api/peritos` e `GET /api/nomeacoes` | `200 OK`, com dados iniciais |
| Configurações `dev` e `prod` das duas aplicações | Consultadas no Config Server |
| Inclusão de nomeação com `peritoId=1` | `201 Created` |
| Inclusão com `peritoId=999999` | `404 Not Found` |
| Recriação dos containers com `down` e `up` | Nomeação `ETAPA3-123708C437F4` e perito 1 preservados |

As suítes automatizadas executaram 17 testes na aplicação principal e 4 no Perito Service, sem falhas. Elas usam H2 em memória. As verificações HTTP desta etapa foram feitas com os dois PostgreSQL do Compose.

## Reflexão arquitetural da Etapa 3

**Quais configurações podem variar entre ambientes?** Portas, endereços dos serviços e bancos, credenciais e exibição das consultas SQL.

**Quais foram externalizadas?** O Config Server fornece portas e URL do Perito Service por profile. As aplicações recebem URLs JDBC, usuários e senhas por variáveis de ambiente. `SPRING_PROFILES_ACTIVE` seleciona o profile e `CONFIG_SERVER_URL` indica o servidor de configuração.

**Por que um serviço não deve acessar diretamente o banco do outro?** Isso cria dependência das tabelas internas e contorna as regras do serviço responsável. Nomeações consulta peritos pela API HTTP e armazena apenas `peritoId`.

**Qual problema o Docker resolve?** Permite construir e executar cada aplicação em uma imagem com ambiente Java definido, reduzindo diferenças entre instalações locais.

**Qual é a função do Docker Compose?** Iniciar e conectar as três aplicações e os dois bancos com um comando, incluindo variáveis, volumes e verificações de disponibilidade.

**Qual problema a configuração centralizada procura resolver?** Permite manter portas e URLs de comunicação por aplicação e profile em um ponto comum, sem alterar código Java ao mudar o ambiente. As senhas permanecem fora do Config Server deste projeto.

## Comunicação assíncrona e processamento em lote (Etapa 4)

### Avisos de nomeação pelo RabbitMQ

`POST /api/nomeacoes/{id}/avisos` publica um aviso com `avisoId`, `nomeacaoId`, `peritoId` e `numeroProcesso`. A aplicação principal responde `202 Accepted` após a confirmação da publicação pelo broker. O Perito Service consome a mensagem e registra o aviso em seu próprio PostgreSQL; os registros podem ser consultados em `GET /api/peritos/{id}/avisos`. Esse registro representa um aviso interno, sem envio de e-mail.

O aviso pode ser tratado depois porque a nomeação já está cadastrada quando a publicação é solicitada. A fila `avisos.nomeacoes` é durável e a mensagem é persistente. O consumidor identifica o aviso pelo `avisoId` para evitar duplicar seu registro se receber a mesma mensagem novamente.

Para observar o fluxo com o Compose em execução:

~~~bash
curl -i -X POST http://localhost:8080/api/nomeacoes/1/avisos
curl -i http://localhost:8081/api/peritos/1/avisos
~~~

Também foi testada a indisponibilidade temporária do consumidor: com o Perito Service parado, a aplicação principal respondeu `202`, a fila apresentou uma mensagem pronta e nenhum consumidor; após reiniciar o serviço, o aviso foi registrado. A publicação ainda depende da disponibilidade do RabbitMQ.

### Importação de atividades com Spring Batch

`POST /api/importacoes/atividades` inicia o job `importarAtividadesCsv`. Seu `ItemReader` lê as seis linhas de `src/main/resources/dados/atividades-importacao.csv`. O `ItemProcessor` normaliza e valida os campos. O `ItemWriter` encontra a nomeação pelo número do processo e grava a atividade no banco da aplicação principal. O Step processa chunks de cinco registros.

O job não executa automaticamente na inicialização. A fonte pode ser indicada por `BATCH_ATIVIDADES_ARQUIVO`; por padrão, usa o CSV incluído na aplicação. Cada atividade importada recebe um `codigo_importacao` único. Uma nova execução lê o arquivo novamente e ignora os códigos já cadastrados, sem duplicar as atividades.

~~~bash
curl -i -X POST http://localhost:8080/api/importacoes/atividades
curl -i http://localhost:8080/api/atividades
~~~

A resposta informa o identificador e o estado da execução, além dos itens lidos e entregues ao Writer. **`processados` não é a quantidade de novas atividades gravadas:** inclui as linhas entregues ao Writer mesmo quando seu código já existe.

Na verificação com PostgreSQL, a primeira execução passou de 4 para 10 atividades. A segunda leu as seis linhas, manteve 10 atividades e recebeu outro identificador de execução. Os códigos `ET4-A01` a `ET4-A06` apareceram uma vez cada no banco e permaneceram após reiniciar a aplicação. A suíte da aplicação principal passou com 18 testes; a do Perito Service terminou sem falhas.

### Reflexão arquitetural da Etapa 4

1. **Operação assíncrona:** registrar no Perito Service o aviso de uma nomeação cadastrada.
2. **Por que pode esperar:** o registro do aviso não precisa terminar durante a requisição de publicação.
3. **Consumidor indisponível:** com o broker ativo, a mensagem aguarda na fila durável até o consumidor voltar.
4. **Operação em lote:** importar atividades periciais de um CSV para nomeações existentes.
5. **Por que usar Batch:** há vários registros a ler, validar e gravar em chunks, com identificação de cada execução.
6. **Quando usar cada abordagem:** REST para consultar e validar o perito com resposta imediata; mensageria para registrar avisos sem esperar o consumidor; Batch para processar o conjunto de atividades do arquivo.

## Histórico das etapas

A tag `etapa-1` preserva a versão anterior à separação, quando Perito, Nomeação e Atividade eram módulos da mesma aplicação Spring Boot. Nesse marco foram demonstradas a organização por domínio, as camadas Controller, Service e Repository, validação, tratamento de exceções, consultas Spring Data, OpenAPI e análise das dependências.

Na Etapa 2, a chamada interna `NomeacaoPericialService → PeritoService` foi substituída por `NomeacaoPericialService → PeritoClient → HTTP → Perito Service`. A tag `etapa-2` registra essa separação.

A Etapa 3 adiciona profiles, variáveis de ambiente, Config Server, PostgreSQL e Docker Compose. A tag `etapa-3` registra essa versão. A Etapa 4 acrescenta mensageria e processamento Batch. A tag `etapa-4` identifica a versão final desta etapa.

## Tecnologias

Java 21, Spring Boot, Spring MVC, Spring Data JPA, PostgreSQL, H2 nos testes, Bean Validation, Spring Cloud OpenFeign, Spring Cloud Config Server, Spring AMQP, RabbitMQ, Spring Batch, Springdoc OpenAPI, Swagger UI, BrasilAPI, Maven, JUnit, MockMvc, Docker, Docker Compose, `curl` e Git.

## Origem do projeto

O ponto de partida foi o projeto desenvolvido na disciplina anterior: [andre-gaspar-api](https://github.com/andregasparinfnet/andre-gaspar-api). Este repositório possui histórico e tags próprios.

## Uso de inteligência artificial

O ChatGPT (Codex), da OpenAI, foi utilizado como apoio à interpretação dos requisitos, análise arquitetural, implementação, diagnóstico de erros, comandos de teste e revisão da documentação. As sugestões foram avaliadas e verificadas pelo aluno, responsável pelo código e pela entrega.

**Referência:** OPENAI. *ChatGPT (Codex)*. Ferramenta de inteligência artificial generativa. Disponível em: <https://chatgpt.com/>. Acesso em: 2 out. 2026.

## Autor

**André Gonçalves Gaspar**

Projeto acadêmico desenvolvido para o Instituto Infnet.
