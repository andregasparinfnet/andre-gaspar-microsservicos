package br.edu.infnet.andre_gaspar_api.importacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.infnet.andre_gaspar_api.atividade.AtividadePericialRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ImportacaoAtividadesIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AtividadePericialRepository atividades;

    @Test
    void importaCsvSemDuplicarNaSegundaExecucao() throws Exception {
        long antes = atividades.count();

        executarImportacao();
        assertEquals(antes + 6, atividades.count());
        assertTrue(atividades.existsByCodigoImportacao("ET4-A01"));
        assertTrue(atividades.existsByCodigoImportacao("ET4-A06"));

        executarImportacao();
        assertEquals(antes + 6, atividades.count());
    }

    private void executarImportacao() throws Exception {
        mockMvc.perform(post("/api/importacoes/atividades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.lidos").value(6))
                .andExpect(jsonPath("$.processados").value(6));
    }
}
