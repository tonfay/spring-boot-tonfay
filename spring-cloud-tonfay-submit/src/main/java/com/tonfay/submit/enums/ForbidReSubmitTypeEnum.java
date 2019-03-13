package com.tonfay.submit.enums;

/**
 * 禁止重复提交的模式
 * 1.ruid 是针对每一次请求的
 * 2.key+val 是针对相同参数请求
 */
public enum ForbidReSubmitTypeEnum {
	ALL(0,"ALL"),//0+1
	RUID(1,"RUID"),//ruid 是针对每一次请求的
	KEY(2,"KEY");//key+val 是针对相同参数请求
	
	
	private Integer index ;
	private String title;

	ForbidReSubmitTypeEnum(Integer index, String title) {
		this.index = index;
		this.title = title;
	}
}
