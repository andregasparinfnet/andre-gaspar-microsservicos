# Histórico do projeto de disciplina

Este histórico se refere ao repositório `andre-gaspar-microsservicos`.
As tags acadêmicas identificam a implementação de cada etapa.

## Etapa 4 — Comunicação assíncrona e processamento em lote

- RabbitMQ integrado às duas aplicações por meio de produtor, fila
  durável e consumidor.
- Avisos de nomeação publicados pela aplicação principal e registrados
  no banco do Perito Service.
- Importação CSV de atividades com Spring Batch, Job, Step, Reader,
  Processor, Writer e chunks de cinco registros.
- Identificação de avisos e atividades importadas para evitar registros
  duplicados nas reexecuções demonstradas.
- Documentação da escolha entre REST, mensageria e Batch no README.

## Etapa 3 — Execução em containers

- Profiles `dev` e `prod`, Config Server e variáveis de ambiente.
- Dois bancos PostgreSQL independentes.
- Dockerfiles e Docker Compose para execução integrada.

## Etapa 2 — Serviço independente

- Cadastro de peritos extraído para uma aplicação Spring Boot própria.
- API REST com DTOs, OpenAPI e consulta pela aplicação principal via
  OpenFeign.
- Tratamento da indisponibilidade da comunicação HTTP.

## Etapa 1 — Organização arquitetural

- Responsabilidades do domínio organizadas em módulos.
- Camadas Controller, Service e Repository, validação, tratamento de
  erros, consultas JPA e documentação OpenAPI.

[Etapa 1]: https://github.com/andregasparinfnet/andre-gaspar-microsservicos/tree/etapa-1
[Etapa 2]: https://github.com/andregasparinfnet/andre-gaspar-microsservicos/tree/etapa-2
[Etapa 3]: https://github.com/andregasparinfnet/andre-gaspar-microsservicos/tree/etapa-3
[Etapa 4]: https://github.com/andregasparinfnet/andre-gaspar-microsservicos/tree/etapa-4
