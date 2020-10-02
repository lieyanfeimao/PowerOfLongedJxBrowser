/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月3日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import com.google.gson.Gson;
import com.xuanyimao.poljb.index.jsimpl.Index;
/**
 * @Description: 注入类
 * @author liuming
 */
public class Injection {
	public final static String CONFIG_FILE="scconfig";
	
	private final static Injection inj=new Injection();
	
	private Injection() {};
	/**
	 * 获取注入类实例
	 * @author:liuming
	 * @return
	 */
	public static Injection getInstance() {
		return inj;
	}
	
	/**注入配置：url->配置文件名*/
	private List<ScpConfig> configs;
	
	/**配置相关内容*/
	private Map<String,Scp> scpInfo;

	/**
	 * scpInfo
	 * @return scpInfo
	 */
	public Map<String, Scp> getScpInfo() {
		return scpInfo;
	}

	/**
	 * configs
	 * @return configs
	 */
	public List<ScpConfig> getConfigs() {
		return configs;
	}
	/**
	 * 设置 configs
	 * @param configs configs
	 */
	public void setConfigs(List<ScpConfig> configs) {
		this.configs = configs;
		reloadScript();
	}
	
	@SuppressWarnings("deprecation")
	public void reloadScript() {
		scpInfo=new HashMap<String,Scp>();
		if(this.configs!=null && !this.configs.isEmpty()) {
			for(ScpConfig sc:this.configs) {
				if( StringUtils.isNotBlank(sc.getName()) ) {
					File file=new File(DevRepertory.getInstance().getPath()+File.separator+Constants.SCP_PATH+File.separator+sc.getFile());
					if(!file.exists()) continue;
					try {
						FileInputStream fis=new FileInputStream(file);
						String data=IOUtils.toString(fis,"UTF-8");
						IOUtils.closeQuietly(fis);
						Scp scp=new Scp();
						scp.setContent(data);
						System.out.println("读取脚本："+sc.getUrl()+">>>"+file.getAbsolutePath());
//						System.out.println(data);
//						System.out.println("......");
						scpInfo.put(sc.getFile(), scp);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	/**
	 * 设置 scpInfo
	 * @param scpInfo scpInfo
	 */
	public void setScpInfo(Map<String, Scp> scpInfo) {
		this.scpInfo = scpInfo;
	}
	/**
	 * 保存配置
	 * @author:liuming
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public void saveConfig() throws IOException {
		String filePath=DevRepertory.getInstance().getPath()+File.separator+Constants.APPDATAFOLDER.replace("{appId}",Index.APP_ID)+File.separator+Injection.CONFIG_FILE;
		FileOutputStream fos=new FileOutputStream(filePath);
		IOUtils.write(new Gson().toJson(configs), fos);
		IOUtils.closeQuietly(fos);
	}
}
