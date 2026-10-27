# LojaCarro - Modulo de Log

Modulo que recebe pelo RabbitMQ as operacoes feitas com os carros na LojaCarro e salva cada uma num arquivo.

Autor: Jose Augusto

Foi feito em cima dos codigos `Enviar.java` e `Receber.java` passados em aula. A fila e a exchange se chamam
`augusto`.

## Como funciona

```
LojaCarro  --(mensagem)-->  RabbitMQ (fila augusto)  --(mensagem)-->  Receber  -->  operacoes.log
```

Toda vez que um carro e cadastrado, atualizado ou excluido, a LojaCarro manda um DTO (`OperacaoCarroDTO`) em JSON:

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

O `Receber` le a mensagem, o `RegistroOperacao` transforma o JSON de volta no `OperacaoCarroDTO` (com o Jackson) e
salva uma linha no `operacoes.log`:

```
27/10/2026 14:05:30 | CARRO_CRIADO | Maria Oliveira (#2) | carro #1 Toyota Corolla 2024 | req=web-abc123
```

Mensagem que nao for JSON (como a de teste do `Enviar`) e salva do jeito que chegou.

Se o modulo estiver desligado, as mensagens ficam guardadas na fila e sao processadas quando ele voltar.

## Como rodar

```bash
./mvnw clean package
java -jar target/lojacarro-log.jar
```

Por padrao conecta no RabbitMQ em `10.209.1.53` (usuario `admin`, senha `admin`), que so funciona na rede cabeada
do laboratorio. Para usar outro endereco:

```bash
RABBITMQ_HOST=localhost java -jar target/lojacarro-log.jar
```

O arquivo e salvo em `operacoes.log` na pasta onde o programa rodou. Da pra trocar com a variavel
`ARQUIVO_OPERACOES`.

Para mandar uma mensagem de teste (manda `augusto` para a fila `augusto`):

```bash
java -cp target/lojacarro-log.jar ifrn.edu.br.Enviar
```
