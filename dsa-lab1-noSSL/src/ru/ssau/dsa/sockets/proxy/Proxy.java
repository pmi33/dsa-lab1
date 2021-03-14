package ru.ssau.dsa.sockets.proxy;

import ru.ssau.dsa.sockets.common.Connection;
import ru.ssau.dsa.sockets.common.imagechunk.ImageChunk;
import ru.ssau.dsa.sockets.common.imagechunk.ImageChunkMessage;
import ru.ssau.dsa.sockets.common.messages.Message;

/**
 * Прокси-сервер
 */
public class Proxy  {

    /**
     * Вероятность шума на изображении
     */
    private static final double NOISE_FACTOR = 0.1;

    /**
     * Вероятность потери сообщения
     */
    private static final double DELIVERY_FACTOR = 0.1;


    /**
     * Класс, позволяющий в отдельном потоке пересылать
     * сообщения из одного соединения в другое
     */
    public static class ThreadConnection extends Thread{

        /**
         * Соединение из которого будем пересылать данные
         */
        private Connection from;

        /**
         * Соединение в которое будем пересылать данные
         */
        private Connection to;

        public ThreadConnection(Connection from, Connection to) {
            this.from = from;
            this.to = to;
            this.setDaemon(true);
        }

        @Override
        public void run() {
            System.out.println("Соединение " + from + " -> " + to + " открыто");
            do {
                Message<?> m = from.readMessage();
                if (m == null){
                    continue;
                }
                if (m instanceof ImageChunkMessage) {
                    ImageChunk original = ((ImageChunkMessage) m).getData();
                    if (Math.random() > DELIVERY_FACTOR) {
                        ImageChunkMessage im = new ImageChunkMessage();

                        ImageChunk chunk = new ImageChunk(original.getNumber(), makeNoise(original.getPixels()));
                        im.setData(chunk);
                        // отправляем зашумленное изображение
                        to.sendMessage(im);
                    }
                } else {
                    to.sendMessage(m);
                }
            } while (from.isConnected() && to.isConnected());
            System.out.println("Соединение " + from + " -> " + to + " закрыто");
        }

        private byte[] makeNoise(byte[] src){
            for (int i = 0; i < src.length; i++) {
                if (Math.random() < NOISE_FACTOR){
                    src[i] = 0;
                }
            }
            return src;
        }
    }

    /**
     * Создание прокси сервера, который в двух разных потоках будет
     * пересылать данные от клиента к серверу и обратно
     * @param client соединение с клиентом
     * @param server соединение с сервером
     */
    public Proxy(Connection client, Connection server){
        new ThreadConnection(client, server).start();
        new ThreadConnection(server, client).start();
    }

}
