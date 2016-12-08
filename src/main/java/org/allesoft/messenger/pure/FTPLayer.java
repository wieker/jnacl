package org.allesoft.messenger.pure;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.BlockingQueue;

/**
 * Created by kabramovich on 23.11.2016.
 */
public class FTPLayer implements Layer {
    BlockingQueue<byte[]> queue;
    Layer bottom;
    FTPLayerState state = FTPLayerState.FREE;
    FileAcceptRequest fileAcceptRequest;
    File fileToTransfer;
    byte[] memoryToTransfer;
    public static final int CHUNK_SIZE = 16 * 1024 - 1;
    int received = 0;
    int expected = 0;
    FTPComplete complete;

    public FTPLayer(FileAcceptRequest fileAcceptRequest, FTPComplete complete) {
        this.fileAcceptRequest = fileAcceptRequest;
        this.complete = complete;
        queue = InfiniThreadFactory.infiniThreadWithQueue((packet) -> {
            System.out.println("Received packet");
            if (state.equals(FTPLayerState.FREE) && (byte) FTPModeHeader.START.ordinal() == packet[0] && fileAcceptRequest != null) {
                long size = ((long) packet[1] << 56) + ((long) packet[2] << 48) + ((long) packet[3] << 40) + ((long) packet[4] << 32) + (packet[5] << 24) + (packet[6] << 16) + (packet[7] << 8) + packet[8];
                byte[] fileNameCode = new byte[packet.length - 8 - 1];
                System.arraycopy(packet, 9, fileNameCode, 0, fileNameCode.length);
                state = FTPLayerState.REQUEST_RECEIVED;
                fileAcceptRequest.accept(this, new String(fileNameCode), (int) size);
            }
            if (state.equals(FTPLayerState.REQUEST_SENT) && FTPModeHeader.ACCEPT.ordinal() == packet[0]) {
                if (fileToTransfer != null) {
                    int size = (int) fileToTransfer.length();
                    int chunks = size / CHUNK_SIZE;
                    byte[] buf = new byte[CHUNK_SIZE + 1];
                    buf[0] = (byte) FTPModeHeader.PART.ordinal();
                    FileInputStream inputStream = new FileInputStream(fileToTransfer);
                    for (int i = 0; i < chunks; i++) {
                        inputStream.read(buf, 1, CHUNK_SIZE);
                        bottom.sendPacket(buf);
                    }
                    if (size > chunks * CHUNK_SIZE) {
                        buf = new byte[size - CHUNK_SIZE * chunks + 1];
                        buf[0] = (byte) FTPModeHeader.PART.ordinal();
                        inputStream.read(buf, 1, buf.length - 1);
                        bottom.sendPacket(buf);
                    }
                }
                if (memoryToTransfer != null) {
                    int size = memoryToTransfer.length;
                    int chunks = size / CHUNK_SIZE;
                    byte[] buf = new byte[CHUNK_SIZE + 1];
                    buf[0] = (byte) FTPModeHeader.PART.ordinal();
                    for (int i = 0; i < chunks; i++) {
                        System.arraycopy(memoryToTransfer, i * CHUNK_SIZE, buf, 1, CHUNK_SIZE);
                        bottom.sendPacket(buf);
                    }
                    if (size > chunks * CHUNK_SIZE) {
                        buf = new byte[size - CHUNK_SIZE * chunks + 1];
                        buf[0] = (byte) FTPModeHeader.PART.ordinal();
                        System.arraycopy(memoryToTransfer, chunks * CHUNK_SIZE, buf, 1, buf.length - 1);
                        bottom.sendPacket(buf);
                    }
                }
                fileToTransfer = null;
                memoryToTransfer = null;
                state = FTPLayerState.FREE;
            }
            if (state.equals(FTPLayerState.TRANSFER) && FTPModeHeader.PART.ordinal() == packet[0]) {
                if (fileToTransfer != null) {
                    System.out.println("file chunk received");
                    received += packet.length - 1;
                }
                if (memoryToTransfer != null) {
                    System.arraycopy(packet, 1, memoryToTransfer, received, packet.length - 1);
                    received += packet.length - 1;
                }
                if (received >= memoryToTransfer.length) {
                    complete.complete(this);
                    fileToTransfer = null;
                    memoryToTransfer = null;
                    state = FTPLayerState.FREE;
                }
            }
        });
    }

    @Override
    public void sendPacket(byte[] packet) throws Exception {

    }

    public void sendFile(File file) throws Exception {
        byte[] nameBytes = file.getName().getBytes();
        long dataLength = (int) file.length();
        sendRequest(nameBytes, dataLength);
        fileToTransfer = file;
    }

    public void sendFile(byte[] data, String name) throws Exception {
        byte[] nameBytes = name.getBytes();
        int dataLength = data.length;
        sendRequest(nameBytes, dataLength);
        memoryToTransfer = data;
    }

    private void sendRequest(byte[] nameBytes, long dataLength) throws Exception {
        byte[] packet = new byte[1 + 8 + nameBytes.length];
        packet[0] = (byte) FTPModeHeader.START.ordinal();
        packet[1] = (byte) (dataLength >> 56 & 0xFF);
        packet[2] = (byte) (dataLength >> 48 & 0xFF);
        packet[3] = (byte) (dataLength >> 40 & 0xFF);
        packet[4] = (byte) (dataLength >> 32 & 0xFF);
        packet[5] = (byte) (dataLength >> 24 & 0xFF);
        packet[6] = (byte) (dataLength >> 16 & 0xFF);
        packet[7] = (byte) (dataLength >> 8 & 0xFF);
        packet[8] = (byte) (dataLength & 0xFF);
        System.arraycopy(nameBytes, 0, packet, 9, nameBytes.length);
        bottom.sendPacket(packet);
        state = FTPLayerState.REQUEST_SENT;
    }

    public void receive(File file) throws Exception {
        byte[] packet = new byte[1];
        packet[0] = (byte) FTPModeHeader.ACCEPT.ordinal();
        fileToTransfer = file;
        state = FTPLayerState.TRANSFER;
        bottom.sendPacket(packet);
    }

    public void receive(byte[] memory) throws Exception {
        byte[] packet = new byte[1];
        packet[0] = (byte) FTPModeHeader.ACCEPT.ordinal();
        memoryToTransfer = memory;
        bottom.sendPacket(packet);
        state = FTPLayerState.TRANSFER;
        received = 0;
    }

    @Override
    public BlockingQueue<byte[]> getWaitingQueue() {
        return queue;
    }

    @Override
    public void setTop(Layer layer) {

    }

    @Override
    public void setBottom(Layer layer) {
        bottom = layer;
    }

    public byte[] getMemoryToTransfer() {
        return memoryToTransfer;
    }
}
