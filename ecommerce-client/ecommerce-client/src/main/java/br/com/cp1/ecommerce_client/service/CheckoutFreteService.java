package br.com.cp1.ecommerce_client.service;

import br.com.cp1.ecommerce_client.dto.FreteResponseDto;
import br.com.cp1.ecommerce_client.soap.TransportadoraSoapClient;
import br.com.cp1.ecommerce_client.wsdl.CalcularFreteResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CheckoutFreteService {

    private final TransportadoraSoapClient transportadoraSoapClient;

    public CheckoutFreteService(TransportadoraSoapClient transportadoraSoapClient) {
        this.transportadoraSoapClient = transportadoraSoapClient;
    }

    public FreteResponseDto calcularFrete(String cepOrigem, String cepDestino, BigDecimal peso) {
        CalcularFreteResponse response = transportadoraSoapClient.calcularFrete(cepOrigem, cepDestino, peso);
        return new FreteResponseDto(
                cepOrigem,
                cepDestino,
                peso,
                response.getValorFrete(),
                response.getPrazoEntregaDias(),
                response.getTransportadora(),
                response.getObservacao()
        );
    }
}
