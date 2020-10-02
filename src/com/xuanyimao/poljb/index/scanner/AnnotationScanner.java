/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月27日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.scanner;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import com.xuanyimao.poljb.index.anno.JsClass;
import com.xuanyimao.poljb.index.anno.JsFunction;
import com.xuanyimao.poljb.index.anno.JsObject;
import com.xuanyimao.poljb.index.bean.AnnoClass;
import com.xuanyimao.poljb.index.bean.AnnoMethod;
import com.xuanyimao.poljb.index.bean.AnnoRepertory;
import com.xuanyimao.poljb.index.bean.MethodParam;
import com.xuanyimao.poljb.index.exception.ScannerPackageNotFoundException;

/**
 * @Description: 注解扫描器
 * 				一瓶伏特加，狗熊都敢X
 * @author liuming
 */
public class AnnotationScanner {
	
	static URLClassLoader urlClassLoader= (URLClassLoader) ClassLoader.getSystemClassLoader();
	
	/**
     * Description:注解扫描入口
     * @author:liuming
     * @since 2017-12-4
     * @return void
     * @throws ScannerPackageNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClassNotFoundException 
	 * @throws InstantiationException 
     */
	public static void scannerMain() throws ScannerPackageNotFoundException, IllegalArgumentException, IllegalAccessException, UnsupportedEncodingException, InstantiationException, ClassNotFoundException{
		AnnoRepertory aRepertory=AnnoRepertory.getInstance();
		if(StringUtils.isBlank(aRepertory.getScannerPackage())){
            throw new ScannerPackageNotFoundException("扫描路径未配置");
        }
		//解析所有需要扫描的包，获取类注解
		getScannerPackages(aRepertory.getScannerPackage());
		//扫描注解类中的所有方法
		analysisAnnoMethodField();
	}
	
