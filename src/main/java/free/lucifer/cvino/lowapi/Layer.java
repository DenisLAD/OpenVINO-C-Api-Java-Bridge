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
abstract class Layer {

    private final Pointer network;
    private final String name;
    protected Layout layout;
    protected Precision precision;
    private final int[] dimesnsions;

    public Layer(Pointer network, String name, Layout layout, Precision precision, int[] dimesnsions) {
        this.network = network;
        this.name = name;
        this.layout = layout;
        this.precision = precision;
        this.dimesnsions = dimesnsions;
    }

    public String getName() {
        return name;
    }

    public Pointer getNetwork() {
        return network;
    }

    public Layout getLayout() {
        return layout;
    }

    public Precision getPrecision() {
        return precision;
    }

    public int[] getDimesnsions() {
        return dimesnsions;
    }

    public abstract void setLayout(Layout layout);

    public abstract void setPrecision(Precision precision);

    public static String toString(int[] dimesnsions) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < dimesnsions.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(dimesnsions[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
