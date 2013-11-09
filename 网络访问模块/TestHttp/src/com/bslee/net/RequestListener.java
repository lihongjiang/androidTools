package com.bslee.net;

public abstract class RequestListener<T extends ResponseBean> {
	public abstract void onStart();
	public abstract void onComplete(T bean);
}
