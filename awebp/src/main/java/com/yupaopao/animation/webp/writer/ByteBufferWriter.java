package com.yupaopao.animation.webp.writer;

import com.yupaopao.animation.webp.DataUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @Description: ByteBufferWriter
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-12
 */
public class ByteBufferWriter implements Writer {

    private ByteBuffer byteBuffer;

    public ByteBufferWriter() {
        reset(10 * 1024);
    }

    @Override
    public void putByte(byte b) {
        byteBuffer.put(b);
    }

    @Override
    public void putBytes(byte[] b) {
        byteBuffer.put(b);
    }

    @Override
    public void putUInt16(int i) {
        byteBuffer.put(DataUtil.uInt16ToByte(i));
    }

    @Override
    public void putUInt24(int i) {
        byteBuffer.put(DataUtil.uInt24ToByte(i));
    }

    @Override
    public void putUInt32(int i) {
        byteBuffer.put(DataUtil.uInt32ToByte(i));
    }

    @Override
    public void put1Based(int i) {
        byteBuffer.put(DataUtil.oneBasedToByte(i));
    }

    @Override
    public void putFourCC(String v) {
        byteBuffer.put(DataUtil.fourCCToByte(v));
    }

    @Override
    public int position() {
        return byteBuffer.position();
    }

    @Override
    public byte[] toByteArray() {
        return byteBuffer.array();
    }

    @Override
    public void close() {
    }

    @Override
    public void reset(int size) {
        if (byteBuffer == null || size > byteBuffer.limit()) {
            byteBuffer = ByteBuffer.allocate(size);
            this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        byteBuffer.clear();
    }
}