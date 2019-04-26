package com.wangyb.springbootamqp.service;

import com.wangyb.springbootamqp.common.NeedCommon;
import com.wangyb.springbootamqp.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author wangyb
 * @Date 2019/4/26 15:30
 * Modified By:
 * Description:
 */
@Component
@Slf4j
public class MsgProducer implements RabbitTemplate.ConfirmCallback {

    //由于rabbitTemplate的scope属性设置为ConfigurableBeanFactory.SCOPE_PROTOTYPE，所以不能自动注入
    private RabbitTemplate rabbitTemplate;

    /**
     * 构造方法注入rabbitTemplate
     */
    @Autowired
    public MsgProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setConfirmCallback(this); //rabbitTemplate如果为单例的话，那回调就是最后设置的内容
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if (b) {
            log.info("success,秒杀成功,货物id:" + correlationData + "，稍后将为您发货");
        } else {
            NeedCommon.NEED_COMMON.setNeed(NeedCommon.NEED_COMMON.getNeed() + 1);
            log.info("sorry，秒杀失败，请重试：" + s);
        }
    }

    public void sendMsg(String content, Integer number) {
        //只有前35个消息会成功
        if (number <= 35) {
            CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
            //把消息放入ROUTINGKEY_A对应的队列当中去，对应的是队列A
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_A, RabbitConfig.ROUTINGKEY_A, content, correlationId);
        } else {
            //如果有任务添加失败，则补充一个任务
            if (NeedCommon.NEED_COMMON.getNeed() > 0) {
                CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
                //把消息放入ROUTINGKEY_A对应的队列当中去，对应的是队列A
                rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_A, RabbitConfig.ROUTINGKEY_A, content, correlationId);
                NeedCommon.NEED_COMMON.setNeed(NeedCommon.NEED_COMMON.getNeed() - 1);
            } else {
                log.info("sorry，秒杀失败，本轮商品已经售空，请等待下次秒杀活动哦");
            }
        }
    }
}
