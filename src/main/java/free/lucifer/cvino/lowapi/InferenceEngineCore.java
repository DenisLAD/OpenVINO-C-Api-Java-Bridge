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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.ptr.PointerByReference;
import free.lucifer.cvino.lowapi.enums.Layout;
import free.lucifer.cvino.lowapi.enums.Precision;
import free.lucifer.cvino.natives.AvailableDevices;
import free.lucifer.cvino.natives.CoreVersion;
import free.lucifer.cvino.natives.CoreVersions;
import free.lucifer.cvino.natives.Dimensions;
import free.lucifer.cvino.natives.InferenceEngineLibrary;
import free.lucifer.cvino.natives.TensorDesc;
import free.lucifer.cvino.natives.Version;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Lucifer
 */
public final class InferenceEngineCore implements AutoCloseable {

    protected static final InferenceEngineLibrary ie = Native.load("inference_engine_c_api", InferenceEngineLibrary.class);
    private final String version;
    private PointerByReference core;

    public InferenceEngineCore() {
        this("");
    }

    public InferenceEngineCore(String xmlConfigFile) {
        final Version.ByValue cApiVersion = ie.ie_c_api_version();
        this.version = cApiVersion.api_version;
        ie.ie_version_free(cApiVersion);

        core = new PointerByReference();
        IEStatusCode.assertOk(ie.ie_core_create(xmlConfigFile, core));
    }

    public String getVersion() {
        return version;
    }

    @Override
    public void close() {
        ie.ie_core_free(core);
    }

    public Collection<Device> getDevices() {
        List<Device> devices = new ArrayList<>();
        AvailableDevices ad = new AvailableDevices();
        IEStatusCode.assertOk(ie.ie_core_get_available_devices(core.getValue(), ad));

        for (int i = 0; i < ad.num_devices.intValue(); i++) {
            String deviceName = ad.devices.getPointer().getPointerArray(0)[i].getString(0);

            CoreVersions versions = new CoreVersions();
            IEStatusCode.assertOk(ie.ie_core_get_versions(core.getValue(), deviceName, versions));

            if (versions.num_vers.intValue() == 0) {
                continue;
            }

            CoreVersion ver = new CoreVersion(versions.versions);
            ver.read();
            devices.add(new Device(core.getValue(), deviceName, ver.build_number, ver.description, ver.major.intValue(), ver.minor.intValue()));

        }

        return devices;
    }

    public InferenceEngineNetwork readNetwork(String file, String data) {
        PointerByReference network = new PointerByReference();
        IEStatusCode.assertOk(ie.ie_core_read_network(core.getValue(), file, data, network));
        return new InferenceEngineNetwork(network, core.getValue());
    }

    public InferenceEngineBlob createBlob(Layout layout, int[] dimensions, Precision precision) {
        TensorDesc tensor = new TensorDesc();
        tensor.dims = new Dimensions.ByValue();
        tensor.layout = layout.getId();
        tensor.precision = precision.getId();
        tensor.dims.ranks = new BaseTSD.SIZE_T(dimensions.length);
        tensor.dims.dims = Arrays.stream(dimensions).mapToObj(d -> new BaseTSD.SIZE_T(d)).collect(Collectors.toList()).toArray(new BaseTSD.SIZE_T[0]);

        PointerByReference pbr = new PointerByReference();
        IEStatusCode.assertOk(ie.ie_blob_make_memory(tensor, pbr));
        return new InferenceEngineBlob(pbr);
    }

    public InferenceEngineBlob createBlob(Layer layer) {
        return createBlob(layer.getLayout(), layer.getDimesnsions(), layer.getPrecision());
    }

    public InferenceEngineBlob createBlob(Layout layout, int[] dimensions, Precision precision, byte[] data) {
        TensorDesc tensor = new TensorDesc();
        tensor.dims = new Dimensions.ByValue();
        tensor.layout = layout.getId();
        tensor.precision = precision.getId();
        tensor.dims.ranks = new BaseTSD.SIZE_T(dimensions.length);
        tensor.dims.dims = Arrays.stream(dimensions).mapToObj(d -> new BaseTSD.SIZE_T(d)).collect(Collectors.toList()).toArray(new BaseTSD.SIZE_T[0]);

        PointerByReference pbr = new PointerByReference();
        Memory mem = new Memory(data.length);
        mem.write(0, data, 0, data.length);
        IEStatusCode.assertOk(ie.ie_blob_make_memory_from_preallocated(tensor, mem.share(0), new BaseTSD.SIZE_T(mem.size()), pbr));
        return new InferenceEngineBlob(pbr);
    }

    public InferenceEngineBlob createBlob(Layer layer, byte[] data) {
        return createBlob(layer.getLayout(), layer.getDimesnsions(), layer.getPrecision(), data);
    }
}
