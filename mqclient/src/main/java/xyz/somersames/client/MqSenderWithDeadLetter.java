package xyz.somersames.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author szh
 * @create 2019-02-26 0:20
 **/
public class MqSenderWithDeadLetter {
    private final String HOST ="127.0.0.1";
    private final String USER="guest";
    private final String PASSWORD="guest";

    private Connection connection;
    private Channel channel;
    private String routingKey = "mq_tutorials";
    private String queueName = "Tutorials";
    private String failqueueName = "FailTutorials";
    private String exchangeName = "MqTutorials";
    private String failExchangeName = "FailMqTutorials";

    public static void main(String[] args) throws IOException, TimeoutException {
        MqSenderWithDeadLetter mqSenderWithDeadLetter =new MqSenderWithDeadLetter();
        mqSenderWithDeadLetter.sengMessgae("测试");
    }

    private MqSenderWithDeadLetter() throws IOException, TimeoutException {
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
        channel.exchangeDeclare(failExchangeName,"direct",true,false,null);
        //将消息队列绑定到Exchange
        //声明一个队列 - 持久化
        channel.queueDeclare(failqueueName, true, false, false, null);
        channel.queueBind(failqueueName, failExchangeName, routingKey);
    }

    private void sengMessgae(String messgae) throws IOException {
        byte[] bytes  =messgae.getBytes();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("x-dead-letter-exchange",failExchangeName);
        map.put("x-dead-letter-routing-key",routingKey);
        //声明一个队列 - 持久化
        channel.queueDeclare(queueName, true, false, false, map);

        //设置通道预取计数
        channel.basicQos(1);

        //将消息队列绑定到Exchange
        channel.queueBind(queueName, exchangeName, routingKey);
        channel.basicPublish(exchangeName, routingKey, null, bytes);
    }
}
