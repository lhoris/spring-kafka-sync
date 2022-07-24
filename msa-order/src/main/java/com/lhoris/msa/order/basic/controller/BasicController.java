package com.lhoris.msa.order.basic.controller;

import com.lhoris.msa.order.basic.dto.Order;
import com.lhoris.msa.order.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("/basic")
public class BasicController {

    @Value("${kafka.reuest.topic}")
    private String requestTopic;

    @Value("${kafka.reply.topic}")
    private String replyTopic;

    @Autowired
    private ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;

    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@RequestBody Order order)
            throws InterruptedException, ExecutionException, TimeoutException {
        String orderString = JsonUtil.toJson(order);
        log.info(">>>> INPUT >>>> " + orderString);

        ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic, 0, "TEST01", orderString);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, replyTopic.getBytes(StandardCharsets.UTF_8)));

        RequestReplyFuture<String, String, String> replyFuture = this.replyingKafkaTemplate.sendAndReceive(record, Duration.ofSeconds(60));
        SendResult<String, String> command = replyFuture.getSendFuture().get(30 * 1000, TimeUnit.SECONDS);

        ConsumerRecord<String, String> reply = replyFuture.get(30 * 1000, TimeUnit.SECONDS);
        String result = reply.value();
        log.info(">>>> OUTPUT >>>> " + JsonUtil.toJson(result));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
