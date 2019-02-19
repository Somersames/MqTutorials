package xyz.somersames.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author szh
 * @create 2019-02-19 23:33
 **/
public class MqSender {
    private final String HOST ="127.0.0.1";
    private final String USER="guest";
    private final String PASSWORD="guest";

    private Connection connection;
    private Channel channel;
    private String routingKey = "mq_tutorials";
    private String queueName = "Tutorials";
    private String exchangeName = "MqTutorials";

    public static void main(String[] args) throws IOException, TimeoutException {
        MqSender mqSender =new MqSender();
        mqSender.sengMessgae("测试");
    }

    private MqSender() throws IOException, TimeoutException {
// 创建一个连接工厂 connection factory
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

        channel.exchangeDeclare(exchangeName,"direct",true,false,null);
    }

    private void sengMessgae(String messgae) throws IOException {
        byte[] bytes  =messgae.getBytes();
        //声明一个队列 - 持久化
        channel.queueDeclare(queueName, true, false, false, null);

        //设置通道预取计数
        channel.basicQos(1);

        //将消息队列绑定到Exchange
        channel.queueBind(queueName, exchangeName, routingKey);
        channel.basicPublish(exchangeName, routingKey, null, bytes);
    }
}
