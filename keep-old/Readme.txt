LogUtil 
日志类


InternetUtil  
1.获取IP
2.ping IP


WifiConnectUtil
wifi连接类  

TimeUtil
一些与Calendar相关的时间类

HandlerCenter
处理Handler的类
所有的Hanlder，先调用HandlerCenter.addHandler()进行添加。
使用
HandlerCenter.sendMessage() 
HandlerCenter.sendMessageDelayed() 
进行消息发送

FileDownloader
下载类，支持多线程，多任务下载;使用Interface作为callbacks的统一接口。
FileDownloader ver1.0 - ver3.0: Require HandlerCenter
FileDownloader ver4.0: Require HandlerGroup