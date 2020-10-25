package com.mostro.ircchat.infrastructure

import com.mostro.ircchat.infrastructure.repository.InMemoryChatRoomRepository
import com.mostro.ircchat.infrastructure.repository.InMemoryChatUserRepository
import com.mostro.ircchat.infrastructure.repository.InMemoryLoggedUserRepository
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.util.concurrent.GlobalEventExecutor

class NettyChatServer(private val port: Int) : ServerBootstrap() {

    internal val chatUserRepository = InMemoryChatUserRepository()
    internal val loggedUserRepository = InMemoryLoggedUserRepository()
    internal val chatRoomRepository = InMemoryChatRoomRepository()
    internal val activeNettyChannelRepository = DefaultChannelGroup("chatUsers", GlobalEventExecutor.INSTANCE)

    fun start() {
        val bossGroup: EventLoopGroup = NioEventLoopGroup()
        val workerGroup: EventLoopGroup = NioEventLoopGroup()

        try {
            val httpBootstrap = ServerBootstrap()
            httpBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(NettyChatServerInitializer())
                .handler( LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)

            val httpChannel: ChannelFuture = httpBootstrap.bind(port).sync()
            println(">> IRC server has started in port $port")
            httpChannel.channel().closeFuture().sync()
        } catch(e: Exception) {
            println(e.message)
        } finally {
            workerGroup.shutdownGracefully()
            bossGroup.shutdownGracefully()
        }
    }

    inner class NettyChatServerInitializer : ChannelInitializer<Channel>() {
        override fun initChannel(ch: Channel) {
            val pipeline: ChannelPipeline = ch.pipeline()

            pipeline.addLast("framer", DelimiterBasedFrameDecoder(8192, *Delimiters.lineDelimiter()))
            pipeline.addLast("decoder", StringDecoder())
            pipeline.addLast("encoder", StringEncoder())

            pipeline.addLast("handler", NettyChatServerHandler(this@NettyChatServer))
        }
    }
}