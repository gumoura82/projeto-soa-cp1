package br.com.cp1.transportadora_server.service;

import br.com.cp1.transportadora_server.exception.FreteInvalidoException;
import br.com.cp1.transportadora_server.wsdl.CalcularFreteRequest;
import br.com.cp1.transportadora_server.wsdl.CalcularFreteResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FreteService {

    private static final BigDecimal BASE_SAO_PAULO = BigDecimal.valueOf(15);
    private static final BigDecimal BASE_OUTROS = BigDecimal.valueOf(25);
    private static final BigDecimal ADICIONAL_POR_QUILO = BigDecimal.valueOf(2);

    public CalcularFreteResponse calcular(CalcularFreteRequest request) {
        validar(request);

        boolean destinoSaoPaulo = request.getCepDestino().startsWith("0");
        BigDecimal base = destinoSaoPaulo ? BASE_SAO_PAULO : BASE_OUTROS;
        BigDecimal excedente = request.getPeso().subtract(BigDecimal.ONE).max(BigDecimal.ZERO);
        BigDecimal valorFinal = base.add(excedente.multiply(ADICIONAL_POR_QUILO)).setScale(2, RoundingMode.HALF_UP);

        CalcularFreteResponse response = new CalcularFreteResponse();
        response.setValorFrete(valorFinal);
        response.setPrazoEntregaDias(destinoSaoPaulo ? 2 : 5);
        response.setTransportadora("Transportadora SOA CP1");
        response.setObservacao(destinoSaoPaulo
                ? "Cotacao com faixa base de Sao Paulo."
                : "Cotacao com faixa padrao nacional.");
        return response;
    }

    private void validar(CalcularFreteRequest request) {
        if (!cepValido(request.getCepOrigem()) || !cepValido(request.getCepDestino())) {
            throw new FreteInvalidoException("CEP_INVALIDO", "Os CEPs devem conter exatamente 8 digitos numericos.");
        }
        if (request.getPeso() == null || request.getPeso().compareTo(BigDecimal.ZERO) < 0) {
            throw new FreteInvalidoException("PESO_INVALIDO", "O peso informado deve ser maior ou igual a zero.");
        }
    }

    private boolean cepValido(String cep) {
        return cep != null && cep.matches("\\d{8}");
    }
}
