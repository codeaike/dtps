package org.dtps.demo.topic.intf;

import java.util.concurrent.TimeUnit;

import org.dtps.scheduler.task.ITopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractTopic<br>
 *
 * @author zc
 * @since 2019.05.31
 */
public abstract class AbstractTopic implements ITopic
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTopic.class);
    
    /**
     * 任务完成需要的最大毫秒数
     */
    private static final long TASK_COMPLETE_MAX_MILLISECONDS = 300000L;


    /**
     * handleTopic<br>
     * 开始处理主题<br>
     *
     * @author zc
     * @return 是否成功
     */
    @Override
    public final void handleTopic()
    {
        LOGGER.info("begin...");
        
        // test
        LOGGER.info("Current topic: ", this.getClass().toString());
        
        System.out.println("Current topic: " + this.getClass().toString());
        
        try
        {
            TimeUnit.SECONDS.sleep(2);
        }
        catch (InterruptedException e)
        {
            LOGGER.error("InterruptedException: ", e);
        }
        
        
        LOGGER.info("end...");

    }

    @Override
    public long requiredMilliSeconds()
    {
        return TASK_COMPLETE_MAX_MILLISECONDS;
    }

}

