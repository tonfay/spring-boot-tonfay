//获取值
function getinput(){
	alert($('#tagsinputval').val());
	return;
}

//赋值
function setinput(){
	$("#tagsinputval").tagsinput('add','mongo,redis,mq,cache,clear_cache,mysql');
}
function removeAll(){
	//移除所有标签
	$("#tagsinputval").tagsinput('removeAll');
}
function gen(){
	var project_name = $("#project_name").val();
	if(project_name == ""){
		alert("name is require");
		return;
	}
	var project_artifact = $("#project_artifact").val();
	if(project_artifact == ""){
		alert("artifact is require");
		return;
	}
	window.open("/gen/ms?projectName="+ project_name + "&projectArtifact=" + project_artifact+"&c="+$('#tagsinputval').val());
}