# lojacarro-log

Modulo de log da LojaCarro. Ele fica escutando a fila `augusto` no RabbitMQ e salva num arquivo cada operacao feita
com os carros.

Autor: Jose Augusto

Fiz em cima do `Enviar.java` e do `Receber.java` que o professor passou. Troquei a fila `preco` por `augusto` e
mudei o `Receber` para salvar a operacao em vez de responder a mensagem.

## Mensagem

A LojaCarro manda um `OperacaoCarroDTO` em JSON, por exemplo:

```json
{
  "operacao": "CARRO_CRIADO",
  "dataHora": "2026-10-27T14:05:30",
  "usuarioId": 2,
  "usuarioNome": "Maria Oliveira",
  "carroId": 1,
  "marca": "Toyota",
  "modelo": "Corolla",
  "ano": 2024,
  "requestId": "web-abc123"
}
```

O `RegistroOperacao` transforma o JSON de volta no DTO (com o Jackson) e escreve uma linha no `operacoes.log`:

```
27/10/2026 14:05:30 | CARRO_CRIADO | Maria Oliveira (#2) | carro #1 Toyota Corolla 2024 | req=web-abc123
```

Se chegar alguma coisa que nao e JSON ela e salva do jeito que veio. E se o modulo estiver desligado as mensagens
ficam esperando na fila e sao lidas quando ele voltar.

## Rodando

```
./mvnw clean package
java -jar target/lojacarro-log.jar
```

Por padrao ele conecta no RabbitMQ do laboratorio (10.209.1.53, admin / admin), que so funciona na rede cabeada.
Fora de la da pra apontar para outro:

```
RABBITMQ_HOST=localhost java -jar target/lojacarro-log.jar
```

O arquivo `operacoes.log` e criado na pasta onde o programa rodou (ou onde a variavel `ARQUIVO_OPERACOES` mandar).

Para mandar uma mensagem de teste para a fila:

```
java -cp target/lojacarro-log.jar ifrn.edu.br.Enviar
```
