package com.hehnev.netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import messages.AbstractMessage;
import messages.FileMessage;
import messages.ListMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Класс сервера предназначен для распознавания командных сообщений и обработчиков команд управления.
 */

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private Path serverClientDir;
    private byte[] buffer;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        serverClientDir = Path.of("server"); // При подключении создать папку TODO сделать при регистрации
        ctx.writeAndFlush(new ListMessage(serverClientDir)); // При подключении клиента отправляем ему список файлов
        buffer = new byte[8192];
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {
        switch (msg.getType()) {
            case FILE_MESSAGE:
                processFile((FileMessage) msg, ctx);
                break;
            case FILE_REQUEST:
                sendFile((FileMessage) msg, ctx);
                break;
        }
    }

    /**
     * Метод для отправки файла клиенту
     * @param msg информация о фале
     * @param ctx объект соединения netty, установленного с клиентом
     */

    private void sendFile(FileMessage msg, ChannelHandlerContext ctx) throws IOException {
        boolean isFirstButch = true;
        Path filePath = serverClientDir.resolve(msg.getName()); // Путь до запрашиваемого файла
        long size = Files.size(filePath); // Размер файла
        try (FileInputStream is = new FileInputStream(filePath.toFile())) {
            int read;
            while ((read = is.read(buffer)) != -1) {
                FileMessage message = FileMessage.builder() // Собрали объект
                        .bytes(buffer)
                        .name(filePath.getFileName().toString())
                        .size(size)
                        .isFirstBatch(isFirstButch)
                        .isFinishBatch(is.available() <= 0)
                        .endByteNum(read)
                        .build();
                ctx.writeAndFlush(message); // Отправили в канал наш объект
                isFirstButch = false;
            }
        } catch (Exception e) {
            log.error("e:", e);
        }
    }


    /**
     * Метод обрабатывающий сообщения нескольких пакетов файла от клиента
     * @param msg информация о фале
     * @param ctx объект соединения netty, установленного с клиентом
     */
    private void processFile(FileMessage msg, ChannelHandlerContext ctx) throws Exception {
        Path file = serverClientDir.resolve(msg.getName());
        if (msg.isFirstBatch()) {
            Files.deleteIfExists(file);
        }

        try (FileOutputStream os = new FileOutputStream(file.toFile(), true)) {
            os.write(msg.getBytes(), 0, msg.getEndByteNum());
        }

        if (msg.isFinishBatch()) {
            ctx.writeAndFlush(new ListMessage(serverClientDir));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
