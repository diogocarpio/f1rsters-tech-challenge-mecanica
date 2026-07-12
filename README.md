# Tech Challenge FIAP - Sistema de Gestao de Oficina Mecanica

API REST para gestao de uma oficina mecanica de medio porte, especializada em manutencao de veiculos. O sistema permite o gerenciamento completo de **clientes**, **veiculos**, **servicos**, **pecas** e **ordens de servico**, com autenticacao JWT, controle de acesso por perfis (roles) e mascaramento de dados sensiveis.

---

## Indice

- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura do Projeto](#arquitetura-do-projeto)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [Pre-requisitos](#pre-requisitos)
- [Configuracao e Execucao](#configuracao-e-execucao)
    - [Opcao 1 - Docker Compose (Recomendado)](#opcao-1---docker-compose-recomendado)
    - [Opcao 2 - Execucao Local (sem Docker)](#opcao-2---execucao-local-sem-docker)
    - [Opcao 3 - Kubernetes](#opcao-3---kubernetes)
    - [Opcao 4 - Terraform (Infraestrutura como Codigo)](#opcao-4---terraform-infraestrutura-como-codigo)
- [Variaveis de Ambiente](#variaveis-de-ambiente)
- [Autenticacao e Seguranca](#autenticacao-e-seguranca)
    - [Perfis de Acesso (Roles)](#perfis-de-acesso-roles)
    - [Fluxo de Autenticacao JWT](#fluxo-de-autenticacao-jwt)
- [Endpoints da API](#endpoints-da-api)
    - [Autenticacao](#autenticacao)
    - [Clientes](#clientes)
    - [Veiculos](#veiculos)
    - [Servicos](#servicos)
    - [Pecas](#pecas)
    - [Ordens de Servico](#ordens-de-servico)
    - [Consulta Publica de Ordem de Servico](#consulta-publica-de-ordem-de-servico)
- [Exemplos de Requisicoes (cURL)](#exemplos-de-requisicoes-curl)
- [Swagger / OpenAPI](#swagger--openapi)
- [Postman Collection](#postman-collection)
- [Modelo de Dominio](#modelo-de-dominio)
- [Validacoes Customizadas](#validacoes-customizadas)
- [Mascaramento de Dados Sensiveis](#mascaramento-de-dados-sensiveis)
- [Testes](#testes)
- [Cobertura de Codigo (JaCoCo)](#cobertura-de-codigo-jacoco)
- [Tratamento de Erros](#tratamento-de-erros)

---

## Tecnologias Utilizadas

| Tecnologia | Versao | Descricao |
|---|---|---|
| **Java** | 17 | Linguagem principal |
| **Spring Boot** | 4.0.5 | Framework para construcao da API REST |
| **Spring Security** | - | Autenticacao e autorizacao |
| **Spring Data JPA** | - | Persistencia de dados com Hibernate |
| **Spring Boot Actuator** | - | Health checks e monitoramento |
| **PostgreSQL** | 15 | Banco de dados em producao |
| **H2 Database** | - | Banco de dados em memoria para testes |
| **JWT (jjwt)** | 0.12.7 | Tokens de autenticacao |
| **Lombok** | - | Reducao de boilerplate no codigo |
| **SpringDoc OpenAPI** | 3.0.2 | Documentacao Swagger/OpenAPI |
| **JaCoCo** | 0.8.12 | Cobertura de testes |
| **Maven** | - | Gerenciamento de dependencias e build |
| **Docker / Docker Compose** | - | Containerizacao da aplicacao |
| **Kubernetes** | - | Orquestracao de containers |
| **Terraform** | >= 1.0 | Infraestrutura como codigo |
| **Bean Validation** | - | Validacoes customizadas (CPF/CNPJ, Placa) |

---

## Arquitetura do Projeto

O projeto segue a arquitetura em camadas (Layered Architecture):

```
Controller (REST)  -->  Service (Regras de Negocio)  -->  Repository (JPA)  -->  Database
     |                       |
     v                       v
    DTO                   Domain (Entidades)
```

- **Controller**: Recebe as requisicoes HTTP e delega para os services.
- **Service**: Contem a logica de negocio (criacao de OS, controle de estoque, validacoes).
- **Repository**: Interface JPA para acesso ao banco de dados.
- **Domain**: Entidades JPA mapeadas para tabelas do banco.
- **DTO**: Objetos de transferencia para entrada e saida de dados.
- **Security**: Filtro JWT, configuracao de seguranca, servico de autenticacao.
- **Validation**: Validadores customizados para CPF/CNPJ e Placa.
- **Mapper**: Conversao de entidades para DTOs de resposta (com mascaramento de dados).
- **Exception**: Tratamento global de excecoes da API.
- **Util**: Classes utilitarias para normalizacao de input, validacao e mascaramento.

---

## Estrutura de Pastas

```
f1rsters-tech-challenge-mecanica/
├── src/
│   ├── main/
│   │   ├── java/com/f1rsters/tech_challenge_mecanica/
│   │   │   ├── TechChallengeMecanicaApplication.java  # Classe principal
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java                # Configuracao Spring Security
│   │   │   │   ├── SecuritySeedConfig.java            # Seed do usuario admin
│   │   │   │   └── OpenApiConfig.java                 # Configuracao Swagger/OpenAPI
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java                # Login / Autenticacao
│   │   │   │   ├── ClienteController.java             # CRUD de Clientes
│   │   │   │   ├── VeiculoController.java             # CRUD de Veiculos
│   │   │   │   ├── ServicoController.java             # CRUD de Servicos
│   │   │   │   ├── PecaController.java                # CRUD de Pecas + Estoque
│   │   │   │   ├── OrdemServicoController.java        # Ordens de Servico (admin)
│   │   │   │   └── OrdemServicoPublicController.java  # Consulta publica de OS
│   │   │   ├── domain/
│   │   │   │   ├── Cliente.java                       # Entidade Cliente
│   │   │   │   ├── Veiculo.java                       # Entidade Veiculo
│   │   │   │   ├── Servico.java                       # Entidade Servico
│   │   │   │   ├── Peca.java                          # Entidade Peca
│   │   │   │   ├── OrdemServico.java                  # Entidade Ordem de Servico
│   │   │   │   ├── Usuario.java                       # Entidade Usuario
│   │   │   │   ├── Role.java                          # Enum de perfis
│   │   │   │   └── StatusOrdemServico.java            # Enum de status da OS
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequestDTO.java               # Requisicao de login
│   │   │   │   ├── LoginResponseDTO.java              # Resposta de login (token)
│   │   │   │   ├── ClienteDTO.java                    # Entrada de dados de cliente
│   │   │   │   ├── ClienteResponseDTO.java            # Resposta com CPF mascarado
│   │   │   │   ├── VeiculoDTO.java                    # Entrada de dados de veiculo
│   │   │   │   ├── VeiculoResponseDTO.java            # Resposta com placa mascarada
│   │   │   │   ├── ServicoDTO.java                    # Entrada de dados de servico
│   │   │   │   ├── PecaDTO.java                       # Entrada de dados de peca
│   │   │   │   ├── BaixaEstoqueDTO.java               # Baixa de estoque
│   │   │   │   ├── CriarOrdemServicoDTO.java          # Criacao de OS
│   │   │   │   ├── AtualizarStatusOSDTO.java          # Atualizacao de status da OS
│   │   │   │   └── OrdemServicoPublicDTO.java         # Visualizacao publica da OS
│   │   │   ├── exception/
│   │   │   │   └── ApiExceptionHandler.java           # Handler global de excecoes
│   │   │   ├── mapper/
│   │   │   │   ├── ClienteMapper.java                 # Mapper Cliente -> ResponseDTO
│   │   │   │   └── VeiculoMapper.java                 # Mapper Veiculo -> ResponseDTO
│   │   │   ├── repository/
│   │   │   │   ├── ClienteRepository.java
│   │   │   │   ├── VeiculoRepository.java
│   │   │   │   ├── ServicoRepository.java
│   │   │   │   ├── PecaRepository.java
│   │   │   │   ├── OrdemServicoRepository.java
│   │   │   │   └── UsuarioRepository.java
│   │   │   ├── security/
│   │   │   │   ├── JwtService.java                    # Geracao e validacao de JWT
│   │   │   │   ├── JwtAuthenticationFilter.java       # Filtro de autenticacao
│   │   │   │   ├── CustomUserDetailsService.java      # Carregamento de usuarios
│   │   │   │   ├── AuthEntryPoint.java                # Handler de erros 401
│   │   │   │   └── AccessDeniedHandlerImpl.java       # Handler de erros 403
│   │   │   ├── service/
│   │   │   │   ├── ClienteService.java
│   │   │   │   ├── VeiculoService.java
│   │   │   │   ├── ServicoService.java
│   │   │   │   ├── PecaService.java
│   │   │   │   └── OrdemServicoService.java
│   │   │   ├── util/
│   │   │   │   ├── InputNormalizer.java               # Normalizacao de CPF, placa, email
│   │   │   │   ├── CpfCnpjValidator.java              # Validacao de CPF/CNPJ
│   │   │   │   ├── PlacaValidator.java                # Validacao de placa BR/Mercosul
│   │   │   │   └── SensitiveDataMasker.java           # Mascaramento de dados
│   │   │   └── validation/
│   │   │       ├── CpfCnpjValido.java                 # Anotacao @CpfCnpjValido
│   │   │       ├── CpfCnpjConstraintValidator.java    # Implementacao do validador
│   │   │       ├── PlacaValida.java                   # Anotacao @PlacaValida
│   │   │       └── PlacaConstraintValidator.java      # Implementacao do validador
│   │   └── resources/
│   │       └── application.yaml                       # Configuracao principal
│   └── test/
│       ├── java/com/f1rsters/tech_challenge_mecanica/ # Testes unitarios e de integracao
│       └── resources/
│           └── application-test.yaml                  # Configuracao para testes (H2)
├── k8s/                                               # Manifestos Kubernetes
│   ├── namespace.yaml                                 # Namespace da aplicacao
│   ├── app-configmap.yaml                              # ConfigMap da aplicacao
│   ├── app-secret.yaml                                 # Secret da aplicacao (JWT)
│   ├── app-deployment.yaml                             # Deployment da aplicacao
│   ├── app-service.yaml                               # Service da aplicacao (NodePort)
│   ├── app-hpa.yaml                                   # Horizontal Pod Autoscaler
│   ├── db-secret.yaml                                 # Secret do banco de dados
│   ├── db-deployment.yaml                             # Deployment do PostgreSQL
│   ├── db-service.yaml                               # Service do PostgreSQL
│   └── db-pvc.yaml                                    # PersistentVolumeClaim para PostgreSQL
├── infra/                                             # Infraestrutura como codigo (Terraform)
│   ├── main.tf                                        # Configuracao principal Terraform
│   ├── variables.tf                                   # Variaveis Terraform
│   ├── outputs.tf                                     # Outputs Terraform
│   └── README.md                                      # Documentacao da infraestrutura
├── Dockerfile                                         # Imagem Docker da aplicacao (multi-stage)
├── docker-compose.yml                                 # Orquestracao (PostgreSQL + App)
├── TechChallengeMecanica.postman_collection.json      # Colecao Postman pronta
├── pom.xml                                            # Dependencias Maven
├── mvnw / mvnw.cmd                                    # Maven Wrapper
└── README.md
```

---

## Pre-requisitos

### Para execucao com Docker (Recomendado)
- [Docker](https://docs.docker.com/get-docker/) (versao 20+)
- [Docker Compose](https://docs.docker.com/compose/install/) (versao 2+)

### Para execucao local (sem Docker)
- [Java JDK 17](https://adoptium.net/) (Eclipse Temurin recomendado)
- [Maven 3.8+](https://maven.apache.org/download.cgi) (ou use o Maven Wrapper incluso: `./mvnw`)
- [PostgreSQL 15](https://www.postgresql.org/download/)
- [Git](https://git-scm.com/downloads)

### Para Kubernetes
- [kubectl](https://kubernetes.io/docs/tasks/tools/) - CLI do Kubernetes
- Cluster Kubernetes local (Kind, Minikube ou K3d)

### Para Terraform
- [Terraform](https://developer.hashicorp.com/terraform/downloads) >= 1.0
- Cluster Kubernetes local (Kind, Minikube ou K3d)

### Ferramentas opcionais
- [Postman](https://www.postman.com/downloads/) - Para testar os endpoints (colecao inclusa)
- [cURL](https://curl.se/) - Para testar via terminal

---

## Configuracao e Execucao

### Opcao 1 - Docker Compose (Recomendado)

Esta e a forma mais simples de executar o projeto. O Docker Compose sobe o banco PostgreSQL e a aplicacao automaticamente.

**1. Clone o repositorio:**

```bash
git clone https://github.com/diogocarpio/f1rsters-tech-challenge-mecanica.git
cd f1rsters-tech-challenge-mecanica
git checkout feature/diogo
```

**2. Suba os containers:**

```bash
docker-compose up --build -d
```

> **Nota:** O Dockerfile utiliza multi-stage build e faz o build Maven automaticamente durante o processo de build da imagem.

Isso ira:
- Subir um container PostgreSQL 15 na porta `5432`
- Subir a aplicacao Spring Boot na porta `8080`
- Criar automaticamente um usuario admin (seed)

**3. Verifique se os containers estao rodando:**

```bash
docker-compose ps
```

**4. Acesse a API:**
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

**5. Para parar os containers:**

```bash
docker-compose down
```

**6. Para parar e remover os dados do banco:**

```bash
docker-compose down -v
```

---

### Opcao 2 - Execucao Local (sem Docker)

**1. Clone o repositorio:**

```bash
git clone https://github.com/diogocarpio/f1rsters-tech-challenge-mecanica.git
cd f1rsters-tech-challenge-mecanica
git checkout feature/diogo
```

**2. Configure o PostgreSQL:**

Crie o banco de dados e o usuario no PostgreSQL:

```sql
CREATE DATABASE oficina;
CREATE USER oficinauser WITH PASSWORD 'oficinapassword';
GRANT ALL PRIVILEGES ON DATABASE oficina TO oficinauser;
```

> Caso ja tenha o PostgreSQL instalado e prefira usar outro usuario/senha, altere o arquivo `src/main/resources/application.yaml` ou defina variaveis de ambiente.

**3. Execute a aplicacao:**

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

A aplicacao ira iniciar na porta `8080` e o Hibernate criara as tabelas automaticamente (`ddl-auto: update`).

**4. Acesse a API:**
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

### Opcao 3 - Kubernetes

Para executar a aplicacao em um cluster Kubernetes local (Kind, Minikube ou K3d).

**1. Build da imagem Docker:**

```bash
docker build -t oficina-app:latest .
```

**2. Carregar a imagem no cluster (se usando Kind):**

```bash
kind load docker-image oficina-app:latest
```

**3. Aplicar os manifestos Kubernetes:**

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/app-configmap.yaml
kubectl apply -f k8s/app-secret.yaml
kubectl apply -f k8s/db-secret.yaml
kubectl apply -f k8s/db-pvc.yaml
kubectl apply -f k8s/db-deployment.yaml
kubectl apply -f k8s/db-service.yaml
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-service.yaml
kubectl apply -f k8s/app-hpa.yaml
```

**4. Verificar os recursos:**

```bash
kubectl get pods -n oficina
kubectl get svc -n oficina
kubectl get hpa -n oficina
```

**5. Acessar a aplicacao:**

```bash
# Obter a porta do NodePort
kubectl get svc oficina-app -n oficina

# Acessar via NodePort
http://localhost:<NODEPORT>
```

**6. Remover os recursos:**

```bash
kubectl delete -f k8s/
```

---

### Opcao 4 - Terraform (Infraestrutura como Codigo)

Para provisionar a infraestrutura Kubernetes usando Terraform.

**1. Inicializar o Terraform:**

```bash
cd infra
terraform init
```

**2. Validar a configuracao:**

```bash
terraform validate
```

**3. Planejar as mudancas:**

```bash
terraform plan
```

**4. Aplicar a infraestrutura:**

```bash
terraform apply
```

**5. Verificar os recursos criados:**

```bash
kubectl get pods -n oficina
kubectl get svc -n oficina
kubectl get hpa -n oficina
```

**6. Destruir a infraestrutura:**

```bash
terraform destroy
```

Para mais detalhes, consulte o `infra/README.md`.

---

## Variaveis de Ambiente

A aplicacao utiliza variaveis de ambiente com valores padrao. Em producao, e **obrigatorio** redefinir os valores sensiveis:

| Variavel | Descricao | Valor Padrao |
|---|---|---|
| `JWT_SECRET_BASE64` | Chave secreta Base64 para assinatura JWT (min. 256 bits) | `QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVo0NTY3ODkwQUJDREVG` |
| `JWT_ACCESS_TOKEN_MINUTES` | Tempo de expiracao do token JWT (minutos) | `15` |
| `JWT_ISSUER` | Emissor do token JWT | `tech-challenge-mecanica-api` |
| `SECURITY_SEED_ENABLED` | Habilita criacao automatica do usuario admin | `true` |
| `SECURITY_SEED_ADMIN_EMAIL` | Email do usuario admin seed | `admin@oficina.local` |
| `SECURITY_SEED_ADMIN_PASSWORD` | Senha do usuario admin seed | `admin123` |
| `SPRING_PROFILES_ACTIVE` | Perfil ativo do Spring | (nenhum / `prod`) |

> **Importante:** Na primeira execucao com `SECURITY_SEED_ENABLED=true`, o sistema cria automaticamente um usuario administrador com as credenciais configuradas. Apos o primeiro login, voce pode desabilitar o seed.

---

## Autenticacao e Seguranca

### Perfis de Acesso (Roles)

O sistema possui 4 perfis de acesso com permissoes diferenciadas:

| Role | Permissoes |
|---|---|
| **ADMIN** | Acesso total a todos os endpoints |
| **ATENDENTE** | CRUD de Clientes, Veiculos, Servicos; Criar e listar Ordens de Servico |
| **MECANICO** | Atualizar status de OS; Consultar pecas e estoque; Baixar estoque; Criar e listar OS |
| **ESTOQUISTA** | CRUD de Pecas; Consultar e baixar estoque |

### Permissoes por Endpoint

| Metodo | Endpoint | Roles Permitidas |
|---|---|---|
| `POST` | `/api/auth/login` | Publico (sem autenticacao) |
| `GET` | `/api/public/ordens-servico/{id}` | Publico (sem autenticacao) |
| `POST/PUT/DELETE` | `/api/admin/clientes/**` | ADMIN, ATENDENTE |
| `GET` | `/api/admin/clientes/**` | ADMIN, ATENDENTE |
| `POST/PUT/DELETE` | `/api/admin/veiculos/**` | ADMIN, ATENDENTE |
| `GET` | `/api/admin/veiculos/**` | ADMIN, ATENDENTE |
| `POST/PUT/DELETE` | `/api/admin/servicos/**` | ADMIN, ATENDENTE |
| `GET` | `/api/admin/servicos/**` | ADMIN, ATENDENTE |
| `POST` | `/api/admin/pecas` | ADMIN, ESTOQUISTA |
| `PUT/DELETE` | `/api/admin/pecas/**` | ADMIN, ESTOQUISTA |
| `GET` | `/api/admin/pecas/**` | ADMIN, ESTOQUISTA, MECANICO |
| `POST` | `/api/admin/pecas/baixa` | ADMIN, ESTOQUISTA, MECANICO |
| `POST` | `/api/admin/ordens-servico` | ADMIN, ATENDENTE, MECANICO |
| `GET` | `/api/admin/ordens-servico/**` | ADMIN, ATENDENTE, MECANICO |
| `PATCH` | `/api/admin/ordens-servico/{id}/status` | ADMIN, MECANICO |

### Fluxo de Autenticacao JWT

```
1. Cliente faz POST /api/auth/login com email e senha
2. Servidor valida credenciais e retorna um token JWT
3. Cliente envia o token no header Authorization: Bearer <token>
4. Filtro JwtAuthenticationFilter intercepta a requisicao e valida o token
5. Se valido, o usuario e autenticado e a requisicao prossegue
6. Se invalido/expirado, retorna 401 Unauthorized
```

O token JWT contem:
- **subject**: email do usuario
- **roles**: lista de perfis (ex: `["ROLE_ADMIN"]`)
- **issuer**: nome do emissor da API
- **expiracao**: configuravel (padrao 15 minutos)

---

## Endpoints da API

### Autenticacao

#### `POST /api/auth/login`
Realiza login e retorna um token JWT.

**Request Body:**
```json
{
  "email": "admin@oficina.local",
  "senha": "admin123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresInSeconds": 900,
  "roles": ["ROLE_ADMIN"]
}
```

---

### Clientes

> **Autenticacao necessaria.** Envie o header: `Authorization: Bearer <token>`

#### `POST /api/admin/clientes` - Criar cliente
```json
{
  "nome": "Cliente Exemplo",
  "cpfCnpj": "12345678901"
}
```

#### `GET /api/admin/clientes` - Listar todos os clientes

#### `GET /api/admin/clientes/{id}` - Buscar cliente por ID

#### `PUT /api/admin/clientes/{id}` - Atualizar cliente
```json
{
  "nome": "Cliente Atualizado",
  "cpfCnpj": "12345678901"
}
```

#### `DELETE /api/admin/clientes/{id}` - Remover cliente

> **Nota:** O CPF/CNPJ e validado com o algoritmo oficial (digitos verificadores). O campo `cpfCnpj` aceita tanto CPF (11 digitos) quanto CNPJ (14 digitos), com ou sem formatacao (pontos, tracos, barras sao removidos automaticamente). Na resposta, o CPF/CNPJ e retornado mascarado (ex: `***.45.***-01`).

---

### Veiculos

> **Autenticacao necessaria.** Envie o header: `Authorization: Bearer <token>`

#### `POST /api/admin/veiculos` - Cadastrar veiculo
```json
{
  "clienteId": 1,
  "placa": "ABC1D23",
  "marca": "Toyota",
  "modelo": "Corolla",
  "ano": 2020
}
```

#### `GET /api/admin/veiculos` - Listar todos os veiculos

#### `GET /api/admin/veiculos/{id}` - Buscar veiculo por ID

#### `PUT /api/admin/veiculos/{id}` - Atualizar veiculo
```json
{
  "clienteId": 1,
  "placa": "ABC1D23",
  "marca": "Toyota",
  "modelo": "Corolla XEi",
  "ano": 2021
}
```

#### `DELETE /api/admin/veiculos/{id}` - Remover veiculo

> **Nota:** A placa aceita o formato brasileiro antigo (`ABC1234`) e o formato Mercosul (`ABC1D23`). Caracteres especiais (tracos, espacos) sao removidos automaticamente. Na resposta, a placa e mascarada (ex: `ABC****`).

---

### Servicos

> **Autenticacao necessaria.** Envie o header: `Authorization: Bearer <token>`

#### `POST /api/admin/servicos` - Cadastrar servico
```json
{
  "descricao": "Troca de oleo",
  "valor": 150.00
}
```

#### `GET /api/admin/servicos` - Listar todos os servicos

#### `GET /api/admin/servicos/{id}` - Buscar servico por ID

#### `PUT /api/admin/servicos/{id}` - Atualizar servico
```json
{
  "descricao": "Alinhamento e Balanceamento",
  "valor": 200.00
}
```

#### `DELETE /api/admin/servicos/{id}` - Remover servico

---

### Pecas

> **Autenticacao necessaria.** Envie o header: `Authorization: Bearer <token>`

#### `POST /api/admin/pecas` - Cadastrar peca
```json
{
  "descricao": "Filtro de oleo",
  "quantidadeEstoque": 20,
  "valorUnitario": 35.90
}
```

#### `GET /api/admin/pecas` - Listar todas as pecas

#### `GET /api/admin/pecas/{id}` - Buscar peca por ID

#### `PUT /api/admin/pecas/{id}` - Atualizar peca
```json
{
  "descricao": "Filtro de ar",
  "quantidadeEstoque": 10,
  "valorUnitario": 49.90
}
```

#### `DELETE /api/admin/pecas/{id}` - Remover peca

#### `GET /api/admin/pecas/estoque` - Consultar estoque de pecas

#### `POST /api/admin/pecas/baixa` - Dar baixa no estoque
```json
{
  "pecaId": 1,
  "quantidade": 1
}
```

> **Nota:** A baixa de estoque desconta a quantidade informada. Se o estoque for insuficiente, retorna erro.

---

### Ordens de Servico

> **Autenticacao necessaria.** Envie o header: `Authorization: Bearer <token>`

#### `POST /api/admin/ordens-servico` - Criar ordem de servico
```json
{
  "cpfCnpjCliente": "12345678901",
  "placaVeiculo": "ABC1D23",
  "servicos": [1],
  "pecas": [1]
}
```

**Logica de criacao:**
1. Busca o cliente pelo CPF/CNPJ
2. Busca o veiculo pela placa
3. Busca os servicos e pecas pelos IDs informados
4. Valida e desconta 1 unidade de cada peca do estoque
5. Calcula o valor total (soma dos servicos + soma das pecas)
6. Cria a OS com status `AGUARDANDO_APROVACAO`

#### `GET /api/admin/ordens-servico` - Listar todas as ordens de servico

#### `GET /api/admin/ordens-servico/{id}` - Detalhar ordem de servico

#### `PATCH /api/admin/ordens-servico/{id}/status` - Atualizar status da OS
```json
{
  "novoStatus": "EM_EXECUCAO"
}
```

#### `GET /api/admin/ordens-servico/{id}/status` - Consultar status da OS
Retorna o status atual da ordem de servico.

**Response (200 OK):**
```json
{
  "id": 1,
  "status": "DIAGNOSTICO",
  "atualizadoEm": "2025-01-15T10:30:00"
}
```

#### `POST /api/admin/ordens-servico/{id}/orcamento/resposta` - Responder orcamento
Recebe notificacao externa de aprovacao ou recusa do orcamento.

**Request Body:**
```json
{
  "aprovado": true,
  "origem": "SISTEMA_EXTERNO",
  "observacao": "Cliente aprovou o orcamento"
}
```

**Logica:**
- Se `aprovado = true`: move a OS para `EM_EXECUCAO`
- Se `aprovado = false`: mantem o status atual mas registra a decisao
- Apenas aceita OS com status `AGUARDANDO_APROVACAO`

#### `POST /api/admin/ordens-servico/{id}/status/notificacao` - Atualizar status via notificacao externa
Simula ou integra recebimento de notificacao externa (ex: email) para atualizar o status da OS.

**Request Body:**
```json
{
  "novoStatus": "AGUARDANDO_APROVACAO",
  "origem": "EMAIL",
  "mensagem": "Diagnostico concluido. Aguardando aprovacao do cliente."
}
```

**Status disponiveis (fluxo da OS):**

```
RECEBIDA --> DIAGNOSTICO --> AGUARDANDO_APROVACAO --> EM_EXECUCAO --> FINALIZADA --> ENTREGUE
```

| Status | Descricao |
|---|---|
| `RECEBIDA` | Veiculo recebido na oficina (status inicial) |
| `DIAGNOSTICO` | Em processo de avaliacao |
| `AGUARDANDO_APROVACAO` | Aguardando aprovacao do cliente |
| `EM_EXECUCAO` | Servico em andamento |
| `FINALIZADA` | Servico concluido |
| `ENTREGUE` | Veiculo entregue ao cliente |

**Regras de listagem:**
- A listagem principal (`GET /api/admin/ordens-servico`) retorna apenas OS ativas
- OS com status `FINALIZADA` e `ENTREGUE` sao excluidas logicamente da listagem
- A ordenacao respeita a prioridade de status: `EM_EXECUCAO` > `AGUARDANDO_APROVACAO` > `DIAGNOSTICO` > `RECEBIDA`
- Dentro do mesmo status, as OS mais antigas aparecem primeiro

---

### Consulta Publica de Ordem de Servico

#### `GET /api/public/ordens-servico/{id}`

Endpoint **publico** (nao requer autenticacao) para o cliente acompanhar o status da sua ordem de servico. Retorna dados com informacoes sensiveis mascaradas.

**Response (200 OK):**
```json
{
  "id": 1,
  "status": "EM_EXECUCAO",
  "criadoEm": "2025-01-15T10:30:00",
  "nomeCliente": "Joao Silva",
  "placaVeiculo": "ABC****",
  "servicos": ["Troca de oleo", "Alinhamento"],
  "pecas": ["Filtro de oleo"],
  "valorTotal": 185.90
}
```

---

## Exemplos de Requisicoes (cURL)

### 1. Login (obter token)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@oficina.local", "senha": "admin123"}'
```

### 2. Cadastrar um cliente (usando o token)

```bash
curl -X POST http://localhost:8080/api/admin/clientes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <SEU_TOKEN_AQUI>" \
  -d '{"nome": "Joao Silva", "cpfCnpj": "52998224725"}'
```

### 3. Cadastrar um veiculo

```bash
curl -X POST http://localhost:8080/api/admin/veiculos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <SEU_TOKEN_AQUI>" \
  -d '{"clienteId": 1, "placa": "ABC1D23", "marca": "Toyota", "modelo": "Corolla", "ano": 2020}'
```

### 4. Cadastrar um servico

```bash
curl -X POST http://localhost:8080/api/admin/servicos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <SEU_TOKEN_AQUI>" \
  -d '{"descricao": "Troca de oleo", "valor": 150.00}'
```

### 5. Cadastrar uma peca

```bash
curl -X POST http://localhost:8080/api/admin/pecas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <SEU_TOKEN_AQUI>" \
  -d '{"descricao": "Filtro de oleo", "quantidadeEstoque": 20, "valorUnitario": 35.90}'
```

### 6. Criar uma ordem de servico

```bash
curl -X POST http://localhost:8080/api/admin/ordens-servico \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <SEU_TOKEN_AQUI>" \
  -d '{"cpfCnpjCliente": "52998224725", "placaVeiculo": "ABC1D23", "servicos": [1], "pecas": [1]}'
```

### 7. Atualizar status da OS

```bash
curl -X PATCH http://localhost:8080/api/admin/ordens-servico/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <SEU_TOKEN_AQUI>" \
  -d '{"novoStatus": "EM_EXECUCAO"}'
```

### 8. Consulta publica da OS (sem autenticacao)

```bash
curl http://localhost:8080/api/public/ordens-servico/1
```

---

## Swagger / OpenAPI

A documentacao interativa da API esta disponivel via Swagger UI:

- **URL:** `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

O projeto tambem configura (via `application.yaml`) um atalho para o Swagger UI em:

- **URL alternativa:** `http://localhost:8080/swagger-ui.html`

### Configuracao do OpenAPI (classe `OpenApiConfig`)

Foi adicionada a classe `com.f1rsters.tech_challenge_mecanica.config.OpenApiConfig`, responsavel por configurar metadados do OpenAPI e o esquema de seguranca JWT para o Swagger:

- **Info da API**
  - **Title:** `Tech Challenge Mecanica API`
  - **Version:** `v1`
  - **Description:** `API de gerenciamento da oficina mecanica`
- **Autenticacao no Swagger (JWT)**
  - Registra o `SecurityScheme` do tipo HTTP Bearer com `bearerFormat` JWT
  - Nome do esquema: `bearerAuth`
  - Com isso, ao clicar em **Authorize** no Swagger UI, voce pode informar `Bearer <seu_token>` e testar endpoints protegidos

### Organizacao por controllers (Swagger)

Alguns controllers foram atualizados com anotacoes do Swagger para melhorar a navegacao no Swagger UI, agrupando endpoints por dominio usando `@Tag`, por exemplo:

- `AuthController` (tag **Auth**)
- `ClienteController` (tag **Clientes**)
- `VeiculoController` (tag **Veiculos**)
- `ServicoController` (tag **Servicos**)
- `PecaController` (tag **Pecas**)
- `OrdemServicoController` (tag **Ordens de Servico**)
- `OrdemServicoPublicController` (tag **Ordens de Servico Publico**)

No Swagger UI voce pode:
1. Visualizar todos os endpoints disponiveis
2. Testar requisicoes diretamente pelo navegador
3. Autenticar clicando em **Authorize** e inserindo `Bearer <seu_token>`

---

## Postman Collection

O projeto inclui uma colecao Postman pronta para uso:

**Arquivo:** `TechChallengeMecanica.postman_collection.json`

### Como importar no Postman:

1. Abra o Postman
2. Clique em **Import** (canto superior esquerdo)
3. Selecione o arquivo `TechChallengeMecanica.postman_collection.json`
4. A colecao sera importada com todas as requisicoes organizadas por controller

### Variaveis da colecao:

| Variavel | Valor Padrao | Descricao |
|---|---|---|
| `baseUrl` | `http://localhost:8080` | URL base da API |
| `accessToken` | (vazio) | Preenchido automaticamente apos login |
| `adminEmail` | `admin@oficina.local` | Email do admin |
| `adminPassword` | `admin123` | Senha do admin |
| `clienteId` | `1` | ID do cliente para testes |
| `veiculoId` | `1` | ID do veiculo para testes |
| `servicoId` | `1` | ID do servico para testes |
| `pecaId` | `1` | ID da peca para testes |
| `ordemServicoId` | `1` | ID da OS para testes |

> **Dica:** Execute primeiro a requisicao `POST /api/auth/login` - o token JWT sera salvo automaticamente na variavel `accessToken` e usado nas demais requisicoes.

---

## Modelo de Dominio

### Entidades e Relacionamentos

```
Usuario (1) ----> (*) Role [ADMIN, ATENDENTE, MECANICO, ESTOQUISTA]

Cliente (1) ----> (*) Veiculo

OrdemServico (*) ----> (1) Cliente
OrdemServico (*) ----> (1) Veiculo
OrdemServico (*) <---> (*) Servico   [ManyToMany]
OrdemServico (*) <---> (*) Peca      [ManyToMany]
```

### Descricao das Entidades

| Entidade | Campos | Descricao |
|---|---|---|
| **Cliente** | `id`, `nome`, `cpfCnpj`, `veiculos` | Pessoa fisica ou juridica dona do veiculo |
| **Veiculo** | `id`, `cliente`, `placa`, `marca`, `modelo`, `ano` | Veiculo associado a um cliente |
| **Servico** | `id`, `descricao`, `valor` | Tipo de servico oferecido pela oficina |
| **Peca** | `id`, `descricao`, `quantidadeEstoque`, `valorUnitario` | Peca com controle de estoque |
| **OrdemServico** | `id`, `cliente`, `veiculo`, `servicos`, `pecas`, `valorTotal`, `status`, `criadoEm` | Ordem de servico com rastreamento de status |
| **Usuario** | `id`, `email`, `senhaHash`, `ativo`, `roles` | Usuario do sistema para autenticacao |

---

## Validacoes Customizadas

O projeto implementa validacoes customizadas utilizando Bean Validation:

### `@CpfCnpjValido`
- Valida CPF (11 digitos) e CNPJ (14 digitos) com calculo dos digitos verificadores
- Remove automaticamente caracteres nao numericos (pontos, tracos, barras)
- Rejeita sequencias de digitos iguais (ex: `11111111111`)

### `@PlacaValida`
- Valida placa no formato brasileiro antigo: `ABC1234` (3 letras + 4 numeros)
- Valida placa no formato Mercosul: `ABC1D23` (3 letras + 1 numero + 1 letra + 2 numeros)
- Remove automaticamente caracteres especiais e converte para maiusculo

---

## Mascaramento de Dados Sensiveis

As respostas da API mascaram dados sensiveis automaticamente:

| Dado | Exemplo Original | Exemplo Mascarado |
|---|---|---|
| CPF | `52998224725` | `***.98.***-25` |
| CNPJ | `11222333000181` | `**.***. 333/****-81` |
| Placa | `ABC1D23` | `ABC****` |
| Email | `admin@oficina.local` | `a***@oficina.local` |

Isso garante que dados sensiveis nao sejam expostos desnecessariamente nas respostas da API, especialmente no endpoint publico de acompanhamento da OS.

---

## Testes

O projeto possui testes unitarios e de integracao abrangentes.

### Executar todos os testes:

```bash
# Linux / macOS
./mvnw test

# Windows
mvnw.cmd test
```

### Tecnologias de teste:
- **JUnit 5** - Framework de testes
- **Mockito** - Mocking de dependencias
- **Spring Boot Test** - Testes de integracao com contexto Spring
- **Spring Security Test** - Testes de autenticacao e autorizacao
- **H2 Database** - Banco em memoria para testes (profile `test`)

### Configuracao de testes:
- Os testes utilizam o profile `test` com banco H2 em memoria (`application-test.yaml`)
- O seed do admin e **desabilitado** nos testes (`security.seed.enabled: false`)
- Os testes rodam em **paralelo** por classe (configurado no `pom.xml`)

---

## Cobertura de Codigo (JaCoCo)

O projeto utiliza JaCoCo para garantir cobertura de testes:

### Executar testes com relatorio de cobertura:

```bash
# Linux / macOS
./mvnw verify

# Windows
mvnw.cmd verify
```

### Visualizar o relatorio:
Apos executar, abra o arquivo no navegador:
```
target/site/jacoco/index.html
```

### Configuracao de cobertura:
- **Cobertura minima exigida:** 100% de linhas no pacote `service`
- **Escopo:** Pacote `com.f1rsters.tech_challenge_mecanica.service`
- **Exclusoes:** Classes geradas pelo Hibernate (proxies, interceptors)

> O build falha (`mvnw verify`) se a cobertura dos services estiver abaixo de 100%.

---

## Tratamento de Erros

A API possui tratamento global de excecoes via `@RestControllerAdvice`:

### Erro de validacao (400 Bad Request):
```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "fields": [
    {
      "field": "cpfCnpj",
      "message": "cpfCnpj invalido"
    }
  ]
}
```

### Erro de autenticacao (401 Unauthorized):
Retornado quando o token JWT e invalido, expirado ou ausente.

### Erro de autorizacao (403 Forbidden):
Retornado quando o usuario nao possui a role necessaria para acessar o endpoint.

### Recurso nao encontrado (404 Not Found):
Retornado quando o recurso solicitado (cliente, veiculo, OS, etc.) nao existe.

---

## Grupo

**Turma:** F1RSTERS FIAP - Diogo, Alexandra, Rodrigo e Livea

**Disciplina:** Tech Challenge - Mecanica  
**Repositorio:** [github.com/diogocarpio/f1rsters-tech-challenge-mecanica](https://github.com/diogocarpio/f1rsters-tech-challenge-mecanica)
