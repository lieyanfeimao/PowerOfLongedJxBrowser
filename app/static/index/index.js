//*************************** 首页导航栏管理 ***********************************//
//添加导航
function addNav(){
	$("#nav_id").val("");
	$("#nav_name").val("");
	$("#nav_url").val("");
	$('#editNav').dialog({
		title: '添加导航',
		width: '300px',
		height: '150px',
		closed: false,
		cache: false,
		modal: true,
		buttons:[{
			text:'&nbsp;保存&nbsp;',
			handler:function(){
				var nav={};
				nav["id"]=NAVINDEX++;
				nav["name"]=$("#nav_name").val();
				nav["url"]=$("#nav_url").val();
				navConfig[navConfig.length]=nav;
				saveNavData();
				infoMsg("添加导航成功！");
				$('#editNav').dialog("close");
				refreshNavList();
			}
		},{
			text:'&nbsp;取消&nbsp;',
			handler:function(){
				$('#editNav').dialog("close");
			}
		}]
	});
}
//编辑导航
function editNavById(id){
	var nav;
	for(var i=0;i<navConfig.length;i++){
		if(navConfig[i].id==id){
			nav=navConfig[i];
			break;
		}
	}
	if(!nav){
		infoMsg("未找到该导航数据！");
		return;
	}
	$("#nav_id").val(nav.id);
	$("#nav_name").val(nav.name);
	$("#nav_url").val(nav.url);
	
	$('#editNav').dialog({
		title: '编辑导航',
		width: '300px',
		height: '150px',
		closed: false,
		cache: false,
		modal: true,
		buttons:[{
			text:'&nbsp;保存&nbsp;',
			handler:function(){
				nav["id"]=$("#nav_id").val();
				nav["name"]=$("#nav_name").val();
				nav["url"]=$("#nav_url").val();
				saveNavData();
				infoMsg("修改成功！");
				$('#editNav').dialog("close");
				updateNavList();
			}
		},{
			text:'&nbsp;取消&nbsp;',
			handler:function(){
				$('#pkgApp').dialog("close");
			}
		}]
	});
}

function delNavById(id){
	var nav;
	var ind=-1;
	for(var i=0;i<navConfig.length;i++){
		if(navConfig[i].id==id){
			nav=navConfig[i];
			ind=i;
			break;
		}
	}
	if(!nav){
		infoMsg("未找到该导航数据！");
		return;
	}
	$.messager.confirm(infoTitle,'你真的要删除&nbsp;<span style="color:red;">'+nav.name+'</span>&nbsp;吗?',function(r){
		if(r){
			navConfig.splice(ind,1);
			saveNavData();
			infoMsg("删除成功！");
			updateNavList();
		}
	});
}

/**刷新导航列表**/
function refreshNavList(){
	var $str='';
	for(var i=0;i<navConfig.length;i++){
		$str+='<li><a href="'+navConfig[i].url+'" target="_blank" title="'+navConfig[i].url+'">'+navConfig[i].name+'</a></li>';
	}
	$("#navHtml").html($str);
}

var nav_updflag=false;
function editNavList(){
	if(!nav_updflag){
		updateNavList();
		nav_updflag=true;
	}else{
		refreshNavList();
		nav_updflag=false;
	}
}
/**操作导航列表**/
function updateNavList(){
	var $str='';
	for(var i=0;i<navConfig.length;i++){
		$str+='<li><a href="'+navConfig[i].url+'" target="_blank" title="'+navConfig[i].url+'">'+navConfig[i].name+'</a>';
		$str+='<img src="static/image/edit.png" style="width:12px;height:12px;" class="opbtn" onclick="editNavById('+navConfig[i].id+');" title="编辑"/>';
		$str+='<img src="static/image/delete.png" style="width:12px;height:12px;" class="opbtn" onclick="delNavById('+navConfig[i].id+');" title="删除"/>';
		$str+='</li>';
	}
	$("#navHtml").html($str);
}

