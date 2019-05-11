<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>GEN - GOFUN</title>
	<!-- 最新版本的 Bootstrap 核心 CSS 文件 -->
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	
	<!-- 可选的 Bootstrap 主题文件（一般不用引入） -->
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
	
	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
	
	<script src="https://code.jquery.com/jquery-3.4.1.min.js" ></script>
	
	<script src="http://cdn.bootcss.com/bootstrap-tagsinput/0.8.0/bootstrap-tagsinput.min.js"></script>
	<link href="http://cdn.bootcss.com/bootstrap-tagsinput/0.8.0/bootstrap-tagsinput.css" rel="stylesheet">

	<link href="/static/index/index.css" rel="stylesheet">
	<script src="/static/index/index.js" ></script>
	


</head>
<body>
		<div class="container">
			<div class="row" style="padding: 20px 0">
				<h2>THIS IS GEN</h2>
			</div>
			<div class="row form-group">
				<!-- <label class="control-label col-lg-1" for="name">项目名称</label> -->
				<div class="col-lg-5 col-md-6">
					<div class="input-group">
						<span class="input-group-addon">Name</span>
						<input class="form-control" id="project_name" type="text">
					</div>
				</div>
			</div>
			<div class="row form-group">
				<!-- <label class="control-label col-lg-1" for="name">项目名称</label> -->
				<div class="col-lg-5 col-md-6">
					<div class="input-group">
						<span class="input-group-addon">Artifact</span>
						<input class="form-control" id="project_artifact" type="text">
					</div>
				</div>
			</div>
			<div class="row form-group">
				<div class="col-lg-5 col-md-6">
					<div class="input-group" style="width:100%">
						<div class="tagsinput-primary">
							<span class="input-group-addon">dependencies</span>
							<input name="tagsinput" id="tagsinputval" class="tagsinput" data-role="tagsinput" value="" placeholder="${list}" size="20"/>
							<button class="bootstrap-tagsinput-btn" onclick="removeAll()">重置</button>
							<button class="bootstrap-tagsinput-btn" onclick="setinput()">ALL</button>
							<button class="bootstrap-tagsinput-btn" onclick="getinput()" style="display:none;">获取输入的值</button>
							
							<button type="button" class="bootstrap-tagsinput-success-btn" onclick="gen()">Generate Project</button>
						</div>
					</div>
				</div>
			</div>
		</div>
</body>
</html>