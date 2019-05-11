package com.tonfay.gen.controller;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tonfay.gen.enums.ComponentsEnum;

@Controller
@RequestMapping("/index")
public class IndexController {
	Logger logger = LoggerFactory.getLogger(IndexController.class);
	@RequestMapping(value = "/toIndex")
	public String index(Model model) {
		ComponentsEnum[] vs = ComponentsEnum.values();
		String c = Arrays.toString(vs);
		model.addAttribute("list", c.replaceAll("\\[", "").replaceAll("\\]", ""));
		return "index/index";
	}
}
