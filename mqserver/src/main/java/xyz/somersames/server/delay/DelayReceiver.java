package xyz.somersames.server.delay;

import com.rabbitmq.client.*;
import xyz.somersames.server.MqReceiver;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * @author szh
 * @create 2019-03-03 23:26
 **/
public class DelayReceiver {

    private final String HOST ="127.0.0.1";
    private final String USER="guest";
    private final String PASSWORD="guest";

    private Connection connection;
    private Channel channel;
    private String routingKey = "mq_tutorials";
    private String dQueueName = "dTutorials";
    private String dExchangeName = "dMqTutorials";
    public static void main(String[] args) throws IOException, TimeoutException {
        new DelayReceiver();
    }

    public DelayReceiver() throws IOException, TimeoutException {
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
        channel.queueBind(dQueueName, dExchangeName, routingKey);
        DefaultConsumer defaultConsumer =new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String str = new String(body);
                System.out.println(str);
                System.out.println(new Date());
                if("测试".equals(str)){
                    channel.basicReject(envelope.getDeliveryTag(),false);
                }else{
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        };
//        GetResponse response = channel.basicGet(queueName,true);
//        Long tag = response.getEnvelope().getDeliveryTag();
////        channel.basicAck(tag,true);
////        channel.basicReject(tag,false);
        channel.basicConsume(dQueueName,defaultConsumer);
    }
}
