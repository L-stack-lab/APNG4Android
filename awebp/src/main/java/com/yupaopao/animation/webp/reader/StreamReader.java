package com.yupaopao.animation.webp.reader;

import android.text.TextUtils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @link {https://developers.google.com/speed/webp/docs/riff_container#terminology_basics}
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-11
 */
public class StreamReader extends FilterInputStream implements Reader {
    private static ThreadLocal<byte[]> __intBytes = new ThreadLocal<>();
    private int position;

    /**
     * Creates a <code>FilterInputStream</code>
     * by assigning the  argument <code>in</code>
     * to the field <code>this.in</code> so as
     * to remember it for later use.
     *
     * @param in the underlying input stream, or <code>null</code> if
     *           this instance is to be created without an underlying stream.
     */
    public StreamReader(InputStream in) {
        super(in);
    }

    private static byte[] ensureBytes() {
        byte[] bytes = __intBytes.get();
        if (bytes == null) {
            bytes = new byte[4];
            __intBytes.set(bytes);
        }
        return bytes;
    }

    @Override
    public byte peek() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 1);
        return buf[0];
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int ret = super.read(b, off, len);
        position += Math.max(0, ret);
        return ret;
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
        position = 0;
    }

    @Override
    public long skip(long n) throws IOException {
        long ret = super.skip(n);
        position += ret;
        return ret;
    }

    @Override
    public int position() {
        return position;
    }

    @Override
    public int getUInt16() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 2);
        return buf[0] & 0xff | (buf[1] & 0xff) << 8;
    }

    @Override
    public int getUInt24() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 3);
        return buf[0] & 0xff | (buf[1] & 0xff) << 8 | (buf[2] & 0xff) << 16;
    }

    @Override
    public int getUInt32() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 4);
        return buf[0] & 0xff | (buf[1] & 0xff) << 8 | (buf[2] & 0xff) << 16 | (buf[3] & 0xff) << 24;
    }

    @Override
    public int getFourCC() throws IOException {
        byte[] buf = ensureBytes();
        read(buf, 0, 4);
        return buf[0] & 0xff | (buf[1] & 0xff) << 8 | (buf[2] & 0xff) << 16 | (buf[3] & 0xff) << 24;
    }

    @Override
    public int get1Based() throws IOException {
        return getUInt24() + 1;
    }

    @Override
    public boolean matchFourCC(String chars) throws IOException {
        if (TextUtils.isEmpty(chars) || chars.length() != 4) {
            return false;
        }
        int fourCC = getFourCC();
        for (int i = 0; i < 4; i++) {
            if (((fourCC >> (i * 8)) & 0xff) != chars.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}