//保存所有导航数据
function saveNavData(){
	var data = JSON.stringify(navConfig);
	writeAppData(appId,NAVFILE,data);
}
//读取保存的所有导航数据
function readNavData(){
	return readAppData(appId,NAVFILE);
}

//*************************** 首页导航栏管理结束 ***********************************//

//*************************** 首页脚本管理 ***********************************//
//保存所有脚本配置
function saveAllSc(){
	if(scConfig.length==0){
		infoMsg("保存成功！");
		return;
	}
	batchMode=true;
	try{
		//获取所有正在编辑的列
		for(var i=0;i<scConfig.length;i++){
			$('#scConfigList').datagrid('endEdit', i);
		}
	}catch(e){
	}
	batchMode=false;
	saveScData();
	infoMsg("保存成功!");
}

//编辑脚本配置
function editScRow(target){
	var rowIndex=getRowIndex(target);
	var rows = $('#scConfigList').datagrid('getRows');//获得所有行
	var row = rows[rowIndex];
	$("#scId").val(row.id);
	for (let key in row) {
		if(key=="file"){
			$("#sc_file").html(row[key]);
		}else{
			$("#sc_"+key).val(row[key]);
		}
	}
	
	$('#editSc').dialog({
		title: '脚本信息编辑',
		width: 'auto',
		height: 'auto',
		closed: false,
		cache: false,
		modal: true,
		buttons:[{
			text:'&nbsp;保存&nbsp;',
			handler:function(){
				var id=$("#scId").val();
				var name=$("#sc_name").val();
				var url=$("#sc_url").val();
				var type=$("#sc_type").val();

				for(var i=0;i<scConfig.length;i++){
					if(scConfig[i].id==id){
						scConfig[i].name=name;
						scConfig[i].url=url;
						scConfig[i].type=type;
						
						scConfig[i]["edit"]=false;
						//更新数据
						$("#scConfigList").datagrid('updateRow',{
							index: rowIndex,
							row:{data:scConfig[i]}
						});
						break;
					}
				}
				
				saveScData();
				infoMsg("保存成功！");
				$('#editSc').dialog("close");
			}
		},{
			text:'&nbsp;取消&nbsp;',
			handler:function(){
				$('#editSc').dialog("close");
			}
		}]
	});
}

//保存脚本配置记录行
function saveScRow(target){
	$('#scConfigList').datagrid('endEdit', getRowIndex(target));
}
//取消脚本配置记录行
function cancelScRow(target){
	$('#scConfigList').datagrid('cancelEdit', getRowIndex(target));
}

//保存所有脚本数据
function saveScData(){
	var data = JSON.stringify(scConfig);
	writeAppData(appId,SCFILE,data);
	execJava("Index.loadScpFiles");
}
//读取保存的所有脚本数据
function readScData(){
	return readAppData(appId,SCFILE);
}

//*************************** 首页脚本管理结束 ***********************************//

//*************************** 首页应用管理 ***********************************//
//刷新应用列表
function refreshAppList(){
	var msg=execJava("Index.appList");
	if(msg.suc){
		var r=new Array();
		if(msg.data) r=JSON.parse(msg.data);
		var $str="";
		for(var i=0;i<r.length;i++){
			$str+='<li title="'+r[i].name+'" onclick="openApp(\''+r[i].id+'\',\''+r[i].path.replace(/\\/g,"\\\\")+'\')">';
			var icon=r[i].icon;
			if(icon==""){
				icon="logo.png";
			}
			$str+='<img src="'+icon+'">';
			$str+='<div class="txt">'+r[i].name+'</div>';
			$str+='</li>';
		}
		$("#appHtml").html($str);
		
		$('#appList').datagrid({
			data:r
		});
	}
}
/**打开指定的APP*/
function openApp(id,path){
	if(path) window.open(path);
	else infoMsg("此应用不支持打开");
}
//删除指定应用
function deleteAppListRow(target){
	var rowIndex=getRowIndex(target);
	var rows = $('#appList').datagrid('getRows');
	var row=rows[rowIndex];
	
	$.messager.confirm(infoTitle,'你真的要删除应用&nbsp;<span style="color:red;">'+row.name+'</span>&nbsp;吗?(为避免误删，应用文件不会被删除，请手动删除或自行开发此功能！)',function(r){
		if (r){
			var msg=execJava("Index.deleteApp",{id:row.id});
			if(msg.suc){
				$('#appList').datagrid('deleteRow', rowIndex);
			}
			infoMsg(msg.msg);
			refreshAppList();
		}
	});
}

