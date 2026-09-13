package br.edu.infnet.andre_gaspar_api.perito;

import br.edu.infnet.andre_gaspar_api.PeritoServiceApplication;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PeritoServiceApplication.class)
@AutoConfigureMockMvc
class PeritoControllerIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PeritoRepository peritoRepository;

    @BeforeEach
    void prepararBanco() {
        peritoRepository.deleteAll();
    }

    @Test
    void deveExecutarFluxoCrudDePerito() throws Exception {
        String resposta = mockMvc.perform(post("/api/peritos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Perito de Teste",
                                  "email": "perito.teste@exemplo.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome")
                        .value("Perito de Teste"))
                .andExpect(jsonPath("$.email")
                        .value("perito.teste@exemplo.com"))
                .andExpect(jsonPath("$.nomeacoes")
                        .doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number id = JsonPath.read(resposta, "$.id");

        mockMvc.perform(get("/api/peritos/{id}", id.longValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome")
                        .value("Perito de Teste"));

        mockMvc.perform(put("/api/peritos/{id}", id.longValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": %d,
                                  "nome": "Perito Atualizado",
                                  "email": "atualizado@exemplo.com"
                                }
                                """.formatted(id.longValue())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome")
                        .value("Perito Atualizado"))
                .andExpect(jsonPath("$.email")
                        .value("atualizado@exemplo.com"));

        mockMvc.perform(delete("/api/peritos/{id}", id.longValue()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/peritos/{id}", id.longValue()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarPeritoComDadosInvalidos() throws Exception {
        mockMvc.perform(post("/api/peritos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "",
                                  "email": "email-invalido"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveDisponibilizarDocumentacaoOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title")
                        .value("Serviço de Peritos"))
                .andExpect(jsonPath(
                        "$.paths['/api/peritos'].get"
                ).exists())
                .andExpect(jsonPath(
                        "$.paths['/api/peritos/{id}'].get"
                ).exists());
    }
}
