/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease.chunk;

import com.google.common.collect.Lists;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionFile {
    private final int[] field_3663 = new int[1024];
    private List field_1174;
    private RandomAccessFile field_1788;
    private final int[] field_2727 = new int[1024];
    private final File field_2702;

    public void method_2558() throws IOException {
        if (this.field_1788 != null) {
            this.field_1788.close();
        }
    }

    private boolean method_1930(int n2, int n22) {
        return n2 < 0 || n2 >= 32 || n22 < 0 || n22 >= 32;
    }

    public RegionFile(File file) {
        this.field_2702 = file;
        try {
            int n2;
            this.field_1788 = new ChunkRandomAccessFile(file, "r");
            int n3 = (int)this.field_1788.length() / 4096;
            this.field_1174 = Lists.newArrayListWithCapacity(n3);
            for (n2 = 0; n2 < n3; ++n2) {
                this.field_1174.add(true);
            }
            this.field_1174.set(0, false);
            this.field_1174.set(1, false);
            this.field_1788.seek(0L);
            for (n2 = 0; n2 < 1024; ++n2) {
                int n4;
                this.field_3663[n2] = n4 = this.field_1788.readInt();
                int n42 = n4 & 0xFF;
                if (n42 == 255 && n4 >> 8 <= this.field_1174.size()) {
                    this.field_1788.seek((n4 >> 8) * 4096);
                    n42 = (this.field_1788.readInt() + 4) / 4096 + 1;
                    this.field_1788.seek(n2 * 4 + 4);
                }
                if (n4 != 0 && (n4 >> 8) + n42 <= this.field_1174.size()) {
                    for (int i = 0; i < n42; ++i) {
                        this.field_1174.set((n4 >> 8) + i, false);
                    }
                    continue;
                }
                if (n42 > 0) continue;
            }
            for (n2 = 0; n2 < 1024; ++n2) {
                this.field_2727[n2] = this.field_1788.readInt();
            }
        } catch (IOException var7) {
            var7.printStackTrace();
        }
    }

    public synchronized DataInputStream method_6317(int n2, int n22) {
        if (this.method_1930(n2, n22)) {
            return null;
        }
        try {
            int n3 = this.method_5128(n2, n22);
            if (n3 == 0) {
                return null;
            }
            int n4 = n3 >> 8;
            int n5 = n3 & 0xFF;
            if (n5 == 255) {
                this.field_1788.seek((long)n4 * 4096L);
                n5 = (this.field_1788.readInt() + 4) / 4096 + 1;
            }
            if (n4 + n5 > this.field_1174.size()) {
                return null;
            }
            this.field_1788.seek(n4 * 4096);
            int n6 = this.field_1788.readInt();
            if (n6 > 4096 * n5) {
                return null;
            }
            if (n6 <= 0) {
                return null;
            }
            byte by = this.field_1788.readByte();
            if (by == 1) {
                byte[] byArray = new byte[n6 - 1];
                this.field_1788.read(byArray);
                return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(byArray))));
            }
            if (by == 2) {
                byte[] byArray = new byte[n6 - 1];
                this.field_1788.read(byArray);
                return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(byArray))));
            }
            return null;
        } catch (IOException var9) {
            return null;
        }
    }

    private int method_5128(int n2, int n22) {
        return this.field_3663[n2 + n22 * 32];
    }

    public boolean method_7275(int n2, int n22) {
        return this.method_5128(n2, n22) != 0;
    }

    @Deprecated
    public synchronized boolean method_3759(int n2, int n22) {
        return this.method_7275(n2, n22);
    }
}

