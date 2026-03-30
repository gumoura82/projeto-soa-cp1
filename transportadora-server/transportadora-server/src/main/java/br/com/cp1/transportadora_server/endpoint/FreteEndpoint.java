package br.com.cp1.transportadora_server.endpoint;

import br.com.cp1.transportadora_server.service.FreteService;
import br.com.cp1.transportadora_server.wsdl.CalcularFreteRequest;
import br.com.cp1.transportadora_server.wsdl.CalcularFreteResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class FreteEndpoint {

    private static final String NAMESPACE_URI = "http://www.cp1.com.br/frete/ws";

    private final FreteService freteService;

    public FreteEndpoint(FreteService freteService) {
        this.freteService = freteService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "calcularFreteRequest")
    @ResponsePayload
    public CalcularFreteResponse calcularFrete(@RequestPayload CalcularFreteRequest request) {
        return freteService.calcular(request);
    }
}
