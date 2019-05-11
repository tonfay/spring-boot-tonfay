//package com.tonfay.gen;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//
//import cn.org.rapid_framework.generator.util.FileHelper;
//import freemarker.template.Configuration;
//import freemarker.template.DefaultObjectWrapper;
//import freemarker.template.Template;
//import freemarker.template.TemplateException;
//
//public class TestApplicationBeifen {
//	public static void main(String[] args) {
//		//项目基础信息
//		Map<String,Object> project = new HashMap<String,Object>();
//		project.put("name", "hhhhhhhhh");
//		project.put("artifact", "h_artifact");
//		
//		//spring版本
//		Map<String,Object> spring = new HashMap<String,Object>();
//		spring.put("version", "x");
//		
//		//springcloud版本
//		Map<String,Object> cloud = new HashMap<String,Object>();
//		cloud.put("version", "Edgware.SR3");
//		spring.put("cloud", cloud);
//		
//		Map<String,Object> root = new HashMap<String,Object>();
//		root.put("project", project);
//		root.put("spring", spring);
//		
//		//组件集合
//		Map<String,Object> components = new HashMap<String,Object>();
//		
//		Map<String, Object> mysql = new HashMap<String,Object>();
//		Map<String, Object> cache = new HashMap<String,Object>();
//		Map<String, Object> clear_cache = new HashMap<String,Object>();//清理缓存
//		Map<String, Object> mongo = new HashMap<String,Object>();
//		Map<String, Object> redis = new HashMap<String,Object>();
//		Map<String, Object> es = new HashMap<String,Object>();
//		Map<String, Object> mq = new HashMap<String,Object>();
//		
//		components.put("mongo", mongo);
//		components.put("redis", redis);
//		components.put("mq", mq);
//		components.put("cache", cache);
//		components.put("clear_cache", clear_cache);
//		
////		components.put("mysql", mysql);
////		components.put("es", es);
//		
//		root.put("components", components);
//		
//		String templateDir = "D:/workspace_ms/spring-boot-tonfay/spring-cloud-tonfay-gen/template/";
//		String outDir = "D:/workspace_ms/spring-boot-tonfay/spring-cloud-tonfay-gen/testrepid/";
//		String uuid = java.util.UUID.randomUUID().toString();
//		outDir = outDir + uuid + "/";
//		File templateBaseDir = new File(templateDir);
//		try {
//			//获取所有的目录及文件的相对目录
//			List<File> srcFiles = FileHelper.searchAllNotIgnoreFile(templateBaseDir);
//			for(int i = 0; i < srcFiles.size(); i++) {
//				File srcFile = (File)srcFiles.get(i);
//				//获取每一个file的相对路径
//				String filePath =FileHelper.getRelativePath(templateBaseDir, srcFile);
//				if(filePath.length() == 0 || filePath.indexOf("$") == -1) {
//					continue;
//				}
//				//获取路径上所有的变量,并替替换成定义的值
//				String filePathVariables[] = filePath.split(Matcher.quoteReplacement("$"));
//				for (String filePathVariable : filePathVariables) {
//					if(filePathVariable.indexOf("{") == -1) {
//						continue;
//					}
//					String columnName = filePathVariable.substring(filePathVariable.indexOf("{") + 1, filePathVariable.indexOf("}"));
//					String[] columnNames = columnName.split("\\.");
//					
//					String value = null;
//					//根据key获取value,ps:key可能是多级 begin
//					Map<String, Object> focusMap = root;
//					for(String focusColumnName : columnNames) {
//						Object get = focusMap.get(focusColumnName);
//						if(get instanceof String) {
//							value = get == null ? null : get.toString();
//							break;
//						}else if(get instanceof Map) {
//							focusMap = ((Map) get);
//						}
//					}
//					//根据key获取value,ps:key可能是多级 end
//					
//					
//					//替换文件名,并去除无用字符
//					filePath = filePath.replace(columnName, value);
//					filePath = filePath.replace("{", "").replaceAll("}", "").replace("$", "");
////					filePath = filePath.replace(Matcher.quoteReplacement("$"+ "{" + columnName +"}"), value);
//				}
//				System.out.println(filePath);
//				
//				//是文件夹先创建,不是则渲染模板
//				File tmp = new File(outDir + filePath);
//				out : if(!tmp.exists()) {
//					if(tmp.getName().indexOf(".") > -1) {
//						//属于文件
//						break out;
//					}
//					//创建文件夹
//					tmp.mkdirs();
//				}
//				if(tmp.isDirectory()) {
//					continue;
//				}
//				//渲染
//				process(tmp, tmp.getName(), new File(srcFile.getPath().replace(tmp.getName(), "")), root);
//			}
//			
//			//打压缩包
//			
//			//response 压缩包
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	/**
//	 * 
//	 * @param fileName				要渲染的文件名称
//	 * @param templateBaseDir		要渲染的文件所在的目录
//	 * @param root					渲染所需要的参数列表
//	 */
//	public static void process(File file,String fileName,File dir,Map<String,Object> root) {
//		try {
//			Configuration cfg = new Configuration();
//			cfg.setDirectoryForTemplateLoading(dir);
//			cfg.setObjectWrapper(new DefaultObjectWrapper());
//			Template temp = cfg.getTemplate(file.getName());
//			FileWriter fw = new FileWriter(file);
//			BufferedWriter bw = new BufferedWriter(fw);
//			try {
//				temp.process(root, bw);
//			} catch (TemplateException e) {
//				e.printStackTrace();
//			}
//			bw.flush();
//			fw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	//初始化组件列表
//	
//	//组装基本参数(模板目录/产出目录等)
//	
//	//初始化模板列表(mybatis/web/aop/mysql/的实现等)
//	
//	//
//	
//}
