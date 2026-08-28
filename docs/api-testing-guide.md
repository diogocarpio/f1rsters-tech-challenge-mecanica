# Guia de Testes da API - Autenticação

Este documento fornece instruções detalhadas para testar os endpoints de autenticação usando Postman ou Swagger.

## Pré-requisitos

- Postman Desktop ou Swagger UI
- Conta de cliente cadastrada no banco de dados
- CPF válido para testes

## Configuração do Ambiente

### Endpoint Base
```
Homologação: https://433fbgzdmc.execute-api.sa-east-1.amazonaws.com/homolog
Produção: https://433fbgzdmc.execute-api.sa-east-1.amazonaws.com/prod
```

### Variáveis de Ambiente (Postman)
Crie as seguintes variáveis no Postman:

| Variável | Valor | Descrição |
|----------|-------|-----------|
| `base_url` | `https://433fbgzdmc.execute-api.sa-east-1.amazonaws.com/homolog` | URL base da API |
| `cpf_valido` | `529.982.247-25` | CPF válido para testes |
| `cpf_invalido` | `111.111.111-11` | CPF inválido para testes |
| `token` | `{{accessToken}}` | Token JWT dinâmico |

## Testes com Postman

### 1. Autenticação (Login)

#### Request
```
POST {{base_url}}/auth/login
Content-Type: application/json
```

#### Body
```json
{
  "cpf": "529.982.247-25"
}
```

#### Test Scripts (Postman)
```javascript
// Test: CPF válido
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has accessToken", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("accessToken");
});

pm.test("Response has tokenType", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.tokenType).to.eql("Bearer");
});

pm.test("Response has expiresInSeconds", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.expiresInSeconds).to.eql(900);
});

pm.test("Response has client info", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("client");
    pm.expect(jsonData.client).to.have.property("id");
    pm.expect(jsonData.client).to.have.property("nome");
    pm.expect(jsonData.client).to.have.property("cpfMascarado");
});

// Salvar token para uso posterior
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.accessToken);
}
```

#### Response Esperado (200 OK)
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJjbGllbnRJZCI6MSwiY3BmIjoiNTI5OTgyMjQ3MjUiLCJub21lIjoiQ2xpZW50ZSBFeGVtcGxvIiwic3RhdHVzIjoiQVRJVk8iLCJzdWIiOiI1Mjk5ODIyNDcyNSIsImF1ZCI6InRlY2gtY2hhbGxlbmdlLWFwaSIsImlzcyI6InRlY2gtY2hhbGxlbmdlLWF1dGgtbGFtYmRhIiwiaWF0IjoxNjkzMDAwMDAwLCJleHAiOjE2OTMwMDA5MDB9.signature",
  "tokenType": "Bearer",
  "expiresInSeconds": 900,
  "client": {
    "id": 1,
    "nome": "Cliente Exemplo",
    "cpfMascarado": "***.982.***-25"
  }
}
```

### 2. Teste CPF Inválido

#### Request
```
POST {{base_url}}/auth/login
Content-Type: application/json
```

#### Body
```json
{
  "cpf": "111.111.111-11"
}
```

#### Test Scripts
```javascript
pm.test("Status code is 400", function () {
    pm.response.to.have.status(400);
});

pm.test("Response has error message", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("error");
    pm.expect(jsonData.error).to.eql("CPF inválido");
});
```

#### Response Esperado (400 Bad Request)
```json
{
  "error": "CPF inválido",
  "statusCode": 400
}
```

### 3. Teste Cliente Não Encontrado

#### Request
```
POST {{base_url}}/auth/login
Content-Type: application/json
```

#### Body
```json
{
  "cpf": "123.456.789-09"
}
```

#### Test Scripts
```javascript
pm.test("Status code is 404", function () {
    pm.response.to.have.status(404);
});

