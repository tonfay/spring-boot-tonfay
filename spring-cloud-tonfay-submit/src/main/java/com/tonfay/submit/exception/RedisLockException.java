package com.tonfay.submit.exception;

public class RedisLockException extends RuntimeException{
	private static final long serialVersionUID = 132L;
	private String msg;
	
	public RedisLockException(String msg) {
		this(msg, null);
	}

	public RedisLockException(String msg, Throwable t) {
		super(t);
		this.msg = msg;
	}
	public RedisLockException(int code, String msg) {
		this.msg = msg;
	}
	
	private String getMsg() {
		return msg;
	}
	private void setMsg(String msg) {
		this.msg = msg;
	}
}
