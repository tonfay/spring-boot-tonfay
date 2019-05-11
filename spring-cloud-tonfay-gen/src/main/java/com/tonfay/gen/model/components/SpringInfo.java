package com.tonfay.gen.model.components;

import java.util.HashMap;

import com.tonfay.gen.model.IComponent;

public class SpringInfo extends HashMap<String, Object> implements IComponent{
	public SpringInfo() {
		
	}
	public SpringInfo(SpringCloudInfo cloud){
		this.put("version", "x");
		this.put("cloud", cloud);
	}
	
	public static String name() {
		return "spring";
	}
}
