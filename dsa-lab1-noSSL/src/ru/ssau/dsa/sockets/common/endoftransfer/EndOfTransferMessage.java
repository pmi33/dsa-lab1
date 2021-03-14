package ru.ssau.dsa.sockets.common.endoftransfer;

import ru.ssau.dsa.sockets.common.messages.Message;

/**
 * Сообщение описывает конец передачи данных
 */
public class EndOfTransferMessage extends Message<Object> {

    @Override
    public Object dataFromBytes(byte[] array) {
        // никаких полезных данных в сообщении нет
        return null;
    }

    @Override
    public byte[] dataToBytes() {
        // никаких полезных данных в сообщении нет
        return new byte[0];
    }

    @Override
    public String dataToString() {
        return "Конец передачи сообщений";
    }
}