//保存
function saveFileListRow(target){
	var rowIndex=getRowIndex(target);
	$('#fileList').datagrid('endEdit', rowIndex);
}
//取消
function cancelFileListRow(target){
	$('#fileList').datagrid('cancelEdit', getRowIndex(target));
}
//编辑
//function editFileListRow(target){
	//打开编辑界面
//}
//删除选中数据
function deleteFileList(){
	//获取选中的数据
	var list=$('#fileList').datagrid('getSelections');
	//弹窗询问是否删除
	if(list.length==0){
		infoMsg("请选择要删除的数据!");
		return;
	}
	$.messager.confirm(infoTitle,'你真的要删除选中的文件记录吗?',function(r){
		if (r){
			for(var i=0;i<list.length;i++){
				var index=$('#fileList').datagrid('getRowIndex',list[i]);
				$('#fileList').datagrid('deleteRow', index);
			}
			infoMsg("删除成功！");
		}
	});
}
//删除记录行
function deleteFileListRow(target){
	$.messager.confirm(infoTitle,'你真的要删除这条文件配置记录吗?',function(r){
		if (r){
			var rowIndex=getRowIndex(target);
			$('#fileList').datagrid('deleteRow', rowIndex);
			infoMsg("删除成功！");
		}
	});
}

//保存所有文件配置
function saveAllAppConfig(){
	if(pkgFiles.length==0){
		infoMsg("保存成功！");
		return;
	}
	batchModel=true;
	try{
		//获取所有正在编辑的列
		for(var i=0;i<pkgFiles.length;i++){
			$('#fileList').datagrid('endEdit', i);
		}
	}catch(e){
	}
	
	batchModel=false;
	infoMsg("保存成功!");
}

//更新文件路径
function changeFilePath(rowIndex){
	var row=pkgFiles[rowIndex];
	if(!row){
		return;
	}
	var model="file";
	var filePath=row.fileName;
	if(row.isFolder){
		model="save";
	}
	var r=chooseFile(model,"选择文件",filePath);
	if(r.length>0){
		var data=pkgFiles[rowIndex];
		var fn=r[0];
		if(data.isFolder){
			var i1=fn.lastIndexOf("\\");
			if(i1!=-1){
				fn=fn.substr(0,i1);
			}
		}
		data["fileName"]=fn;
		
		$('#fileList').datagrid('updateRow',{
			index: rowIndex,
			row:{data:data}
		});
	}
}

