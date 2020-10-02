/**  
 * http://www.xuanyimao.com
 * @author:liuming
 * @date: 2019年10月14日
 * @version V1.0 
 */
package com.xuanyimao.poljb.index.delegate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.IOUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamdev.jxbrowser.chromium.BeforeURLRequestParams;
import com.teamdev.jxbrowser.chromium.DataReceivedParams;
import com.teamdev.jxbrowser.chromium.RequestCompletedParams;
import com.teamdev.jxbrowser.chromium.swing.DefaultNetworkDelegate;
//import com.xuanyimao.poljb.index.bean.DevRepertory;

/**
 * @Description: 网络请求处理。不建议使用，容易崩溃。若JxBrowser后续版本有修复此问题，可考虑使用
 * @author liuming
 */
public class XymNetworkDelegate extends DefaultNetworkDelegate{
    /**采集标志：true 初始化    false  当天*/
    final static boolean INIT_FLAG=true;
    /**当前年份*/
    final static int NOW_YEAR=2020;
    
    List < byte[] > blist=new ArrayList<byte[]>();
	/* 
	 * 当即将发生请求时，将调用此方法。在建立任何TCP连接之前发送此事件，该事件可用于将请求重定向到另一个位置。
	 * 该方法被同步调用。执行此方法时，URL请求将被阻止。如果此方法引发异常，则将使用默认行为-目标URL将不会更改。
	 * params:提供有关请求URL和HTTP方法的信息（“ GET”，“ POST”等）。要覆盖目标URL，请使用  RequestParams.setURL(String)  方法。
	 */
	@Override
	public void onBeforeURLRequest(BeforeURLRequestParams params) {
//		System.out.println("可以被修改请求的URL:"+params.getURL());
//		params.setURL("");
//		if(params.getURL().startsWith("https://img.bafang.com/cdn/assets/okfe/socket/1.4.13/main.js")) {
//			params.setURL("file:///"+DevRepertory.getInstance().getPath()+"/scp/wss2.js");
//		}
		super.onBeforeURLRequest(params);
	}

    @Override
    public void onDataReceived(DataReceivedParams params) {
//        System.out.println("数据到达...");
        String url=params.getURL();
//        System.out.println("URL:"+params.getURL());
        if(url.indexOf("/all.js?hexin-v")!=-1) {
            System.out.println("接收数据...");
            blist.add(params.getData());
        }
        
        super.onDataReceived(params);
    }

    @Override
    public void onCompleted(RequestCompletedParams params) {
        String url=params.getURL();
        if(url.indexOf("/all.js?hexin-v")!=-1) {
            System.out.println("数据接收完成...");
            try {
                saveByteList();
            }catch(Exception e) {
                e.printStackTrace();
            }
            blist.clear();
        }
        
        super.onCompleted(params);
    }
	
