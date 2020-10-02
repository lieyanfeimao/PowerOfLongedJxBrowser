/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月28日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.jsimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xuanyimao.poljb.index.anno.JsClass;
import com.xuanyimao.poljb.index.anno.JsFunction;
import com.xuanyimao.poljb.index.bean.AppConfig;
import com.xuanyimao.poljb.index.bean.AppConfig.AppType;
import com.xuanyimao.poljb.index.bean.AppPackage;
import com.xuanyimao.poljb.index.bean.Constants;
import com.xuanyimao.poljb.index.bean.CopyFile;
import com.xuanyimao.poljb.index.bean.DevRepertory;
import com.xuanyimao.poljb.index.bean.FileConfig;
import com.xuanyimao.poljb.index.bean.Injection;
import com.xuanyimao.poljb.index.bean.Message;
import com.xuanyimao.poljb.index.bean.Scp;
import com.xuanyimao.poljb.index.bean.ScpConfig;
import com.xuanyimao.poljb.index.util.ToolUtil;
import com.xuanyimao.poljb.index.util.ZipUtil;

import net.lingala.zip4j.exception.ZipException;

/**
 * index相关的JS函数
 * @author liuming
 */
@JsClass(jspre="Index")
public class Index {
	
	public final static String APP_ID="index";
	
	Gson gson=new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
	/**
	 * 获取所有脚本文件
	 * @author:liuming
	 * @return
	 */
	@JsFunction(name="getScpFiles")
	public Message getScpFiles() {
		File file=new File(DevRepertory.getInstance().getPath()+File.separator+Constants.SCP_PATH+File.separator);
		if(!file.exists()) {
			return Message.success("");
		}
		return Message.success("操作成功",gson.toJson(file.list()));
	}
	/**
	 * 载入所有脚本文件
	 * @author:liuming
	 * @return
	 */
	@JsFunction(name="loadScpFiles")
	@SuppressWarnings("deprecation")
	public Message loadScpFiles() {
		String filePath=DevRepertory.getInstance().getPath()+File.separator+Constants.APPDATAFOLDER.replace("{appId}",APP_ID)+File.separator+Injection.CONFIG_FILE;
		File file=new File(filePath);
		if(file.exists()) {
			
			try {
				FileInputStream fis = new FileInputStream(file);
				String content=IOUtils.toString(fis,"UTF-8");
				IOUtils.closeQuietly(fis);
				Injection.getInstance().setConfigs(new Gson().fromJson(content,new TypeToken<ArrayList<ScpConfig>>(){}.getType()));
			} catch (IOException e) {
				e.printStackTrace();
				return Message.error("操作失败!");
			}
		}
		return Message.success();
	}
	/**
	 * 重载脚本
	 * @author:liuming
	 * @return
	 */
	@JsFunction(name="reloadScript")
	public Message reloadScript() {
		Injection.getInstance().reloadScript();
		return Message.success();
	}
	
	/**
	 * 为指定脚本文件动态添加额外的JS
	 * @author:liuming
	 * @param file 文件名
	 * @param data 添加的JS
	 * @param type 1，添加在脚本末尾，其他值为添加在脚本之前
	 * @return
	 */
	@JsFunction(name="addJs")
	public Message addJs(String file,String data,Integer type) {
		if(StringUtils.isBlank(file)) {
			return Message.error("文件名不能为空!");
		}
		List<ScpConfig> configs = Injection.getInstance().getConfigs();
		if(configs!=null && !configs.isEmpty()) {
			for(ScpConfig sc:configs) {
				if(file.equals(sc.getFile())) {
					Scp scp = Injection.getInstance().getScpInfo().get(sc.getFile());
					if(scp!=null) {
						if(type!=null && type.intValue()==1) {
							scp.setActiveEnd(data);
						}else {
							scp.setActiveStart(data);
						}
					}
					break;
				}
			}
		}
		
		return Message.success();
	}
	
