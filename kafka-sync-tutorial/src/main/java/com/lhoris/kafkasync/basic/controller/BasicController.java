package com.lhoris.kafkasync.basic.controller;

import com.lhoris.kafkasync.common.util.JsonUtil;
import com.lhoris.kafkasync.basic.dto.Result;
import com.lhoris.kafkasync.basic.dto.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/basic")
public class BasicController {

    @Value("${kafka.reuest.topic}")
    private String requestTopic;

    @Autowired
    private ReplyingKafkaTemplate<String, Order, Result> replyingKafkaTemplate;

    @PostMapping("/order")
    public ResponseEntity<Result> getObject(@RequestBody Order order)
            throws InterruptedException, ExecutionException {
        log.info(">>>> INPUT >>>> " + JsonUtil.toJson(order));

        ProducerRecord<String, Order> record = new ProducerRecord<>(requestTopic, null, "TEST01", order);
        RequestReplyFuture<String, Order, Result> future = replyingKafkaTemplate.sendAndReceive(record);
        ConsumerRecord<String, Result> response = future.get();

        Result result = response.value();
        log.info(">>>> OUTPUT >>>> " + JsonUtil.toJson(result));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
