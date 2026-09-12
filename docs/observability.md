# Observabilidade da Aplicação

## Logs estruturados

A aplicação utiliza o logging estruturado nativo do Spring Boot com formato `logstash` no console. Os logs são emitidos em JSON para facilitar a coleta por ferramentas de monitoramento e agregadores de logs.

O filtro HTTP registra a conclusão de cada requisição com:

- método HTTP;
- caminho requisitado;
- status HTTP;
- duração em milissegundos;
- `correlation_id` no contexto MDC.

Dados sensíveis, como CPF completo, credenciais e tokens, não devem ser adicionados às mensagens de log.

## Correlação de requisições

O header utilizado é:

```text
X-Correlation-ID
```

Comportamento:

1. Se o header for recebido, o valor é preservado.
2. Se estiver ausente ou inválido, a aplicação gera um UUID.
3. O valor é devolvido no header da resposta.
4. O mesmo valor é colocado no MDC com a chave `correlation_id`.
5. O MDC é limpo ao final da requisição.

O API Gateway e a Lambda devem propagar o mesmo header para que uma requisição possa ser acompanhada entre os componentes.
