package org.dtps.scheduler.task.graph;

import java.util.List;
import java.util.Map;

import org.dtps.scheduler.task.ITaskGraphBuilder;
import org.dtps.scheduler.task.ITopic;

/**
 * TopicTaskGraph<br>
 * 任务类型为ITopic的任务图
 * 
 * @author zc
 * @since 2019,05.31
 * 
 * @param <T> 具体topic对应的类型
 */
public abstract class TopicTaskGraph<T> extends TaskGraph<ITopic, T> implements ITaskGraphBuilder<ITopic, T>
{

    /**
     * 构造函数<br>
     * 
     * @author zc
     * @since 2019.05.31
     * 
     * @param topicTypeDepends
     */
    public TopicTaskGraph(Map<T, List<T>> topicTypeDepends)
    {
        super(topicTypeDepends);
    }
    
    @Override
    public abstract ITopic createTopicInstance(T type);
    
    @Override
    public abstract TaskGraph<ITopic, T> getInstance();
}
