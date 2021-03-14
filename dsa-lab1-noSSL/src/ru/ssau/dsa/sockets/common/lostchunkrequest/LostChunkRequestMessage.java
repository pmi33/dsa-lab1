package ru.ssau.dsa.sockets.common.lostchunkrequest;

import ru.ssau.dsa.sockets.common.messages.Message;

/**
 * Сообщение, описывающее запрос потерянных кусков
 */
public class LostChunkRequestMessage extends Message<LostChunkRequest> {

    private static final String PATTERN = "Потерянные куски (%d шт)";

    @Override
    public LostChunkRequest dataFromBytes(byte[] array) {
        int len = array.length / 4;
        int[] chunkNumbers = new int[len];
        for (int i = 0; i < len; i++) {
            chunkNumbers[i] = intFromBytes(array, i * 4);
        }
        return new LostChunkRequest(chunkNumbers);
    }

    @Override
    public byte[] dataToBytes() {
        int[] chunkNumbers = data.getNumbers();
        if (chunkNumbers == null){
            return new byte[0];
        }
        byte[] bytes = new byte[chunkNumbers.length * 4], temp;
        for (int i = 0; i < chunkNumbers.length; i++) {
            temp = intToBytes(chunkNumbers[i]);
            System.arraycopy(temp, 0, bytes, i * 4, 4);
        }
        return bytes;
    }

    @Override
    public String dataToString() {
        int[] numbers = data.getNumbers();
        return String.format(
                PATTERN, numbers != null ? numbers.length : 0
        );
    }
}
