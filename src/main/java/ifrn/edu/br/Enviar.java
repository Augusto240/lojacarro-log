package ifrn.edu.br;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class Enviar {

    static final String HOST = System.getenv().getOrDefault("RABBITMQ_HOST", "10.209.1.53");

    public static void main(String[] args) {
        enviarMensagem("augusto", "augusto");
    }

    public static void enviarMensagem(String message, String queue) {
        ConnectionFactory factory = new ConnectionFactory();

        try {
            factory.setUri("amqp://" + HOST + ":5672");
            factory.setUsername("admin");
            factory.setPassword("admin");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.queueDeclare(queue, true, false, false, null);
            channel.basicPublish("", queue, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("MENSAGEM ENVIADA = '" + message + "'");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
