1. 安装maven

   XP的同学：
   在 http://maven.apache.org/ 下载maven，我装的是2.2.1的，最新的3.0还没有看过
   设置MAVEN_HOME
   在PATH中
   ;%MAVEN_HOME%\bin

   Ubuntu的同学:
   sudo apt-get install maven2
   
2. 检出
   svn checkout https://litefetion.googlecode.com/svn/trunk litefetion
   
3. 导入到eclipse
   cd litefetion
   mvn eclipse:eclipse
   之后再eclipse中  File -> Import ->Existing Projects into Workspace

4. Eclipse的设置
   Window -> Preferences -> Java -> Build Path -> Classpath Variables，New
   Name命名为M2_REPO，路径为你的本地的maven类库地址，
      Window下为C:/Documents and Settings/{user}/.m2/repository，
      Linux下为/home/{user}/.m2/repository
设置成功后重新编译即可

5. 打包
   mvn clean package
  
      





