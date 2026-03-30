package br.com.cp1.ecommerce_client.service;

import br.com.cp1.ecommerce_client.dto.FreteResponseDto;
import br.com.cp1.ecommerce_client.soap.TransportadoraSoapClient;
import br.com.cp1.ecommerce_client.wsdl.CalcularFreteResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckoutFreteServiceTest {

    @Test
    void deveRetornarDtoComDadosDaTransportadora() {
        TransportadoraSoapClient soapClient = Mockito.mock(TransportadoraSoapClient.class);
        CheckoutFreteService service = new CheckoutFreteService(soapClient);

        CalcularFreteResponse soapResponse = new CalcularFreteResponse();
        soapResponse.setValorFrete(BigDecimal.valueOf(17).setScale(2));
        soapResponse.setPrazoEntregaDias(2);
        soapResponse.setTransportadora("Transportadora SOA CP1");
        soapResponse.setObservacao("Cotacao mockada");

        Mockito.when(soapClient.calcularFrete("11000000", "01001000", BigDecimal.valueOf(2)))
                .thenReturn(soapResponse);

        FreteResponseDto response = service.calcularFrete("11000000", "01001000", BigDecimal.valueOf(2));

        assertEquals(BigDecimal.valueOf(17).setScale(2), response.valorFrete());
        assertEquals(2, response.prazoEntregaDias());
        assertEquals("Transportadora SOA CP1", response.transportadora());
    }
}
