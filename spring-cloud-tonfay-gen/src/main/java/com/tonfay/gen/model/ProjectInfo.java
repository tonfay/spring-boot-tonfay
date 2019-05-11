package com.tonfay.gen.model;

import java.util.HashMap;

/**
 * 项目基础信息
 */
public class ProjectInfo extends HashMap<String, Object>{
	public ProjectInfo(String name,String artifact) {
		this.put("name", name);
		this.put("artifact", artifact);
	}
}

