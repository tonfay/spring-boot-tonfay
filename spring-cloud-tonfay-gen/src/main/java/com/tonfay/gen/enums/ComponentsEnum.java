package com.tonfay.gen.enums;

public enum ComponentsEnum {
	
	MONGO("mongo"),
	CACHE("cache"),
	CLEAN_CACHE("clean_cache"),
	MQ("mq"),
	MYSQL("mysql"),
	REDIS("redis");
	
	
	String name;
	
	ComponentsEnum(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
