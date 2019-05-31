package org.dtps.demo.topic.intf;

import org.dtps.demo.topic.impl.TopicApple;
import org.dtps.demo.topic.impl.TopicAppleJuice;
import org.dtps.demo.topic.impl.TopicCoconut;
import org.dtps.demo.topic.impl.TopicCoconutMixedJuice;
import org.dtps.demo.topic.impl.TopicOrange;
import org.dtps.demo.topic.impl.TopicOrangeJuice;
import org.dtps.demo.topic.type.EnumTopicType;
import org.dtps.scheduler.task.ITopic;

/**
 * TopicFactory<br>
 * 
 * @author zc
 * @since 2019.05.31
 */
public class TopicFactory
{
    /**
     * createDataHolder<br>
     * 
     * @author zc
     * @param type 类型
     * @return ITopic
     */
    public static ITopic createTopicInstance(EnumTopicType type)
    {
        if (type == EnumTopicType.APPLE) {
            return new TopicApple();
        } else if (type == EnumTopicType.ORANGE) {
            return new TopicOrange();
        } else if (type == EnumTopicType.APPLE_JUICE) {
            return new TopicAppleJuice();
        } else if (type == EnumTopicType.ORANGE_JUICE) {
            return new TopicOrangeJuice();
        } else if (type == EnumTopicType.COCONUT) {
            return new TopicCoconut();
        } else if (type == EnumTopicType.COCONUT_MIXED_JUICE) {
            return new TopicCoconutMixedJuice();
        } else {
            return null;
        }
    }

}
