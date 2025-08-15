# Getting Started

### pre requirements
linux with IO_URING enabled
### start java process
```bash
./mvnw spring-boot:run
```

```
2025-08-15T06:16:58.117Z  INFO 30530 --- [uds] [ty-start-thread] com.chaney.infra.uds.NettyStarter        : netty event loop group 使用 io_uring方式构建
2025-08-15T06:16:58.183Z  INFO 30530 --- [uds] [ty-start-thread] com.chaney.infra.uds.NettyStarter        : ready to accept connections
```

### 启动python进程连接socket

```bash
python3 tmp/socket-test.py -s tmp/demo.sock -m "hello" -c 10 -t 10
```
应该就能看到如下日志：

ps. 如果修改配置文件中的 netty.vth=false，就不会出现这个问题
```
2025-08-15T06:20:45.484Z  INFO 31565 --- [uds] [     work-vth-2] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:20:45.485Z  INFO 31565 --- [uds] [     work-vth-7] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:20:45.485Z  INFO 31565 --- [uds] [    work-vth-10] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:20:45.485Z  INFO 31565 --- [uds] [     work-vth-5] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:20:45.485Z  INFO 31565 --- [uds] [     work-vth-3] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:20:45.486Z  INFO 31565 --- [uds] [     work-vth-8] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:20:45.487Z  INFO 31565 --- [uds] [     work-vth-1] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:20:55.494Z  INFO 31565 --- [uds] [     work-vth-7] c.c.infra.uds.ProcessMessageHandler      : channelRead
2025-08-15T06:20:45.484Z  INFO 31565 --- [uds] [     work-vth-6] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:20:55.495Z  INFO 31565 --- [uds] [     work-vth-2] c.c.infra.uds.ProcessMessageHandler      : channelRead
2025-08-15T06:20:45.484Z  INFO 31565 --- [uds] [     work-vth-4] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:20:55.495Z  INFO 31565 --- [uds] [     work-vth-5] c.c.infra.uds.ProcessMessageHandler      : channelRead
2025-08-15T06:20:55.495Z  INFO 31565 --- [uds] [     work-vth-3] c.c.infra.uds.ProcessMessageHandler      : channelRead
2025-08-15T06:20:55.494Z  INFO 31565 --- [uds] [     work-vth-8] c.c.infra.uds.ProcessMessageHandler      : channelRead
2025-08-15T06:20:55.495Z  INFO 31565 --- [uds] [     work-vth-6] c.c.infra.uds.ProcessMessageHandler      : channelRead
2025-08-15T06:20:55.691Z  INFO 31565 --- [uds] [     work-vth-2] c.c.infra.uds.ProcessMessageHandler      : channelInactive
2025-08-15T06:20:55.692Z  INFO 31565 --- [uds] [     work-vth-3] c.c.infra.uds.ProcessMessageHandler      : channelInactive
2025-08-15T06:20:55.693Z  INFO 31565 --- [uds] [     work-vth-5] c.c.infra.uds.ProcessMessageHandler      : channelInactive
2025-08-15T06:20:55.693Z  INFO 31565 --- [uds] [     work-vth-8] c.c.infra.uds.ProcessMessageHandler      : channelInactive
2025-08-15T06:20:55.692Z  INFO 31565 --- [uds] [     work-vth-7] c.c.infra.uds.ProcessMessageHandler      : channelInactive
2025-08-15T06:20:55.856Z  WARN 31565 --- [uds] [     work-vth-6] i.n.u.c.SingleThreadEventExecutor        : Unexpected exception from an event executor: 

java.lang.RuntimeException: SQ ring full and no submissions accepted
        at io.netty.channel.uring.SubmissionQueue.enqueueSqe(SubmissionQueue.java:125) ~[netty-transport-classes-io_uring-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.uring.SubmissionQueue.addTimeout(SubmissionQueue.java:189) ~[netty-transport-classes-io_uring-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.uring.IoUringIoHandler.submitAndWaitWithTimeout(IoUringIoHandler.java:343) ~[netty-transport-classes-io_uring-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.uring.IoUringIoHandler.run(IoUringIoHandler.java:180) ~[netty-transport-classes-io_uring-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.SingleThreadIoEventLoop.runIo(SingleThreadIoEventLoop.java:206) ~[netty-transport-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.SingleThreadIoEventLoop.run(SingleThreadIoEventLoop.java:177) ~[netty-transport-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:1073) ~[netty-common-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) ~[netty-common-4.2.2.Final.jar:4.2.2.Final]
        at java.base/java.lang.VirtualThread.run(VirtualThread.java:466) ~[na:na]

Exception in thread "work-vth-6" java.lang.RuntimeException: SQ ring full and no submissions accepted
        at io.netty.channel.uring.SubmissionQueue.enqueueSqe(SubmissionQueue.java:125)
        at io.netty.channel.uring.SubmissionQueue.addCancel(SubmissionQueue.java:208)
        at io.netty.channel.uring.IoUringIoHandler.drainEventFd(IoUringIoHandler.java:452)
        at io.netty.channel.uring.IoUringIoHandler.destroy(IoUringIoHandler.java:387)
        at io.netty.channel.SingleThreadIoEventLoop.cleanup(SingleThreadIoEventLoop.java:247)
        at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:1147)
        at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
        at java.base/java.lang.VirtualThread.run(VirtualThread.java:466)
2025-08-15T06:20:45.485Z  INFO 31565 --- [uds] [     work-vth-9] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:20:55.860Z  WARN 31565 --- [uds] [     work-vth-9] i.n.u.c.SingleThreadEventExecutor        : Unexpected exception from an event executor: 

java.lang.RuntimeException: ioUringEnter syscall returned -9
        at io.netty.channel.uring.SubmissionQueue.submit(SubmissionQueue.java:238) ~[netty-transport-classes-io_uring-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.uring.SubmissionQueue.submitAndWait(SubmissionQueue.java:222) ~[netty-transport-classes-io_uring-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.uring.IoUringIoHandler.submitAndWaitWithTimeout(IoUringIoHandler.java:346) ~[netty-transport-classes-io_uring-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.uring.IoUringIoHandler.run(IoUringIoHandler.java:180) ~[netty-transport-classes-io_uring-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.SingleThreadIoEventLoop.runIo(SingleThreadIoEventLoop.java:206) ~[netty-transport-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.SingleThreadIoEventLoop.run(SingleThreadIoEventLoop.java:177) ~[netty-transport-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:1073) ~[netty-common-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) ~[netty-common-4.2.2.Final.jar:4.2.2.Final]
        at java.base/java.lang.VirtualThread.run(VirtualThread.java:466) ~[na:na]

Exception in thread "work-vth-9" java.lang.RuntimeException: ioUringEnter syscall returned -9
        at io.netty.channel.uring.SubmissionQueue.submit(SubmissionQueue.java:238)
        at io.netty.channel.uring.SubmissionQueue.submitAndWait(SubmissionQueue.java:222)
        at io.netty.channel.uring.IoUringIoHandler.drainEventFd(IoUringIoHandler.java:443)
        at io.netty.channel.uring.IoUringIoHandler.destroy(IoUringIoHandler.java:387)
        at io.netty.channel.SingleThreadIoEventLoop.cleanup(SingleThreadIoEventLoop.java:247)
        at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:1147)
        at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
        at java.base/java.lang.VirtualThread.run(VirtualThread.java:466)
2025-08-15T06:20:55.494Z  INFO 31565 --- [uds] [     work-vth-1] c.c.infra.uds.ProcessMessageHandler      : channelRead
2025-08-15T06:20:55.861Z  INFO 31565 --- [uds] [     work-vth-1] c.c.infra.uds.ProcessMessageHandler      : channelInactive
```
也可能看到如下报错

