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
package free.lucifer.cvino.natives;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD;

/**
 *
 * @author Lucifer
 */
@Structure.FieldOrder({"ranks", "dims"})
public class Dimensions extends Structure {

    public BaseTSD.SIZE_T ranks;
    public BaseTSD.SIZE_T[] dims = new BaseTSD.SIZE_T[8];

    public ByValue byValue() {
        ByValue bv = new ByValue();
        bv.ranks = ranks;
        bv.dims = dims;
        return bv;
    }

    public static class ByValue extends Dimensions implements Structure.ByValue {
    }

}
