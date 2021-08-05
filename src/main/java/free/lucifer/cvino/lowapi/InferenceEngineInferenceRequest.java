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
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.ptr.PointerByReference;
import free.lucifer.cvino.natives.CompleteCallBack;
import free.lucifer.cvino.natives.CompleteCallBackFunc;

/**
 *
 * @author Lucifer
 */
public class InferenceEngineInferenceRequest implements AutoCloseable {

    private final PointerByReference inferenceRequest;

    protected InferenceEngineInferenceRequest(PointerByReference inferenceRequest) {
        this.inferenceRequest = inferenceRequest;
    }

    @Override
    public void close() {
        InferenceEngineCore.ie.ie_infer_request_free(inferenceRequest);
    }

    public <T extends Layer> void setBlob(T layer, InferenceEngineBlob blob) {
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_infer_request_set_blob(inferenceRequest.getValue(), layer.getName(), blob.getBlobPointer()));
    }

    public <T extends Layer> InferenceEngineBlob getBlob(T layer) {
        PointerByReference pbr = new PointerByReference();
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_infer_request_get_blob(inferenceRequest.getValue(), layer.getName(), pbr));
        return new InferenceEngineBlob(pbr);
    }

    public void request() {
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_infer_request_infer(inferenceRequest.getValue()));
    }

    public void requestAsync() {
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_infer_request_infer_async(inferenceRequest.getValue()));
    }

    public void setCallback(InferenceEngineCallback callbackFunc) {
        CompleteCallBack callback = new CompleteCallBack();
        callback.args = null;
        callback.callbackFunction = new CompleteCallBackFunc() {
            @Override
            public void callback(Pointer args) {
                callbackFunc.callback();
            }
        };
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_infer_set_completion_callback(inferenceRequest.getValue(), callback));
    }

    public void setBatch(int size) {
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_infer_request_set_batch(inferenceRequest.getValue(), new BaseTSD.SIZE_T(size)));
    }

    public void waiting(long timeout) {
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_infer_request_wait(inferenceRequest.getValue(), timeout));
    }
}
