package com.example.quartz;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/11/1 14:32
 * Modified By:
 * Description:
 */
public class TestService {

    public Boolean addScheduleJob(String timeSting){
        String[] times = timeSting.split(":");
        String cronExpression = String.format("0 %d %d ? * *", Integer.valueOf(times[1]), Integer.valueOf(times[0]));
        System.out.println(cronExpression);
        return true;
    }
}
