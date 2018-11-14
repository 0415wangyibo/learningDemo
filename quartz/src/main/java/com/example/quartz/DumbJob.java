package com.example.quartz;

import org.quartz.*;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/30 16:20
 * Modified By:
 * Description:
 */
public class DumbJob implements Job {

    public DumbJob() {
    }

    public void execute(JobExecutionContext context)
            throws JobExecutionException
    {
        JobKey key = context.getJobDetail().getKey();

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String jobSays = dataMap.getString("jobSays");
        float myFloatValue = dataMap.getFloat("myFloatValue");

        System.err.println("Instance " + key + " of DumbJob says: " + jobSays + ", and val is: " + myFloatValue);
    }
}