```
2025-08-15T06:32:03.832Z  INFO 37178 --- [uds] [ty-start-thread] com.chaney.infra.uds.NettyStarter        : netty event loop group 使用 io_uring方式构建
2025-08-15T06:32:03.886Z  INFO 37178 --- [uds] [ty-start-thread] com.chaney.infra.uds.NettyStarter        : ready to accept connections
2025-08-15T06:32:17.174Z  INFO 37178 --- [uds] [     work-vth-7] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:32:17.175Z  INFO 37178 --- [uds] [     work-vth-6] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:32:17.175Z  INFO 37178 --- [uds] [     work-vth-1] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:32:17.175Z  INFO 37178 --- [uds] [     work-vth-5] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:32:17.175Z  INFO 37178 --- [uds] [     work-vth-2] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:32:17.175Z  INFO 37178 --- [uds] [     work-vth-4] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:32:17.175Z  INFO 37178 --- [uds] [     work-vth-3] c.c.infra.uds.ProcessMessageHandler      : channelActive
2025-08-15T06:32:17.190Z  WARN 37178 --- [uds] [     work-vth-3] i.n.u.c.SingleThreadEventExecutor        : Unexpected exception from an event executor: 

java.lang.RuntimeException: SQ ring full and no submissions accepted
        at io.netty.channel.uring.SubmissionQueue.enqueueSqe(SubmissionQueue.java:125) ~[netty-transport-classes-io_uring-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.uring.SubmissionQueue.addTimeout(SubmissionQueue.java:189) ~[netty-transport-classes-io_uring-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.uring.IoUringIoHandler.submitAndWaitWithTimeout(IoUringIoHandler.java:343) ~[netty-transport-classes-io_uring-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.uring.IoUringIoHandler.run(IoUringIoHandler.java:180) ~[netty-transport-classes-io_uring-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.SingleThreadIoEventLoop.runIo(SingleThreadIoEventLoop.java:206) ~[netty-transport-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.channel.SingleThreadIoEventLoop.run(SingleThreadIoEventLoop.java:177) ~[netty-transport-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:1073) ~[netty-common-4.2.2.Final.jar:4.2.2.Final]
        at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) ~[netty-common-4.2.2.Final.jar:4.2.2.Final]
        at java.base/java.lang.VirtualThread.run(VirtualThread.java:466) ~[na:na]
```


