package io.ffreedom.transport.netty;

import io.ffreedom.common.annotation.mark.TestMain;
import io.ffreedom.transport.base.role.TransportServer;
import io.ffreedom.transport.netty.config.NettyConfigurator;
import io.ffreedom.transport.netty.handler.GeneralNettyHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer extends NettyTransport implements TransportServer {

	private EventLoopGroup bossGroup;
	private ServerBootstrap serverBootstrap;

	/**
	 * @param tag
	 * @param configurator
	 * @param lambdaInterface
	 * @param mode
	 */
	public NettyServer(String tag, NettyConfigurator configurator, ChannelHandler... channelHandlers) {
		super(tag, configurator, channelHandlers);
	}

	public void init() {
		this.bossGroup = new NioEventLoopGroup();
		this.serverBootstrap = new ServerBootstrap();
		this.serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel socketChannel) throws Exception {
						socketChannel.pipeline().addLast(channelHandlers);
					}
				}).option(ChannelOption.SO_BACKLOG, configurator.getBacklog())
				.childOption(ChannelOption.SO_KEEPALIVE, configurator.isKeepAlive())
				.childOption(ChannelOption.TCP_NODELAY, configurator.isTcpNoDelay());
		logger.info(tag + " : Init-ServerBootStrap.bind -> " + configurator.getPort());
	}

	@Override
	public void startup() {
		try {
			// Start server.
			serverBootstrap.bind(configurator.getHost(), configurator.getPort()).sync()
					// Wait close.
					.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			logger.error("NettyServer method startup() -> " + e.getMessage());
			logger.error(e.getStackTrace());
			destroy();
		}
	}

	@Override
	public void destroy() {
		logger.info("NettyServer call method destroy().");
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}

	@Override
	public boolean isConnected() {
		return bossGroup.isShutdown() && workerGroup.isShutdown();
	}

	@TestMain
	public static void main(String[] args) throws Exception {

		NettyConfigurator configurator = NettyConfigurator.builder().setHost("192.168.1.138").setPort(7901).build();

		NettyServer nettyServer = new NettyServer("LocalTest", configurator, new GeneralNettyHandler() {

			@Override
			public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
				sendBytes(ctx, "hello".getBytes());
			}

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				byte[] recvBytes = getRecvBytes((ByteBuf) msg);
				System.out.println(new String(recvBytes));
			}

		});

		nettyServer.startup();

	}

}