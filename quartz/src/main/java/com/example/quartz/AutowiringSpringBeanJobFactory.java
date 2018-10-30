package com.example.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/30 10:41
 * Modified By:
 * Description:
 */
public class AutowiringSpringBeanJobFactory  extends SpringBeanJobFactory
        implements ApplicationContextAware{

    private transient AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(final ApplicationContext context) {

        beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);

        beanFactory.autowireBean(job);

        return job;
    }
}
