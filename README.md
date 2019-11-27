## netty组件
* Channel：Socket
* EventLoop：控制流、多线程处理、并发
* ChannelFuture：异步通知

## Channel
* 每个 Channel 都将会被分配一个 ChannelPipeline 和 ChannelConfig
* ChannelConfig 包含了该 Channel 的所有配置设置，并且支持热更新

### Channel的生命周期状态
* ChannelUnregistered  Channel 已经被创建，但还未注册到 EventLoop 
* ChannelRegistered  Channel 已经被注册到了 EventLoop 
* ChannelActive  Channel处于活动状态（已经连接到它的远程节点）。它现在可以接收和发送数据了 
* ChannelInactive  Channel 没有连接到远程节点

### ChannelHandler声明周期
* handlerAdded  当把 ChannelHandler 添加到 ChannelPipeline 中时被调用 
* handlerRemoved  当从 ChannelPipeline 中移除 ChannelHandler 时被调用 
* exceptionCaught  当处理过程中在 ChannelPipeline 中有错误产生时被调用

## EventLoop
* 一个EventLoopGroup包含一个或者多个EventLoop； 
* 一个EventLoop在它的生命周期内只和一个Thread绑定； 
* 所有由EventLoop处理的I/O事件都将在它专有的Thread上被处理； 
* 一个Channel在它的生命周期内只注册于一个EventLoop； 
* 一个EventLoop可能会被分配给一个或多个Channel； 

## ChannelHandler

## ChannelPipeline
* ChannelPipeline提供了ChannelHandler链的容器，并定义了用于在该链上传播入站和出站事件流的API。当Channel被创建时，它会被自动地分配到它专属的ChannelPipeline。
* 有两种发送消息的方式：
    * 直接写到Channel中，将会导致消息从ChannelPipeline的尾端开始流动
    * 写到和ChannelHandler相关联的ChannelHandlerContext对象中，将导致消息从ChannelPipeline中的下一个ChannelHandler开始流动