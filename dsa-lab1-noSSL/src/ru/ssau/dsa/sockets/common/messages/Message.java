package ru.ssau.dsa.sockets.common.messages;

import java.util.stream.Stream;

/**
 * Базовый класс для сообщений
 * @param <T> тип объекта, содержащегося в сообщении
 */
public abstract class Message<T> {

    private static final String MESSAGE_PATTERN = "Сообщение {тип: %d, размер: %4d, данные: %s}";

    private static final String ILLEGAL_INDEXES = "Некорректные позиции начала и конца";

    private static final String ARRAY_LENGTH_EXCEPTION_MESSAGE = "Размер массива байтов некорректен";

    /**
     * Идентификатор типа сообщения
     */
    private byte messageId;

    /**
     * Размер данных в сообщении
     */
    private int dataSize;

    /**
     * Данные, передаваемые в сообщении
     */
    protected T data;

    /**
     * Восстановление объекта c данными из массива байтов
     * @param array байтовое представление объекта
     * @return восстановленный объект
     */
    public abstract T dataFromBytes(byte[] array);

    /**
     * Конвертация объекта с данными в массив байтов
     * @return байтовое представление объекта
     */
    public abstract byte[] dataToBytes();

    /**
     * Конвертация данных в строку
     */
    public abstract String dataToString();

    /**
     * Создание сообщения.
     * При создании сообщения на лету определяется идентификатор типа сообщения.
     */
    public Message(){
        messageId = MessageGenerator.getByClass(this.getClass()).getMessageId();
        dataSize = 0;
    }

    public byte getMessageId() {
        return messageId;
    }

    public int getDataSize() {
        return dataSize;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
        dataSize = dataToBytes().length;
    }

    /**
     * Установка данных сообщения, представленных в байтовом виде.
     * При вызове метода данные будут конвертированы из байтов в объект.
     */
    public void setData(byte[] bytes) {
        this.data = dataFromBytes(bytes);
        dataSize = bytes.length;
    }

    @Override
    public String toString() {
        return String.format(
                MESSAGE_PATTERN, messageId, dataSize, data != null ? dataToString() : "null"
        );
    }

    /**
     * Склеивание нескольких массивов
     * @param arrays массивы байтов
     * @return склеенный массив
     */
    protected static byte[] concat(byte[]... arrays){
        int len = Stream.of(arrays)
                .mapToInt(array -> array.length)
                .sum();
        byte[] bytes = new byte[len];
        int written = 0;
        for (byte[] array : arrays){
            System.arraycopy(array, 0, bytes, written, array.length);
            written += array.length;
        }
        return bytes;
    }

    /**
     * Трансформация интового значения в массив из 4 байтов
     * @param value интовое значение
     */
    protected static byte[] intToBytes(int value){
        byte[] bytes = new byte[4];

        bytes[0] = (byte) value;
        bytes[1] = (byte) (value >> 8);
        bytes[2] = (byte) (value >> 16);
        bytes[3] = (byte) (value >> 24);
        return bytes;
    }

    /**
     * Трансформация 4 байтов из массива в интовое число
     * @param bytes массив байтов
     * @param from позиция начала считывания 4 байтов
     */
    protected static int intFromBytes(byte[] bytes, int from){
        int v0 = (int) bytes[from],
                v1 = (int) bytes[from + 1],
                v2 = (int) bytes[from + 2],
                v3 = (int) bytes[from + 3];
        return ((v3 & 0xFF) << 24) +
                ((v2 & 0xFF) << 16) +
                ((v1 & 0xFF) << 8) +
                (v0 & 0xFF);
    }

    /**
     * Трансформация первых 4 байтов из массива в интовое число
     * @param bytes массив байтов
     */
    protected static int intFromBytes(byte[] bytes){
        return intFromBytes(bytes, 0);
    }


    /**
     * Проверка соответствия размера массива ожидаемому значению
     * @param array массив байтов
     * @param expectedSize ожидаемое значение размера
     * @throws IllegalArgumentException если размер не соответствует ожидаемому
     */
    protected void checkArraySize(byte[] array, int expectedSize) throws IllegalArgumentException {
        if (array.length != expectedSize){
            throw new IllegalArgumentException(ARRAY_LENGTH_EXCEPTION_MESSAGE);
        }
    }
}
