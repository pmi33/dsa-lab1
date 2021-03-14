package ru.ssau.dsa.sockets.common;

import ru.ssau.dsa.sockets.common.messages.Message;
import ru.ssau.dsa.sockets.common.messages.Messages;

import java.io.IOException;
import java.net.Socket;

/**
 * Класс, описывающий соединение между двумя устройствами.
 * В данном класе содержатся методы для приема и отправки сообщений.
 */
public class Connection {

    /**
     * Вспомогательный функциональный интерфейс,
     * предназначенный для устранения дублирования кода
     * при чтении сообщения
     * @param <T> тип сообщения
     */
    private interface MessageSupplier<T extends Message<?>> {
        /**
         * Считывание сообщения
         * @throws IOException если чтение не удалось
         */
        T read() throws IOException;
    }

    /**
     * Сокет, через который будет осуществяться взаимодействие устройств
     */
    private final Socket socket;

    /**
     * Флаг, показывающий активно ли текущее соединение
     */
    private boolean connected;

    /**
     * Создание соединения с ip:port
     * @param ip IP адрес
     * @param port номер порта
     * @throws IOException если не удалось создать подключение
     */
    public Connection(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    /**
     * Создание соединения через сокет
     * @param socket сокет, срединяющий два устройства
     */
    public Connection(Socket socket) {
        this.socket = socket;
        connected = true;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public String toString() {
        return socket.getInetAddress().getHostAddress() + ':' + socket.getPort();
    }

    /**
     * Отправка сообщения через сокет
     * @param message отправляемое сообщение
     */
    public void sendMessage(Message<?> message){
        try {
            System.out.println("Send: " + message);
            Messages.sendMessage(socket.getOutputStream(), message);
        }catch (IOException e) {
            onIOException(e);
        }
    }

    /**
     * Чтение сообщение заранее неизвестного типа
     * @param <T> тип прочитанного сообщения
     * @return прочитанное сообщение
     */
    public <T extends Message<?>> T readMessage() {
        return readMessageSafety(() -> Messages.readMessage(socket.getInputStream()));
    }

    /**
     * Чтение сообщение заранее известного типа
     * @param <T> тип ожидаемого сообщения
     * @return прочитанное сообщение
     */
    public <T extends Message<?>> T readMessage(Class<T> messageClass) {
        return readMessageSafety(() -> Messages.readMessage(socket.getInputStream(), messageClass));
    }

    /**
     * Чтение сообщения с обработкой ошибок
     * @param messageSupplier объект, который читает сообщение
     * @param <T> тип сообщения
     * @return прочитанное сообщение
     */
    private <T extends Message<?>> T readMessageSafety(MessageSupplier<T> messageSupplier){
        try {
            T m = messageSupplier.read();
            System.out.println("Read: " + m);
            return m;
        }catch (IOException ignored) {
            disconnect();
        }
        return null;
    }

    /**
     * Обработка ошибки ввода-вывода
     */
    private void onIOException(IOException e) {
        if (!socket.isClosed()) {
            e.printStackTrace();
            disconnect();
        }
    }

    /**
     * Разрыв соединения
     */
    private void disconnect(){
        connected = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
