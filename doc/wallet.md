#  模块介绍
* wallet-common-->公用的一些方法类信息
* wallet-nofity-->区块的通知信息，btc和eth 区块钱包的通知信息
        application.properties 配置相关队列以及通知参数的相关信息
* wallet-blockcoin-->区块的rpc服务信息
        blockcoin.properties 配置区块服务的相关信息
* wallet-monitor-->定时去扫描提现的数据信息防止数据丢失
        application.properties 配置相关定时任务时间的相关信息

#  主要启动服务，监听的服务信息wallet-notify，监控的服务信息wallet-monitor
* wallet-notify 根据币种进行部署，一个币一个单点服务
   example: nohup java -Dcoin="eth" -jar notify-1.0.0.jar --server.port=8080 1>/dev/null 2>&1 &
            nohup java -Dcoin="btc" -jar notify-1.0.0.jar --server.port=8080 1>/dev/null 2>&1 &
   注意：-Dcoin="eth" 会将所有的ethtoken启动，不用在单独启动token币种
* wallet-monitor服务可以单点部署

# 启动添加java jvm的配置信息，同时关注下gc的日志
example:
 nohup java -jar -Xmx1024m -Xms1024m -verbose:gc -Xloggc:./gc.log -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSCompactAtFullCollection -XX:MaxTenuringThreshold=10  -XX:PermSize=256M -XX:MaxPermSize=512M -XX:SurvivorRatio=3  -XX:NewRatio=2 -XX:+PrintGCDateStamps   -XX:+PrintGCDetails -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=1 wallet-web-1.0.0.jar --server.port=2000 1>/dev/null 2>&1 &


