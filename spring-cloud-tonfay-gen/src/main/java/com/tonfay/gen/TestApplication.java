package com.tonfay.gen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
@SpringBootApplication
@ComponentScan(basePackages = {"com"})
public class TestApplication {
	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}
	
	
	/**
	 * 
	 * @param fileName				要渲染的文件名称
	 * @param templateBaseDir		要渲染的文件所在的目录
	 * @param root					渲染所需要的参数列表
	 */
	public static void process(File file,String fileName,File dir,Map<String,Object> root) {
		try {
			Configuration cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(dir);
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			Template temp = cfg.getTemplate(file.getName());
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			try {
				temp.process(root, bw);
			} catch (TemplateException e) {
				e.printStackTrace();
			}
			bw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//初始化组件列表
	
	//组装基本参数(模板目录/产出目录等)
	
	//初始化模板列表(mybatis/web/aop/mysql/的实现等)
	
	//
	
}
