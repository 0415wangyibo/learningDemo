package com.example.quartz;

import org.quartz.*;


import static org.quartz.JobBuilder.newJob;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/11/1 12:12
 * Modified By:
 * Description:
 */
public class Test02 {
    public static void main(String[] args) {
        Integer minute=12;
        Integer hour = 14;
        Integer second=0;
        String cronExpression = String.format("%d %d %d ? * *",second, minute, hour);
        try {
            SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

            Scheduler scheduler = schedFact.getScheduler();

            // define the job and tie it to our HelloJob class
            JobDetail job = newJob(HelloJob.class)
                    .withIdentity("job2", "group1")
                    .build();

            CronTrigger trigger = newTrigger()
                    .withIdentity("trigger2", "group1")
                    .withSchedule(cronSchedule(cronExpression))
                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);
            // and start it off
            scheduler.start();
            try {
                Thread.sleep(300L * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }
}
