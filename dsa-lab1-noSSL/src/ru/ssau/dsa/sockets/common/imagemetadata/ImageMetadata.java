package ru.ssau.dsa.sockets.common.imagemetadata;

import java.awt.image.BufferedImage;

/**
 * Метаданные изображения
 */
public class ImageMetadata {

    /**
     * Ширина изображения
     */
    private final int width;

    /**
     * Высота изображения
     */
    private final int height;

    public ImageMetadata(BufferedImage image){
        width = image.getWidth();
        height = image.getHeight();
    }

    public ImageMetadata(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
