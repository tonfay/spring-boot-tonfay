package com.tonfay.gen.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 渲染工具类
 *
 */
public class ProcessUtil {
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
}
