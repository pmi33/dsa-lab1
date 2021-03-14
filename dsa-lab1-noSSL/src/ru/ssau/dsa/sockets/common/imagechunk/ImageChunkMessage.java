package ru.ssau.dsa.sockets.common.imagechunk;

import ru.ssau.dsa.sockets.common.messages.Message;

import java.util.Arrays;

/**
 * Сообщение, описывающее кусок изображения
 */
public class ImageChunkMessage extends Message<ImageChunk> {

    private static final String PATTERN = "ImageChunk {номер: %d, размер:%d}";

    @Override
    public ImageChunk dataFromBytes(byte[] array) {
        int number = intFromBytes(array);
        byte[] pixels = Arrays.copyOfRange(array, 4, 4 + ImageChunk.DEFAULT_CHUNK_SIZE);
        return new ImageChunk(number, pixels);
    }

    @Override
    public byte[] dataToBytes() {
        byte[] number = intToBytes(data.getNumber());
        byte[] pixels = data.getPixels();
        if (pixels == null){
            return new byte[0];
        }
        return concat(number, pixels);
    }

    @Override
    public String dataToString() {
        byte[] pixels = data.getPixels();
        return String.format(
                PATTERN, data.getNumber(), pixels != null ? pixels.length : 0
        );
    }
}
