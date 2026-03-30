package br.com.cp1.transportadora_server.service;

import br.com.cp1.transportadora_server.exception.FreteInvalidoException;
import br.com.cp1.transportadora_server.wsdl.CalcularFreteRequest;
import br.com.cp1.transportadora_server.wsdl.CalcularFreteResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FreteServiceTest {

    private final FreteService freteService = new FreteService();

    @Test
    void deveCalcularFaixaDeSaoPaulo() {
        CalcularFreteRequest request = new CalcularFreteRequest();
        request.setCepOrigem("11000000");
        request.setCepDestino("01001000");
        request.setPeso(BigDecimal.valueOf(3));

        CalcularFreteResponse response = freteService.calcular(request);

        assertEquals(BigDecimal.valueOf(19).setScale(2), response.getValorFrete());
        assertEquals(2, response.getPrazoEntregaDias());
    }

    @Test
    void deveRejeitarPesoNegativo() {
        CalcularFreteRequest request = new CalcularFreteRequest();
        request.setCepOrigem("11000000");
        request.setCepDestino("01001000");
        request.setPeso(BigDecimal.valueOf(-1));

        FreteInvalidoException exception = assertThrows(FreteInvalidoException.class, () -> freteService.calcular(request));

        assertEquals("PESO_INVALIDO", exception.getCodigo());
    }
}
