package com.example.quartz;

import org.quartz.SchedulerException;

import java.io.IOException;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/11/2 14:27
 * Modified By:
 * Description:
 */
public class Test04 {
    public static void main(String[] args){
        String cronString = "20 * * * * ? *";
        String startTime = "2018-11-06 13:53:00";
        String endTime = "2018-11-06 13:54:00";
        try {
//            JobSchedule.addHelloJob("name1", "name1", "name2", "name2",
//                    HelloJob.class, cronString, 1, TimeUtil.parseStringToDateTime(startTime), TimeUtil.parseStringToDateTime(endTime));
            JobSchedule.addJob("name2",HelloJob.class,"0 * * * * ? *");
            JobSchedule.addJob("name3",HelloJob.class,"40 * * * * ? *");
            try {
                Thread.sleep(60000L);
                JobSchedule.removeJob("name3");
                Thread.sleep(60000L);
                JobSchedule.startJobs();
                Thread.sleep(120000L);
                JobSchedule.shutdownJobs();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
