# ADR-002 — Validação do JWT de cliente na aplicação principal

- **Status:** Aceita
- **Data:** 2026-09-12

## Contexto

A aplicação já autentica usuários internos por e-mail e senha. A Lambda de autenticação recebe CPF, consulta cliente e status e emite um JWT com subject igual ao CPF.

Os dois fluxos possuem diferentes tipos de principal e não podem ser tratados como o mesmo usuário persistido em `usuarios`.

## Decisão

A aplicação principal aceitará os dois contextos de autenticação:

- usuários internos continuam usando o JWT atual e suas roles persistidas;
- clientes usam o JWT emitido pela Lambda, identificado por issuer, audience, CPF e status permitido.

O cliente recebe a autoridade de runtime `ROLE_CLIENTE`. Essa autoridade não é persistida como role de usuário interno.

A rota inicial protegida é:

```text
GET /api/clientes/me
```

O cliente é buscado pelo CPF do subject do token, sem permitir que o identificador de outro cliente seja informado na URL.

## Consequências

- O login administrativo existente permanece compatível.
- O JWT da Lambda precisa usar a mesma chave configurada para validação na aplicação.
- A aplicação valida assinatura, expiração, issuer, audience, CPF e status.
- O Gateway precisa encaminhar a rota para a aplicação em uma etapa de integração posterior.
- As rotas administrativas continuam protegidas pelas roles existentes.
