/**
 * common.js V1.1 - PowerOfLengedJxBrowser
 * author:玄翼猫
 */

/**
 * 打开文件选择框
 * @param model 模式:save 保存，file 单个文件，mfile  多个文件
 * @param title 标题，可不传或为null
 * @param filePath 默认文件路径，可不传或为null
 * @param filterName 过滤器名，可不传或为null
 * @param filters 过滤器数组，可不传或为null。示例: ["png","jpg"]
 * @returns 选择的文件数组
 */
function chooseFile(model,title,filePath,filterName,filters){
	if(!model) model="file";
	if(!title) title=null;
	if(!filePath) filePath=null;
	if(!filterName) filterName=null;
	if(!filters) filters=null;
	
	var params={
		model:model,
		title:title,
		filePath:filePath,
		filterName:filterName,
		filters:filters
	};
	
	var result=Java.exec("chooseFile",JSON.stringify(params));
	return JSON.parse(result);
}

/***
 * 保存应用数据
 * @param appId 应用ID
 * @param fileName 应用数据文件名
 * @param data 需保存的数据
 * @returns 系统通用消息 {success:true,msg:''}
 */
function writeAppData(appId,fileName,data){
	if(!appId || typeof appId!=="string" || appId==""){
		infoMsg("appId必须填写，appId是当前应用的ID");
		return;
	}
	var params={
		appId:appId,
		fileName:fileName,
		data:data
	}
	var result=Java.exec("writeAppData",params);
	return JSON.parse(result);
}

/**
 * 读取应用的数据文件
 * @param appId 应用ID
 * @param fileName 应用数据文件名
 * @returns
 */
function readAppData(appId,fileName){
	if(!appId || typeof appId!=="string" || appId==""){
		infoMsg("appId必须填写，appId是当前应用的ID");
		return;
	}
	var params={
		appId:appId,
		fileName:fileName
	}
	var result=Java.exec("readAppData",params);
	return JSON.parse(result);
}

/**
 * 调用windows资源管理器打开指定目录
 * @param path 文件路径，目录末尾需加上\
 * @returns
 */
function openExplorer(path){
	if(!path || typeof path!=="string"){
		infoMsg("文件路径不合法！");
		return;
	}
	Java.exec("openExplorer",{path:path});
}
/**
 * 调用Java方法
 * @param funcName 函数名
 * @param params 参数
 * @returns
 */
function execJava(funcName,params){
	if(!params) params="";
	var result=Java.exec(funcName,params);
	if(result){
		return JSON.parse(result);
	}
}

/**
 * 显示提示信息
 * @param msg 消息内容
 * @returns
 */
function infoMsg(msg){
	$.messager.alert("系统提示",msg,'info');
}

$.extend($.messager.defaults,{  
    ok:"确定",  
    cancel:"取消"  
});