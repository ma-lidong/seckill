package com.malidong.seckill.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public void send(String message){
        rabbitTemplate.convertAndSend("seckillExchange", "seckill.message", message);
    }
}
