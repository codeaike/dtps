package org.dtps.scheduler.task.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.apache.commons.collections.CollectionUtils;
import org.dtps.scheduler.task.ICreateTopic;
import org.dtps.scheduler.task.ITimeMgr;

/**
 * TaskGraph<br>
 * 任务有向图<br>
 * 
 * @author zc
 * @since 2019.05.31
 * 
 * @param <T> 具体业务实例类
 * @param <R> 具体业务对应类型
 */
public abstract class TaskGraph<T extends ITimeMgr, R> implements ICreateTopic<T, R>
{
    /**
     * 根节点
     */
    private Task<T> root;
    
    /**
     * "最宽"层的节点数量
     */
    private int maxLevelTaskNumber;
    
    /**
     * 构造函数<br>
     * 构建任务有向图<br>
     * 
     * @author zc
     * @param topicTypeDepends 任务类型依赖关系
     */
    public TaskGraph(Map<R, List<R>> topicTypeDepends)
    {
        if (topicTypeDepends != null)
        {
            // 构建图
            this.root = buildGraph(topicTypeDepends);
            // 初始化为任务数量
            this.maxLevelTaskNumber = topicTypeDepends.size();
        }
    }
    
    /**
     * getToDoTasks<br>
     * 广度遍历图，按层输出任务列表，除去根节点<br>
     * 
     * @author zc
     * @return 除根节点外的任务列表，层次访问任务顺序加入返回的任务列表
     */
    public final List<Task<T>> getToDoTasksAndUpdateMaxLevelTaskNum()
    {
        List<Task<T>> tasks = new ArrayList<Task<T>>();
        // 1、根节点为空 或者 子任务为空
        if (this.root == null || CollectionUtils.isEmpty(this.root.getChildren())) 
        {
            return tasks;
        }
        
        // 2、层次遍历任务，添加到列表中
        Queue<Task<T>> queue = new LinkedList<Task<T>>();
        queue.add(this.root);
        
        // 3、初始化最宽层的节点数量为1
        int curMaxLevelTaskNumber = 1;
        // 当前层节点数量
        int currentLevelTaskNumber = 1;
        // 下一层节点数量
        int nextLevelTaskNumber = 0;
        
        // 遍历队列
        while (!queue.isEmpty()) 
        {
            // 3.1、弹出队列第一个元素
            Task<T> curTask = queue.poll();
            // 更新当前层节点数(减1)
            currentLevelTaskNumber--;
            
            // 3.2、任务加入任务列表，头结点没有要执行的任务体，所以不加入任务列表
            if (curTask != this.root) 
            {
                // 如果某任务跨层依赖，可能该任务先于依赖的任务添加到列表中，为保证该任务最后添加到任务列表中，需要删除之前添加的任务记录
                delDupEleAndAddEleToList(curTask, tasks);
            }
            
            // 3.3、获取子任务列表，添加子任务到队列中
            List<Task<T>> children = curTask.getChildren();
            if (children.size() > 0)
            {
                // 添加子任务到队列中，任务在队列中，不重复添加
                for (Task<T> childTask : children)
                {
                    // 任务不在队列中，则加入队列，下层任务数量加1
                    if (!isTaskInContainer(childTask, queue))
                    {
                        queue.offer(childTask);
                        nextLevelTaskNumber += 1;
                    }
                }
            }
            
            // 3.4、当前层遍历完毕，更新当前、下层及最宽层节点数量
            if (currentLevelTaskNumber == 0) 
            {
                // 更新最宽层节点数量
                curMaxLevelTaskNumber = Math.max(curMaxLevelTaskNumber, nextLevelTaskNumber);
                // 更新当前层及下一层节点数量
                currentLevelTaskNumber = nextLevelTaskNumber;
                nextLevelTaskNumber = 0;
            }
        }
        
        // 更新Graph的最宽层节点数量
        this.maxLevelTaskNumber = curMaxLevelTaskNumber;
        
        return tasks;
    }
    
    // 删除列表中与要添加的目标元素重复的元素，并添加目标元素
    private void delDupEleAndAddEleToList(Task<T> task, List<Task<T>> taskList) 
    {
        // 1、使用迭代器删除与目标添加元素重复的元素
        Iterator<Task<T>> iter = taskList.iterator();
        while (iter.hasNext()) 
        {
            if (iter.next() == task) 
            {
                iter.remove();
            }
        }
        // 添加目标元素
        taskList.add(task);
    }
    
