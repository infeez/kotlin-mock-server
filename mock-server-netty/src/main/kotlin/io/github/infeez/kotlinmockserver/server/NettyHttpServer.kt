package io.github.infeez.kotlinmockserver.server

import io.github.infeez.kotlinmockserver.mockmodel.MockWebRequest
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.ServerChannel
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.charset.Charset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Level

class NettyHttpServer(serverConfiguration: ServerConfiguration) : Server(serverConfiguration) {

    private var executor: ExecutorService? = null
    private var inet: InetSocketAddress? = null

    override fun start() {
        if (Epoll.isAvailable()) {
            start(EpollEventLoopGroup(), EpollServerSocketChannel::class.java)
        } else {
            start(NioEventLoopGroup(), NioServerSocketChannel::class.java)
        }
    }

    private fun start(
        loopGroup: EventLoopGroup,
        serverChannelClass: Class<out ServerChannel?>
    ) {
        executor = Executors.newCachedThreadPool {
            Thread(it, "NettyServerThread").apply {
                isDaemon = false
            }
        }
        executor!!.execute {
            println("Netty Started")
            try {
                inet = InetSocketAddress(InetAddress.getByName(serverConfiguration.host), serverConfiguration.port)
                val serverBootstrap = ServerBootstrap().apply {
                    option(ChannelOption.SO_BACKLOG, SO_BACKLOG)
                    option(ChannelOption.SO_REUSEADDR, true)
                    group(loopGroup).channel(serverChannelClass).childHandler(WebServerInitializer())
                    option(ChannelOption.MAX_MESSAGES_PER_READ, Int.MAX_VALUE)
                    childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator(true))
                    childOption(ChannelOption.SO_REUSEADDR, true)
                    childOption(ChannelOption.MAX_MESSAGES_PER_READ, Int.MAX_VALUE)
                }

                serverBootstrap.bind(inet).sync().channel().closeFuture().sync()
            } catch (t: IOException) {
                logger.log(Level.WARNING, t.message)
            } finally {
                loopGroup.shutdownGracefully().sync()
            }
        }
    }

    override fun stop() {
        executor!!.shutdownNow()
        println("Netty Stopped")
    }

    override fun getUrl(): String {
        if (inet == null) {
            error("Try getting url before start server")
        }

        // http hardcoded!
        return "http://${inet!!.hostName}"
    }

    private inner class WebServerInitializer : ChannelInitializer<SocketChannel>() {
        public override fun initChannel(ch: SocketChannel) {
            ch.pipeline().apply {
                addLast("decoder", HttpRequestDecoder(MAX_INITIAL_LINE_LENGTH, MAX_HEADER_SIZE, MAX_CHUNK_SIZE, false))
                addLast("aggregator", HttpObjectAggregator(MAX_CONTENT_LENGTH))
                addLast("encoder", HttpResponseEncoder())
                addLast("handler", WebServerHandler())
            }
        }
    }

    private inner class WebServerHandler : SimpleChannelInboundHandler<Any>() {
        override fun channelRead0(ctx: ChannelHandlerContext, msg: Any) {
            if (msg !is FullHttpRequest) {
                return
            }

            val mockWebRequest = MockWebRequest(
                method = msg.method().name(),
                path = msg.uri(),
                headers = msg.headers().associate { it.key to it.value },
                body = msg.content().copy().toString(Charset.forName("utf-8"))
            )

            onDispatch.invoke(mockWebRequest).let {
                writeResponse(ctx, it.code, it.body, it.headers, it.mockWebResponseParams.delay)
            }
        }
    }

    private fun writeResponse(
        ctx: ChannelHandlerContext,
        code: Int,
        content: String?,
        headers: Map<String, String> = emptyMap(),
        delayMs: Long = 0
    ) {
        val bytes = content?.toByteArray() ?: byteArrayOf()
        val entity = Unpooled.wrappedBuffer(bytes)

        val response = DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.valueOf(code),
            entity,
            false
        )
        val dateTime = ZonedDateTime.now()
        val formatter = DateTimeFormatter.RFC_1123_DATE_TIME
        (response.headers() as DefaultHttpHeaders).apply {
            this[HttpHeaderNames.SERVER] = "NettyMockServer"
            this[HttpHeaderNames.DATE] = dateTime.format(formatter)
            this[HttpHeaderNames.CONTENT_LENGTH] = bytes.size
            if (headers.isNotEmpty()) {
                headers.forEach(::add)
            }
        }

        sleepIfDelayed(delayMs)

        ctx.writeAndFlush(response, ctx.voidPromise())
    }

    private fun sleepIfDelayed(delayMs: Long) {
        if (delayMs != 0L) {
            Thread.sleep(delayMs)
        }
    }

    companion object {
        private const val SO_BACKLOG = 1024
        private const val MAX_INITIAL_LINE_LENGTH = 4096
        private const val MAX_HEADER_SIZE = 8192
        private const val MAX_CHUNK_SIZE = 8192
        private const val MAX_CONTENT_LENGTH = 100 * 1024 * 1024
    }
}
