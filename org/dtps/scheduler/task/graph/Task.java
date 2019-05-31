package org.dtps.scheduler.task.graph;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.dtps.scheduler.task.IExecute;
import org.dtps.scheduler.task.ILockMgr;
import org.dtps.scheduler.task.ITimeMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * Task<br>
 * 任务节点<br>
 * 
 * @author z00367645
 * 
 * @param <T> 具体业务(topic)
 */
public class Task<T extends ITimeMgr> implements IExecute, ILockMgr
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);
    
    private static final TimeUnit TIME_UNIT_MILLISECONDS = TimeUnit.MILLISECONDS;
    
    /**
     * 当前任务
     */
    @Getter
    @Setter
    private T task;
    
    /**
     * 依赖的父任务列表
     */
    @Getter
    private List<Task<T>> parents = new LinkedList<>();
    
    /**
     * 依赖此任务的任务列表
     */
    @Getter
    private List<Task<T>> children = new LinkedList<>();
    
    /**
     * 当前任务的锁
     */
    private CountDownLatch countDownLatch;
    
    /**
     * 构造函数<br>
     * 
     * @author zc
     * @param task 具体任务
     */
    public Task(T task) 
    {
        // 初始化任务
        this.task = task;
    }

    @Override
    public void initLock()
    {
        this.countDownLatch = new CountDownLatch(1);
    }

    @Override
    public CountDownLatch getLock()
    {
        return this.countDownLatch;
    }
    
    @Override
    public void releaseLock()
    {
        if (this.countDownLatch != null)
        {
            this.countDownLatch.countDown();
        }
    }

    @Override
    public void preExecute()
    {
        // 执行任务前需要等待依赖的父任务全部执行完毕
        for (Task<T> parent : parents) 
        {
            // 获取父任务的锁
            CountDownLatch latch = parent.getLock();
            if (latch == null)
            {
                continue;
            }
            // 存在锁，需要等待锁释放
            try
            {
                // 等待父任务执行完毕, 超时时间由业务决定
                latch.await(task.requiredMilliSeconds(), TIME_UNIT_MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                LOGGER.error("an interruption occurred.", e);
            }
        }
    }

    @Override
    public void postExecute()
    {
        // 执行完毕后释放锁
        this.releaseLock();
    }

}
