package com.bslee.net;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import com.bslee.User.UserAction;
import com.bslee.User.UserLoginRequestParam;
import com.bslee.User.UserLoginResponseBean;
import com.bslee.util.AbAppUtil;

public class AsyncTaskManager {
	private ExecutorService mPool;
	private static AsyncTaskManager imageDownload;
	private UserAction userAction=null;   
	private AsyncTaskManager(int nThreads) {
		mPool = Executors.newFixedThreadPool(5);
		userAction=new UserAction();
	}
	/**
	 * 单例构造图片下载器.
	 *
	 * @return single instance of AbImageDownloadPool
	 */
    public static AsyncTaskManager getInstance() { 
        
		if (imageDownload == null) { 
        	//得到cpu核心数目
        	int nThreads = AbAppUtil.getNumCores();
        	//同时执行的线程数目
        	imageDownload = new AsyncTaskManager(nThreads*5); 
        } 
        return imageDownload;
    } 
    
	public void getVisitor(UserLoginRequestParam param,
			RequestListener<UserLoginResponseBean> requestListener) {
		userAction.asyncGetVisitor(mPool, param, requestListener);
	}
}
