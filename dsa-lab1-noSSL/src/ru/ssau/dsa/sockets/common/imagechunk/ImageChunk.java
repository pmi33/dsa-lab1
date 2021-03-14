package ru.ssau.dsa.sockets.common.imagechunk;

/**
 * Кусок изображения
 */
public class ImageChunk {

    /**
     * Стандартный размер куска изображения
     */
    public static final int DEFAULT_CHUNK_SIZE = 512;

    /**
     * Номер куска изображения
     */
    private final int number;

    /**
     * Массив пикселей
     */
    private final byte[] pixels;

    public ImageChunk(int number, byte[] pixels) {
        this.number = number;
        this.pixels = pixels;
    }

    public int getNumber() {
        return number;
    }

    public byte[] getPixels() {
        return pixels;
    }
}
