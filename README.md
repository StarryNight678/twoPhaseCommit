# twoPhaseCommit
# 两阶段提交协议模拟

#说明
采用Java，Socket通信方式

客户端提交写入数据的请求。
协调者使用两阶段协议协调各个参与者，写入文件，保证文件写入的正确性。

过程图如下：
Tool类中，将发送和接收消息过程进行封装。
Signal类中，定义了各个传送的信号常量。

#运行程序
1. 启动协调者程序。协调者，会显示，参与者的启动和状态情况。
2. 依次启动三个（必须是3个，在程序中可以自己定义）参与者进程
运行命令
···
java Participant
···
3. 启动写入客户端，输入要写入数据
运行命令
···
java Client
···

## 结果
1)	都启动后，协调者显示信息如下。
--Coordinator start--
参与者0 启动
接受到消息:Participant 0 is ok.
参与者1 启动
接受到消息:Participant 1 is ok.
参与者2 启动
接受到消息:Participant 2 is ok.
----ProcessClientRequests----
与客户端连接成功！

1）在客户端输入信息，
2）参与者：收到提交请求和正式提交的两个消息。
3）协调者，得到各个参与者的同意提交和提交完成的两个消息。
4） 产生文件
文件写入路径，工程的bin目录下 MyTwo_phaseCommit\bin

## 错误处理
错误产生，数据回滚
将参与者1关闭，协调者显示。参与者1出错。
参与者2，得到进行回滚的消息.
参与者0，得到进行回滚的消息。
文件没有被写入

#参考
[1] 二阶段提交 https://zh.wikipedia.org/wiki/%E4%BA%8C%E9%98%B6%E6%AE%B5%E6%8F%90%E4%BA%A4
[2] 深入理解java异常处理机制http://blog.csdn.net/hguisu/article/details/6155636