	/**
	 * 创建安装包
	 * @author:liuming
	 * @param pkg
	 * @throws ZipException
	 * @throws IOException
	 */
	@JsFunction(name="createInstallPkg")
	@SuppressWarnings("deprecation")
	public void createInstallPkg(AppPackage pkg) throws ZipException, IOException {
//		System.out.println(new Gson().toJson(pkg));
		//删除tmp目录
		String tmpPath=DevRepertory.getInstance().getPath()+File.separator+Constants.TMP_PATH;
		File tmpFile=new File(tmpPath);
		if(tmpFile.exists() && tmpFile.isDirectory()) {
//			tmpFile.delete();
			FileUtils.deleteDirectory(tmpFile);
		}
		//新建tmp目录
		tmpFile.mkdirs();
		
		//生成压缩文件列表
		List<String> zipFiles=new ArrayList<String>();
		List<FileConfig> fileConfigs = pkg.getFileConfigs();
		for(FileConfig fc:fileConfigs) {
			File file=new File(fc.getFileName());
			fc.setZipFileName(file.getName());
			if(fc.getIsFolder()) {
				List<CopyFile> cfList = findAllFile(fc.getFileName(), "");
				for(CopyFile cf:cfList) {
//					System.out.println(cf.getFileName()+"..."+cf.getIsFolder()+"..."+cf.getSrcPath());
					copyFile(cf.getSrcPath(), tmpPath+File.separator+file.getName()+File.separator+cf.getFileName());
				}
			}else {
				copyFile(fc.getFileName(), tmpPath+File.separator+fc.getZipFileName());
			}
			
			zipFiles.add(tmpPath+File.separator+fc.getZipFileName());
			fc.setFileName("");
		}
		
		AppConfig appConfig = pkg.getAppConfig();
		appConfig.setType(AppType.html);
		String zipPath=pkg.getAppPkg();
		if(!zipPath.endsWith("zip")) {
			zipPath+=".zip";
		}
		
		//生成安装的配置文件
		String content=new Gson().toJson(pkg);
		File configFile=new File(tmpPath+File.separator+Constants.CONFIGFILE);
		FileOutputStream fos=new FileOutputStream(configFile);
		IOUtils.write(content, fos,"UTF-8");
		IOUtils.closeQuietly(fos);
		zipFiles.add(configFile.getAbsolutePath());
		//打包
		ZipUtil.zip(zipFiles, zipPath);
	}
	/**
	 * 安装应用
	 * @author:liuming
	 * @param path 应用安装包
	 * @return
	 * @throws IOException
	 */
	@JsFunction(name="installApp")
	@SuppressWarnings("deprecation")
	public Message installApp(String path) throws IOException {
		//删除tmp目录
		String tmpPath=DevRepertory.getInstance().getPath()+File.separator+Constants.TMP_PATH;
		File tmpFile=new File(tmpPath);
		if(tmpFile.exists() && tmpFile.isDirectory()) {
//			tmpFile.delete();
			FileUtils.deleteDirectory(tmpFile);
		}
		//新建tmp目录
		tmpFile.mkdirs();
		//将文件解压到tmp目录
		try {
			ZipUtil.unzip(path, tmpPath);
		} catch (ZipException e) {
			e.printStackTrace();
			return Message.error("安装包解压失败！");
		}
		
		//读取配置文件
		String configPath=tmpPath+File.separator+Constants.CONFIGFILE;
		File configFile=new File(configPath);
		if(!configFile.exists()) {
			return Message.error("未找到配置文件，安装失败。");
		}
		FileInputStream fis=new FileInputStream(configFile);
		String data=IOUtils.toString(fis,"UTF-8");
		IOUtils.closeQuietly(fis);
		
		AppPackage pkg=new Gson().fromJson(data,AppPackage.class);
		List<FileConfig> fileConfigs = pkg.getFileConfigs();
		AppConfig appConfig=pkg.getAppConfig();
//		FileOutputStream fos=null;
		for(FileConfig fc:fileConfigs) {
			String filePath=tmpPath+File.separator+fc.getZipFileName();
			String exportPath=ToolUtil.getTransData(fc.getExportPath(), appConfig.getId());
			if(fc.getIsFolder()) {
				List<CopyFile> cfList = findAllFile(filePath, "");
				for(CopyFile cf:cfList) {
//					System.out.println(cf.getFileName()+"..."+cf.getIsFolder()+"..."+cf.getSrcPath());
					copyFile(cf.getSrcPath(), exportPath+File.separator+cf.getFileName());
				}
			}else {//复制文件
				copyFile(filePath, exportPath+File.separator+fc.getZipFileName());
			}
		}
		//更新app配置信息
		List<AppConfig> appList = DevRepertory.getInstance().getAppList();
		boolean flag=false;
		for(AppConfig ac:appList) {
			if(ac.getId().equals(appConfig.getId())) {
//				System.out.println("应用ID相同："+ac.getId());
				ac.setCommand(appConfig.getCommand());
				ac.setIcon(appConfig.getIcon());
				ac.setLib(appConfig.getLib());
				ac.setLibjar(appConfig.getLibjar());
				ac.setName(appConfig.getName());
				ac.setPath(appConfig.getPath());
				ac.setScannerpkg(appConfig.getScannerpkg());
				ac.setType(appConfig.getType());
				ac.setVersion(appConfig.getVersion());
				ac.setVersionNum(appConfig.getVersionNum());
				flag=true;
				break;
			}
		}
		if(!flag) {
			appList.add(appConfig);
		}
		DevRepertory.getInstance().saveAppList();
		//更新脚本配置信息
		List<ScpConfig> scpConfigs = pkg.getScpConfigs();
		if(scpConfigs!=null && !scpConfigs.isEmpty()) {
			List<ScpConfig> scpConfigs1 = Injection.getInstance().getConfigs();
			for(ScpConfig sc:scpConfigs) {
				flag=false;
				for(ScpConfig sc1:scpConfigs1) {
					if(sc1.getFile().equals(sc.getFile())) {
						sc1.setName(sc.getName());
						sc1.setType(sc.getType());
						sc1.setUrl(sc.getUrl());
						flag=true;
						break;
					}
				}
				if(!flag) {
					scpConfigs1.add(sc);
				}
			}
			//保存脚本配置
			Injection.getInstance().saveConfig();
		}
		
		return Message.success("应用安装成功");
	}
	/**
	 * 获取应用列表
	 * @author:liuming
	 * @return
	 */
	@JsFunction(name="appList")
	public Message appList() {
		List<AppConfig> appList = DevRepertory.getInstance().getAppList();
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		for(AppConfig config:appList) {
			Map<String,String> map=new HashMap<String, String>();
			map.put("id", config.getId());
			map.put("name", config.getName());
			map.put("path", config.getRealPath());
			map.put("icon", config.getRealIcon());
			list.add(map);
		}
		return Message.success("操作成功",gson.toJson(list));
	}
	/**
	 * 删除应用
	 * @author:liuming
	 * @param id 应用ID
	 * @return
	 */
	@JsFunction(name="deleteApp")
	public Message deleteApp(String id) {
		try {
			DevRepertory.getInstance().deleteApp(id);
		} catch (IOException e) {
			e.printStackTrace();
			return Message.error("删除应用失败："+e.getMessage());
		}
		return Message.success("删除应用成功！");
	}
	/**
	 * 复制文件
	 * @author:liuming
	 * @param src 原文件
	 * @param dest 目标文件
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static void copyFile(String src,String dest) throws IOException {
		File file=new File(dest);
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileOutputStream fos=new FileOutputStream(dest);
		FileInputStream fis=new FileInputStream(src);
		IOUtils.copy(fis, fos);
		IOUtils.closeQuietly(fis);
		IOUtils.closeQuietly(fos);
	}
	
	/**
	 * 遍历获取目录下所有文件
	 * @author:liuming
	 * @param path
	 * @param parentName
	 * @return
	 */
	public static List<CopyFile> findAllFile(String path,String parentName){
		if(StringUtils.isNotBlank(parentName)) {
			parentName+=File.separator;
		}
		List<CopyFile> cfList=new ArrayList<CopyFile>();
		File file=new File(path);
		if(file.exists()) {
			File[] files=file.listFiles();
			for(File f:files) {
				if(f.isDirectory()) {
//					cf.setIsFolder(true);
					cfList.addAll(findAllFile(f.getAbsolutePath(), parentName+f.getName()));
				}else {
					CopyFile cf=new CopyFile();
					cf.setSrcPath(f.getAbsolutePath());
					cf.setFileName(parentName+f.getName());
					cfList.add(cf);
				}
			}
		}
		return cfList;
	}
}
