package xyz.somersames.delay;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import xyz.somersames.client.MqSender;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author szh
 * @create 2019-03-03 23:14
 **/
public class DelaySender {
    private final String HOST ="127.0.0.1";
    private final String USER="guest";
    private final String PASSWORD="guest";

    private Connection connection;
    private Channel channel;
    private String routingKey = "mq_tutorials";
    private String queueName = "Tutorials";
    private String dQueeueName = "dTutorials";
    private String exchangeName = "MqTutorials";
    private String dExchangeName = "dMqTutorials";


    public static void main(String[] args) throws IOException, TimeoutException {
        DelaySender mqSender =new DelaySender();
        mqSender.sengMessgae("测试");
        System.out.println(new Date());
    }

    private DelaySender() throws IOException, TimeoutException {
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
        channel.exchangeDeclare(dExchangeName,"direct",true,false,null);
    }

    private void sengMessgae(String messgae) throws IOException {
        byte[] bytes  =messgae.getBytes();
        //声明一个队列 - 持久化
        Map<String,Object> args = new HashMap<String, Object>();
        args.put("x-dead-letter-exchange",dExchangeName);
        args.put("x-dead-letter-routing-key",routingKey);
        args.put("x-message-ttl",2000);
        channel.queueDeclare(queueName, true, false, false, args);
        channel.queueDeclare(dQueeueName, true, false, false, null);

        //设置通道预取计数
        channel.basicQos(1);

        //将消息队列绑定到Exchange
        channel.queueBind(queueName, exchangeName, routingKey);
        channel.queueBind(dQueeueName, dExchangeName, routingKey);
        channel.basicPublish(exchangeName, routingKey, null, bytes);
    }
}
