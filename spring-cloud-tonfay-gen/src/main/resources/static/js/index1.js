//获取值
function get(){
	var arry = new Array();
	$('input[name="components"]:checked').each(function(){
		arry.push($(this).val());
	})
	return arry;
}

function gen(){
	var arrystr = get().join(',');
	var project_name = $("#project_name").val();
	if(project_name == ""){
		alert("项目名称 is require");
		return;
	}
	var project_group_id = $("#project_group_id").val();
	if(project_group_id == ""){
		alert("Group Id is require");
		return;
	}
	var project_package = $("#project_package").val();
	if(project_package == ""){
		alert("Package is require");
		return;
	}
	var project_port = $("#project_port").val();
	if(project_port == ""){
		alert("端口号 is require");
		return;
	}
	
	console.log(arrystr);
	
	var url = "/gen/ms?port="+ project_port +"&groupId="+ project_group_id +"&projectName="+ project_name + "&packageName=" + project_package+"&c=" + arrystr;
	
	
	window.open(url);
}
