package ifrn.edu.br;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class Receber {

    static RegistroOperacao registro = new RegistroOperacao(
            Path.of(System.getenv().getOrDefault("ARQUIVO_OPERACOES", "operacoes.log")));

    public static void main(String[] args) {
        try {
            lerMensagem();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void lerMensagem() throws IOException {
        ConnectionFactory factory;
        Connection connection;
        Channel channel;

        String QUEUE_NAME1 = "augusto";
        String EXCHANGE_NAME1 = "augusto";
        try {
            factory = new ConnectionFactory();
            factory.setUri("amqp://" + Enviar.HOST + ":5672");
            factory.setUsername("admin");
            factory.setPassword("admin");

            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME1, "fanout", true, false, null);

            channel.queueDeclare(QUEUE_NAME1, true, false, false, null);

            channel.queueBind(QUEUE_NAME1, EXCHANGE_NAME1, "");

            DeliverCallback deliverCallbackQuestion = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("MENSAGEM RECEBIDA = '" + message + "'");

                String linha = registro.salvar(message);
                System.out.println("SALVO = " + linha);
            };

            channel.basicConsume(QUEUE_NAME1, true, deliverCallbackQuestion, consumerTag -> {
            });

            System.out.println("Esperando mensagens na fila " + QUEUE_NAME1 + " em " + Enviar.HOST);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
