=============================================
LiteFetion的说明
http://lifefetion.googlecode.com
=============================================


== 关于LiteFetion==

LiteFetion使用官方飞信的[https://webim.feixin.10086.cn WebFetion]的通信协议，模拟浏览器和服务器交互，完成发送消息，添加好友的操作。
LiteFetion已经完全实现了WebFetion的全部功能，能基本实现常用的操作，是对WebFetion的友好封装。您可以使用LiteFetion将飞信应用嵌入到您的应用程序中，实现发送短信等。 
做这个项目的目的，是希望大家能充分的利用飞信资源，方便自己应用，但请你们不要使用MapleFetion做一些无聊的事情。

== LiteFetion 1.0.0 特性 ==

    # 使用WebFetion的协议
    # 支持使用飞信号或者手机号登陆
    # 接收和发送短信或则飞信
    # 给自己发送短信
    # 以飞信号或者手机号添加好友
    # 同意或者拒绝对方添加好友请求
    # 修改自己个性签名
    # 修改在线状态
    # 获取好友头像
    # 将好友加入黑名单
    # 支持好友分组
    # 完全同的步的操作
  
== 接口调用 ==
由于我比较懒，LiteFetion的接口也很简单，注释也很详尽，就懒得写文档了，不过我写了一个测试文件(在test目录下，LiteFetionTest.java)，里面已经包含了基本上所有的操作的调用方法，可以作为参考。
如果在使用过程中有疑问，或者有更好的建议，热烈欢迎您给我发送邮件，我会在第一时间给您回复的。

== 兄弟项目 - [http://maplefetion.googlecode.com MapleFetion] ==
MapleFetion是LiteFetion的兄弟项目，使用的是官方的客户端的通信协议，模拟客户端登录飞信服务器来完成操作，功能更强大，性能更加稳定，如果LiteFetion满足不了您的需求，欢迎您可以尝试使用

MapleFetion. [http://maplefetion.googlecode.com 项目地址]

== 关于作者 ==
欢迎光临我的博客: http://www.solosky.net
或者给我发邮件  ：solosky772@qq.com

欢迎大家测试并提出改进意见，我会倾听大家的建议和反馈，做最好的飞信开发库。


== 重要声明 ==
   # LiteFetion完全是把WebFetion当做黑盒研究，不存在破解或重打包客户端等行为。
   # LiteFetion以学习为目的，不涉及任何商业利益。任何企业和个人与此接口有关的商业行为，请与移动公司联系。
   # 任何人使用LiteFetion而造成的不良后果，均由使用者承担，与MapleFetion的作者没有任何关系。