pm.test("Response has error message", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("error");
    pm.expect(jsonData.error).to.eql("Cliente não encontrado");
});
```

#### Response Esperado (404 Not Found)
```json
{
  "error": "Cliente não encontrado",
  "statusCode": 404
}
```

### 4. Teste Status Não Permitido

#### Request
```
POST {{base_url}}/auth/login
Content-Type: application/json
```

#### Body
```json
{
  "cpf": "987.654.321-00"
}
```

#### Test Scripts
```javascript
pm.test("Status code is 403", function () {
    pm.response.to.have.status(403);
});

pm.test("Response has error message", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("error");
    pm.expect(jsonData.error).to.eql("Cliente com status não permitido");
});
```

#### Response Esperado (403 Forbidden)
```json
{
  "error": "Cliente com status não permitido",
  "statusCode": 403
}
```

### 5. Rota Protegida (Requer JWT)

#### Request
```
GET {{base_url}}/clientes/me
Authorization: Bearer {{token}}
```

#### Test Scripts
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has client info", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("id");
    pm.expect(jsonData).to.have.property("nome");
});
```

#### Response Esperado (200 OK)
```json
{
  "id": 1,
  "nome": "Cliente Exemplo",
  "cpfMascarado": "***.982.***-25",
  "status": "ATIVO"
}
```

### 6. Rota Protegida Sem Token

#### Request
```
GET {{base_url}}/clientes/me
```

#### Test Scripts
```javascript
pm.test("Status code is 401", function () {
    pm.response.to.have.status(401);
});
```

#### Response Esperado (401 Unauthorized)
```json
{
  "message": "Unauthorized"
}
```

### 7. Rota Protegida com Token Inválido

#### Request
```
GET {{base_url}}/clientes/me
Authorization: Bearer invalid_token_here
```

#### Test Scripts
```javascript
pm.test("Status code is 401", function () {
    pm.response.to.have.status(401);
});
```

#### Response Esperado (401 Unauthorized)
```json
{
  "message": "Unauthorized"
}
```

## Testes com Swagger UI

### Configuração do Swagger

Crie um arquivo `swagger.yaml` ou use Swagger Editor:

```yaml
openapi: 3.0.0
info:
  title: Tech Challenge Mecânica API
  version: 1.0.0
  description: API de autenticação para Tech Challenge Mecânica

servers:
  - url: https://433fbgzdmc.execute-api.sa-east-1.amazonaws.com/homolog
    description: Homologação

paths:
  /auth/login:
    post:
      summary: Autenticar cliente via CPF
      operationId: authenticate
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - cpf
              properties:
                cpf:
                  type: string
                  example: "529.982.247-25"
      responses:
        '200':
          description: Autenticação bem-sucedida
          content:
            application/json:
              schema:
                type: object
                properties:
                  accessToken:
                    type: string
                  tokenType:
                    type: string
                  expiresInSeconds:
                    type: integer
                  client:
                    type: object
                    properties:
                      id:
                        type: integer
                      nome:
                        type: string
                      cpfMascarado:
                        type: string
        '400':
          description: CPF inválido
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                  statusCode:
                    type: integer
        '404':
          description: Cliente não encontrado
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                  statusCode:
                    type: integer
        '403':
          description: Status não permitido
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                  statusCode:
                    type: integer

  /clientes/me:
    get:
      summary: Obter informações do cliente autenticado
      operationId: getClientInfo
      security:
        - bearerAuth: []
      responses:
        '200':
          description: Informações do cliente
          content:
            application/json:
              schema:
                type: object
                properties:
                  id:
                    type: integer
                  nome:
                    type: string
                  cpfMascarado:
                    type: string
                  status:
                    type: string
        '401':
          description: Não autorizado
          content:
            application/json:
              schema:
                type: object
                properties:
                  message:
                    type: string

components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
```

### Como Usar Swagger UI

1. **Importar o arquivo YAML**:
   - Acesse https://editor.swagger.io
   - Copie o conteúdo YAML acima
   - Cole no editor

2. **Testar endpoints**:
   - Clique em "Try it out" ao lado do endpoint
   - Preencha os parâmetros necessários
   - Clique em "Execute"

