package br.com.cp1.ecommerce_client.soap;

import br.com.cp1.ecommerce_client.exception.TransportadoraSoapException;
import br.com.cp1.ecommerce_client.wsdl.CalcularFreteRequest;
import br.com.cp1.ecommerce_client.wsdl.CalcularFreteResponse;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import java.math.BigDecimal;

public class TransportadoraSoapClient {

    private final WebServiceTemplate webServiceTemplate;
    private final String uri;

    public TransportadoraSoapClient(WebServiceTemplate webServiceTemplate, String uri) {
        this.webServiceTemplate = webServiceTemplate;
        this.uri = uri;
    }

    public CalcularFreteResponse calcularFrete(String cepOrigem, String cepDestino, BigDecimal peso) {
        CalcularFreteRequest request = new CalcularFreteRequest();
        request.setCepOrigem(cepOrigem);
        request.setCepDestino(cepDestino);
        request.setPeso(peso);

        try {
            return (CalcularFreteResponse) webServiceTemplate.marshalSendAndReceive(uri, request);
        } catch (SoapFaultClientException exception) {
            throw new TransportadoraSoapException("A transportadora rejeitou a cotacao: " + exception.getFaultStringOrReason(), exception);
        } catch (WebServiceIOException exception) {
            throw new TransportadoraSoapException("Nao foi possivel conectar ao endpoint SOAP da transportadora.", exception);
        }
    }
}
