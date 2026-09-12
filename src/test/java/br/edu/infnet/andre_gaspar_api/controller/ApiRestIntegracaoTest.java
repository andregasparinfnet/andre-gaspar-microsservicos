package br.edu.infnet.andre_gaspar_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import br.edu.infnet.andre_gaspar_api.service.NomeacaoPericialService;
import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiRestIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NomeacaoPericialService nomeacaoService;

    @Test
    void deveListarOsTresContextosDeNegocio() throws Exception {
        mockMvc.perform(get("/api/peritos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber());

        mockMvc.perform(get("/api/nomeacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber());

        mockMvc.perform(get("/api/atividades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber());
    }

    @Test
    void deveExecutarCrudCompletoDeAtividade()
            throws Exception {

        Long nomeacaoId =
                nomeacaoService
                        .obterPorNumeroProcesso(
                                "0000001-00.2026.8.00.0001"
                        )
                        .getId();

        String atividadeNova = """
            {
              "descricao": "Organizar documentos da perícia",
              "prazo": "2026-09-20",
              "horasEstimadas": 3.0,
              "concluida": false
            }
            """;

        String respostaInclusao =
                mockMvc.perform(post("/api/atividades")
                                .param(
                                        "nomeacaoId",
                                        nomeacaoId.toString()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(atividadeNova))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").isNumber())
                        .andExpect(jsonPath("$.descricao")
                                .value(
                                        "Organizar documentos da perícia"
                                ))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Number idGerado =
                JsonPath.read(respostaInclusao, "$.id");

        Long atividadeId = idGerado.longValue();

        String atividadeAlterada = """
            {
              "id": %d,
              "descricao": "Organizar e revisar documentos da perícia",
              "prazo": "2026-09-22",
              "horasEstimadas": 4.0,
              "concluida": false
            }
            """.formatted(atividadeId);

        mockMvc.perform(put(
                        "/api/atividades/" + atividadeId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atividadeAlterada))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(atividadeId))
                .andExpect(jsonPath("$.descricao")
                        .value(
                                "Organizar e revisar documentos da perícia"
                        ))
                .andExpect(jsonPath("$.horasEstimadas")
                        .value(4.0));

        mockMvc.perform(get(
                        "/api/atividades/" + atividadeId
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(atividadeId));

        mockMvc.perform(delete(
                        "/api/atividades/" + atividadeId
                ))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(
                        "/api/atividades/" + atividadeId
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void devePadronizarRespostasDeErro() throws Exception {
        mockMvc.perform(get("/api/peritos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Not Found"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Entidade não encontrada: 999"))
                .andExpect(jsonPath("$.caminho")
                        .value("/api/peritos/999"));

        mockMvc.perform(get("/api/atividades/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveRejeitarAtividadeComDadosInvalidos() throws Exception {
        Long nomeacaoId =
                nomeacaoService
                        .obterPorNumeroProcesso(
                                "0000001-00.2026.8.00.0001"
                        )
                        .getId();

        String atividadeInvalida = """
        {
          "prazo": "2026-09-20",
          "horasEstimadas": 3.0,
          "concluida": false
        }
        """;

        mockMvc.perform(post("/api/atividades")
                        .param(
                                "nomeacaoId",
                                nomeacaoId.toString()
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atividadeInvalida))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro")
                        .value("Bad Request"))
                .andExpect(jsonPath("$.mensagem")
                        .value(
                                "A descrição da atividade é obrigatória"
                        ))
                .andExpect(jsonPath("$.caminho")
                        .value("/api/atividades"));
    }

    @Test
    void deveAplicarValidacaoERejeitarIdNaInclusaoDePerito()
            throws Exception {

        String peritoInvalido = """
            {
              "nome": "",
              "email": "email-invalido"
            }
            """;

        mockMvc.perform(post("/api/peritos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(peritoInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        String peritoComId = """
            {
              "id": 999,
              "nome": "Perito com identificador",
              "email": "perito.id@exemplo.com"
            }
            """;

        mockMvc.perform(post("/api/peritos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(peritoComId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value(
                                "O ID não deve ser informado na inclusão"
                        ));
    }

    @Test
    void devePreservarRelacionamentosAoAlterarNomeacao()
            throws Exception {

        Long peritoId =
                nomeacaoService
                        .obterPorNumeroProcesso(
                                "0000001-00.2026.8.00.0001"
                        )
                        .getPerito()
                        .getId();

        String nomeacaoNova = """
            {
              "numeroProcesso": "PROCESSO-API-RELACIONAMENTO",
              "dataNomeacao": "2026-11-01",
              "prazoEmDias": 10,
              "status": "RECEBIDA",
              "honorarios": {
                "valorProposto": 1500.00,
                "valorFixado": 0.00,
                "valorRecebido": 0.00,
                "depositado": false
              }
            }
            """;

        String respostaNomeacao =
                mockMvc.perform(post("/api/nomeacoes")
                                .param(
                                        "peritoId",
                                        peritoId.toString()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(nomeacaoNova))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").isNumber())
                        .andExpect(jsonPath("$.dataLimite")
                                .value("2026-11-11"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Number numeroNomeacao =
                JsonPath.read(respostaNomeacao, "$.id");

        Long nomeacaoId = numeroNomeacao.longValue();

        String atividadeNova = """
            {
              "descricao": "Atividade vinculada para preservação",
              "prazo": "2026-11-08",
              "horasEstimadas": 2.0,
              "concluida": false
            }
            """;

        String respostaAtividade =
                mockMvc.perform(post("/api/atividades")
                                .param(
                                        "nomeacaoId",
                                        nomeacaoId.toString()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(atividadeNova))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Number numeroAtividade =
                JsonPath.read(respostaAtividade, "$.id");

        Long atividadeId = numeroAtividade.longValue();

        String nomeacaoAlterada = """
            {
              "id": %d,
              "numeroProcesso": "PROCESSO-API-ALTERADO",
              "dataNomeacao": "2026-11-01",
              "prazoEmDias": 15,
              "status": "ACEITA",
              "honorarios": {
                "valorProposto": 1800.00,
                "valorFixado": 1700.00,
                "valorRecebido": 0.00,
                "depositado": true
              }
            }
            """.formatted(nomeacaoId);

        mockMvc.perform(put("/api/nomeacoes/" + nomeacaoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nomeacaoAlterada))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroProcesso")
                        .value("PROCESSO-API-ALTERADO"))
                .andExpect(jsonPath("$.dataLimite")
                        .value("2026-11-16"))
                .andExpect(jsonPath("$.status")
                        .value("ACEITA"))
                .andExpect(jsonPath("$.atividades.length()")
                        .value(1))
                .andExpect(jsonPath("$.atividades[0].id")
                        .value(atividadeId));

        mockMvc.perform(get("/api/atividades/" + atividadeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(atividadeId));

        mockMvc.perform(delete("/api/nomeacoes/" + nomeacaoId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/atividades/" + atividadeId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveExecutarConsultasPersonalizadasPelaApi()
            throws Exception {

        mockMvc.perform(get(
                        "/api/nomeacoes/status/RECEBIDA"
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status")
                        .value("RECEBIDA"));

        mockMvc.perform(get(
                        "/api/nomeacoes/ordenadas-por-prazo"
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].dataLimite")
                        .value("2026-08-25"))
                .andExpect(jsonPath("$[1].dataLimite")
                        .value("2026-09-04"));

        mockMvc.perform(get("/api/nomeacoes/processo")
                        .param(
                                "numeroProcesso",
                                "0000001-00.2026.8.00.0001"
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroProcesso")
                        .value(
                                "0000001-00.2026.8.00.0001"
                        ));

        mockMvc.perform(get("/api/atividades/filtro")
                        .param("concluida", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].concluida")
                        .value(false))
                .andExpect(jsonPath("$[0].prazo")
                        .value("2026-09-01"));
    }

    @Test
    void deveDisponibilizarDocumentacaoOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title")
                        .value("André Gaspar API"))
                .andExpect(jsonPath("$.info.version")
                        .value("0.4.0"))
                .andExpect(jsonPath("$.paths['/api/peritos'].get")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/nomeacoes'].get")
                        .exists())
                .andExpect(jsonPath(
                        "$.paths['/api/nomeacoes/status/{status}'].get"
                ).exists())
                .andExpect(jsonPath(
                        "$.paths['/api/nomeacoes/ordenadas-por-prazo'].get"
                ).exists())
                .andExpect(jsonPath(
                        "$.paths['/api/nomeacoes/processo'].get"
                ).exists())
                .andExpect(jsonPath(
                        "$.paths['/api/atividades/filtro'].get"
                ).exists())
                .andExpect(jsonPath("$.paths['/api/atividades'].post")
                        .exists())
                .andExpect(jsonPath(
                        "$.paths['/api/atividades']"
                                + ".post.responses['201']"
                ).exists());
    }
}