	/**
     * Description:获取所有需要扫描的包列表
     * @author:liuming
     * @since 2017-12-4
     * @param packagePath 扫描包路径
	 * @throws UnsupportedEncodingException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
     */
    public static void getScannerPackages(String packagePath) throws UnsupportedEncodingException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        //获取一共要扫描多少包
        String[] packages=packagePath.split(";");
        //获取所有需要扫描的包
        List<String> fpg=new ArrayList<String>();
        for(int i=0;i<packages.length;i++){
        	if(StringUtils.isBlank(packages[i])) continue;
            fpg.add(packages[i].replace(".","/"));
        }
        getScannerPackage(fpg);
    }
    /**
     * Description:递归获取所有的包，将*号转换成具体的包名，遍历里面的类
     * @author:liuming
     * @since 2017-12-4
     * @param pgs
     * @return List<String>
     * @throws UnsupportedEncodingException 
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public static void getScannerPackage(List<String> pgs) throws UnsupportedEncodingException, ClassNotFoundException, InstantiationException, IllegalAccessException{
    	List<AnnoClass> annoClassList=new ArrayList<AnnoClass>();
    	/***********************扫描指定jar包***********************/
    	//获取包名的正则，用于jar扫描时做目录匹配
    	List<String> regPgs=new ArrayList<String>();
    	for(String pg:pgs) {
    		regPgs.add(getPkgReg(pg));
    	}
    	
    	List<String> jarList = AnnoRepertory.getInstance().getExtraJars();
    	for(String jar:jarList) {
    		try {
				JarFile jarFile=new JarFile(jar);
				Enumeration<JarEntry> entry = jarFile.entries();
				JarEntry jarEntry;
				while (entry.hasMoreElements()) {
					jarEntry = entry.nextElement();
					String name = jarEntry.getName();
					if (name.charAt(0) == '/') {
						name=name.substring(1);
					}
					for(String reg:regPgs) {
						if(name.matches(reg)) {//匹配成功
							System.out.println(jar+"扫描的类："+name);
//							System.out.println(name.matches(".*?\\$\\d+.*?"));
							if(name.toLowerCase().endsWith(".class")  && !jarEntry.isDirectory()) {//如果是class文件，加载
								AnnoClass ac = loadJsClass(name.replace("/",".").substring(0,name.length()-6));
								if(ac!=null) annoClassList.add(ac);
							}
							break;
						}
					}
					
				}
				jarFile.close();
			} catch (IOException e) {
//				e.printStackTrace();
				System.out.println(jar+"文件加载失败,跳过扫描...");
			}
    	}
    	
    	/***********************扫描未在jar包的class**********************/
        for(String pg:pgs){
        	analysisAnnoClass(pg,annoClassList);
        }
        AnnoRepertory.getInstance().setAnnoClassList(annoClassList);
    }
    
    /**
     * 扫描非jar包内的class，工程的bin目录
     * @author:liuming
     * @param pg
     * @param annoClassList
     * @throws UnsupportedEncodingException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void analysisAnnoClass(String pg,List<AnnoClass> annoClassList) throws UnsupportedEncodingException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    	int sindex=pg.indexOf("*");
    	String pgPath=pg;
        if(sindex!=-1){//如果存在*号
        	pgPath=pg.substring(0,sindex);
        }
    	
    	String protocol ="";//协议名称
        String filePath ="";//资源物理路径
        File file;//文件对象
        URL url = urlClassLoader.getResource(pgPath);
        if(url==null){
            return;
        }
        
        // 得到协议的名称
        protocol = url.getProtocol();
        if("file".equals(protocol)){
            filePath = URLDecoder.decode(url.getFile(), "UTF-8");
            file=new File(filePath);
            if(file.isDirectory()){//如果是目录才处理
            	if(pg.indexOf("*")!=-1) {//获取当前包下所有目录，继续向下探查
            		for(File f:file.listFiles()){
                        if(f.isDirectory()){
                        	analysisAnnoClass(pgPath+f.getName()+pg.substring(sindex+1), annoClassList);
                        }
                    }
            		return;
            	}
            	//获取所有的class文件
                 File[] fList=file.listFiles(new FileFilter() {
                     @Override
                     public boolean accept(File f) {
//                    	 System.out.println("扫描的文件:"+f.getAbsolutePath());
                         return !f.isDirectory() && f.getName().endsWith(".class");
                     }
                 });
                 if(fList!=null){
                     for(File f:fList){
                    	 AnnoClass ac = loadJsClass((pg+"/"+f.getName().substring(0,f.getName().length()-6)).replace("/","."));
						 if(ac!=null) annoClassList.add(ac);
                     }
                 }
            }
        }
    }
    
    /**
     * 扫描注解文件
     * @author:liuming
     * @param clsName Class名
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static AnnoClass loadJsClass(String clsName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    	Class<?> cls=Class.forName(clsName);
//	   	 System.out.println("处理的类文件："+cls);
		 //获取类上的JsClass注解
		 JsClass jsClass=cls.getAnnotation(JsClass.class);
		 if(jsClass!=null) {
	   		 System.out.println("扫描到的注解类:"+cls+"..."+clsName);
//	   		 System.out.println("注解name:"+jsClass.name());
			 String className=jsClass.name();
			 if(StringUtils.isBlank(className)) {
//	   			 System.out.println("扫描到的注解>>"+cls.getName());  // 包名.类名
				 className=cls.getSimpleName();
				 className=className.substring(0,1).toLowerCase()+className.substring(1);
	//   			 System.out.println(className);
			 }
			 return new AnnoClass(cls, cls.newInstance(), className,cls.getName(),jsClass.jspre());
		 }
		 return null;
    }
    
    /**
     * 获取包名的正则表达式
     * @author:liuming
     * @param pkg
     * @return
     */
    public static String getPkgReg(String pkg) {
    	if(!pkg.endsWith("*") && !pkg.endsWith("/")) {
			pkg+="/";
		}
		if(pkg.endsWith("*")) {
			pkg=pkg.substring(0,pkg.length()-1)+"[^/]*?/[^/]*?";
		}else if(pkg.endsWith("/")) {
			pkg=pkg+"[^/]*?";
		}
		pkg=pkg.replace("/*/", "/[^/]*?/");
		return pkg;
    }
    
    /**
	 * 解析注解类中的方法
	 * @author:liuming
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static void analysisAnnoMethodField() throws IllegalArgumentException, IllegalAccessException {
		List<AnnoClass> annoClassList=AnnoRepertory.getInstance().getAnnoClassList();
		Map<String,AnnoMethod> methodMap=new HashMap<String,AnnoMethod>();
		if(annoClassList!=null && !annoClassList.isEmpty()) {
			for(AnnoClass annoClass:annoClassList) {
//				System.out.println(annoClass);
				//为类中含有@JsObject注解的字段注入实例，只能注入有@JsClass注解的对象，如果@JsObject标注的字段不是注解类对象集合中，抛出注入失败异常
				Field[] fields=annoClass.getCls().getDeclaredFields();
				if(fields.length>0) {
					for(int i=0;i<fields.length;i++) {
						fields[i].setAccessible(true);
						JsObject jsObject=fields[i].getAnnotation(JsObject.class);
						if(jsObject!=null) {
//							System.out.println(fields[i].getGenericType().getTypeName());
							//为属性赋值，以后根据需要做优化
							for(AnnoClass ac:annoClassList) {
								if(fields[i].getGenericType().getTypeName().equals(ac.getClsName())) {//如果与列表的类名一致
									fields[i].set(annoClass.getObj(), ac.getObj());
									break;
								}
							}
						}
					}
				}
				//解析含有@JsFunction注解的方法，获取方法中的参数
				Method[] methods=annoClass.getCls().getDeclaredMethods();
				if(methods.length>0) {
					for(int i=0;i<methods.length;i++) {
						methods[i].setAccessible(true);
						JsFunction jsFunction=methods[i].getAnnotation(JsFunction.class);
						if(jsFunction!=null) {//方法含有jsFunction注解
//							System.out.println(jsFunction.name());//函数名
//							System.out.println("方法名:"+methods[i].getName());//方法名，不需要
							AnnoMethod annoMethod=new AnnoMethod(methods[i], annoClass);
							//获取方法的所有参数
							Class<?>[] paramClass=methods[i].getParameterTypes();
							if(paramClass.length>0) {//存在参数
								List<MethodParam> paramList=new ArrayList<MethodParam>();
								//使用spring LocalVariableTableParameterNameDiscoverer获取参数名
								ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
								String[] pn=parameterNameDiscoverer.getParameterNames(methods[i]);
								for(int j=0;j<paramClass.length;j++) {
//									System.out.println(paramClass[j]+"...."+pn[j]);
									paramList.add(new MethodParam(paramClass[j], pn[j]));
								}
								annoMethod.setMethodParam(paramList);
							}
							String funcName=(StringUtils.isNotBlank(annoClass.getJspre())?annoClass.getJspre()+".":"") + jsFunction.name();
							System.out.println("扫描到的JS函数："+funcName);
							annoMethod.setDesc(jsFunction.desc());
							methodMap.put(funcName,annoMethod);
							
						}
					}
				}
			}
		}
		
		AnnoRepertory.getInstance().setMethodMap(methodMap);
	}
    
	/** 
	 *  根据JsClass注解的name获取扫描器保存的实体对象
	 * @author:liuming
	 * @param name
	 * @return
	 */
	public static Object getJsClassInstance(String name) {
		List<AnnoClass> annoClassList = AnnoRepertory.getInstance().getAnnoClassList();
		if(annoClassList!=null && !annoClassList.isEmpty()) {
			for(AnnoClass ac:annoClassList) {
				if(name.equals(ac.getName())) {
					return ac.getObj();
				}
			}
		}
		return null;
	}
}
