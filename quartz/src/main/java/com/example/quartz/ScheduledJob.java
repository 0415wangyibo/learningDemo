package com.example.quartz;

import org.jboss.logging.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/30 10:52
 * Modified By:
 * Description:
 */
@Component
public class ScheduledJob implements Job{
    private static final Logger logger= Logger.getLogger(ScheduledJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        //执行任务逻辑....
        logger.info("执行自定义定时任务");
    }
}