	public void saveByteList() {
	    if(blist.isEmpty()) {
	        return;
	    }
	    int length=0;
	    for(int i=0;i<blist.size();i++) {
	        length+=blist.get(i).length;
	    }
	    byte[] b1=new byte[length];
	    length=0;
	    for(int i=0;i<blist.size();i++) {
	        System.arraycopy(blist.get(i), 0, b1,length, blist.get(i).length);
	        length+=blist.get(i).length;
	    }
	    
	    try {
            String data=new String(b1, "UTF-8");
            System.out.println(data);
            //处理data
            int i1=data.indexOf("(");
            if(i1!=-1) {
                data=data.substring(i1+1, data.length()-1);
            }
//            System.out.println(data);
            JsonParser jp=new JsonParser();
            JsonObject jobj=jp.parse(data).getAsJsonObject();
//            System.out.println(jobj.get("name"));
//            System.out.println(jobj.get("price"));
            if(INIT_FLAG) {//获取指数初始化脚本
                createInitSql(jobj.get("name").getAsString(), jobj.get("price").getAsString(), jobj.get("dates").getAsString());
            }else {//获取指数最新日期的数据
                createNowSql(jobj.get("name").getAsString(), jobj.get("price").getAsString(), jobj.get("dates").getAsString());
            }
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	/**
	 * 生成最新的SQL脚本
	 * @author:liuming
	 * @param name
	 * @param price
	 * @param date
	 */
	public void createNowSql(String name,String price,String date) {
	    
        String[] prices=price.split(",");
        String[] dates=date.split(",");
        
        //当前起始年份
        int nowYear=NOW_YEAR;
        
        StringBuffer sbuf=new StringBuffer("");
        
        int dayIndex=dates.length-1;
        int priceIndex=prices.length-1;
        
        String day=dates[dayIndex].substring(0,2)+"-"+dates[dayIndex].substring(2);
        
        
        String num4=prices[priceIndex];
        String num1=prices[priceIndex-3];
        
        int lsPriceIndex=priceIndex-4;
        String lsNum4=prices[lsPriceIndex];
        String lsNum1=prices[lsPriceIndex-3];
        
        sbuf.append("insert into fund_index_history(index_id,date_id,num,last_num) ");
        sbuf.append("select (select id from fund_index where `name`='"+name+"'),fd.id,"+num1+"+"+num4+","+lsNum1+"+"+lsNum4+" " + 
                "from fund_date fd " + 
                "where fd.fund_day='"+nowYear+"-"+day+"';");
        sbuf.append("\r\n");
        
        File file=new File("D:\\基金分析\\"+nowYear+"-"+day+"\\");
        if(!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File("D:\\基金分析\\"+nowYear+"-"+day+"\\"+name+".sql"));
            IOUtils.write(sbuf.toString(), fos,"UTF-8");
            IOUtils.closeQuietly(fos);
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	/**
	 * 生成初始化的sql
	 * @author:liuming
	 * @param price
	 */
	public void createInitSql(String name,String price,String date) {
	    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	    String[] prices=price.split(",");
	    String[] dates=date.split(",");
	    System.out.println("counts:"+prices.length+"-"+dates.length);
	    //当前起始年份
	    int nowYear=NOW_YEAR;
	    //上次的时间
	    String lastTime=null;
	    //采集历史结果的结束日期
	    Date endDate=new Date();
	    try {
            endDate=sdf.parse("1999-12-31");
        }
        catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
//	    String endTime="";
	    
//	    List < String > dateList=new ArrayList<String>();
	    
	    StringBuffer sbuf=new StringBuffer("");
	    
	    int dayIndex=dates.length-1;
	    int priceIndex=prices.length-1;
	    for(int i=0;i<dates.length;i++) {
            String day=dates[dayIndex].substring(0,2)+"-"+dates[dayIndex].substring(2);
            if(lastTime!=null) {
                
                //判断是否要处理年份
                try {
                    Date d1=sdf.parse(nowYear+"-"+day);
                    Date d2=sdf.parse(lastTime);
                    if(d1.getTime()>d2.getTime()) {
                        nowYear--;
                    }
                    d1=sdf.parse(nowYear+"-"+day);
                    if(d1.getTime()<endDate.getTime()) {
                        break;
                    }
                }
                catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }
            
            String num4=prices[priceIndex];
            String num1=prices[priceIndex-3];
            
            int lsPriceIndex=priceIndex-4;
            String lsNum4="0";
            String lsNum1="0";
            if(lsPriceIndex>0) {
                lsNum4=prices[lsPriceIndex];
                lsNum1=prices[lsPriceIndex-3];
            }
            
            sbuf.append("insert into fund_index_history(index_id,date_id,num,last_num) ");
            sbuf.append("select (select id from fund_index where `name`='"+name+"'),fd.id,"+num1+"+"+num4+","+lsNum1+"+"+lsNum4+" " + 
                    "from fund_date fd " + 
                    "where fd.fund_day='"+nowYear+"-"+day+"';");
            sbuf.append("\r\n");
            
            
            lastTime=nowYear+"-"+day;
            
            dayIndex--;
            priceIndex-=4;
        }
	    
	    FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File("D:\\基金分析\\初始化数据\\fund_index_history_init_"+name+".sql"));
            IOUtils.write(sbuf.toString(), fos,"UTF-8");
            IOUtils.closeQuietly(fos);
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    
	}
}
