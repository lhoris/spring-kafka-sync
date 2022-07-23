package com.lhoris.kafkasync.basic.listener;

import com.lhoris.kafkasync.common.util.JsonUtil;
import com.lhoris.kafkasync.basic.dto.Result;
import com.lhoris.kafkasync.basic.dto.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class AccountListener {

    @KafkaListener(topics = "${kafka.reuest.topic}", groupId = "${kafka.group.id}")
    @SendTo
    public Result handle(Order order) {

        log.info(">>>> Listener COMMAND INPUT >>>> " + JsonUtil.toJson(order));

        Result result = new Result();
        BeanUtils.copyProperties(order, result);

        result.setAccountNo(UUID.randomUUID().toString());
        result.setOrderNo(UUID.randomUUID().toString());

        log.info(">>>> Listener REPLY OUTPUT >>>> " + JsonUtil.toJson(result));
        return result;
    }
}