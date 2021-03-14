package ru.ssau.dsa.sockets.common.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Класс предоставляет базовый набор методов для чтения и отправки сообщений
 */
public class Messages {

    private static final String UNEXPECTED_ID = "Неожиданный идентификатор сообщения";
    private static final String END_OF_STREAM = "В потоке нет данных";

    /**
     * Восстановление сообщения заранее неизвестного типа из потока данных
     * @return восстановленное сообщение
     */
    public static <T extends Message<?>> T readMessage(InputStream stream) throws IOException {
        // получаем идентификатор сообщения, пришедший к нам в самом сообщении
        byte messageId = readNextByte(stream);
        MessageGenerator generator = MessageGenerator.getById(messageId);
        // читаем сообщения без повторного считывания первого байта
        return (T) readMessage(stream, generator.getClazz(), false);
    }

    /**
     * Восстановление сообщения из потока данных
     * @param clazz предполагаемый тип сообщения
     * @return восстановленное сообщение
     */
    public static <T extends Message<?>> T readMessage(InputStream stream, Class<T> clazz) throws IOException {
        // читаем сообщение из потока вместе с первым байтом
        return readMessage(stream, clazz, true);
    }

    /**
     * Восстановление сообщения из потока данных
     * @param stream поток данных
     * @param clazz ожидаемый тип сообщения
     * @param readMessageId флаг, позволяющий считывать (или не считывать) первый байт потока данных
     * @return восстановленное сообщение
     * @throws IOException если возникла ошибка при чтении данных из потока
     */
    private static <T extends Message<?>> T readMessage(InputStream stream, Class<T> clazz, boolean readMessageId) throws IOException {
        MessageGenerator generator = MessageGenerator.getByClass(clazz);
        if (readMessageId){
            // получаем идентификатор сообщения с помощью MessageGenerator
            byte id = generator.getMessageId();
            // получаем идентификатор сообщения, пришедший к нам в самом сообщении
            byte messageId = readNextByte(stream);
            // проверка соответствия ожидаемого идентификатора сообщения и полученного
            if (id != messageId){
                throw new IllegalStateException(UNEXPECTED_ID);
            }
        }
        Message<?> message = generator.create();
        // считываем размер сообщения
        int dataSize = readNextInt(stream);
        if (dataSize != 0){
            // в качестве данных сообщения устанавливаем массив байтов, который считаем из потока
            message.setData(readNextBytes(stream, dataSize));
        }
        return (T) message;
    }

    /**
     * Отправка сообщения
     * @param stream поток в который отправляется сообщение
     * @param message соббщение
     * @param <T> тип сообщения
     * @throws IOException если не удалось отправить данные
     */
    public static <T extends Message<?>> void sendMessage(OutputStream stream, T message) throws IOException {
        stream.write(message.getMessageId());
        stream.write(Message.intToBytes(message.getDataSize()));
        if (message.getDataSize() != 0){
            stream.write(message.dataToBytes());
        }
    }

    /**
     * Чтение интового числа из потока
     * @param stream входной поток
     * @return прочитанное интовое число
     * @throws IOException если данных в потоке нет или их не удалось прочитать
     */
    private static int readNextInt(InputStream stream) throws IOException {
        // массив, который будет содержать 4 байта для представления интового числа
        byte[] bytes = new byte[4];
        // читаем данные в только что объявленный массив,
        // если метод read() вернет -1, то данных в потоке нет
        if (stream.read(bytes) == -1){
            throw new IOException(END_OF_STREAM);
        }
        // преобразуем массив из 4 байтов в одно интовое число
        return Message.intFromBytes(bytes);
    }

    /**
     * Чтение байта из потока
     * @param stream входной поток
     * @return прочитанный байт
     * @throws IOException если данных в потоке нет или их не удалось прочитать
     */
    private static byte readNextByte(InputStream stream) throws IOException {
        // считываем первый байт из потока
        int i = stream.read();
        // согласно документации -1 эквивалетно отсутствию данных
        if (i == -1){
            throw new IOException("End of stream");
        }
        return (byte) i;
    }

    /**
     * Чтение массива байтов из потока
     * @param stream входной поток
     * @param length длина массива, который хотим прочитать
     * @return прочитанный массив байтов
     * @throws IOException если данных в потоке нет или их не удалось прочитать
     */
    private static byte[] readNextBytes(InputStream stream, int length) throws IOException {
        byte[] bytes = new byte[length];
        // читаем данные в только что объявленный массив,
        // если метод read() вернет -1, то данных в потоке нет
        if (stream.read(bytes) == -1){
            throw new IOException(END_OF_STREAM);
        }
        return bytes;
    }
}
