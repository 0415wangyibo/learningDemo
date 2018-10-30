package com.example.quartz;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/30 10:57
 * Modified By:
 * Description:
 */

@RestController
@RequestMapping("/quartz")
public class QuartzController {
    @Autowired
    public SchedulerManager myScheduler;

    @RequestMapping(value = "/job2",method = RequestMethod.GET)
    public String scheduleJob2()
    {
        try {
            myScheduler.startJob("0/5 * * * * ?","job2","group2", ScheduledJob.class);//每五秒执行一次
            //0 0/5 14 * * ?在每天下午2点到下午2:55期间的每5分钟触发
            //0 50 14 * * ?在每天下午2点50分5秒执行一次
//            myScheduler.startJob("5 50 14 * * ?","job2","group2", ScheduledJob.class);
            return "启动定时器成功";
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return "启动定时器失败";
    }
    @RequestMapping(value = "/del_job2",method = RequestMethod.GET)
    public String deleteScheduleJob2()
    {
        try {
            myScheduler.deleteJob("job2","group2");
            return "删除定时器成功";
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return "删除定时器失败";
    }
}
