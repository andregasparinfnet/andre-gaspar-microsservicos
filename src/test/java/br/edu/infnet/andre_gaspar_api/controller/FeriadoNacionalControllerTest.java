package br.edu.infnet.andre_gaspar_api.controller;

import br.edu.infnet.andre_gaspar_api.client.BrasilApiClient;
import br.edu.infnet.andre_gaspar_api.dto.FeriadoNacional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FeriadoNacionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrasilApiClient brasilApiClient;

    @Test
    void deveConsultarFeriadosNacionaisPorAno() throws Exception {
        when(brasilApiClient.listarFeriados(2026))
                .thenReturn(List.of(
                        new FeriadoNacional(
                                "2026-01-01",
                                "Confraternização mundial",
                                "national"
                        ),
                        new FeriadoNacional(
                                "2026-04-21",
                                "Tiradentes",
                                "national"
                        )
                ));

        mockMvc.perform(get("/api/feriados/2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date")
                        .value("2026-01-01"))
                .andExpect(jsonPath("$[0].name")
                        .value("Confraternização mundial"))
                .andExpect(jsonPath("$[0].type")
                        .value("national"))
                .andExpect(jsonPath("$[1].date")
                        .value("2026-04-21"))
                .andExpect(jsonPath("$[1].name")
                        .value("Tiradentes"));

        verify(brasilApiClient).listarFeriados(2026);
    }

    @Test
    void deveRejeitarAnoInvalidoSemConsultarBrasilApi()
            throws Exception {

        mockMvc.perform(get("/api/feriados/1800"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(brasilApiClient);
    }

    @Test
    void deveDocumentarEndpointDeFeriadosNoOpenApi()
            throws Exception {

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.paths['/api/feriados/{ano}'].get"
                ).exists())
                .andExpect(jsonPath(
                        "$.paths['/api/feriados/{ano}']"
                                + ".get.summary"
                ).value("Consultar feriados nacionais"));
    }

}
