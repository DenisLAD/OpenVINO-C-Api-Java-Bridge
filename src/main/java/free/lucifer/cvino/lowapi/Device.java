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
import free.lucifer.cvino.natives.Param;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author Lucifer
 */
public class Device {

    private final Pointer core;
    private final String deviceName;
    private final String buildNumber;
    private final String description;
    private final int minorVersion;
    private final int majorVersion;
    private final Map<String, Object> metrics;
    private final Map<String, String> configs;

    protected Device(Pointer core, String deviceName, String buildNumber, String description, int minorVersion, int majorVersion) {
        this.core = core;
        this.deviceName = deviceName;
        this.buildNumber = buildNumber;
        this.description = description;
        this.minorVersion = minorVersion;
        this.majorVersion = majorVersion;
        this.metrics = new HashMap<>();
        this.configs = new HashMap<>();
        loadMetrics();
        loadConfigs();
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public String getDescription() {
        return description;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    private void loadMetrics() {
        Param result = loadMetric(Constants.SUPPORTED_METRICS);
        String metrics = (String) result.readField("params");

        Stream.of(metrics.split(",")).map(String::trim).forEach(metric -> {
            Param metricValue = loadMetric(metric);
            Object value = null;
            switch (metric) {
                case Constants.RANGE_FOR_ASYNC_INFER_REQUESTS:
                    value = metricValue.readField("reage_for_async_infer_request");
                    break;
                case Constants.RANGE_FOR_STREAMS:
                    value = metricValue.readField("reage_for_streams");
                    break;
                case Constants.SUPPORTED_CONFIG_KEYS:
                case Constants.SUPPORTED_METRICS:
                    return;
                default:
                    int num = metricValue.number;
                    value = num > -0xffff && num < 0xffff ? num : metricValue.readField("params");
                    break;
            }

            this.metrics.put(metric, value);
        });
    }

    private void loadConfigs() {
        Param result = loadMetric(Constants.SUPPORTED_CONFIG_KEYS);
        String configs = (String) result.readField("params");

        Stream.of(configs.split(",")).map(String::trim).forEach(config -> {
            Param configValue = loadConfig(config);
            String value = (String) configValue.readField("params");
            this.configs.put(config, value);
        });
    }

    private Param loadMetric(String metricName) {
        Param result = new Param();
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_core_get_metric(core, deviceName, metricName, result));
        return result;
    }

    private Param loadConfig(String configName) {
        Param result = new Param();
        IEStatusCode.assertOk(InferenceEngineCore.ie.ie_core_get_config(core, deviceName, configName, result));
        return result;
    }

    public Map<String, String> getConfigs() {
        return Collections.unmodifiableMap(configs);
    }

    public Map<String, Object> getMetrics() {
        return Collections.unmodifiableMap(metrics);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("DEVICE: ").append(deviceName).append(" (").append(description).append(" ").append(majorVersion).append(".").append(minorVersion).append(") ").append(buildNumber).append("\n");
        sb.append("  METRICS:\n");
        getMetrics().entrySet().forEach(e -> {
            sb.append("    ").append(e.getKey()).append(": ");
            if (e.getValue().getClass().isArray()) {
                int[] val = (int[]) e.getValue();
                for (int i = 0; i < val.length; i++) {
                    if (i != 0) {
                        sb.append(", ");
                    }
                    sb.append(val[i]);
                }
            } else {
                sb.append(e.getValue());
            }
            sb.append("\n");
        });
        sb.append("  CONFIGS:\n");
        getConfigs().entrySet().forEach(e -> {
            sb.append("    ").append(e.getKey()).append(": ");
            sb.append(e.getValue());
            sb.append("\n");
        });
        return sb.toString();
    }
}
