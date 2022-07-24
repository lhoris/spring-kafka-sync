package com.lhoris.msa.account.basic.listener;

import com.lhoris.msa.account.basic.dto.Order;
import com.lhoris.msa.account.basic.dto.Result;
import com.lhoris.msa.account.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class AccountListener {

    @Value("${kafka.send.partition}")
    private String sendPartition;

    @KafkaListener(topics = "${kafka.reuest.topic}", groupId = "${kafka.group.id}")
    @SendTo
    public Message<?> listen(ConsumerRecord record,
                             @Header(KafkaHeaders.REPLY_TOPIC) byte[] replyTopic,
                             @Header(KafkaHeaders.CORRELATION_ID) byte[] correlationId) throws InterruptedException {

        String value = record.value().toString();
        Order order = JsonUtil.fromJson(value, Order.class);
        Result result = new Result();
        BeanUtils.copyProperties(order, result);

        result.setAccountNo(UUID.randomUUID().toString());
        result.setOrderNo(UUID.randomUUID().toString());

        return MessageBuilder.withPayload(JsonUtil.toJson(result))
                .setHeader(KafkaHeaders.TOPIC, replyTopic)
                .setHeader(KafkaHeaders.MESSAGE_KEY, UUID.randomUUID().toString())
                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                .setHeader(KafkaHeaders.PARTITION_ID, Integer.parseInt(sendPartition))
                .build();
    }

}