package org.dtps.scheduler.task;

import org.dtps.scheduler.task.ITimeMgr;
import org.dtps.scheduler.task.graph.TaskGraph;

/**
 * ICreateTaskGraph<br>
 * 
 * @author zc
 * @since 2019.05.31
 * 
 * @param <T> 具体业务实例类
 * @param <R> 具体业务对应类型
 */
public interface ITaskGraphBuilder<T extends ITimeMgr, R>
{
    /**
     * createInstance<br>
     * 
     * @author zc
     * @since 2019.05.31
     * @return 任务依赖图实例
     */
    public TaskGraph<T, R> getInstance();
}
