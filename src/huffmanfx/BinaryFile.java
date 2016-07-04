/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffmanfx;

/**
 *
 * @author mhrimaz
 */
import java.io.*;

public class BinaryFile {
    private boolean inputFile;
    private RandomAccessFile file;
    private byte buffer;
    private int buf_length;
    private int total_bits;
    private int bitsleft;
    private int bitsread;

    public BinaryFile(String filename, char readOrWrite) {
        buffer = (byte) 0;
        int buf_length = 0;
        total_bits = 0;
        bitsleft = 0;
        bitsread = 0;
        total_bits = 0;
        buffer = 0;
        bitsread = 0;
        try {
            if (readOrWrite == 'w' || readOrWrite == 'W') {
                inputFile = false;
                file = new RandomAccessFile(filename, "rw");
                file.writeInt(0);
                /* header -- # of bits in the file */
            } else if (readOrWrite == 'r' || readOrWrite == 'R') {
                inputFile = true;
                file = new RandomAccessFile(filename, "r");
                total_bits = file.readInt();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    public boolean EndOfFile() {
        return bitsread == total_bits;
    }

    public char readChar() {
        int charbuf = 0;
        int revcharbuf = 0;
        int i;

        for (i = 0; i < 8; i++) {
            charbuf = charbuf << 1;
            if (readBit()) {
                charbuf += 1;
            }
        }
        for (i = 0; i < 8; i++) {
            revcharbuf = revcharbuf << 1;
            revcharbuf += charbuf % 2;
            charbuf = charbuf >> 1;
        }
        return (char) revcharbuf;
    }

    public int readInt() {
        int charbuf = 0;
        int revcharbuf = 0;
        int i;

        for (i = 0; i < 8; i++) {
            charbuf = charbuf << 1;
            if (readBit()) {
                charbuf += 1;
            }
        }
        for (i = 0; i < 8; i++) {
            revcharbuf = revcharbuf << 1;
            revcharbuf += charbuf & 1;
            charbuf = charbuf >> 1;
        }
        if (!readBit()) {
            revcharbuf = 0 - revcharbuf;
        }
        return revcharbuf;
    }

    public void writeChar(char c) {
        int i;
        int charbuf = (int) c;
        for (i = 0; i < 8; i++) {
            writeBit(charbuf % 2 > 0);
            charbuf = charbuf >> 1;
        }
    }

    public void writeInt(int i) {
        int j;
        int intbuf = i;
        boolean sign = i > 0;
        if (!sign) {
            intbuf = 0 - intbuf;
        }
        for (j = 0; j < 8; j++) {
            writeBit((intbuf & 1) > 0);
            intbuf = intbuf >> 1;
        }
        writeBit(sign);
    }

    public void writeBit(boolean bit) {
        byte bit_;
        total_bits++;

        if (bit) {
            bit_ = 1;
        } else {
            bit_ = 0;
        }
        buffer |= (bit_ << (7 - buf_length++));
        try {
            if (buf_length == 8) {
                file.writeByte(buffer);
                buf_length = 0;
                buffer = 0;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    public boolean readBit() {
        try {
            if (bitsleft == 0) {
                buffer = file.readByte();
                bitsleft = 8;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        bitsread++;
        return (((buffer >> --bitsleft) & 0x01) > 0);
    }

    public void close() {
        try {
            if (!inputFile) {
                if (buf_length != 0) {
                    while (buf_length < 8) {
                        buffer |= (0 << (7 - buf_length++));
                    }
                    file.writeByte(buffer);
                }
                file.seek(0);
                file.writeInt(total_bits);
            }
            file.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

    }

}
