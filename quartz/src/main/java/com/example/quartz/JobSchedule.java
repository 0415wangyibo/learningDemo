package com.example.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/11/1 14:19
 * Modified By:
 * Description:
 */
public class JobSchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobSchedule.class);

    private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();
    private static String JOB_GROUP_NAME = "JobName";
    private static String TRIGGER_GROUP_NAME = "TriggerName";

    private JobSchedule(){

    }

    /**
     * @Description: 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     *
     * @param jobName
     *            任务名
     * @param cls
     *            任务
     * @param cron
     *            时间设置，参考quartz说明文档
     * @throws SchedulerException
     *
     */
    public static void addJob(String jobName, Class cls, String cron)
            throws SchedulerException {
        Scheduler sched = gSchedulerFactory.getScheduler();

        // 用于描叙Job实现类及其他的一些静态信息，构建一个作业实例
        JobDetail jobDetail = JobBuilder.newJob(cls)
                .withIdentity(jobName, JOB_GROUP_NAME).build();

        // 构建一个触发器，规定触发的规则
        Trigger trigger = TriggerBuilder.newTrigger()// 创建一个新的TriggerBuilder来规范一个触发器
                .withIdentity(jobName, TRIGGER_GROUP_NAME)// 给触发器起一个名字和组名
                .startNow()// 立即执行
                .withSchedule(CronScheduleBuilder.cronSchedule(cron)) // 触发器的执行时间
                .build();// 产生触发器

        sched.scheduleJob(jobDetail, trigger);
        LOGGER.debug("添加任务:{},{},{}",jobName,cls,cron);
        // 启动
        if (!sched.isShutdown()) {
            sched.start();
        }
    }

    /**
     * @Description: 添加一个定时任务
     *
     * @param jobName
     *            任务名
     * @param jobGroupName
     *            任务组名
     * @param triggerName
     *            触发器名
     * @param triggerGroupName
     *            触发器组名
     * @param jobClass
     *            任务
     * @param cron
     *            时间设置，参考quartz说明文档
     */
    public static void addJob(String jobName, String jobGroupName,
                              String triggerName, String triggerGroupName, Class jobClass, String cron)
            throws SchedulerException {

        Scheduler sched = gSchedulerFactory.getScheduler();
        // 用于描叙Job实现类及其他的一些静态信息，构建一个作业实例
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroupName).build();

        // 构建一个触发器，规定触发的规则
        Trigger trigger = TriggerBuilder.newTrigger()// 创建一个新的TriggerBuilder来规范一个触发器
                .withIdentity(jobName, triggerGroupName)// 给触发器起一个名字和组名
                .startNow()// 立即执行
                .withSchedule(CronScheduleBuilder.cronSchedule(cron)) // 触发器的执行时间
                .build();// 产生触发器

        sched.scheduleJob(jobDetail, trigger);
        LOGGER.debug("添加任务:{},{},{},{},{},{}",jobName,jobGroupName,triggerName,triggerGroupName,jobClass,cron);
        // 启动
        if (!sched.isShutdown()) {
            sched.start();
        }

    }

    /**
     * @Description: 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
     *
     * @param jobName
     * @param cron
     * @throws SchedulerException
     *
     */
    public static void modifyJobTime(String jobName, String cron) throws SchedulerException {
        Scheduler sched = gSchedulerFactory.getScheduler();
        TriggerKey triggerKey = new TriggerKey(jobName, TRIGGER_GROUP_NAME);
        CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);
        if (trigger == null) {
            return;
        }
        String oldTime = trigger.getCronExpression();
        if (!oldTime.equalsIgnoreCase(cron)) {
            JobDetail jobDetail = sched.getJobDetail(new JobKey(jobName,
                    JOB_GROUP_NAME));
            Class objJobClass = jobDetail.getJobClass();
            removeJob(jobName);
            addJob(jobName, objJobClass, cron);
            LOGGER.debug("修改任务:{},{}",jobName,cron);
        }
    }

    /**
     * @Description: 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
     *
     * @param jobName
     *
     * @throws SchedulerException
     */
    public static void removeJob(String jobName) throws SchedulerException {
        Scheduler sched = gSchedulerFactory.getScheduler();

        JobKey jobKey = new JobKey(jobName, TRIGGER_GROUP_NAME);
        // 停止触发器
        sched.pauseJob(jobKey);
        sched.unscheduleJob(new TriggerKey(jobName, TRIGGER_GROUP_NAME));// 移除触发器
        sched.deleteJob(jobKey);// 删除任务
        LOGGER.debug("移除任务:{}",jobName);
    }

    /**
     * 移除任务
     *
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @throws SchedulerException
     */
    public static void removeJob(String jobName, String jobGroupName,
                                 String triggerName, String triggerGroupName)
            throws SchedulerException {
        Scheduler sched = gSchedulerFactory.getScheduler();
        JobKey jobKey = new JobKey(jobName, jobGroupName);
        // 停止触发器
        sched.pauseJob(jobKey);
        sched.unscheduleJob(new TriggerKey(jobName, triggerGroupName));// 移除触发器
        sched.deleteJob(jobKey);// 删除任务
        LOGGER.debug("移除任务:{},{},{},{},{},{}",jobName,jobGroupName,triggerName,triggerGroupName);
    }

    /**
     * 启动所有任务
     *
     * @throws SchedulerException
     */
    public static void startJobs() throws SchedulerException {
        Scheduler sched = gSchedulerFactory.getScheduler();
        sched.start();
        LOGGER.debug("启动所有任务");
    }

    /**
     * 关闭所有定时任务
     *
     * @throws SchedulerException
     *
     */
    public static void shutdownJobs() throws SchedulerException {
        Scheduler sched = gSchedulerFactory.getScheduler();
        if (!sched.isShutdown()) {
            sched.shutdown();
            LOGGER.debug("关闭所有任务");
        }
    }

    public static void addHelloJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
                             Class<HelloJob> jobClass, String cron, Integer providerId, Date startTime, Date endTime)
            throws SchedulerException {
        Scheduler scheduler = gSchedulerFactory.getScheduler();
        // 用于描叙Job实现类及其他的一些静态信息，构建一个作业实例
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroupName)
                .usingJobData("providerId", providerId)
                .build();

        // 构建一个触发器，规定触发的规则
        Trigger trigger = TriggerBuilder.newTrigger()// 创建一个新的TriggerBuilder来规范一个触发器
                .withIdentity(triggerName, triggerGroupName)// 给触发器起一个名字和组名
                .startAt(startTime)//到指定时间执行任务
                .withSchedule(CronScheduleBuilder.cronSchedule(cron)) // 触发器的执行时间
                .endAt(endTime)
                .build();// 产生触发器

        scheduler.scheduleJob(jobDetail, trigger);
        LOGGER.debug("添加任务:{},{},{},{},{},{}",jobName,jobGroupName,triggerName,triggerGroupName,jobClass,cron);
        // 启动
        if (!scheduler.isShutdown()) {
            scheduler.start();
            LOGGER.debug("启动定时任务");
        }
    }

}
