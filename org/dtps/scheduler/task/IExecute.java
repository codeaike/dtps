package org.dtps.scheduler.task;

/**
 * IExecute<br>
 * 
 * @author zc
 * @since 2019.05.31
 */
public interface IExecute
{
    /**
     * preExecute<br>
     * 执行前准备<br>
     * 
     * @author zc
     */
    void preExecute();
    
    /**
     * postExecute<br>
     * 执行后处理<br>
     * 
     * @author zc
     */
    void postExecute();
}
