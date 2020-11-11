## 必读
本项目使用的是JxBrowser 6.24.3  
JxBrowser是一个不免费的优秀项目。本项目没有包含许可证信息，意味着你导入到eclipse后并不能直接运行。请自行获取许可证。  
JxBrowser官网：https://www.teamdev.com/jxbrowser  
请支持正版，支持他人劳动成果！  
建议先去本项目目录 app\helpdoc，用浏览器打开index.html 查阅帮助文档，或者查阅线上文档：http://xuanyimao.com/softarticle/jxbrowser.html  

## 目录说明
src：JAVA代码目录  
app：HTML文件存放目录，可以理解为应用目录。各个应用的HTML部分应以单个目录的形式存放在这里。  
bin：class文件目录    
data：数据储存目录，以应用ID生成相应的目录。data数据以json格式数据储存，方便修改  
lib：JAR文件目录  
scp：脚本文件目录，对应首页的脚本管理      

## 功能模块说明
**应用管理：** 可视化界面管理应用，实现应用安装与打包，较鸡肋，也许后面会完善  
**脚本管理：** 管理项目的脚本文件，设置脚本注入路径，替换方式等  
**代码模板生成器：** 以模板的形式自动生成代码文件，也可以用于生成其他文件  
**JxBrowser帮助文档：** JxBrowser帮助文档，含本人写的教程和带中文翻译的JxBrowser文档和Jxbrowser帮助文档， 可以到项目目录 app\helpdoc，用浏览器打开index.html 查看  

## 创建一个JS与JAVA交互的接口
在com.xuanyimao.poljb下面创建一个自己的包，比如：com.xuanyimao.polj.test，在下面继续创建一个jsimpl包：com.xuanyimao.polj.test.jsimpl。  
新建一个普通JAVA类，加上注解 @JsClass ，创建一个方法，加上注解：@JsFunction(name="test1")。里面随便写一些代码。可参考各jsimpl包下的代码。  
前台HTML页面引入 app\static\js\common.js，执行类似形式的代码 var msg=execJava("test1",{data:data}); ，建议直接在index.html做测试，可参考index.html中注释掉的测试代码。  
common.js中的execJava函数弹层使用的是easyui，如果想换成其他ui，请参照该代码进行改写。  
  
从StartupApp中可以看出，程序在启动时扫描了指定目录下的jsimpl包，如果你的应用想要用其他的包名，请在这里指定，或在打包成应用包的时候指定扫描的包名。  
有时候你的数据可能无法被Gson解析成相应参数，遇到这种情况，建议把参数放到一个Java实体类里面。参考com.xuanyimao.poljb.index.jsimpl.IndexJs的createInstallPkg方法  
**注解说明：**  
**@JsClass** 表示这个类是个JS接口类  
**@JsFunction** 标明前台该如何调用这个JAVA方法，name属性是JS调用时使用的名称  
**@JsObject** 可用此注解动态注入@JsClass的类对象，不建议使用，建议用：AnnotationScanner.getJsClassInstance(JsClass名)   
如果阁下需要了解注解解析器的设计思路，请查阅帮助文档 “通过注解处理Js与Java交互”及之后的章节  

## **代码相关说明**
因为PowerOfLongedJxBrowser的作者拥有良好的编码规范，几乎所有方法都有注释，所以，你可以尽情的研究源码。PowerOfLongedJxBrowser的核心代码都在com.xuanyimao.poljb.index 下。  
不要纳闷我为什么没有new MainFrame()，窗口却启动了。注解扫描器扫描时会自动实例化一个对象到内存中。它会被打开的原因就是因为注解扫描器扫描了它，而它有JsClass这个注解。  
**com.xuanyimao.poljb.StartupApp**  
项目启动类，加载配置，扫描JS接口，启动窗口  
**com.xuanyimao.poljb.index.MainFrame**  
主窗口类，从这里初始化JxbManager数据  
**com.xuanyimao.polj.index.JxbManager**  
JxBrowser对象管理类，和浏览器相关的事件都在里面，例如创建窗口，关闭窗口  
**com.xuanyimao.poljb.index.scanner.AnnotationScanner**  
注解扫描器主类，扫描注解，执行JS和Java代码交互  
**com.xuanyimao.poljb.index.PoljbJsToJava**  
Js与Java交互的总入口，此工具类会被加载到Browser的JS上下文中。JS访问Java程序，经过此入口程序，程序对JS数据进行分析，从扫描器获取对应的对象并执行相应的方法。示例：**com.xuanyimao.poljb.index.jsimpl.CommonFunction**中的**chooseFile**  
**com.xuanyimao.polj.index.util.ZipUtil**  
文件压缩工具类  
**com.xuanyimao.polj.index.util.ToolUtil**  
乱七八糟的工具类  

## **二次开发规范**
如果你想使用PowerOfLongedJxBrowser直接开发，我建议遵循以下规则：  
尽量不修改 com.xuanyimao.poljb 下的代码，自立门户，创建自己的包。在StratupApp中添加自己的包的扫描路径。这样是为了防止我万一吃饱了没事干去更新一个比较好的新版本，你手足无措。  
src下的包名和app下的包名与你的应用ID保持一致，这样方便你自己开发  
JS交互接口的名字以 应用ID.方法名 的形式，以免和我的产生冲突。  
总之，你按规定，我随意。

## **版本记录**
v1.0 PowerOfLongedJcef，虽然抛弃它很不舍，但没办法
v2.0 更名PowerOfLongedJxBrowser，修改代码以适应JxBrowser 

## **目前的版本有些功能需要完善，但总体架构完善程度高，已满足本人需要，无特殊情况不会更新此项目。因为这是工作之余做的项目，。**