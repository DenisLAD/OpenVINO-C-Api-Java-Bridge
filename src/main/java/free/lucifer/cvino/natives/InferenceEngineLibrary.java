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

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 *
 * @author Lucifer
 */
public interface InferenceEngineLibrary extends Library {

    Version.ByValue ie_c_api_version();

    void ie_version_free(Version version);

    void ie_param_free(Param param);

    int ie_core_create(String xml_config_file, PointerByReference core);

    void ie_core_free(PointerByReference core);

    int ie_core_get_available_devices(Pointer core, AvailableDevices devices);

    void ie_core_available_devices_free(AvailableDevices devices);

    int ie_core_get_versions(Pointer core, String device_name, CoreVersions versions);

    void ie_core_versions_free(CoreVersions versions);

    int ie_core_read_network(Pointer core, String xml, String weights, PointerByReference network);

    int ie_core_read_network_from_memory(Pointer core, byte[] xmlContent, BaseTSD.SIZE_T xml_content_size, Pointer blob, PointerByReference network);

    int ie_core_load_network(Pointer core, Pointer network, String device_name, Config config, PointerByReference execNetwork);

    int ie_core_load_network_from_file(Pointer core, String xml, String device_name, Config config, PointerByReference execNetwork);

    int ie_core_set_config(Pointer core, Config core_config, String device_name);

    int ie_core_register_plugin(Pointer core, String plugin_name, String device_name);

    int ie_core_register_plugins(Pointer core, String xml_config_file);

    int ie_core_unregister_plugin(Pointer core, String device_name);

    int ie_core_add_extension(Pointer core, String extension_path, String device_name);

    int ie_core_get_metric(Pointer core, String device_name, String metric_name, Param param_result);

    int ie_core_get_config(Pointer core, String device_name, String config_name, Param param_result);

    // ExecNetwork 
    void ie_exec_network_free(PointerByReference execNetwork);

    int ie_exec_network_create_infer_request(Pointer execNetwork, PointerByReference infer_request);

    int ie_exec_network_get_metric(Pointer execNetwork, String metric_name, Param param_result);

    int ie_exec_network_set_config(Pointer execNetwork, Config param_config);

    int ie_exec_network_get_config(Pointer execNetwork, String metric_config, Param param_result);

    // Infer
    void ie_infer_request_free(PointerByReference infer_request);

    int ie_infer_request_get_blob(Pointer infer_request, String name, PointerByReference blob);

    int ie_infer_request_set_blob(Pointer infer_request, String name, Pointer blob);

    int ie_infer_request_infer(Pointer infer_request);

    int ie_infer_request_infer_async(Pointer infer_request);

    int ie_infer_set_completion_callback(Pointer infer_request, CompleteCallBack callback);

    int ie_infer_request_wait(Pointer infer_request, long timeout);

    int ie_infer_request_set_batch(Pointer infer_request, BaseTSD.SIZE_T size);

    // Network
    void ie_network_free(PointerByReference network);

    int ie_network_get_name(Pointer network, PointerByReference name);

    int ie_network_get_inputs_number(Pointer network, IntByReference size_result);

    int ie_network_get_input_name(Pointer network, BaseTSD.SIZE_T number, PointerByReference name);

    int ie_network_get_input_precision(Pointer network, String input_name, IntByReference prec_result);

    int ie_network_set_input_precision(Pointer network, String input_name, int precision);

    int ie_network_get_input_layout(Pointer network, String input_name, IntByReference layout_result);

    int ie_network_set_input_layout(Pointer network, String input_name, int l);

    int ie_network_get_input_dims(Pointer network, String input_name, Dimensions dims_result);

    int ie_network_get_input_resize_algorithm(Pointer network, String input_name, IntByReference resize_alg_result);

    int ie_network_set_input_resize_algorithm(Pointer network, String input_name, int resize_alg);

    int ie_network_get_color_format(Pointer network, String input_name, IntByReference colformat_result);

    int ie_network_set_color_format(Pointer network, String input_name, int color_format);

    int ie_network_get_input_shapes(Pointer network, InputShapes shapes);

    int ie_network_reshape(Pointer network, InputShapes.ByValue shapes);

    int ie_network_get_outputs_number(Pointer network, IntByReference size_result);

    int ie_network_get_output_name(Pointer network, BaseTSD.SIZE_T number, PointerByReference name);

    int ie_network_get_output_precision(Pointer network, String output_name, IntByReference prec_result);

    int ie_network_set_output_precision(Pointer network, String output_name, int precision);

    int ie_network_get_output_layout(Pointer network, String output_name, IntByReference layout_result);

    int ie_network_set_output_layout(Pointer network, String output_name, int l);

    int ie_network_get_output_dims(Pointer network, String output_name, Dimensions dims_result);

    void ie_network_input_shapes_free(InputShapes inputShapes);

    void ie_network_name_free(PointerByReference name);

    // Blob
    int ie_blob_make_memory(TensorDesc tensor_desc, PointerByReference blob);

    int ie_blob_make_memory_from_preallocated(TensorDesc tensor_desc, Pointer ptr, BaseTSD.SIZE_T size, PointerByReference blob);

    int ie_blob_make_memory_with_roi(Pointer inputBlob, ROI roi, PointerByReference blob);

    int ie_blob_make_memory_nv12(Pointer y, Pointer uv, PointerByReference nv12Blob);

    int ie_blob_make_memory_i420(Pointer y, Pointer u, Pointer v, PointerByReference i420Blob);

    int ie_blob_size(Pointer blob, IntByReference size_result);

    int ie_blob_byte_size(Pointer blob, IntByReference size_result);

    void ie_blob_deallocate(PointerByReference blob);

    int ie_blob_get_buffer(Pointer blob, BlobBuffer blob_buffer);

    int ie_blob_get_cbuffer(Pointer blob, BlobBuffer blob_buffer);

    int ie_blob_get_dims(Pointer blob, Dimensions dims_result);

    int ie_blob_get_layout(Pointer blob, IntByReference layout_result);

    int ie_blob_get_precision(Pointer blob, IntByReference prec_result);

    void ie_blob_free(PointerByReference blob);

}
