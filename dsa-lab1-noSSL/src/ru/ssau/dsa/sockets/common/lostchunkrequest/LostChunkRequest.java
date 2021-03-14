package ru.ssau.dsa.sockets.common.lostchunkrequest;

/**
 * Запрос потерянных кусков
 */
public class LostChunkRequest {

    /**
     * Массив номеров потерянных кусков изображения
     */
    private final int[] numbers;

    public LostChunkRequest(int[] numbers) {
        this.numbers = numbers;
    }

    public int[] getNumbers() {
        return numbers;
    }
}
