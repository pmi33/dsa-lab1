package ru.ssau.dsa.sockets.server;

import ru.ssau.dsa.sockets.common.Connection;
import ru.ssau.dsa.sockets.common.ImageTool;
import ru.ssau.dsa.sockets.common.endoftransfer.EndOfTransferMessage;
import ru.ssau.dsa.sockets.common.imagechunk.ImageChunk;
import ru.ssau.dsa.sockets.common.imagechunk.ImageChunkMessage;
import ru.ssau.dsa.sockets.common.imagemetadata.ImageMetadataMessage;
import ru.ssau.dsa.sockets.common.lostchunkrequest.LostChunkRequest;
import ru.ssau.dsa.sockets.common.lostchunkrequest.LostChunkRequestMessage;
import ru.ssau.dsa.sockets.common.messages.Message;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLServerSocket;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ServerRunner {

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345);
            while (true){
                run(ss);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void run(ServerSocket ss) throws IOException {
        Socket s = ss.accept();

        Connection connection = new Connection(s);

        ImageMetadataMessage im = connection.readMessage(ImageMetadataMessage.class);
        int chunkCount = ImageTool.getChunksCount(im.getData().getHeight() * im.getData().getWidth());

        HashMap<Integer, ImageChunk> receivedChunk = new HashMap<>();
        int[] lost;

        do {
            while (true){
                Message<?> m = connection.readMessage();
                if (m instanceof ImageChunkMessage){
                    ImageChunkMessage chunkMessage = (ImageChunkMessage) m;
                    int n = chunkMessage.getData().getNumber();

                    receivedChunk.put(n, chunkMessage.getData());
                }else if (m instanceof EndOfTransferMessage){
                    break;
                }
            }
            lost = IntStream.range(0, chunkCount)
                    .filter(e -> !receivedChunk.containsKey(e))
                    .toArray();

            if (lost.length == 0){
                break;
            }
            int i = Math.min(lost.length, 100);

            int[] lostChunkNumbers = Arrays.copyOfRange(lost, 0, i);

            LostChunkRequestMessage lm = new LostChunkRequestMessage();
            lm.setData(new LostChunkRequest(lostChunkNumbers));
            connection.sendMessage(lm);
        }while (true);
        connection.sendMessage(new EndOfTransferMessage());
        byte[] bytes = ImageTool.bytesFromChunks(receivedChunk);

        BufferedImage nonFiltered = ImageTool.imageFromBytes(bytes, im.getData());
        ImageIO.write(nonFiltered, "png", new File("nonFiltered.png"));

        BufferedImage filtered = ImageTool.medianFilter(bytes, im.getData());
        ImageIO.write(filtered, "png", new File("filtered.png"));
    }
}
