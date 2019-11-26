## netty组件
* Channel：Socket； 
* EventLoop：控制流、多线程处理、并发； 
* ChannelFuture：异步通知

## Channel

## EventLoop
* 一个 EventLoopGroup 包含一个或者多个 EventLoop； 
* 一个 EventLoop 在它的生命周期内只和一个 Thread 绑定； 
* 所有由 EventLoop 处理的 I/O 事件都将在它专有的 Thread 上被处理； 
* 一个 Channel 在它的生命周期内只注册于一个 EventLoop； 
* 一个 EventLoop 可能会被分配给一个或多个 Channel； 

## ChannelHandler

## ChannelPipeline
* ChannelPipeline 提供了 ChannelHandler 链的容器，并定义了用于在该链上传播入站和出站事件流的API。当Channel被创建时， 它会被自动地分配到它专属的ChannelPipeline。
* 在Netty中， 有两种发送消息的方式。直接写到Channel中，也 可以 写到和ChannelHandler相关联的ChannelHandlerContext对象中。前一种方式将会导致消息从ChannelPipeline 的尾端开始流动，而后者将导致消息从 ChannelPipeline 中的下一个 ChannelHandler 开始流动。