//添加目录
function addAppFolder(){
	var r=chooseFile("save","选择目录",COMMON_FILEPATH);
	for(var i=0;i<r.length;i++){
		var flag=false;
		for(var j=0;j<pkgFiles.length;j++){
			if(r[i]==pkgFiles[j].fileName){
				flag=true;
				break;
			}
		}
		if(!flag){
			var i1=r[i].lastIndexOf("\\");
			var fn=r[i];
			if(i1!=-1){
				fn=fn.substr(0,i1);
			}
			pkgFiles[pkgFiles.length]={fileName:fn,exportPath:"",isFolder:true};
		}
	}
	
	if(r.length>0){//设置文件夹路径
		COMMON_FILEPATH=r[0];
	}
	$('#fileList').datagrid({
		data:pkgFiles
	});
}
//添加文件
function addAppFiles(){
	var r=chooseFile("mfile","选择文件-多选",COMMON_FILEPATH);
	for(var i=0;i<r.length;i++){
		var flag=false;
		for(var j=0;j<pkgFiles.length;j++){
			if(r[i]==pkgFiles[j].fileName){
				flag=true;
				break;
			}
		}
		if(!flag){
			pkgFiles[pkgFiles.length]={fileName:r[i],exportPath:"",isFolder:false};
		}
	}
	if(r.length>0){//设置文件夹路径
		COMMON_FILEPATH=r[0];
	}
	$('#fileList').datagrid({
		data:pkgFiles
	});
}
//应用安装
function appInstall(){
	var r=chooseFile("file","选择文件",COMMON_FILEPATH);
	if(r.length>0){//设置文件夹路径
		COMMON_FILEPATH=r[0];
		//调用APP安装方法
		var msg=execJava("Index.installApp",{path:r[0]});
		if(msg.suc){
			refreshAppList();
		}
		infoMsg(msg.msg);
	}
}
//应用打包
function appPkg(){
	$("#app_id").val("helpdoc");
	$("#app_name").val("JCEF开发文档");
	$("#app_version").val("V1.0");
	$("#app_versionNum").val("1");
	$("#app_icon").val("{appPath}/icon.png");
	$("#app_path").val("{appPath}/index.html");
	$("#app_scannerpkg").val("");
	$("#app_libjar").val("");
	$("#app_appPkg").val("d:\\helpdoc.zip");
	
	$('#scConfigList1').datagrid({
		data:scConfig
	});
	$('#pkgApp').dialog({
		title: '应用打包',
		width: '80%',
		height: '80%',
		closed: false,
		cache: false,
		modal: true,
		buttons:[{
			text:'&nbsp;打包&nbsp;',
			handler:function(){
				var pkg={};
				//获取appConfig信息
				var appConfig={};
				appConfig.id=$("#app_id").val();
				appConfig.name=$("#app_name").val();
				appConfig.version=$("#app_version").val();
				appConfig.versionNum=$("#app_versionNum").val();
				appConfig.icon=$("#app_icon").val();
				appConfig.path=$("#app_path").val();
				appConfig.scannerpkg=$("#app_scannerpkg").val();
				appConfig.libjar=$("#app_libjar").val();
				//appConfig.appPkg=$("#app_appPkg").val();
				
				pkg["appConfig"]=appConfig;
				pkg["appPkg"]=$("#app_appPkg").val();
				//获取要压缩的文件，去除解压路径为空的目录
				for(var i=0;i<pkgFiles.length;i++){
					if(pkgFiles[i].exportPath==""){
						pkgFiles.splice(i,1);
						i--;
					}
				}
				
				pkg["fileConfigs"]=pkgFiles;
				
				//获取要添加的脚本
				//获取选中的数据
				var list=$('#scConfigList1').datagrid('getSelections');
				var nscp=new Array();
				for(var i=0;i<list.length;i++){
					nscp[i]=scConfig[i];
				}
				if(nscp.length>0){
					pkg["scpConfigs"]=nscp;
				}
				var msg=execJava("Index.createInstallPkg",{pkg:pkg});
				infoMsg("安装包创建成功!");
			}
		},{
			text:'&nbsp;取消&nbsp;',
			handler:function(){
				$('#pkgApp').dialog("close");
			}
		}]
	});
}
//*************************** 首页应用管理结束 ***********************************//

//*************************** 公共函数管理 ***********************************//
//获取数据索引
function getRowIndex(target){
	var tr = $(target).closest('tr.datagrid-row');
	return parseInt(tr.attr('datagrid-row-index'));
}
function updateActions(obj,index,rowData){
	$(obj).datagrid('updateRow',{
		index: index,
		row:{data:rowData}
	});
}