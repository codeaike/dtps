package org.dtps.demo.taskgraph;

import java.util.List;
import java.util.Map;

import org.dtps.demo.topic.intf.TopicFactory;
import org.dtps.demo.topic.type.EnumTopicType;
import org.dtps.scheduler.task.ITopic;
import org.dtps.scheduler.task.graph.TaskGraph;
import org.dtps.scheduler.task.graph.TopicTaskGraph;


/**
 * TopicTaskGraphImpl<br>
 * 任务有向图实现类<br>
 * 
 * @author zc
 * @since 2019.05.31
 */
public class TopicTaskGraphImpl extends TopicTaskGraph<EnumTopicType>
{

    /**
     * 构造函数<br>
     * 
     * @author zc
     * @param topicTypeDepends topic类型依赖关系
     */
    public TopicTaskGraphImpl(Map<EnumTopicType, List<EnumTopicType>> topicTypeDepends)
    {
        super(topicTypeDepends);
    }

    @Override
    public ITopic createTopicInstance(EnumTopicType type)
    {
        return TopicFactory.createTopicInstance(type);
    }

    @Override
    public TaskGraph<ITopic, EnumTopicType> getInstance()
    {
        return this;
    }

}
