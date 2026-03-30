package br.com.cp1.ecommerce_client.dto;

import java.math.BigDecimal;

public record FreteResponseDto(
        String cepOrigem,
        String cepDestino,
        BigDecimal peso,
        BigDecimal valorFrete,
        int prazoEntregaDias,
        String transportadora,
        String observacao
) {
}