3. **Para rotas protegidas**:
   - Clique no botão "Authorize"
   - Insira o token JWT obtido no endpoint `/auth/login`
   - Formato: `Bearer eyJhbGciOiJIUzI1NiJ9...`

## Coleção Postman

### Estrutura da Coleção

```
Tech Challenge Mecânica
├── Autenticação
│   ├── POST /auth/login (CPF válido)
│   ├── POST /auth/login (CPF inválido)
│   ├── POST /auth/login (Cliente não encontrado)
│   └── POST /auth/login (Status não permitido)
└── Rotas Protegidas
    ├── GET /clientes/me (Com token válido)
    ├── GET /clientes/me (Sem token)
    └── GET /clientes/me (Token inválido)
```

### Importar Coleção

1. Crie um arquivo `postman_collection.json` com a estrutura acima
2. No Postman: File → Import → Selecione o arquivo
3. Configure as variáveis de ambiente
4. Execute os testes

## Testes Automatizados

### Script de Teste (Node.js)

```javascript
// test-auth-api.js
const axios = require('axios');

const BASE_URL = 'https://433fbgzdmc.execute-api.sa-east-1.amazonaws.com/homolog';

async function testAuth() {
    console.log('=== Testando API de Autenticação ===\n');

    // Test 1: CPF válido
    console.log('Test 1: CPF válido');
    try {
        const response = await axios.post(`${BASE_URL}/auth/login`, {
            cpf: '529.982.247-25'
        });
        console.log('✅ Status:', response.status);
        console.log('✅ Token:', response.data.accessToken.substring(0, 20) + '...');
        console.log('✅ Cliente:', response.data.client.nome);
        const token = response.data.accessToken;
        
        // Test 2: Rota protegida com token
        console.log('\nTest 2: Rota protegida com token');
        const protectedResponse = await axios.get(`${BASE_URL}/clientes/me`, {
            headers: { Authorization: `Bearer ${token}` }
        });
        console.log('✅ Status:', protectedResponse.status);
        console.log('✅ Cliente:', protectedResponse.data.nome);
        
    } catch (error) {
        console.log('❌ Erro:', error.response?.data || error.message);
    }

    // Test 3: CPF inválido
    console.log('\nTest 3: CPF inválido');
    try {
        const response = await axios.post(`${BASE_URL}/auth/login`, {
            cpf: '111.111.111-11'
        });
        console.log('❌ Deveria retornar 400');
    } catch (error) {
        console.log('✅ Status:', error.response.status);
        console.log('✅ Erro:', error.response.data.error);
    }

    console.log('\n=== Testes concluídos ===');
}

testAuth();
```

### Executar Testes
```bash
npm install axios
node test-auth-api.js
```

## Troubleshooting

### Erro 401 Unauthorized
- **Causa**: Token inválido, expirado ou ausente
- **Solução**: Obtenha um novo token via `/auth/login`

### Erro 403 Forbidden
- **Causa**: Cliente com status não permitido
- **Solução**: Verifique o status do cliente no banco

### Erro 404 Not Found
- **Causa**: Cliente não cadastrado
- **Solução**: Cadastre o cliente no banco antes de testar

### Erro 400 Bad Request
- **Causa**: CPF inválido ou formato incorreto
- **Solução**: Use um CPF válido com formato correto

### Timeout
- **Causa**: Lambda timeout ou problema de conexão
- **Solução**: Verifique logs no CloudWatch

## Checklist de Testes

- [ ] Testar autenticação com CPF válido
- [ ] Testar autenticação com CPF inválido
- [ ] Testar autenticação com cliente não encontrado
- [ ] Testar autenticação com status não permitido
- [ ] Testar rota protegida com token válido
- [ ] Testar rota protegida sem token
- [ ] Testar rota protegida com token inválido
- [ ] Testar expiração do token (após 15 minutos)
- [ ] Verificar mascaramento de CPF nas respostas
- [ ] Verificar logs no CloudWatch

## Referências

- [Postman Documentation](https://learning.postman.com/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)
- [JWT.io](https://jwt.io/)
