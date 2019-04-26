package com.wangyb.springbootamqp.service;

import com.wangyb.springbootamqp.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author wangyb
 * @Date 2019/4/26 16:27
 * Modified By:
 * Description:
 */
@Component
@RabbitListener(queues = RabbitConfig.QUEUE_A)
@Slf4j
public class MsgReceiverAOne {
    //可执行比较复杂的处理逻辑
    @RabbitHandler
    public void process(String content) {
        log.info("One处理器正在发送A货物：    " + content);
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
