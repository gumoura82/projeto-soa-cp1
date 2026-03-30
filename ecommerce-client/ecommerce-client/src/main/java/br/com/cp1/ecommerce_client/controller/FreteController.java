package br.com.cp1.ecommerce_client.controller;

import br.com.cp1.ecommerce_client.dto.FreteResponseDto;
import br.com.cp1.ecommerce_client.service.CheckoutFreteService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Validated
@RestController
@RequestMapping("/api/checkout")
public class FreteController {

    private final CheckoutFreteService checkoutFreteService;

    public FreteController(CheckoutFreteService checkoutFreteService) {
        this.checkoutFreteService = checkoutFreteService;
    }

    @GetMapping("/frete")
    public FreteResponseDto calcularFrete(
            @RequestParam @Pattern(regexp = "\\d{8}") String cepOrigem,
            @RequestParam @Pattern(regexp = "\\d{8}") String cepDestino,
            @RequestParam @DecimalMin("0.0") BigDecimal peso) {
        return checkoutFreteService.calcularFrete(cepOrigem, cepDestino, peso);
    }
}
