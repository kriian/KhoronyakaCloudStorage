package com.hehnev.netty.server;

import com.hehnev.netty.handlers.ChatMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;

@Slf4j
public class Server {

    public Server() {
        EventLoopGroup auth = new NioEventLoopGroup(1); // лайт оброботчик для приема входящих подключений
        EventLoopGroup worker = new NioEventLoopGroup(); // авто создания потоков с учетом ЦП для обработки потоков данных

        try {
            /*
             * helper класса ServerBootstrap.
             * Этот объект позволяет сконфигурировать сервер,
             * наполнить его ключевыми компонентами и наконец, запустить.
             */
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker) // передали наших работников
                    .channel(NioServerSocketChannel.class) // создаем новый канал для приема сообщений на сервере
                    .childHandler(new ChannelInitializer<>() { // настраиваем канал

                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            /*
                             * добавляем декодоры енкодоры с лего на право
                             * всегда сначало отработаю все in потом out
                             * первый in ByteBuff - последний out ByteBuff
                             */
                            channel.pipeline().addLast(
                                    new StringEncoder(StandardCharsets.UTF_8), // out-1
                                    new StringDecoder(StandardCharsets.UTF_8), // in-1
                                    new ChatMessageHandler() //  in-2
                            );
                        }
                    });
            ChannelFuture future = bootstrap.bind(8189).sync(); // задаем порт и запускаем сервер
            log.debug("Server started...");
            future.channel().closeFuture().sync(); // закрываем сервер
            log.debug("Server closed");
        } catch (InterruptedException e) {
            log.error("error: ", e);
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
