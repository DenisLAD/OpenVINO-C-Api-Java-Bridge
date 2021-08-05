/*
 * The MIT License
 *
 * Copyright 2021 Lucifer.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package free.lucifer.cvino.lowapi;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import free.lucifer.cvino.natives.BlobBuffer;

/**
 *
 * @author Lucifer
 */
public class InferenceEngineBlob implements AutoCloseable {

    private final PointerByReference blobPointer;

    InferenceEngineBlob(PointerByReference blobPointer) {
        this.blobPointer = blobPointer;
    }

    @Override
    public void close() {
        InferenceEngineCore.ie.ie_blob_free(blobPointer);
    }

    public Pointer getBlobPointer() {
        return blobPointer.getValue();
    }

    public Pointer getWriteBuffer() {
        BlobBuffer blob = new BlobBuffer();
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_blob_get_buffer(blobPointer.getValue(), blob));
        return (Pointer) blob.readField("buffer");
    }

    public Pointer getReadBuffer() {
        BlobBuffer blob = new BlobBuffer();
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_blob_get_buffer(blobPointer.getValue(), blob));
        return (Pointer) blob.readField("cbuffer");
    }

    public int getBufferSizeInBytes() {
        IntByReference ibr = new IntByReference();
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_blob_byte_size(blobPointer.getValue(), ibr));
        return ibr.getValue();
    }

    public int getBufferSize() {
        IntByReference ibr = new IntByReference();
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_blob_size(blobPointer.getValue(), ibr));
        return ibr.getValue();
    }

    public byte[] readBuffer() {
        int size = getBufferSizeInBytes();
        Pointer data = getReadBuffer();
        return data.getByteArray(0, size);
    }

    public void writeBuffer(byte data[]) {
        int size = getBufferSizeInBytes();
        if (size != data.length) {
            throw new IllegalStateException("Buffer size mismatch " + data.length + " != " + size);
        }
        Pointer buf = getWriteBuffer();
        buf.write(0, data, 0, size);
    }

    public float[] readFloatBuffer() {
        int size = getBufferSize();
        Pointer data = getReadBuffer();
        return data.getFloatArray(0, size);
    }

    public void writeFloatBuffer(float data[]) {
        int size = getBufferSize();
        if (size != data.length) {
            throw new IllegalStateException("Buffer size mismatch " + data.length + " != " + size);
        }
        Pointer buf = getWriteBuffer();
        buf.write(0, data, 0, size);
    }
}
