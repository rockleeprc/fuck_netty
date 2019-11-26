## netty组件
* Channel：Socket
* EventLoop：控制流、多线程处理、并发
* ChannelFuture：异步通知

## Channel

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