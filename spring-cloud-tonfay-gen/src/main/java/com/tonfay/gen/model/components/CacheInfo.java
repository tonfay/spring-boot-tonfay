package com.tonfay.gen.model.components;

import java.util.HashMap;

import com.tonfay.gen.model.IComponent;


public class CacheInfo extends HashMap<String, Object> implements IComponent{
	public static String name() {
		return "cache";
	}
}
