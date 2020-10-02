/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月26日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.jsimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import com.xuanyimao.poljb.index.JxbManager;
import com.xuanyimao.poljb.index.anno.JsClass;
import com.xuanyimao.poljb.index.anno.JsFunction;
import com.xuanyimao.poljb.index.bean.Constants;
import com.xuanyimao.poljb.index.bean.DevRepertory;
import com.xuanyimao.poljb.index.bean.Message;
import com.xuanyimao.poljb.index.bean.TabBrowser;

/**
 * 通用函数
 * @author liuming
 */
@JsClass
public class CommonFunction {
	
	/**文件选择框-选择目录**/
	private final static String CHOOSER_SAVE="save";
	/**文件选择框-选择文件**/
	private final static String CHOOSER_FILE="file";
	/**文件选择框-选择多个文件**/
	private final static String CHOOSER_MFILE="mfile";
	
	/**
	 * 文件选择框
	 * @author:liuming
	 * @param model  模式:save 保存，file 单个文件，mfile  多个文件
	 * @param title  弹出框标题
	 * @param filePath  默认路径
	 * @param filters   过滤器
	 * @return
	 */
	@JsFunction(name="chooseFile")
	public List<String> chooseFile(String model,String title,String filePath,String filterName,String[] filters){
		TabBrowser tb = JxbManager.getInstance().getSelectTabBrowser();
		JFileChooser fileChooser = new JFileChooser();
		//设置过滤器
		if(filters!=null && filters.length>0) {
			FileNameExtensionFilter filter=new FileNameExtensionFilter(filterName, filters);
			fileChooser.setFileFilter(filter);
		}
		//转换成小写
		model=model.toLowerCase();
		//如果传乱七八糟的，默认为选择文件
		if(!CHOOSER_FILE.equals(model) && !CHOOSER_MFILE.equals(model) && !CHOOSER_SAVE.equals(model)) {
			model=CHOOSER_FILE;
		}
		//如果是多文件选择
		if(CHOOSER_MFILE.equals(model)) {
			fileChooser.setMultiSelectionEnabled(true);
		}
		//设置弹出框默认标题
		if(StringUtils.isBlank(title)) {
			if(CHOOSER_MFILE.equals(model) || CHOOSER_FILE.equals(model)) title="打开文件(请开始你的表演)";
			else title="保存文件";
		}
		fileChooser.setDialogTitle(title);
		//设置文件选择的默认路径
		if(StringUtils.isNotBlank(filePath)) {
			fileChooser.setSelectedFile(new File(filePath));
		}
		
		List<String> files=new ArrayList<String>();
		//打开文件选择框
		int result=-1;
		if(CHOOSER_MFILE.equals(model) || CHOOSER_FILE.equals(model)) {
			result=fileChooser.showOpenDialog(tb.getView());
		}else {
			result=fileChooser.showSaveDialog(tb.getView());
		}
		//点击确认按钮
        if (result == JFileChooser.APPROVE_OPTION) {
        	if(CHOOSER_MFILE.equals(model)) {
        		File[] selectedFiles = fileChooser.getSelectedFiles();
        		for(File file:selectedFiles) {
        			files.add(file.getAbsolutePath());
        		}
        	}else {
        		files.add(fileChooser.getSelectedFile().getAbsolutePath());
        	}
        }
        
		return files;
	}
	/**
	 * 获取应用根目录
	 * @author:liuming
	 * @return
	 */
	@JsFunction(name="appPath")
	public String appPath() {
		return DevRepertory.getInstance().getAppPath();
	}
	/**
	 * 保存应用数据
	 * @author:liuming
	 * @param appId 应用ID
	 * @param fileName 数据文件名称
	 * @param data 保存的数据
	 * @return 处理结果，如果处理成功，返回空字符串
	 */
	@JsFunction(name="writeAppData")
	@SuppressWarnings("deprecation")
	public Message writeAppData(String appId,String fileName,String data) {
		if(StringUtils.isBlank(appId)) {
			return Message.error("appId必须填写，appId是当前应用的ID");
		}
		if(StringUtils.isBlank(fileName)) {
			fileName=Constants.APPDEFAULTFILE;
		}
		String filePath=DevRepertory.getInstance().getPath()+File.separator+Constants.APPDATAFOLDER.replace("{appId}",appId);
		File file=new File(filePath);
		if(!file.exists()) {
			file.mkdirs();
		}
		filePath+=File.separator+fileName;
		System.out.println(filePath);
		try {
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			IOUtils.write(data, fos,"UTF-8");
			IOUtils.closeQuietly(fos);
		} catch (IOException e) {
			e.printStackTrace();
			return Message.error("应用数据保存失败："+e.getMessage());
		}
		
		return Message.success();
	}
	
	/**
	 * 读取应用的数据文件
	 * @author:liuming
	 * @param appId 应用ID
	 * @param fileName 数据文件名称
	 * @return
	 */
	@JsFunction(name="readAppData")
	@SuppressWarnings("deprecation")
	public Message readAppData(String appId,String fileName) {
		if(StringUtils.isBlank(appId)) {
			return Message.error("appId必须填写，appId是当前应用的ID");
		}
		if(StringUtils.isBlank(fileName)) {
			fileName=Constants.APPDEFAULTFILE;
		}
		String filePath=DevRepertory.getInstance().getPath()+File.separator+File.separator+Constants.APPDATAFOLDER.replace("{appId}",appId)+File.separator+fileName;
		File file=new File(filePath);
		System.out.println("应用数据读取路径:"+file.getAbsolutePath());
		if(!file.exists()) {//首次使用应用时没有文件，所以返回true
			return Message.success("操作成功","");
		}
		try {
			FileInputStream fis=new FileInputStream(file);
			String content=IOUtils.toString(fis,"UTF-8");
			IOUtils.closeQuietly(fis);
//			System.out.println("读取的内容："+content);
			return Message.success("操作成功",content);
		} catch (IOException e) {
			e.printStackTrace();
			return Message.error("应用数据读取失败："+e.getMessage());
		}
	}
	/**
	 * 打开文件资源管理器
	 * @author:liuming
	 * @param path 指定路径
	 */
	@JsFunction(name="openExplorer")
	public void openExplorer(String path) {
		try {
			if(!path.endsWith("\\")) {
				int i1=path.lastIndexOf("\\");
				if(i1!=-1) {
					path=path.substring(0,i1);
				}
			}
			System.out.println("打开目录:"+path);
			Runtime.getRuntime().exec("explorer " + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
