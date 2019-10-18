package com.tonfay.gen.rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.gofun.ms.interfaces.StorageRMI;
import com.tonfay.gen.config.MsConfigProperties;
import com.tonfay.gen.enums.ComponentsEnum;
import com.tonfay.gen.model.Components;
import com.tonfay.gen.model.ProjectInfo;
import com.tonfay.gen.model.components.CacheInfo;
import com.tonfay.gen.model.components.CleanCacheInfo;
import com.tonfay.gen.model.components.MQInfo;
import com.tonfay.gen.model.components.MongoInfo;
import com.tonfay.gen.model.components.MysqlInfo;
import com.tonfay.gen.model.components.RedisInfo;
import com.tonfay.gen.model.components.SpringCloudInfo;
import com.tonfay.gen.model.components.SpringInfo;
import com.tonfay.gen.utils.PathUtil;
import com.tonfay.gen.utils.ProcessUtil;
import com.tonfay.gen.utils.ZipUtil;

import cn.org.rapid_framework.generator.util.FileHelper;

@RestController
@RequestMapping("/gen")
public class GenMSRest {
	Logger logger = LoggerFactory.getLogger(GenMSRest.class);
	@Autowired
	MsConfigProperties msConfigProperties;
	@Autowired
	StorageRMI storageRmi;
	@RequestMapping(value = "/oss/ms", method = RequestMethod.GET)
	public void ms_oss() {
//		storageRmi.resourceUpload(arg0, arg1, arg2);
		
	}
	@RequestMapping(value = "/ms", method = RequestMethod.GET)
	public void ms(
			@RequestParam(required = true) String projectName,
			@RequestParam(required = true) String packageName,
			@RequestParam(required = true) String groupId,
			@RequestParam(required = true , defaultValue = "") String c) {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		HttpServletResponse response = servletRequestAttributes.getResponse();
		try {
			//基本信息
			Map<String,Object> root = new HashMap<String,Object>();
			root.put("project", new ProjectInfo(projectName, packageName,groupId));
			root.put("spring", new SpringInfo(new SpringCloudInfo("Edgware.SR3")));
			
			
			//组件
			root.put("components", new Components(c.split(",")));
			
			String templateDir = msConfigProperties.getTemplateDir();
			logger.info("templateDir:" + templateDir);
			String outDir = msConfigProperties.getOutDir();
			outDir = PathUtil.getOutDirPath(outDir);
			logger.info("outDir:" + outDir);
			//do
			execute(root, templateDir, outDir);
			
			//TODO 删除无用文件夹
//			PathUtil.delFile(outDir+"");
			
			
			//压缩
			ByteArrayOutputStream tmp = new ByteArrayOutputStream();
			ZipUtil.toZip(outDir, tmp, true);
			
			//response 压缩包
			response.getOutputStream().write(tmp.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 支持的组件列表
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(HttpServletResponse response) throws IOException {
		String list = JSON.toJSONString(ComponentsEnum.values());
		return list;
	}

	private void execute(Map<String, Object> root, String templateDir, String outDir) {
			File templateBaseDir = new File(templateDir);
			//获取所有的目录及文件的相对目录
			List<File> srcFiles = FileHelper.searchAllNotIgnoreFile(templateBaseDir);
			for(int i = 0; i < srcFiles.size(); i++) {
				File srcFile = (File)srcFiles.get(i);
				//获取每一个file的相对路径
				String filePath =FileHelper.getRelativePath(templateBaseDir, srcFile);
				if(filePath.length() == 0 || filePath.indexOf("$") == -1) {
					continue;
				}
				//获取路径上所有的变量,并替替换成定义的值
				String filePathVariables[] = filePath.split(Matcher.quoteReplacement("$"));
				for (String filePathVariable : filePathVariables) {
					if(filePathVariable.indexOf("{") == -1) {
						continue;
					}
					String columnName = filePathVariable.substring(filePathVariable.indexOf("{") + 1, filePathVariable.indexOf("}"));
					String[] columnNames = columnName.split("\\.");
					
					String value = null;
					//根据key获取value,ps:key可能是多级 begin
					Map<String, Object> focusMap = root;
					for(String focusColumnName : columnNames) {
						Object get = focusMap.get(focusColumnName);
						if(get instanceof String) {
							value = get == null ? null : get.toString();
							break;
						}else if(get instanceof Map){
							focusMap = ((Map) get);
						}
					}
					//根据key获取value,ps:key可能是多级 end
					
					
					//替换文件名,并去除无用字符
					filePath = filePath.replace(columnName, value);
					filePath = filePath.replace("{", "").replaceAll("}", "").replace("$", "");
//					filePath = filePath.replace(Matcher.quoteReplacement("$"+ "{" + columnName +"}"), value);
				}
				logger.info(filePath);
				
				//是文件夹先创建,不是则渲染模板
				File tmp = new File(outDir + filePath);
				out : if(!tmp.exists()) {
					if(tmp.getName().indexOf(".") > -1) {
						//属于文件
						break out;
					}
					//创建文件夹
					tmp.mkdirs();
				}
				if(tmp.isDirectory()) {
					continue;
				}
				//渲染
				ProcessUtil.process(tmp, tmp.getName(), new File(srcFile.getPath().replace(tmp.getName(), "")), root);
			}
	}
}
