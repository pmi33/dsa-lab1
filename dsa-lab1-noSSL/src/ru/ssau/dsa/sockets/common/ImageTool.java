package ru.ssau.dsa.sockets.common;

import org.w3c.dom.css.RGBColor;
import ru.ssau.dsa.sockets.common.imagechunk.ImageChunk;
import ru.ssau.dsa.sockets.common.imagemetadata.ImageMetadata;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Класс предоставляет базовую функциональность для работы с картинками
 */
public class ImageTool {

    public static int getChunksCount(int imageLength){
        int chunksCount = imageLength / ImageChunk.DEFAULT_CHUNK_SIZE;
        if (imageLength % ImageChunk.DEFAULT_CHUNK_SIZE != 0){
            chunksCount++;
        }
        return chunksCount;
    }

    /**
     * Считывание картинки
     * @return байтовое представление картинки
     */
    public static byte[] imageToBytes(BufferedImage image){
        byte[] bytes = new byte[image.getWidth() * image.getHeight()];
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                bytes[y * image.getWidth() + x] = (byte)image.getRGB(x, y);
            }
        }
        return bytes;
    }

    /**
     * Разбитие картинки на куски
     * @param image исходная картинка
     * @return словарь с кусками картинки
     */
    public static HashMap<Integer, ImageChunk> getChunks(BufferedImage image){
        return getChunks(imageToBytes(image));
    }

    /**
     * Разбитие картинки на куски
     * @param image байтовое представление картинки
     * @return словарь с кусками картинки
     */
    public static HashMap<Integer, ImageChunk> getChunks(byte[] image){
        HashMap<Integer, ImageChunk> chunkMap = new HashMap<>();
        int chunksCount = getChunksCount(image.length);
        for (int i = 0; i < chunksCount; i++) {
            int from = i * ImageChunk.DEFAULT_CHUNK_SIZE;
            int to = min((i + 1) * ImageChunk.DEFAULT_CHUNK_SIZE, image.length);
            chunkMap.put(i, new ImageChunk(i, Arrays.copyOfRange(image, from, to)));
        }
        return chunkMap;
    }

    /**
     * Преобразование кусков изображения в массив байтов
     * @param chunks куски изображения, разложенные в HashMap (словарь) по номерам
     */
    public static byte[] bytesFromChunks(HashMap<Integer, ImageChunk> chunks){
        int len = chunks.values().stream()
                .mapToInt(c -> c.getPixels().length)
                .sum();
        byte[] bytes = new byte[len];
        int chunksCount = getChunksCount(len);
        int written = 0;
        for (int i = 0; i < chunksCount; i++) {
            byte[] pixels = chunks.get(i).getPixels();
            System.arraycopy(pixels, 0, bytes, written, pixels.length);
            written += pixels.length;
        }
        return bytes;
    }

    /**
     * Формирование картинки из массива байтов
     * @param bytes массив байтов
     * @param metadata метаданные изображения
     */
    public static BufferedImage imageFromBytes(byte[] bytes, ImageMetadata metadata){
        BufferedImage bImage = new BufferedImage(metadata.getWidth(),
                metadata.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < bImage.getHeight(); y++) {
            for (int x = 0; x < bImage.getWidth(); x++) {
                int gray = 0xFF & bytes[y * bImage.getWidth() + x];
                bImage.setRGB(x, y, new Color(gray, gray, gray).getRGB());
            }
        }
        return bImage;
    }

    /**
     * Формирование массива пикселей из массива байтов
     * @param bytes массив байтов
     * @param metadata метаданные изображения
     */
    public static int[][] pixelsFromBytes(byte[] bytes, ImageMetadata metadata){
        int w = metadata.getWidth(), h = metadata.getHeight();
        int[][] pixels = new int[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = 0xFF & bytes[y * w + x];
                pixels[y][x] = rgb;
            }
        }
        return pixels;
    }

    /**
     * Применение медианного фильтра 3х3 к изображению в виде байтов.
     * @param bytes байтовое представление изображения
     * @param metadata метаданные изображения
     * @return отфильтрованное изображение
     */
    public static BufferedImage medianFilter(byte[] bytes, ImageMetadata metadata){
        final int SIZE = 3;
        int[][] pixels = pixelsFromBytes(bytes, metadata);
        int w = metadata.getWidth(), h = metadata.getHeight();
        BufferedImage bImage = new BufferedImage(metadata.getWidth(),
                metadata.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        ArrayList<Integer> arr = new ArrayList<>(9);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int ii, jj;

                // еще два форика, чтобы не вытаскивать элементы руками
                for (int i = -SIZE / 2; i <= SIZE / 2; i++) {
                    for (int j = -SIZE / 2; j <= SIZE / 2; j++) {
                        // min & max чтобы не вылезти за границы картинки
                        ii = max(0, min(y + i, h - 1));
                        jj = max(0, min(x + j, w - 1));
                        arr.add(pixels[ii][jj]);
                    }
                }
                arr.sort(Integer::compareTo);
                int gray = arr.get(arr.size() / 2);
                arr.clear();
                bImage.setRGB(x, y, new Color(gray, gray, gray).getRGB());
            }
        }
        return bImage;
    }
}
