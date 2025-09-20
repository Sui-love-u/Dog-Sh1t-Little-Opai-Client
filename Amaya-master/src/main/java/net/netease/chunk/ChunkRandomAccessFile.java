/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.chunk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ChunkRandomAccessFile
extends RandomAccessFile {
    private long currentPosition = 0L;

    public ChunkRandomAccessFile(String fileName, String mode2) throws FileNotFoundException {
        super(fileName, mode2);
    }

    @Override
    public int read(byte[] byteArray) throws IOException {
        int bytesRead = super.read(byteArray);
        if (bytesRead != -1) {
            CustomChunkMethod.processData(byteArray, this.currentPosition);
            this.currentPosition += (long)bytesRead;
        }
        return bytesRead;
    }

    public ChunkRandomAccessFile(File file, String mode2) throws FileNotFoundException {
        super(file, mode2);
    }

    @Override
    public int read(byte[] byteArray, int offset, int length) throws IOException {
        int bytesRead = super.read(byteArray, offset, length);
        if (bytesRead != -1) {
            CustomChunkMethod.processData(byteArray, this.currentPosition, offset, length);
            this.currentPosition += (long)bytesRead;
        }
        return bytesRead;
    }

    @Override
    public int read() throws IOException {
        int byteRead = super.read();
        if (byteRead != -1) {
            int processedByte = CustomChunkMethod.processByte(byteRead, this.currentPosition);
            ++this.currentPosition;
            return processedByte;
        }
        return byteRead;
    }

    @Override
    public void seek(long position) throws IOException {
        super.seek(position);
        this.currentPosition = position;
    }
}

