<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>Generate - GoFun</title>
		<script src="/static/commons/jquery-3.4.1.min.js"></script>
		<link rel="stylesheet" type="text/css" href="/static/css/index1.css"/>
		<script src="/static/js/index1.js" ></script>
	</head>
	<body>
		<div class="content mc">
			<h4>Generate GoFun Spring Cloud Application</h4>
			
			<div class="table-box mt20">
				<div class="left">
					<font class="red-font">*</font> 项目类型
				</div>
				<div class="right">
					<label class="msg-label"><input type="radio" name="recall" value="0" checked="checked">MS</label>					
					<label class="msg-label"><input type="radio" name="recall" disabled value="1">WEB(实现中)</label>
				</div>
			</div>
			
			<div class="skz" style="display:none;">
				<img src="/static/img/green.png" alt="" />
				<span></span>
				<img src="/static/img/huise.png" alt="" />
				<span></span>
				<img src="/static/img/huise.png" alt="" />
			</div>
			<div class="table-box">
				<div class="left">
					<font class="red-font">*</font> 项目名称
				</div>
				<div class="right">
					<p>gofun-[ms-]</p>
					<span><input type="text" name="" id="project_name" value="" / class="input1"></span>
					<p>-parent</p>
				</div>
			</div>
			<div class="table-box2">
				<h4><font class="red-font">*</font> Group Id</h4>
				<span style="margin-left: 27px;"><input type="text" readonly name="" id="project_group_id" placeholder="com.gofun" value="com.gofun" / class="input1"></span>
				<h4><font class="red-font">*</font> Package</h4>
				<span><input type="text" name="" id="project_package" placeholder="demo"  value="" class="input1"></span>
			</div>
			<div class="table-box">
				<div class="left">
					Description
				</div>
				<div class="right">
					<textarea name="content" class="input2" placeholder="项目备注及说明" id="message_msgTextArea"></textarea>
				</div>
			</div>
			<div class="table-box mt20">
				<div class="left">
					<font class="red-font">*</font> 端口号
				</div>
				<div class="right">
					<input type="text" name="" id="project_port" value="8080" placeholder="8080" / class="input3">
				</div>
			</div>
			<div class="table-box mt20">
				<div class="left">
					<font class="red-font">*</font> 项目组件 
				</div>
				<div class="right">
					<label class="msg-lable2"><input name="components" type="checkbox" value="mongo" />Mongo </label> 
					<label class="msg-lable2"><input name="components" type="checkbox" value="redis" />Redis </label> 
					<label class="msg-lable2"><input name="components" type="checkbox" value="mysql" />Mysql </label> 
					<label class="msg-lable2"><input name="components" type="checkbox" value="mq" />RocketMQ </label> 
					<label class="msg-lable2"><input name="components" type="checkbox" value="cache" />Cache </label>
					<label class="msg-lable2"><input name="components" type="checkbox" value="clear_cache" />ClearCache </label> 
				</div>
			</div>
			<button class="next-btn" onclick="gen()">Generate Project</button>
			<div class="table-box mt20" style="display:none;">
				<div class="left">
					<font class="red-font"></font> 日志 
				</div>
				<div class="right">
					<textarea name="content" readonly class="input2" id="message_msgTextArea"></textarea>
				</div>
			</div>
		</div>
	</body>
</html>