    // 任务是否已经存在于任务列表中
    private boolean isTaskInContainer(Task<T> task, Queue<Task<T>> taskContainer)
    {
        for (Task<T> curTask : taskContainer)
        {
            if (task.getTask() == curTask.getTask()) 
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * getMaxLevelTaskNumber<br>
     * 输出"最宽"层的节点数量<br>
     * 
     * @author zc
     * @return "最宽"层的节点数量 
     */
    public final int getMaxLevelTaskNumber()
    {
        return this.maxLevelTaskNumber;
    }
    
    /**
     * buildGraph<br>
     * 根据任务类型依赖构建任务依赖图<br>
     * 
     * @author zc
     * @param topicTypeDepends 任务类型依赖Map
     * @return Task<T> 图的根节点
     */
    private Task<T> buildGraph(Map<R, List<R>> topicTypeDepends) 
    {
        // 1、初始化需要的变量与容器
        // 1.1、节点数量
        int taskSize = topicTypeDepends.size();
        // 1.2、初始化任务实例Map
        Map<R, T> instances = new HashMap<>(taskSize);
        // 1.3、初始化根任务节点(没有挂接子任务)
        Task<T> taskRoot = buildBaseRootTask();
        // 1.4、初始化要执行的任务节点Map
        Map<R, Task<T>> tasks = new HashMap<>(taskSize);
        
        // 2、遍历任务类型依赖集合，构建依赖任务对象
        for (Entry<R, List<R>> entry : topicTypeDepends.entrySet()) 
        {
            // 2.1、获取当前任务类型
            R type = entry.getKey();
            
            // 2.2、取得当前任务实例，不存在则创建并放入容器中
            T taskInstance = getOrCreateInstance(type, instances);
            
            // 2.3、任务没有依赖则挂接到根节点下，有任务依赖则获取当前指向的任务，并完成依赖的任务的挂接
            
            // 获取当前任务类型对应的任务
            Task<T> curTask = getOrCreateTask(type, taskInstance, tasks);
            
            // 获取依赖的任务类型
            List<R> dependTypes = entry.getValue();
            
            // 2.3.1、当前任务无依赖，则把当前任务挂接到根节点即可
            if (dependTypes == null || dependTypes.isEmpty())
            {
                // 根节点下挂接第一批要执行的任务
                addChildTaskToParent(taskRoot, curTask);
            }
            // 2.3.2、当前任务有依赖，则挂接依赖的任务到当前任务下
            else 
            {
                for (R dependType : dependTypes)
                {
                    // 获取依赖的任务类型对应的T实例
                    T dependTaskInstance = getOrCreateInstance(dependType, instances);
                    // 获取依赖的任务
                    Task<T> dependTask = getOrCreateTask(dependType, dependTaskInstance, tasks);
                    // 挂接依赖的任务与当前任务
                    addChildTaskToParent(dependTask, curTask);
                }
            }
        }
        
        // 3、返回根任务节点
        return taskRoot;
    }
    
    
    // 从实例集合中取出T实例，不存在则新建实例放入Map中
    private T getOrCreateInstance(R type, Map<R, T> instances)
    {
        T taskInstance = instances.get(type);
        if (taskInstance == null)
        {
            // 没有则创建对应任务实例
            taskInstance = createTopicInstance(type);
            // 缓存到Map中
            instances.put(type, taskInstance);
        }
        return taskInstance;
    }
    
    // 从任务集合中取出任务实例，不存在则新建任务放入Map中
    private Task<T> getOrCreateTask(R type, T taskInstance, Map<R, Task<T>> tasks)
    {
        Task<T> task = tasks.get(type);
        if (task == null)
        {
            // 没有则创建对应任务
            task = new Task<T>(taskInstance);
            // 初始化锁
            task.initLock();
            // 缓存到Map中
            tasks.put(type, task);
        }
        return task;
    }
    
    // 构建图的基础根任务节点(没有挂接子任务)，标识任务的开始，根任务没有真正需要执行的任务实体
    private Task<T> buildBaseRootTask()
    {
        // 1、构建没有任务执行的空任务节点(没有挂接子任务)
        Task<T> emptyTask = new Task<T>(null);
        // 2、返回空任务节点
        return emptyTask;
    }
    
    // 添加子任务到父任务列表中, 子任务parents成员加入父任务
    private void addChildTaskToParent(Task<T> parent, Task<T> child)
    {
        // 父任务children挂接子任务
        parent.getChildren().add(child);
        // 子任务parent指向父任务
        child.getParents().add(parent);
    }
    
    /**
     * createTaskInstance<br>
     * 根据任务类型创建任务实例<br>
     * 
     * @author zc
     * @param type 创建任务实例
     * @return 任务实例
     */
    @Override
    public abstract T createTopicInstance(R type);
    
}
