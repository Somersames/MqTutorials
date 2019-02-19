package xyz.somersames.server;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author szh
 * @create 2019-02-19 23:32
 **/
public class MqReceiver {

    private final String HOST ="127.0.0.1";
    private final String USER="guest";
    private final String PASSWORD="guest";

    private Connection connection;
    private Channel channel;
    private String routingKey = "mq_tutorials";
    private String queueName = "Tutorials";
    private String exchangeName = "MqTutorials";
    public static void main(String[] args) throws IOException, TimeoutException {
        new MqReceiver();
    }

    public MqReceiver() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(HOST);
        factory.setUsername(USER);
        factory.setPassword(PASSWORD);
        factory.setPort(5672);
        factory.setVirtualHost("/");

        // 声明一个连接
        connection = factory.newConnection();

        // 声明消息通道
        channel = connection.createChannel();
        channel.queueBind(queueName, exchangeName, routingKey);
        DefaultConsumer defaultConsumer =new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String str = new String(body);
                System.out.println(str);
            }
        };
        channel.basicConsume(queueName, true, defaultConsumer);
    }
}
