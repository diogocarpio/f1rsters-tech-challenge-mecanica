# ADR-001 — PostgreSQL gerenciado no AWS RDS

- **Status:** Aceita
- **Data:** 2026-09-12

## Contexto

A aplicação possui entidades relacionadas para clientes, veículos, ordens de serviço, serviços, peças e usuários. A abertura de uma ordem e a baixa de estoque precisam ser persistidas com consistência transacional.

A aplicação Spring Boot e a Lambda de autenticação já utilizam PostgreSQL, JPA, JDBC e consultas compatíveis com esse banco.

## Decisão

Manter PostgreSQL 15 como banco principal e utilizar AWS RDS nos ambientes em nuvem. PostgreSQL em Docker Compose permanece disponível para desenvolvimento local, e H2 continua sendo usado pelos testes automatizados existentes.

## Consequências

- Aproveitamento do driver, entidades e consultas existentes.
- Uso de constraints, índices, relacionamentos e transações relacionais.
- RDS fornece banco gerenciado, persistência, backup e criptografia.
- A aplicação Kubernetes precisa receber a conexão do RDS por configuração segura.
- O schema e o modelo relacional devem permanecer compatíveis com a consulta de cliente por CPF e status.

## Alternativas rejeitadas

- MySQL ou SQL Server: exigiriam migração sem benefício necessário para o cenário atual.
- DynamoDB: exigiria remodelar os relacionamentos e as transações do domínio principal.
- PostgreSQL dentro do Kubernetes: não atende ao requisito de banco gerenciado para os ambientes em nuvem.
