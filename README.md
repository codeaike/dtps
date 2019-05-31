# 有依赖的任务的并行调度器(Parallel Scheduler for Dependent Tasks)

## 包结构
- scheduler为任务调度代码
- demo为使用示例

## scheduler-调度器实现思路
- 根据标识Topic(要执行的真正任务主题，接口为ITopic)的枚举类型T，构建任务依赖有向图TaskGraph(每个Task节点持有锁，来标识本Task是否完成，执行中为锁定状态，执行完毕，释放锁)，虚拟TaskGraph的起始节点为root，标识任务的开始(无真正要执行的任务主题)
- 广度遍历TaskGraph，类似二叉树分层遍历，把任务列表放入一个有序列表List<Task<T>>，最宽层的任务数量放入maxLevelTaskNumber
- 任务执行器TaskActuator使用线程池进行任务调度，线程池核心线程数量为maxLevelTaskNumber，任务按照之前从上到下分层遍历的顺序加入线程池，保证没有依赖的任务率先执行
  
## demo-调度器使用思路
- 定义Topic枚举类型EnumTopicType，及对应的Topic(业务)抽象类AbstractTopic(实现ITopic)、实现类、工厂。
- 定义EnumTopicType的依赖关系，构建Map<EnumTopicType,List<EnumTopicType>>依赖Map
- 使用者TaskCaller根据以上构建的Map实例化TaskActuator<EnumTopicType>，调用任务执行接口executeTasks即可
  
## 优点
- 简单，稳定，可用性高，性能较高，资源利用较少，任务管理与业务耦合低，层次清晰
- 新加入新的业务类型时，只需要依赖Map中添加对应依赖，并添加对应实现类，丰富工厂方法即可，易扩展。

## 缺陷及演进
- 任务管理对象Task持有业务ITopic，存在一定程度的耦合，任务状态在Task内部维护，没有一个统一的管理机制，无法达到调度最优
- 任务之间的依赖，没有进行合理的stage等状态分析与切分，只是简单的父子依赖任务管理，无法达到调度最优
- 线程池的核心线程数为了达到最大执行效率，选取了可能并发任务的最大值，存在一定程度的线程浪费

## 适用场景
- 对资源与调度要求没有太苛刻的，有依赖的任务并发场景
- 代码轻量级，一般场景的有依赖的任务并发
