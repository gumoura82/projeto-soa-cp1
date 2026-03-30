package br.com.cp1.transportadora_server.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import javax.xml.namespace.QName;
import java.util.Properties;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "frete")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema freteSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setPortTypeName("FretePort");
        definition.setLocationUri("/ws");
        definition.setTargetNamespace("http://www.cp1.com.br/frete/ws");
        definition.setSchema(freteSchema);
        return definition;
    }

    @Bean
    public XsdSchema freteSchema() {
        return new SimpleXsdSchema(new ClassPathResource("wsdl/frete.xsd"));
    }

    @Bean
    public SoapFaultMappingExceptionResolver exceptionResolver() {
        SoapFaultMappingExceptionResolver resolver = new SoapFaultMappingExceptionResolver() {
            @Override
            protected void customizeFault(Object endpoint, Exception ex, org.springframework.ws.soap.SoapFault fault) {
                if (ex instanceof br.com.cp1.transportadora_server.exception.FreteInvalidoException freteInvalidoException) {
                    QName detailName = new QName("http://www.cp1.com.br/frete/ws", "freteFault");
                    org.springframework.ws.soap.SoapFaultDetail detail = fault.addFaultDetail();
                    detail.addFaultDetailElement(new QName(detailName.getNamespaceURI(), "codigo"))
                            .addText(freteInvalidoException.getCodigo());
                    detail.addFaultDetailElement(new QName(detailName.getNamespaceURI(), "mensagem"))
                            .addText(freteInvalidoException.getMessage());
                }
            }
        };

        SoapFaultDefinition definition = new SoapFaultDefinition();
        definition.setFaultCode(SoapFaultDefinition.CLIENT);
        resolver.setDefaultFault(definition);

        Properties mappings = new Properties();
        mappings.setProperty("br.com.cp1.transportadora_server.exception.FreteInvalidoException", SoapFaultDefinition.CLIENT.toString());
        resolver.setExceptionMappings(mappings);
        resolver.setOrder(1);
        return resolver;
    }
}
