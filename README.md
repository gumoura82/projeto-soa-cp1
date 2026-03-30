# CP1 - Integração SOA com SOAP 

Projeto que demonstra, de forma prática, a **publicação e consumo de um serviço SOAP** em um cenário de integração B2B entre sistemas.

O caso simulado representa um **e-commerce consumindo o serviço de cálculo de frete de uma transportadora**, utilizando um contrato XML formal (XSD).

## Objetivo

Atender aos seguintes requisitos:

* Publicar um serviço SOAP (provedor)
* Desenvolver uma aplicação consumidora
* Documentar:

    * Contexto de implantação
    * Problemas resolvidos
    * Boas práticas adotadas
    * Evoluções futuras

## Visão Geral da Solução

O projeto é composto por **dois sistemas independentes**:

* `transportadora-server` → Serviço SOAP (provedor)
* `ecommerce-client` → Consumidor SOAP + API REST

### Fluxo da Integração

1. Cliente chama o endpoint REST do e-commerce
2. O e-commerce monta uma requisição SOAP
3. A transportadora processa a cotação
4. O serviço SOAP retorna a resposta
5. O e-commerce converte para JSON e responde ao cliente

## Arquitetura

Arquitetura baseada em **SOA (Service-Oriented Architecture)** com desacoplamento por contrato:

* **Provedor:** transportadora-server
* **Consumidor:** ecommerce-client
* **Contrato:** `frete.xsd`
* **Comunicação interna:** SOAP over HTTP
* **Exposição externa:** REST

> Ambos os sistemas estão no mesmo repositório por conveniência acadêmica — conceitualmente são independentes.

### 🔌 Portas

| Serviço        | Porta |
| -------------- | ----- |
| Transportadora | 8080  |
| E-commerce     | 8081  |

## Stack Tecnológica

* Java 17+ / 21
* Spring Boot 3.4.4
* Spring Web Services (SOAP)
* Spring Web (REST)
* JAXB (via `jaxb2-maven-plugin`)
* Maven Wrapper

## Contexto de Implantação

Simulação de integração entre empresas:

* A transportadora expõe um serviço corporativo de frete
* O e-commerce consome sem conhecer a implementação interna
* A comunicação é baseada em um **contrato versionável (XSD)**

Isso reflete um cenário real de integração entre sistemas heterogêneos.

## Problemas Resolvidos

* Integração entre sistemas desacoplados
* Padronização de comunicação via contrato
* Redução de dependência entre aplicações
* Validação consistente de dados
* Reutilização do serviço por múltiplos consumidores

## Boas Práticas Aplicadas

* **Contract-first** (XSD definido antes da implementação)
* Separação clara de responsabilidades
* Baixo acoplamento entre sistemas
* Geração automática de classes via JAXB
* Tratamento de erros com:

    * SOAP Fault (provedor)
    * HTTP status (consumidor)
* Testes unitários de regras e integração

## 📋 Regra de Negócio

### Entradas

* `cepOrigem`
* `cepDestino`
* `peso`

### Regras

* CEP destino iniciando com `0`:

    * Frete base: **R$ 15,00**
    * Prazo: **2 dias**
* Demais casos:

    * Frete base: **R$ 25,00**
    * Prazo: **5 dias**
* Adicional:

    * **+ R$ 2,00/kg** acima de 1kg

### Validações

* CEPs devem ter **8 dígitos numéricos**
* Peso não pode ser negativo
* Erros geram **falha funcional (SOAP Fault)**

## Contrato SOAP

Localização:

```
transportadora-server/src/main/resources/wsdl/frete.xsd
```

Define:

* `calcularFreteRequest`
* `calcularFreteResponse`
* `freteFault`

Usado para:

* Geração do WSDL
* Geração de classes JAXB (server + client)

## Endpoints

### SOAP (Transportadora)

* WSDL: `http://localhost:8080/ws/frete.wsdl`
* Endpoint: `http://localhost:8080/ws`

### REST (E-commerce)

```
GET /api/checkout/frete
```

Exemplo:

```
http://localhost:8081/api/checkout/frete?cepOrigem=11000000&cepDestino=01001000&peso=3
```

---

## Exemplo de Resposta

```json
{
  "cepOrigem": "11000000",
  "cepDestino": "01001000",
  "peso": 3,
  "valorFrete": 19.00,
  "prazoEntregaDias": 2,
  "transportadora": "Transportadora SOA CP1",
  "observacao": "Cotacao com faixa base de Sao Paulo."
}
```

## Tratamento de Erros

Casos tratados:

* CEP inválido
* Peso negativo
* Serviço indisponível

Fluxo:

* SOAP retorna **Fault**
* REST traduz para **HTTP apropriado**

## Como Executar

### Pré-requisitos

* Java 17+
* `JAVA_HOME` configurado

### Validar ambiente

```bash
java -version
mvn -v
```

### 1. Subir a transportadora (obrigatório primeiro)

```bash
cd transportadora-server
./mvnw spring-boot:run
```

### 2. Subir o e-commerce

```bash
cd ecommerce-client
./mvnw spring-boot:run
```

> A transportadora deve estar no ar antes de iniciar o e-commerce.

## Como Testar

1. Acessar WSDL:

```
http://localhost:8080/ws/frete.wsdl
```

2. Chamada válida:

```
http://localhost:8081/api/checkout/frete?cepOrigem=11000000&cepDestino=01001000&peso=3
```

3. Chamada inválida:

```
http://localhost:8081/api/checkout/frete?peso=-1
```

## Próximas Evoluções

* **WS-Security** — autenticação entre sistemas B2B em produção exige controle de acesso no nível do serviço
* **Cache de cotações** — evitar chamadas SOAP redundantes para os mesmos parâmetros
* **Observabilidade** — logs correlacionados para rastrear requisições ponta a ponta
* **Testes E2E** — cobertura completa do fluxo REST → SOAP → REST
* **Configuração por ambiente** — profiles para dev, staging e produção
