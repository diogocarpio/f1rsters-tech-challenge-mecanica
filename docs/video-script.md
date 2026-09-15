# Roteiro do Vídeo — Tech Challenge Fase 3

Duração máxima: 15 minutos.

## 1. Contexto e arquitetura — 1 minuto

- Apresentar o problema da oficina.
- Mostrar os quatro repositórios e a responsabilidade de cada um.
- Apresentar o diagrama de componentes.

## 2. Banco e infraestrutura — 2 minutos

- Mostrar o repositório Terraform do banco.
- Explicar PostgreSQL no RDS.
- Mostrar state remoto, outputs e ambientes.
- Explicar a integração com Lambda e aplicação.

## 3. Autenticação com CPF — 3 minutos

- Enviar CPF para o API Gateway.
- Mostrar validação na Lambda.
- Mostrar consulta do cliente/status.
- Exibir o JWT retornado.
- Mostrar issuer, audience, CPF e status sem expor o token completo.

## 4. Aplicação e API protegida — 2 minutos

- Consumir `GET /api/clientes/me` com o JWT.
- Mostrar a resposta do cliente autenticado.
- Demonstrar requisição sem token e resposta `401`.
- Demonstrar que um usuário administrativo não acessa a rota de cliente.

## 5. Abertura de ordem de serviço — 2 minutos

- Criar uma OS autenticada.
- Mostrar validação de cliente, veículo, serviços e peças.
- Mostrar a OS criada com status `RECEBIDA`.
- Mostrar as métricas de criação/falha, quando disponíveis.

## 6. Pipeline e deploy — 2 minutos

- Abrir uma Pull Request ou executar a pipeline aprovada.
- Mostrar build e testes.
- Mostrar Terraform plan/apply, quando aplicável.
- Mostrar publicação da imagem e deploy Kubernetes, quando o ambiente estiver disponível.

## 7. Dashboard e observabilidade — 2 minutos

- Mostrar dashboard New Relic.
- Mostrar volume diário de OS.
- Mostrar latência e recursos Kubernetes.
- Mostrar tempo por status e falhas.
- Mostrar logs JSON com `X-Request-Id`, `X-Correlation-ID` e traces.

## 8. Encerramento — 1 minuto

- Relembrar os quatro repositórios.
- Mostrar documentação, ER, ADRs e checklist.
- Informar limitações ou integrações ainda dependentes de ambiente, se existirem.
