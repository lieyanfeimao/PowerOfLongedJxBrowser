/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月24日
 * @version V1.0 
 */
package com.xuanyimao.poljb;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;
import com.teamdev.jxbrowser.chromium.be;
import com.xuanyimao.poljb.index.bean.AnnoClass;
import com.xuanyimao.poljb.index.bean.AnnoRepertory;
import com.xuanyimao.poljb.index.bean.AppConfig;
import com.xuanyimao.poljb.index.bean.DevRepertory;
import com.xuanyimao.poljb.index.exception.ScannerPackageNotFoundException;
import com.xuanyimao.poljb.index.javadoc.XymDoclet;
import com.xuanyimao.poljb.index.scanner.AnnotationScanner;
import com.xuanyimao.poljb.index.util.ToolUtil;

/**
 * @Description: 力量启动入口，Run As > Java Application，沐浴在JxBrowser的光辉，跨入众神力量的殿堂。
 * 				众神雄伟而让你心潮澎湃的威严声音响彻殿堂：“你渴望力量吗！”
 * 				“是的，这便是我所渴望的力量。”
 * 				“给钱！”
 * 				- 对待代码要像对待自己的情人一样，用心，细心。不要让BUG去伤害你的情人。
 * @author liuming
 */
public class StartupApp {
	
	/** 默认的扫描包 */
	public final static String defaultPkg="com.xuanyimao.poljb.index;com.xuanyimao.poljb.*.jsimpl;";
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, ScannerPackageNotFoundException, FileNotFoundException, IOException, NoSuchMethodException, SecurityException, InvocationTargetException {
		//扫描包
		String path=System.getProperty("user.dir");
		DevRepertory dr = DevRepertory.getInstance();
		dr.setPath(path);
		//加载应用配置信息
		dr.loadAppConfig();
		//设置DPI
		dr.setDpi(Toolkit.getDefaultToolkit().getScreenResolution());
		
		List<AppConfig> appList = dr.getAppList();
		//加载应用需要的jar
		List<String> jarList=new ArrayList<String>();
		
		//扫描所有应用的注册类，包含jsimpl
		String scannerPkg="";
		
		for(AppConfig appConfig:appList) {
			if(StringUtils.isNotBlank(appConfig.getScannerpkg())) {
				scannerPkg+=appConfig.getScannerpkg();
				if(!appConfig.getScannerpkg().endsWith(";")) {
					scannerPkg+=";";
				}
			}else if(StringUtils.isNotBlank(appConfig.getLibjar())) {
				String[] jars=appConfig.getLibjar().split(";");
				for(String jar:jars) {
					if(StringUtils.isNotBlank(jar)) {
						jarList.add(ToolUtil.getTransData(jar,appConfig.getId()));
					}
				}
			}
		}
		scannerPkg+=defaultPkg;
//		System.out.println(scannerPkg);
		
		//得到系统类加载器
		URLClassLoader urlClassLoader= (URLClassLoader) ClassLoader.getSystemClassLoader();
		//加载jar，未做深度测试。run目录下的modeldeal即是以单独的jar文件引入的
		for(int i=0;i<jarList.size();i++) {
			System.out.println("测试加载jar:"+jarList.get(i));
			File file=new File(jarList.get(i));
			if(!file.exists()) {
				System.out.println(jarList.get(i)+"不存在...");
				jarList.remove(i);
				i--;
				continue;
			}
			URL url = file.toURI().toURL();
			
			Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
			add.setAccessible(true);
			add.invoke(urlClassLoader, new Object[] {url });
		}
		
		AnnoRepertory ar=AnnoRepertory.getInstance();
		ar.setScannerPackage(scannerPkg);
		ar.setExtraJars(jarList);
		AnnotationScanner.scannerMain();
		
//		//获取方法上的注释并生成文档。开发时使用。
//		com.sun.tools.javadoc.Main.execute(new String[] {"-doclet"
//				,XymDoclet.class.getName()
//				,"-encoding"
//				,"utf-8"
//                ,"-sourcepath"
//                ,dr.getPath()+File.separator+"src"+File.separator
//                ,"-subpackages"
//                ,"com"
////                ,"D:\\software-java\\javaproject\\PowerOfLongedJxBrowser\\bin"
//////                ,"-sourcepath"
////                ,"D:\\software-java\\javaproject\\PowerOfLongedJxBrowser\\src"
//                
//		});
//		ClassDoc[] classes =XymDoclet.rootDoc.classes();
//		List<AnnoClass> annoClassList = AnnoRepertory.getInstance().getAnnoClassList();
//		for (int i = 0; i < classes.length; ++i) {
//			for(AnnoClass acls:annoClassList) {
////				System.out.println(acls.getClsName()+">>" );
//				if(acls.getClsName().equals(classes[i].toString())) {
////					System.out.println("///////////////////////////////////////////////////匹配成功....");
////					System.out.println("class>>"+classes[i].name()+".."+classes[i]+"///"+classes[i].toString()+",,,"+classes[i].simpleTypeName());
//					System.out.println("JS接口类："+acls.getClsName());
////					String clsDesc=classes[i].commentText();
//					System.out.println("类注释："+classes[i].commentText());
//					System.out.println("前缀:"+acls.getJspre());
//					String menuStr=StringUtils.isBlank(acls.getJspre())?"无前缀方法":acls.getJspre();
//					
//					
////					Tag[] tags=classes[i].tags();
////					for(Tag tag:tags) {
////						System.out.println(tag.kind()+"..."+tag.name()+".."+tag.text());
////					}
////					System.out.println(classes[i].commentText());
//					for(MethodDoc method:classes[i].methods()){
//		                System.out.printf("\t%s\n", method.commentText());
//		            }
//					
//					break;
//				}
//			}
//        }
		
	}
}
