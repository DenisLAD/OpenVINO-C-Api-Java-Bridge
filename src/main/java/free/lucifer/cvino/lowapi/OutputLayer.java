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
import free.lucifer.cvino.lowapi.enums.Layout;
import free.lucifer.cvino.lowapi.enums.Precision;

/**
 *
 * @author Lucifer
 */
public class OutputLayer extends Layer {

    public OutputLayer(Pointer network, String name, Layout layout, Precision precision, int[] dimesnsions) {
        super(network, name, layout, precision, dimesnsions);
    }

    @Override
    public String toString() {
        return "OutputLayer{name=" + getName() + ", layout=" + getLayout() + ", precision=" + getPrecision() + ", dimesnsions=" + toString(getDimesnsions()) + '}';
    }

    @Override
    public void setLayout(Layout layout) {
        this.layout = layout;
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_set_output_layout(getNetwork(), getName(), layout.getId()));
    }

    @Override
    public void setPrecision(Precision precision) {
        this.precision = precision;
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_set_output_precision(getNetwork(), getName(), precision.getId()));
    }

}
