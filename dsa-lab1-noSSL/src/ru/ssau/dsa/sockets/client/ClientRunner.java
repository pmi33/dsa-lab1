package ru.ssau.dsa.sockets.client;

import ru.ssau.dsa.sockets.common.Connection;
import ru.ssau.dsa.sockets.common.ImageTool;
import ru.ssau.dsa.sockets.common.endoftransfer.EndOfTransferMessage;
import ru.ssau.dsa.sockets.common.imagechunk.ImageChunk;
import ru.ssau.dsa.sockets.common.imagechunk.ImageChunkMessage;
import ru.ssau.dsa.sockets.common.imagemetadata.ImageMetadata;
import ru.ssau.dsa.sockets.common.imagemetadata.ImageMetadataMessage;
import ru.ssau.dsa.sockets.common.lostchunkrequest.LostChunkRequestMessage;
import ru.ssau.dsa.sockets.common.messages.Message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ClientRunner {

    public static void main(String[] args) throws IOException {
        BufferedImage src = ImageIO.read(new File("256.png"));
        ImageMetadataMessage meta = new ImageMetadataMessage();
        meta.setData(new ImageMetadata(src));
        byte[] imageBytes = ImageTool.imageToBytes(src);
        HashMap<Integer, ImageChunk> chunks = ImageTool.getChunks(imageBytes);

        Connection connection = new Connection("localhost", 54321);
        connection.sendMessage(meta);

        int chunksCount = ImageTool.getChunksCount(imageBytes.length);
        for (int i = 0; i < chunksCount; i++) {
            ImageChunkMessage chunk = new ImageChunkMessage();
            chunk.setData(chunks.get(i));
            connection.sendMessage(chunk);
        }
        connection.sendMessage(new EndOfTransferMessage());

        while (true){
            Message<?> m = connection.readMessage();
            if (m instanceof LostChunkRequestMessage){
                LostChunkRequestMessage lm = (LostChunkRequestMessage)m;
                for (int i : lm.getData().getNumbers()){
                    ImageChunkMessage chunk = new ImageChunkMessage();
                    chunk.setData(chunks.get(i));
                    if (Math.random() < 0.95){
                        connection.sendMessage(chunk);
                    }
                }
                connection.sendMessage(new EndOfTransferMessage());
            }else if (m instanceof EndOfTransferMessage){
                break;
            }
        }
    }
}
