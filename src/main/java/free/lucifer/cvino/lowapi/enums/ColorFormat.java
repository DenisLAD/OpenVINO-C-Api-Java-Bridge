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
package free.lucifer.cvino.lowapi.enums;

import java.util.stream.Stream;

/**
 *
 * @author Lucifer
 */
public enum ColorFormat {
    RAW(0),
    RGB(1),
    BGR(2),
    RGBX(3),
    BGRX(4),
    NV12(5),
    I420(6);
    private final int id;

    private ColorFormat(int id) {
        this.id = id;
    }

    public static ColorFormat byValue(int value) {
        return Stream.of(values()).filter(l -> l.id == value).findFirst().get();
    }

    public int getId() {
        return id;
    }
    
    
}
