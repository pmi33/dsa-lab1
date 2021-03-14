package ru.ssau.dsa.sockets.common.messages;

import ru.ssau.dsa.sockets.common.endoftransfer.EndOfTransferMessage;
import ru.ssau.dsa.sockets.common.imagechunk.ImageChunkMessage;
import ru.ssau.dsa.sockets.common.imagemetadata.ImageMetadata;
import ru.ssau.dsa.sockets.common.imagemetadata.ImageMetadataMessage;
import ru.ssau.dsa.sockets.common.lostchunkrequest.LostChunkRequestMessage;

/**
 * Enum перечисление, позволяющее создавать экземпляры сообщений
 * исходя из их типа (класса) или идентификатора.
 *
 * Данное перечисление позволит автоматически проставлять
 * идентификатор сообщения внутри конструктора родительского класс Message.
 * Так же данное перечисление позволит на лету создавать сообщения
 * исходя из байтового идентификатора, который передается через сокеты.
 */
public enum MessageGenerator {

    IMAGE_METADATA(ImageMetadataMessage.class, 1) {
        public ImageMetadataMessage create() {
            return new ImageMetadataMessage();
        }
    },

    IMAGE_CHUNK(ImageChunkMessage.class, 2){
        public ImageChunkMessage create() {
            return new ImageChunkMessage();
        }
    },

    LOST_CHUNK_REQUEST(LostChunkRequestMessage.class, 3){
        public LostChunkRequestMessage create() {
            return new LostChunkRequestMessage();
        }
    },

    END_OF_TRANSFER(EndOfTransferMessage.class, 4){
        public EndOfTransferMessage create() {
            return new EndOfTransferMessage();
        }
    };

    public static final String UNSUPPORTED_CLASS = "MessageGenerator не поддерживает класс %s";
    private static final String UNSUPPORTED_ID = "MessageGenerator не поддерживает идентификатор %d";

    /**
     * Класс, описывающий сообщение
     */
    private Class<? extends Message<?>> clazz;

    /**
     * Идентификатор типа сообщения
     */
    private byte messageId;

    /**
     * Конструктор для каждого элемента MessageFactory
     */
    MessageGenerator(Class<? extends Message<?>> clazz, int messageId) {
        this.clazz = clazz;
        this.messageId = (byte) messageId;
    }

    public Class<? extends Message<?>> getClazz() {
        return clazz;
    }

    public byte getMessageId() {
        return messageId;
    }

    /**
     * Данный метод должен быть реализован каждым элементом MessageFactory.
     * Метод позволяет 'фабрично' создавать новые экземпляры сообщений.
     */
    public abstract Message<?> create();

    /**
     * Получение генератора сообщений по типу сообщения
     * @param clazz тип сообщения
     */
    public static MessageGenerator getByClass(Class<?> clazz){
        for (MessageGenerator m : MessageGenerator.values()){
            if (m.clazz.equals(clazz)){
                return m;
            }
        }
        throw new IllegalArgumentException(String.format(
                UNSUPPORTED_CLASS, clazz.getName()
        ));
    }

    /**
     * Получение генератора сообщений по идентификатору типа сообщения
     * @param id идентификатор типа сообщения
     */
    public static MessageGenerator getById(byte id){
        for (MessageGenerator m : MessageGenerator.values()){
            if (id == m.messageId){
                return m;
            }
        }
        throw new IllegalArgumentException(String.format(
                UNSUPPORTED_ID, id
        ));
    }
}
