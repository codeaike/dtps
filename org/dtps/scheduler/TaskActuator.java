package org.dtps.scheduler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.dtps.scheduler.task.ITopic;
import org.dtps.scheduler.task.graph.Task;
import org.dtps.scheduler.task.graph.TaskGraph;
import org.dtps.scheduler.task.graph.TopicTaskGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务调度执行器
 * 
 * @author zc
 * @since 2019.05.31
 */
@SuppressWarnings("rawtypes")
public class TaskActuator<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskActuator.class);

    private static final long TIMEOUT = 300L;
    
    private static final TimeUnit TIME_UNIT_SECONDS = TimeUnit.SECONDS;
    
    private TopicTaskGraph topicTaskGraph;
    
    public TaskActuator(TopicTaskGraph topicTaskGraph) {
        this.topicTaskGraph = topicTaskGraph;
    }
    
    /**
     * executeTasks<br>
     * 多线程执行任务<br>
     * 
     * @author zc
     * @param <T>
     * @param taskTypeDepends 任务类型依赖集合
     */
    @SuppressWarnings("hiding")
    public <T> void executeTasks() 
    {
        LOGGER.info(" tasks start");
        
        if (this.topicTaskGraph == null) 
        {
            LOGGER.error("Invalid topicTaskGraph instance");
            return;
        }
        
        // 1、构建要执行的任务有向图
        @SuppressWarnings("unchecked")
        TaskGraph<ITopic, T> taskGraph = this.topicTaskGraph.getInstance();
        
        // 2、获取要执行的任务列表
        List<Task<ITopic>> toDoTasks = taskGraph.getToDoTasksAndUpdateMaxLevelTaskNum();
        int taskSize = toDoTasks.size();
        if (taskSize <= 0) 
        {
            LOGGER.error("To do tasks is empty.");
            return;
        }
        
        // 3、任务最大可并行数量
        int maxParallelNum = taskGraph.getMaxLevelTaskNumber();
        
        // 4、初始化线程池 自定义线程池
        ExecutorService executor = new ThreadPoolExecutor(maxParallelNum, maxParallelNum, 0L, TimeUnit.MILLISECONDS, 
            new LinkedBlockingQueue<Runnable>(taskSize));

        // 5、创建多线程任务执行器
        CompletionService<Void> completionService = new ExecutorCompletionService<Void>(
            executor);

        // 6、提交任务
        for (Task<ITopic> curTask : toDoTasks)
        {
            completionService.submit(new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    // 执行任务
                    runTask(curTask);
                    // 返回Void实例，这里返回null即可
                    return null;
                }
            });
        }

        // 7、等待所有任务完成
        try
        {
            for (int taskCount = 0; taskCount < taskSize; taskCount++)
            {
                completionService.take().get();
            }
        }
        catch (InterruptedException | ExecutionException e)
        {
            LOGGER.error(" Task InterruptedException | ExecutionException.", e);
        }
        catch (Exception e)
        {
            LOGGER.error(" Task exception.", e);
        }
        finally
        {
            // 8、关闭线程池
            try
            {
                executor.shutdownNow();
                executor.awaitTermination(TIMEOUT, TIME_UNIT_SECONDS);
            }
            catch (InterruptedException e)
            {
                LOGGER.error("Executor service shut down fail, Exception is: ", e);
            }
        }
        
        LOGGER.info("All  tasks end.");
    }

    // 执行单个任务
    private void runTask(Task<ITopic> curTask) 
    {
        // 1、等待parent任务执行完毕
        curTask.preExecute();
        
        // 2、执行任务主题
        try 
        {
            curTask.getTask().handleTopic();
        }
        finally
        {
            // 3、设置本任务状态为完成
            curTask.postExecute();
        }
    }
    
}
