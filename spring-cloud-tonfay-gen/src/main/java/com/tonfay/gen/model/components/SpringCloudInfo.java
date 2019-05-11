package com.tonfay.gen.model.components;

import java.util.HashMap;

import com.tonfay.gen.model.IComponent;

public class SpringCloudInfo extends HashMap<String, Object> implements IComponent{
	public SpringCloudInfo() {
		
	}
	
	//默认版本
	static String defaultVersion = "Edgware.SR3";
	public SpringCloudInfo(String version){
		this.put("version", version == null ? defaultVersion : version);
	}
	
	public static String name() {
		return "cloud";
	}
	
}
