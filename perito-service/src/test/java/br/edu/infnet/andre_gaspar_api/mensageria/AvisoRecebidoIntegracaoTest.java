package br.edu.infnet.andre_gaspar_api.mensageria;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.infnet.andre_gaspar_api.PeritoServiceApplication;
import br.edu.infnet.andre_gaspar_api.perito.Perito;
import br.edu.infnet.andre_gaspar_api.perito.PeritoService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = PeritoServiceApplication.class)
@AutoConfigureMockMvc
class AvisoRecebidoIntegracaoTest {

    @Autowired
    private ConsumidorAvisos consumidor;

    @Autowired
    private PeritoService peritos;

    @Autowired
    private AvisoRecebidoRepository avisos;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registraUmaVezEPermiteConsultarPorPerito() throws Exception {
        Perito perito = peritos.incluir(new Perito(
                "Perito para Avisos",
                "avisos-" + UUID.randomUUID() + "@exemplo.com"
        ));
        Long peritoId = perito.getId();
        UUID avisoId = UUID.randomUUID();
        AvisoNomeacaoMessage mensagem = new AvisoNomeacaoMessage(
                avisoId, 1L, peritoId, "PROCESSO-TESTE-AVISO"
        );

        consumidor.receber(mensagem);
        consumidor.receber(mensagem);

        long encontrados = avisos.findByPeritoIdOrderByIdDesc(peritoId)
                .stream()
                .filter(aviso -> avisoId.toString()
                        .equals(aviso.getAvisoId()))
                .count();
        assertEquals(1L, encontrados);

        mockMvc.perform(get("/api/peritos/{id}/avisos", peritoId))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(avisoId.toString())
                ));

        mockMvc.perform(get("/api/peritos/999999/avisos"))
                .andExpect(status().isNotFound());
    }
}
