package org.dtps.scheduler.task;

import java.util.concurrent.CountDownLatch;

/**
 * ILockMgr<br>
 * 
 * @author zc
 * @since 2019.05.31
 */
public interface ILockMgr
{
    /**
     * initLock<br>
     * 
     * @author zc
     */
    void initLock();
    
    /**
     * getLock<br>
     * 
     * @author zc
     * @return CountDownLatch 闭锁
     */
    CountDownLatch getLock();
    
    /**
     * releaseLock<br>
     * 
     * @author zc
     */
    void releaseLock();
}
