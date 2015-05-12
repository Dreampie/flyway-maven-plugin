# flyway-maven-plugin
flyway maven plugin  是基于flyway的的数据库脚本升级插件

## 使用方式
### 1. 在resources下创建application.propeties，存放数据库相关配置
```JAVA
#default表示数据源的名字
db.default.url=jdbc:mysql://127.0.0.1/resty-demo?useUnicode=true&characterEncoding=UTF-8
db.default.user=dev
db.default.password=dev1010

#flyway database migration
#验证失败时自动清理
flyway.default.valid.clean=true
#自动升级，如果false会先检测数据库状态
flyway.default.migration.auto=true
#执行升级时，如果数据库还没有被初始化执行初始化
flyway.default.migration.initOnMigrate=true
```
### 2. 配置Maven插件
```XML
<plugin>
  <groupId>cn.dreampie</groupId>
  <artifactId>flyway-maven-plugin</artifactId>
  <version>1.1</version>
  <configuration>
   <!-配置文件--->
    <config>${basedir}/src/main/resources/application.properties</config>
    <!--sql脚本目录 多个数据库 按数据源名字创建文件夹  如：db/migration/default,db/migration/demo 配置只需要到migration目录-->
    <location>filesystem:${basedir}/src/main/resources/db/migration/</location>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>${mysql.version}</version>
    </dependency>
  </dependencies>
</plugin>
```

