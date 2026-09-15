# Diagrama de Sequência — Abertura de Ordem de Serviço

## Fluxo principal

```mermaid
sequenceDiagram
    actor Atendente
    participant API as API Spring Boot
    participant Sec as Spring Security
    participant OS as OrdemServicoService
    participant ClienteRepo as ClienteRepository
    participant VeiculoRepo as VeiculoRepository
    participant ServicoRepo as ServicoRepository
    participant PecaRepo as PecaRepository
    participant DB as PostgreSQL

    Atendente->>API: POST /api/admin/ordens-servico
    API->>Sec: Validar JWT e role permitida
    Sec-->>API: Requisicao autorizada
    API->>OS: criarOrdem(dto)
    OS->>OS: Normalizar CPF/CNPJ e placa
    OS->>ClienteRepo: findByCpfCnpj(cpfNormalizado)
    ClienteRepo->>DB: Consultar cliente
    DB-->>ClienteRepo: Cliente
    ClienteRepo-->>OS: Cliente
    OS->>VeiculoRepo: findByPlaca(placaNormalizada)
    VeiculoRepo->>DB: Consultar veiculo
    DB-->>VeiculoRepo: Veiculo
    VeiculoRepo-->>OS: Veiculo
    OS->>ServicoRepo: findAllById(servicos)
    ServicoRepo->>DB: Consultar servicos
    DB-->>ServicoRepo: Servicos
    ServicoRepo-->>OS: Servicos
    OS->>PecaRepo: findAllById(pecas)
    PecaRepo->>DB: Consultar pecas e estoque
    DB-->>PecaRepo: Pecas
    PecaRepo-->>OS: Pecas
    OS->>OS: Validar estoque e calcular valor total
    OS->>PecaRepo: Salvar baixa de estoque
    PecaRepo->>DB: Atualizar estoque
    OS->>DB: Salvar OS com status RECEBIDA
    DB-->>OS: OrdemServico persistida
    OS-->>API: OrdemServico
    API-->>Atendente: 200 OK
```

## Regras representadas

- A rota exige autenticação e autorização conforme as regras administrativas atuais.
- CPF/CNPJ e placa são normalizados antes das consultas.
- Cliente, veículo, serviços e peças são buscados antes da criação da ordem.
- A abertura é transacional no serviço.
- Cada peça informada sofre baixa de uma unidade conforme a regra atual.
- A ordem é criada inicialmente com status `RECEBIDA`.

## Principais falhas

- Cliente inexistente: erro de negócio.
- Veículo inexistente: erro de negócio.
- Peça sem estoque: erro de negócio e a transação não deve concluir a abertura.
- Dados inválidos: erro de validação antes do processamento do serviço.
