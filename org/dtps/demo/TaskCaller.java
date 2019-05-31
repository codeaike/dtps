package org.dtps.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dtps.demo.taskgraph.TopicTaskGraphImpl;
import org.dtps.demo.topic.type.EnumTopicType;
import org.dtps.scheduler.TaskActuator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * 任务调度测试类
 * 
 * @author zc
 * @since 2019.05.31
 */
public class TaskCaller 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCaller.class);

    /**
     * 要执行的任务类型及依赖关系
     */
    private static final Map<EnumTopicType,List<EnumTopicType>> TOPIC_TYPES_DEPENDS = new HashMap<>();

    static 
    {
        TOPIC_TYPES_DEPENDS.put(EnumTopicType.APPLE, null);
        TOPIC_TYPES_DEPENDS.put(EnumTopicType.ORANGE, null);
        TOPIC_TYPES_DEPENDS.put(EnumTopicType.COCONUT, null);
        TOPIC_TYPES_DEPENDS.put(EnumTopicType.APPLE_JUICE, ImmutableList.of(EnumTopicType.APPLE));
        TOPIC_TYPES_DEPENDS.put(EnumTopicType.ORANGE_JUICE, ImmutableList.of(EnumTopicType.ORANGE));
        TOPIC_TYPES_DEPENDS.put(EnumTopicType.COCONUT_MIXED_JUICE, ImmutableList.of(EnumTopicType.COCONUT, EnumTopicType.APPLE_JUICE, EnumTopicType.ORANGE_JUICE));
    }

    
    /**
     * process
     * 
     * @author zc
     */
    public void process()
    {
        // 1、开始
        LOGGER.info("task start...");
        // 2、触发任务计算
        new TaskActuator<EnumTopicType>(new TopicTaskGraphImpl(getTopicTypeDepends())).executeTasks();
        // 3、结束
        LOGGER.info("task end...");
    }
    
    private Map<EnumTopicType, List<EnumTopicType>> getTopicTypeDepends()
    {
        return TOPIC_TYPES_DEPENDS;
    }
    
}
