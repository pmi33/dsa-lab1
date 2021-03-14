package ru.ssau.dsa.sockets.common.imagemetadata;

import ru.ssau.dsa.sockets.common.messages.Message;

/**
 * Сообщение, описывающее метаданные изображения
 */
public class ImageMetadataMessage extends Message<ImageMetadata> {

    private static final String PATTERN = "ImageMetadata{Ширина: %d, высота:%d}";

    @Override
    public ImageMetadata dataFromBytes(byte[] array) {
        checkArraySize(array, 8);
        int width = intFromBytes(array);
        int height = intFromBytes(array, 4);
        return new ImageMetadata(width, height);
    }

    @Override
    public byte[] dataToBytes() {
        if (data == null){
            return new byte[0];
        }
        byte[] width = intToBytes(data.getWidth());
        byte[] height = intToBytes(data.getHeight());
        return concat(width, height);
    }

    @Override
    public String dataToString() {
        return String.format(
                PATTERN, data.getWidth(), data.getHeight()
        );
    }
}
