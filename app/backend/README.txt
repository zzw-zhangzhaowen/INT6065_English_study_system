英语等级智能学习与考试系统 - 后端运行说明
================================================

【环境要求】
1. Java 17 或更高版本
2. MySQL 8.0 或更高版本
3. （可选）Redis 6.0+ - 用于缓存功能
4. （可选）ActiveMQ - 用于消息队列功能

【快速启动】

1. 创建数据库
   - 依次执行 sql/schema.sql、sql/init-data.sql
   - 如需社区相关表，再执行 sql/community_tables.sql
   - 默认数据库名: english_learning
   - 默认用户名: root，密码: 123456

2. 修改数据库配置（如需要）
   - 编辑 src/main/resources/application.yml 中的数据库连接信息
   - 修改后需重新打包，或用外部配置文件覆盖

3. 启动服务
   - 双击运行 start.bat
   - 或命令行执行: java -jar english-learning-system-1.0.0.jar

4. 访问服务
   - API地址: http://localhost:8080
   - Swagger文档: http://localhost:8080/swagger-ui.html

【文件说明】
- english-learning-system-1.0.0.jar : 主程序（包含所有依赖）
- start.bat : Windows启动脚本
- sql/community_tables.sql : 数据库初始化脚本
- application.yml : 配置文件（在jar内，可通过外部配置覆盖）

【注意事项】
- 确保MySQL服务已启动
- 确保数据库 english_learning 已创建
- 默认端口8080，如被占用可修改 application.yml 中的 server.port
