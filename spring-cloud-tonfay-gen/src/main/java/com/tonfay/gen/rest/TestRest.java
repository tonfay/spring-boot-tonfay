package com.tonfay.gen.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tonfay.gen.config.MsConfigProperties;
import com.tonfay.gen.model.ProjectInfo;

@RestController
@RequestMapping("/test")
public class TestRest {
	@Autowired
	MsConfigProperties msConfigProperties;
	
	@RequestMapping(value = "/d1", method = RequestMethod.GET)
	public String d1() throws IOException {
		
		ProjectInfo projectInfo = new ProjectInfo("hhhhh", "hhhhh_artifact");
//		Component springInfo = 
//		new SpringInfo
		return "ok";
	}
}
