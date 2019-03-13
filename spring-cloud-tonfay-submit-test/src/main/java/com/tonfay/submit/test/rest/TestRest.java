package com.tonfay.submit.test.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tonfay.submit.annotation.GoForbidReSubmit;
import com.tonfay.submit.enums.ForbidReSubmitTypeEnum;

@RestController
@RequestMapping("/test")
public class TestRest {
	//http://localhost:8080/test/t1?no=1234
	@GoForbidReSubmit(perFix = "TEST_",key = "no",forbidReSubmitType = ForbidReSubmitTypeEnum.KEY)
	@RequestMapping(value = "/t1", method = RequestMethod.GET)
	public String t1(String no) {
		return "t1";
	}
}
