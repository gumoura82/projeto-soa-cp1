package br.com.cp1.ecommerce_client.config;

import br.com.cp1.ecommerce_client.soap.TransportadoraSoapClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class SoapClientConfig {

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("br.com.cp1.ecommerce_client.wsdl");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate(
            Jaxb2Marshaller marshaller,
            @Value("${transportadora.ws.default-uri}") String defaultUri) {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setUnmarshaller(marshaller);
        webServiceTemplate.setDefaultUri(defaultUri);
        return webServiceTemplate;
    }

    @Bean
    public TransportadoraSoapClient transportadoraSoapClient(
            WebServiceTemplate webServiceTemplate,
            @Value("${transportadora.ws.uri}") String uri) {
        return new TransportadoraSoapClient(webServiceTemplate, uri);
    }
}
