package com.example.barcodes.generators;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.Result;
import com.google.zxing.qrcode.QRCodeReader;
import net.glxn.qrgen.javase.QRCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class QRGenBarcodeGenerator {

    public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
        ByteArrayOutputStream stream = QRCode
                .from(barcodeText).withCharset(StandardCharsets.UTF_8.displayName())
                .withSize(250, 250)
                .stream();
        ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

        return ImageIO.read(bis);
    }

    public static String decodeQRCodeImage(BinaryBitmap image) throws Exception {
        QRCodeReader qr = new QRCodeReader();
        Result res = qr.decode(image);
        return res.getText();
    }
}
