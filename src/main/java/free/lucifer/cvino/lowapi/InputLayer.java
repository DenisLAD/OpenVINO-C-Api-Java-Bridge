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
import free.lucifer.cvino.lowapi.enums.ColorFormat;
import free.lucifer.cvino.lowapi.enums.Layout;
import free.lucifer.cvino.lowapi.enums.Precision;
import free.lucifer.cvino.lowapi.enums.ResizeAlgorithm;

/**
 *
 * @author Lucifer
 */
public class InputLayer extends Layer {

    private ColorFormat colorFormat;
    private ResizeAlgorithm resizeAlgorithm;

    protected InputLayer(Pointer network, String name, ColorFormat colorFormat, Layout layout, Precision precision, ResizeAlgorithm resizeAlgorithm, int[] dimesnsions) {
        super(network, name, layout, precision, dimesnsions);
        this.colorFormat = colorFormat;
        this.resizeAlgorithm = resizeAlgorithm;
    }

    public ColorFormat getColorFormat() {
        return colorFormat;
    }

    public void setColorFormat(ColorFormat colorFormat) {
        this.colorFormat = colorFormat;
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_set_color_format(getNetwork(), getName(), colorFormat.getId()));
    }

    public ResizeAlgorithm getResizeAlgorithm() {
        return resizeAlgorithm;
    }

    public void setResizeAlgorithm(ResizeAlgorithm resizeAlgorithm) {
        this.resizeAlgorithm = resizeAlgorithm;
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_set_input_resize_algorithm(getNetwork(), getName(), resizeAlgorithm.getId()));
    }

    @Override
    public String toString() {
        return "InputLayer{name=" + getName() + ", colorFormat=" + colorFormat + ", layout=" + getLayout() + ", precision=" + getPrecision() + ", resizeAlgorithm=" + resizeAlgorithm + ", dimesnsions=" + toString(getDimesnsions()) + '}';
    }

    @Override
    public void setLayout(Layout layout) {
        this.layout = layout;
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_set_input_layout(getNetwork(), getName(), layout.getId()));
    }

    @Override
    public void setPrecision(Precision precision) {
        this.precision = precision;
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_set_input_precision(getNetwork(), getName(), precision.getId()));
    }
}
