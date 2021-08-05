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
package free.lucifer.cvino;

import free.lucifer.cvino.lowapi.Device;
import free.lucifer.cvino.lowapi.InferenceEngineBlob;
import free.lucifer.cvino.lowapi.InferenceEngineCore;
import free.lucifer.cvino.lowapi.InferenceEngineExecutionNetwork;
import free.lucifer.cvino.lowapi.InferenceEngineInferenceRequest;
import free.lucifer.cvino.lowapi.InferenceEngineNetwork;
import free.lucifer.cvino.lowapi.InputLayer;
import free.lucifer.cvino.lowapi.OutputLayer;
import free.lucifer.cvino.lowapi.enums.Layout;
import free.lucifer.cvino.lowapi.enums.Precision;
import free.lucifer.cvino.lowapi.enums.ResizeAlgorithm;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author Lucifer
 */
public class App {

    public static void main(String[] args) throws IOException {

//        if (args.length == 0) {
//            args = new String[]{
//                "C:\\Program Files (x86)\\Intel\\openvino_2021\\inference_engine\\demos\\monodepth_demo\\python\\public\\midasnet\\FP16\\midasnet",
//                "CPU",
//                "1.jpg",
//                "2.jpg"
//            };
//        }

        if (args.length != 4) {
            System.out.println("Please provide inference network path and device\nExample: java -jar " + extractJarName() + " [...openvino_install_path]/midasnet/FP16/midasnet CPU inImage outImage");
            System.exit(0);
        }

        try (InferenceEngineCore core = new InferenceEngineCore()) {
            String path = args[0];
            String devName = args[1];

            try (InferenceEngineNetwork net = core.readNetwork(path + ".xml", path + ".bin")) {

                InputLayer il = net.getInputs().get("image");
                OutputLayer ol = net.getOutputs().get("inverse_depth");

                il.setResizeAlgorithm(ResizeAlgorithm.RESIZE_BILINEAR);
                il.setPrecision(Precision.U8);
                il.setLayout(Layout.NHWC);
                
                ol.setPrecision(Precision.FP32);
                ol.setLayout(Layout.CHW);

                BufferedImage img = ImageIO.read(new File(args[2]));

                DataBufferByte imgData = (DataBufferByte) img.getRaster().getDataBuffer();

                InferenceEngineExecutionNetwork exec;
                try (InferenceEngineBlob blob = core.createBlob(Layout.NHWC, new int[]{1, 3, img.getHeight(), img.getWidth()}, Precision.U8, imgData.getData())) {
                    Device dev = core.getDevices().stream().filter(d -> devName.equals(d.getDeviceName())).findFirst().get();
                    Map<String, String> config = new HashMap<>();
                    System.out.println(dev);
                    exec = net.loadNetwork(dev, config);
                    InferenceEngineBlob oblob;
                    try (InferenceEngineInferenceRequest req = exec.createRequest()) {
                        req.setBlob(il, blob);
                        long start = System.currentTimeMillis();
                        req.request();
                        System.out.println("Inference time: " + (System.currentTimeMillis() - start) + "ms");
                        oblob = req.getBlob(ol);
                        BufferedImage outImg = new BufferedImage(384, 384, BufferedImage.TYPE_BYTE_GRAY);
                        DataBufferByte outImgData = (DataBufferByte) outImg.getRaster().getDataBuffer();
                        float[] data = oblob.readFloatBuffer();
                        System.arraycopy(reshape(data), 0, outImgData.getData(), 0, data.length);
                        ImageIO.write(outImg, "JPEG", new File(args[3]));
                    }
                    oblob.close();
                }
                exec.close();
            }
        }
    }

    private static byte[] reshape(float[] data) {
        float max = Integer.MIN_VALUE;
        float min = Integer.MAX_VALUE;
        for (float f : data) {
            if (max < f) {
                max = f;
            }
            if (min > f) {
                min = f;
            }
        }

        byte[] ret = new byte[data.length];

        float div = max - min;

        for (int i = 0; i < data.length; i++) {
            ret[i] = (byte) (((data[i] - min) * 255) / div);
        }

        return ret;
    }

    private static String extractJarName() {
        return App.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
    }
}
