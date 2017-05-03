# 帆软报表连接redis缓存数据库插件
## 插件编译
将帆软报表安装目录下的相关jar包:

$installDir/fr-designer-core-8.0.jar

$installDir/fr-designer-chart-8.0.jar

$installDir/fr-designer-report-8.0.jar

$installDir/WebReport/WEB-INF/lib/fr-core-8.0.jar

$installDir/WebReport/WEB-INF/lib/fr-chart-8.0.jar

$installDir/WebReport/WEB-INF/lib/fr-report-8.0.jar

$installDir/WebReport/WEB-INF/lib/fr-platform-8.0.jar

$installDir/WebReport/WEB-INF/lib/fr-third-8.0.jar

拷贝到项目工程的lib/report目录下（如果没有该目录则自行新建）

然后执行ant命名来编辑插件包：
`ant -f build.xml jar`

## 插件安装
使用帆软设计器自带的插件管理器即可安装。
## 插件使用
### 新建数据连接
安装完插件后，在新建数据连接的地方可以看到Redis类型的数据连接，点击新建后可以看到如下的界面：

![1](screenshots/1.png)

按实际的配置填写上即可。

### 新建数据集
在模板数据集添加的地方会出现Redis数据集:

![1](screenshots/2.png)

点击新建后会出现如下的界面，输入查询条件即可：

![1](screenshots/3.png)


### 支持的查询语句

mget key [key ...]

hgetall key

lrange key 0 -1

smembers key

zrange key 0 -1

### 更多查询语句的支持
只需要实现com.fr.plugin.db.redis.core.visit.Visitor接口，并在VisitorFactory中注册即可。


