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
public enum Precision {
    UNSPECIFIED(255),
    MIXED(0),
    FP32(10),
    FP16(11),
    FP64(13),
    Q78(20),
    I16(30),
    U4(39),
    U8(40),
    I4(49),
    I8(50),
    U16(60),
    I32(70),
    I64(72),
    U64(73),
    U32(74),
    BIN(71),
    CUSTOM(80);
    private final int id;

    private Precision(int id) {
        this.id = id;
    }

    public static Precision byValue(int value) {
        return Stream.of(values()).filter(l -> l.id == value).findFirst().get();
    }
    
        public int getId() {
        return id;
    }
}
