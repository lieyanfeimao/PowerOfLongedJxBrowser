/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年9月24日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.xuanyimao.poljb.index.anno.JsClass;
import com.xuanyimao.poljb.index.bean.Constants;
import com.xuanyimao.poljb.index.bean.DevRepertory;
import com.xuanyimao.poljb.index.delegate.XymNetworkDelegate;

/**
 * @Description: 力量主界面
 * @author liuming
 */
@JsClass
public class MainFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8237668391453234775L;
	
	public MainFrame() {
//		String dir=BrowserContext.defaultContext().getDataDir();
//		System.out.println(dir);
		//--disable-web-security：开启跨域
		//--allow-running-insecure-content：默认情况下，https 页面不允许从 http 链接引用 javascript/css/plug-ins。添加这一参数会放行这些内容
		//--remote-debugging-port=9222：远程开发者调试端口
		BrowserPreferences.setChromiumSwitches("--disable-web-security","--allow-running-insecure-content",
				"--enable-system-flash=true","--remote-debugging-port=9222"
				,"--disable-google-traffic");
		//每个实例需使用不同的数据目录
		int index=0;
		DevRepertory dr=DevRepertory.getInstance();
		String dir=dr.getPath()+File.separator+Constants.JDATA_PATH+File.separator+Constants.JDATA_PATH_PRE;
		while(true) {
			String dataDir=dir+index;
			File file=new File(dataDir);
			if(!file.exists()) {
				file.mkdirs();
			}
			boolean f=file.renameTo(file);
			if(f) {
				dr.setBrowserContext(new BrowserContext(new BrowserContextParams(dataDir)));
				break;
			}
			index++;
		}
//		dr.setBrowserContext(BrowserContext.defaultContext());
//		System.out.println("重命名:"+f);
		
        JxbManager jmg = JxbManager.getInstance(this);
        //创建网络监听。实现此接口容易造成程序崩溃，不推荐使用
        dr.getBrowserContext().getNetworkService().setNetworkDelegate(new XymNetworkDelegate());
        //资源加载监听
//        dr.getBrowserContext().getNetworkService().setResourceHandler(new XymResourceHandler());
        
        Browser browser=new Browser(dr.getBrowserContext());
        browser.loadURL("file:///"+DevRepertory.getInstance().getAppPath()+"/index.html");
//        browser.setDialogHandler(new DialogHandler() {
//            @Override
//            public void onAlert(DialogParams params) {
//                String url = params.getURL();
//                String title = "The page at " + url + " says:";
//                String message = params.getMessage();
//                JOptionPane.showMessageDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
//            }
//
//            @Override
//            public CloseStatus onConfirmation(DialogParams params) {
//                return CloseStatus.OK;
//            }
//
//            @Override
//            public CloseStatus onPrompt(PromptDialogParams params) {
//                return CloseStatus.CANCEL;
//            }
//
//            @Override
//            public CloseStatus onBeforeUnload(UnloadDialogParams arg0) {
//                return CloseStatus.CANCEL;
//            }
//
//            @Override
//            public CloseStatus onColorChooser(ColorChooserParams arg0) {
//                // TODO Auto-generated method stub
//                return CloseStatus.CANCEL;
//            }
//
//            @Override
//            public CloseStatus onFileChooser(FileChooserParams arg0) {
//                // TODO Auto-generated method stub
//                return CloseStatus.CANCEL;
//            }
//
//            @Override
//            public CloseStatus onReloadPostData(ReloadPostDataParams arg0) {
//                // TODO Auto-generated method stub
//                return CloseStatus.CANCEL;
//            }
//
//            @Override
//            public CloseStatus onSelectCertificate(CertificatesDialogParams arg0) {
//                // TODO Auto-generated method stub
//                return CloseStatus.CANCEL;
//            }
//            
//        });
		jmg.createBrowser(browser);
        
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    	this.setTitle("PowerOfLongedJxBrowser-玄翼猫-V1.0");
    	ImageIcon icon=new ImageIcon(DevRepertory.getInstance().getAppPath()+File.separator+"logo.png");
    	this.setIconImage(icon.getImage());
	}
}
