package com.tonfay.gen.model.components;

import java.util.HashMap;

import com.tonfay.gen.enums.ComponentsEnum;
import com.tonfay.gen.model.IComponent;

public class MongoInfo extends HashMap<String, Object> implements IComponent{
	public static String name() {
		return ComponentsEnum.MONGO.getName();
	}
	
}
