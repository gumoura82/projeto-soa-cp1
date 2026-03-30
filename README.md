# 📦 CP1 - Integração SOA com SOAP

Projeto acadêmico que demonstra, de forma prática, a **publicação e consumo de um serviço SOAP** em um cenário de integração B2B entre sistemas.

O caso simulado representa um **e-commerce consumindo o serviço de cálculo de frete de uma transportadora**, utilizando um contrato XML formal (XSD).

---

## 📌 O Caso de Uso: E-commerce + Transportadora

```
┌──────────────────┐                    ┌────────────────────┐
│   Cliente da     │                    │   E-commerce       │
│    Loja Online   │  (Checkout)        │   (REST API)       │
│                  ├───────────────────→│                    │
│  Qual é o frete? │                    │ GET /api/checkout/ │
│                  │←───────────────────┤      frete         │
│  R$ 19,00 em     │ (Frete com prazo)  │                    │
│  2 dias          │                    └────────┬───────────┘
└──────────────────┘                             │ (SOAP)
                                                 │
                                        ┌────────▼──────────┐
                                        │ Transportadora     │
                                        │ (SOAP WebService)  │
                                        │                    │
                                        │ calcularFrete()    │
                                        │ (Regras de negócio)│
                                        └────────────────────┘
```

**Resumo:**
- Cliente acessa o e-commerce querendo saber o frete
- E-commerce **chama em tempo real** um serviço SOAP da transportadora
- Transportadora aplica regras de negócio e retorna frete + prazo
- E-commerce exibe o resultado em JSON para o cliente

---

## 🎯 Objetivo

Atender aos seguintes requisitos:

* Publicar um serviço SOAP (provedor)
* Desenvolver uma aplicação consumidora
* Documentar:

    * Contexto de implantação
    * Problemas resolvidos
    * Boas práticas adotadas
    * Evoluções futuras

---

## 🧠 Visão Geral da Solução

O projeto é composto por **dois sistemas independentes**:

* `transportadora-server` → Serviço SOAP (provedor)
* `ecommerce-client` → Consumidor SOAP + API REST

### � Cenário de Negócio

Um e-commerce precisa oferecer aos seus clientes o cálculo do frete na **hora do checkout**. Para isso, ele não mantém sua própria tabela de fretes, mas sim consome um serviço corporativo de uma transportadora parceira. 

Esse tipo de integração é comum em cenários B2B, onde:
- A transportadora expõe um serviço padronizado (SOAP/WebService)
- Múltiplos e-commerces e marketplaces podem consumir esse serviço
- A comunicação é baseada em um **contrato versionável (XSD)** que ambas as partes concordam
- O consumidor não precisa conhecer os detalhes internos da transportadora

### 🔄 Fluxo da Integração

1. **Cliente acessa o e-commerce** e inicia o checkout com um produto
2. **E-commerce consultado para simular frete** → chama a API REST GET `/api/checkout/frete` com CEP origem, CEP destino e peso
3. **E-commerce monta requisição SOAP** com os dados recebidos
4. **Transportadora processa a cotação** aplicando regras de negócio (localização, peso, etc)
5. **Transportadora retorna resposta SOAP** com valor do frete e prazo de entrega
6. **E-commerce converte resposta SOAP → JSON** e devolve ao cliente da loja
7. **Cliente visualiza frete estimado** e pode finalizar a compra

---

## 🏗️ Arquitetura

Arquitetura baseada em **SOA (Service-Oriented Architecture)** com desacoplamento por contrato:

* **Provedor:** transportadora-server ← Expõe o serviço de frete (SOAP)
* **Consumidor:** ecommerce-client ← Consome o serviço para responder a clientes (REST)
* **Contrato:** `frete.xsd` ← Define a estrutura de requisições e respostas
* **Comunicação:** SOAP over HTTP ← Padrão corporativo W3C

> Ambos os sistemas estão no mesmo repositório por conveniência acadêmica — mas **conceitualmente são independentes** e poderiam rodar em servidores, empresas e datacenters diferentes em um cenário real.

### 🔌 Portas

| Serviço        | Porta |
| -------------- | ----- |
| Transportadora | 8080  |
| E-commerce     | 8081  |

---

## 🛠️ Stack Tecnológica

* Java 17+ / 21
* Spring Boot 3.4.4
* Spring Web Services (SOAP)
* Spring Web (REST)
* JAXB (via `jaxb2-maven-plugin`)
* Maven Wrapper

---

## 🌍 Contexto de Implantação

**Simulação de integração corporativa entre empresas em um checkout real:**

O e-commerce **não calcula o frete localmente**. Todos os cálculos são delegados ao serviço SOAP da transportadora. Isso permite:

* ✅ A transportadora manter regras de negócio centralizadas e versionadas
* ✅ Múltiplos e-commerces consumindo o mesmo serviço sem duplicar código
* ✅ Atualizações na transportadora refletem imediatamente em todos os consumidores
* ✅ Redução de acoplamento entre sistemas — se a implementação interna da transportadora mudar, o contrato (XSD) permanece compatível

**Tecnicamente:** A comunicação ocorre via **SOAP (Simple Object Access Protocol)** — um padrão corporativo amplamente utilizado em integrações B2B. O **contrato XSD (frete.xsd)** é versionável e desacopla provider do consumer. Esse padrão reflete cenários reais em indústrias como varejo, logística, seguros e financeiro.

---

## 🚧 Problemas Resolvidos

* Integração entre sistemas desacoplados
* Padronização de comunicação via contrato
* Redução de dependência entre aplicações
* Validação consistente de dados
* Reutilização do serviço por múltiplos consumidores

---

## ✅ Boas Práticas Aplicadas

* **Contract-first** (XSD definido antes da implementação)
* Separação clara de responsabilidades
* Baixo acoplamento entre sistemas
* Geração automática de classes via JAXB
* Tratamento de erros com:

    * SOAP Fault (provedor)
    * HTTP status (consumidor)
* Testes unitários de regras e integração

---

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

---

## 📄 Contrato SOAP

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

---

## 🔗 Endpoints

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

## 📦 Exemplo de Resposta

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

---

## ❌ Tratamento de Erros

Casos tratados:

* CEP inválido
* Peso negativo
* Serviço indisponível

Fluxo:

* SOAP retorna **Fault**
* REST traduz para **HTTP apropriado**

---

## ▶️ Como Executar

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

---

## 🧪 Como Testar

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

---

## 🚀 Próximas Evoluções

* **WS-Security** — autenticação entre sistemas B2B em produção exige controle de acesso no nível do serviço
* **Cache de cotações** — evitar chamadas SOAP redundantes para os mesmos parâmetros
* **Observabilidade** — logs correlacionados para rastrear requisições ponta a ponta
* **Testes E2E** — cobertura completa do fluxo REST → SOAP → REST
* **Configuração por ambiente** — profiles para dev, staging e produção