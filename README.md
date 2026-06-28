# API de Tabela Tarifária de Água

API REST para gerenciamento e cálculo de tarifas de água com cobrança progressiva por faixas de consumo.

## Tecnologias

- Java 17
- Spring Boot 3.4.3
- PostgreSQL 17
- Maven
- Swagger (Springdoc OpenAPI 2.8.5)
- JUnit 5 + H2 (testes)

## Pré-requisitos

- Java 17 ou superior
- Maven 3.9+ (ou use o wrapper `./mvnw` incluso)
- PostgreSQL 17

## Instalação e configuração

### 1. Clone o repositório
```bash
git clone https://github.com/RuanSpinelli/api-tabela-tarifa-agua.git
cd api-tabela-tarifa-agua
```

### 2. Configure o banco de dados

Crie o banco no PostgreSQL:
```bash
CREATE DATABASE tarifa_agua_db;
```

3. Configure as credenciais

Edite src/main/resources/application.properties:
```bash
spring.datasource.username=[usuario do banco]
spring.datasource.password=[senha do usuario do banco]
```

4. Execute a aplicação
```bash
./mvnw spring-boot:run
```
A API estará disponível em: http://localhost:8080

Como testar
Swagger (recomendado)

Acesse: http://localhost:8080/swagger-ui/index.html

Health check
```bash
curl http://localhost:8080/
```
Retorna: "API funcionando!"


Executar testes unitários
```bash
./mvnw test
```
## Endpoints
| Método | URL | Descrição |
|--------|-----|-----------|
| GET | `/` | Health check |
| POST | `/api/tabelas-tarifarias` | Criar nova tabela tarifária |
| GET | `/api/tabelas-tarifarias` | Listar todas as tabelas |
| PUT | `/api/tabelas-tarifarias/{id}` | Atualizar uma tabela |
| DELETE | `/api/tabelas-tarifarias/{id}` | Excluir uma tabela |
| POST | `/api/calculos` | Calcular valor a pagar |
| GET | `/api/categorias` | Listar categorias disponíveis |

## Categorias disponíveis
| Nome | Descrição |
|------|-----------|
| COMERCIAL | Estabelecimentos comerciais |
| INDUSTRIAL | Indústrias e fábricas |
| PARTICULAR | Residências |
| PUBLICO | Órgãos públicos |


## Exemplos de requests

### Criar tabela tarifária

**POST** `/api/tabelas-tarifarias`

```json
{
  "nome": "Tarifa 2025",
  "vigenciaInicio": "2025-01-01",
  "categorias": [
    {
      "categoriaNome": "COMERCIAL",
      "faixas": [
        {"limiteInferior": 0, "limiteSuperior": 10, "valorUnitario": 5.00},
        {"limiteInferior": 11, "limiteSuperior": 20, "valorUnitario": 8.00},
        {"limiteInferior": 21, "limiteSuperior": 30, "valorUnitario": 12.00},
        {"limiteInferior": 31, "limiteSuperior": 99999, "valorUnitario": 15.00}
      ]
    },
    {
      "categoriaNome": "INDUSTRIAL",
      "faixas": [
        {"limiteInferior": 0, "limiteSuperior": 10, "valorUnitario": 1.00},
        {"limiteInferior": 11, "limiteSuperior": 20, "valorUnitario": 2.00},
        {"limiteInferior": 21, "limiteSuperior": 30, "valorUnitario": 3.00},
        {"limiteInferior": 31, "limiteSuperior": 99999, "valorUnitario": 4.00}
      ]
    }
  ]
}
```

### Calcular consumo (exemplo do enunciado)

**POST** `/api/calculos`

```json
{
  "tabelaId": 1,
  "categoria": "INDUSTRIAL",
  "consumo": 18
}
```

**Resposta:**
```json
{
  "categoria": "INDUSTRIAL",
  "consumoTotal": 18,
  "valorTotal": 26.00,
  "detalhamento": [
    {
      "faixa": { "inicio": 0, "fim": 10 },
      "m3Cobrados": 10,
      "valorUnitario": 1.00,
      "subtotal": 10.00
    },
    {
      "faixa": { "inicio": 11, "fim": 20 },
      "m3Cobrados": 8,
      "valorUnitario": 2.00,
      "subtotal": 16.00
    }
  ]
}
```

## Regras de validação

| Regra | Descrição |
|-------|-----------|
| Não sobreposição | Faixas não podem ter intervalos que se cruzam |
| Ordem válida | Limite inferior deve ser menor que o superior |
| Cobertura completa | Primeira faixa deve iniciar em 0 m³ |
| Cobertura suficiente | Última faixa deve cobrir até 99999 m³ |

## Parametrização

As tarifas são totalmente parametrizadas no banco de dados. Para alterar valores sem modificar código:

**Via API:** `PUT /api/tabelas-tarifarias/{id}` com os novos valores.

Os novos valores refletem nos cálculos imediatamente, sem alterar código.

## Estrutura do projeto

```
src/
├── main/
│   ├── java/com/desafio/tarifa/agua/
│   │   ├── config/          # OpenAPI/Swagger
│   │   ├── controller/      # Endpoints REST
│   │   ├── dto/             # Objetos de transferência
│   │   ├── exception/       # Tratamento de erros
│   │   ├── model/           # Entidades JPA
│   │   ├── repository/      # Repositórios
│   │   └── service/         # Regras de negócio
│   └── resources/
│       ├── application.properties
│       └── data.sql         # Categorias iniciais
└── test/
    └── java/.../service/    # Testes unitários
```
