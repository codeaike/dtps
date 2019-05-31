package org.dtps.scheduler.task;

/**
 * ICreateTopic<br>
 * 
 * @author zc
 * @since 2019.05.31
 * 
 * @param <T> topic实例
 * @param <R> 具体业务对应类型
 */
public interface ICreateTopic<T, R>
{
    /**
     * createTaskInstance<br>
     * 根据任务类型创建任务实例<br>
     * 
     * @author zc
     * @param type 创建任务实例
     * @return 任务实例
     */
    public abstract T createTopicInstance(R type);
}
