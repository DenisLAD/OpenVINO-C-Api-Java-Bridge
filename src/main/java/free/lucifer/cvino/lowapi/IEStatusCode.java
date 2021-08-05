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

import free.lucifer.cvino.lowapi.exceptions.GeneralErrorException;
import free.lucifer.cvino.lowapi.exceptions.InferenceEngineException;
import free.lucifer.cvino.lowapi.exceptions.UnknownException;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

/**
 *
 * @author Lucifer
 */
public enum IEStatusCode {
    OK(0, null),
    GENERAL_ERROR(-1, GeneralErrorException.class),
    NOT_IMPLEMENTED(-2, UnknownException.class),
    NETWORK_NOT_LOADED(-3, UnknownException.class),
    PARAMETER_MISMATCH(-4, UnknownException.class),
    NOT_FOUND(-5, UnknownException.class),
    OUT_OF_BOUNDS(-6, UnknownException.class),
    UNEXPECTED(-7, UnknownException.class),
    REQUEST_BUSY(-8, UnknownException.class),
    RESULT_NOT_READY(-9, UnknownException.class),
    NOT_ALLOCATED(-10, UnknownException.class),
    INFER_NOT_STARTED(-11, UnknownException.class),
    NETWORK_NOT_READ(-12, UnknownException.class),
    INFER_CANCELLED(-13, UnknownException.class),
    UNKNOWN(-2555, UnknownException.class);

    private final int status;
    private final Class<?> throwable;

    private IEStatusCode(int status, Class<? extends InferenceEngineException> throwable) {
        this.status = status;
        this.throwable = throwable;
    }

    public int getStatus() {
        return status;
    }

    public Class<?> getThrowable() {
        return throwable;
    }

    public static IEStatusCode byStatus(int status) {
        return Stream.of(values()).filter(e -> e.status == status).findFirst().orElse(UNKNOWN);
    }

    public static void assertOk(int status) {
        IEStatusCode sc = byStatus(status);
        if (sc == OK) {
            return;
        }

        try {
            Exception t = (Exception) sc.getThrowable().getConstructor(IEStatusCode.class, String.class).newInstance(sc, "IEError(" + status + ":" + sc + ")");
            throwUnchecked(t);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void throwUnchecked(final Exception ex) {
        throwsUnchecked(ex);
    }

    public static <T extends Exception> void throwsUnchecked(Exception toThrow) throws T {
        throw (T) toThrow;
    }
}
