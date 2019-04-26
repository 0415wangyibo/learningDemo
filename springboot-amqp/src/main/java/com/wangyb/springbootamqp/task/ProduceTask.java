package com.wangyb.springbootamqp.task;

import com.wangyb.springbootamqp.service.MsgProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author wangyb
 * @Date 2019/4/26 15:49
 * Modified By:
 * Description:
 */
@Component
@Slf4j
public class ProduceTask {

    @Autowired
    private MsgProducer msgProducer;

    //模拟秒杀商品活动
    @Scheduled(initialDelay = 10000, fixedRate = 4 * 60 * 1000)
    public void addATask() {
        log.info("本期秒杀活动开始：");
        for (int i = 1; i <= 50; i++) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("用户" + i + "正在秒杀商品A");
            msgProducer.sendMsg("这是用户" + i + "所要的商品A，购物时间：" + LocalDateTime.now(), i);
        }
        log.info("本期秒杀活动结束，请等待下一期");
    }
}
