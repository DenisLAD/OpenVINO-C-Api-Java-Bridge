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
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import free.lucifer.cvino.lowapi.enums.ColorFormat;
import free.lucifer.cvino.lowapi.enums.Layout;
import free.lucifer.cvino.lowapi.enums.Precision;
import free.lucifer.cvino.lowapi.enums.ResizeAlgorithm;
import free.lucifer.cvino.natives.Config;
import free.lucifer.cvino.natives.Dimensions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Lucifer
 */
public class InferenceEngineNetwork implements AutoCloseable {

    private final PointerByReference network;
    private final Pointer core;
    private final String name;
    private final Map<String, InputLayer> inputs = new HashMap<>();
    private final Map<String, OutputLayer> outputs = new HashMap<>();

    InferenceEngineNetwork(PointerByReference network, Pointer core) {
        this.network = network;
        this.name = getNetworkName();
        this.core = core;
        readInputs();
        readOutputs();
    }

    private String getNetworkName() {
        PointerByReference pbr = new PointerByReference();
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_get_name(network.getValue(), pbr));
        String networkName = pbr.getValue().getString(0);
        InferenceEngineCore.ie.ie_network_name_free(pbr);
        return networkName;
    }

    public String getName() {
        return name;
    }

    @Override
    public void close() {
        InferenceEngineCore.ie.ie_network_free(network);
    }

    private void readInputs() {
        IntByReference ibr = new IntByReference();
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_get_inputs_number(network.getValue(), ibr));
        int inputCount = ibr.getValue();
        PointerByReference pbr = new PointerByReference();
        for (int i = 0; i < inputCount; i++) {
            IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_get_input_name(network.getValue(), new BaseTSD.SIZE_T(i), pbr));
            String layerName = pbr.getValue().getString(0);
            InferenceEngineCore.ie.ie_network_name_free(pbr);

            IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_get_input_precision(network.getValue(), layerName, ibr));
            Precision precision = Precision.byValue(ibr.getValue());

            IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_get_input_layout(network.getValue(), layerName, ibr));
            Layout layout = Layout.byValue(ibr.getValue());

            IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_get_input_resize_algorithm(network.getValue(), layerName, ibr));
            ResizeAlgorithm resizeAlgorithm = ResizeAlgorithm.byValue(ibr.getValue());

            IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_get_color_format(network.getValue(), layerName, ibr));
            ColorFormat colorFormat = ColorFormat.byValue(ibr.getValue());

            Dimensions dimensions = new Dimensions();
            IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_get_input_dims(network.getValue(), layerName, dimensions));

            int[] layerDimensions = toArray(dimensions.ranks.intValue(), dimensions.dims);
            InputLayer inputLayer = new InputLayer(network.getValue(), layerName, colorFormat, layout, precision, resizeAlgorithm, layerDimensions);

            this.inputs.put(layerName, inputLayer);
        }
    }

    private void readOutputs() {
        IntByReference ibr = new IntByReference();
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_get_outputs_number(network.getValue(), ibr));
        int outputCount = ibr.getValue();
        PointerByReference pbr = new PointerByReference();
        for (int i = 0; i < outputCount; i++) {
            IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_get_output_name(network.getValue(), new BaseTSD.SIZE_T(i), pbr));
            String layerName = pbr.getValue().getString(0);
            InferenceEngineCore.ie.ie_network_name_free(pbr);

            IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_get_output_precision(network.getValue(), layerName, ibr));
            Precision precision = Precision.byValue(ibr.getValue());

            IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_get_output_layout(network.getValue(), layerName, ibr));
            Layout layout = Layout.byValue(ibr.getValue());

            Dimensions dimensions = new Dimensions();
            IEStatusCode.assertOk(InferenceEngineCore.ie.ie_network_get_output_dims(network.getValue(), layerName, dimensions));

            int[] layerDimensions = toArray(dimensions.ranks.intValue(), dimensions.dims);
            OutputLayer outputLayer = new OutputLayer(network.getValue(), layerName, layout, precision, layerDimensions);

            this.outputs.put(layerName, outputLayer);
        }
    }

    private int[] toArray(int size, BaseTSD.SIZE_T[] dims) {
        int[] dimesions = new int[size];
        for (int i = 0; i < size; i++) {
            dimesions[i] = dims[i].intValue();
        }
        return dimesions;
    }

    public Map<String, InputLayer> getInputs() {
        return Collections.unmodifiableMap(inputs);
    }

    public Map<String, OutputLayer> getOutputs() {
        return Collections.unmodifiableMap(outputs);
    }

    public InferenceEngineExecutionNetwork loadNetwork(Device dev, Map<String, String> configuration) {
        PointerByReference pbr = new PointerByReference();
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_core_load_network(core, network.getValue(), dev.getDeviceName(), makeConfiguration(configuration == null ? dev.getConfigs() : configuration), pbr));
        return new InferenceEngineExecutionNetwork(pbr);
    }

    private Config makeConfiguration(Map<String, String> configuration) {
        if (configuration == null || configuration.isEmpty()) {
            return new Config();
        }

        Config root = null;
        Config current = null;
        for (Map.Entry<String, String> e : configuration.entrySet()) {
            Config.ByReference conf = new Config.ByReference();
            conf.name = e.getKey();
            conf.value = e.getValue();
            if (root == null) {
                root = conf;
                current = conf;
                continue;
            }
            current.next = conf;
            current = conf;
        }

        return root;
    }
}
