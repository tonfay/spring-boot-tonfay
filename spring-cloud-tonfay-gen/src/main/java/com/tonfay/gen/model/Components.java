package com.tonfay.gen.model;

import java.util.HashMap;

import com.tonfay.gen.model.components.CacheInfo;
import com.tonfay.gen.model.components.CleanCacheInfo;
import com.tonfay.gen.model.components.MQInfo;
import com.tonfay.gen.model.components.MongoInfo;
import com.tonfay.gen.model.components.MysqlInfo;
import com.tonfay.gen.model.components.RedisInfo;
import com.tonfay.gen.model.components.SpringCloudInfo;
import com.tonfay.gen.model.components.SpringInfo;

public class Components extends HashMap<String, Object> implements com.tonfay.gen.model.IComponent {
	public Components(String...component) {
		for (String c : component) {
			c = c.trim().toLowerCase();
			if(c.equals(MongoInfo.name())) {
				this.put(c, new MongoInfo());
			}else if(c.equals(CacheInfo.name())){
				this.put(c, new CacheInfo());
			}else if(c.equals(CleanCacheInfo.name())){
				this.put(c, new CleanCacheInfo());
			}else if(c.equals(MQInfo.name())){
				this.put(c, new MQInfo());
			}else if(c.equals(MysqlInfo.name())){
				this.put(c, new MysqlInfo());
			}else if(c.equals(RedisInfo.name())){
				this.put(c, new RedisInfo());
			}else if(c.equals(SpringCloudInfo.name())){
				this.put(c, new SpringCloudInfo());
			}else if(c.equals(SpringInfo.name())){
				this.put(c, new SpringInfo());
			}
		}
	}
}
