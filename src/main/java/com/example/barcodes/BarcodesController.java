package com.example.barcodes;

import com.example.barcodes.generators.QRGenBarcodeGenerator;
import com.example.barcodes.generators.ZxingBarcodeGenerator;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

@RestController
@RequestMapping("/barcodes")
public class BarcodesController {
    @PostMapping(value = "/zxing/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> zxingQRCode(@RequestBody String barcode) throws Exception {
        return okResponse(ZxingBarcodeGenerator.generateQRCodeImage(barcode));
    }

    //QRGen

    @PostMapping(value = "/qrGenerate/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> qrgenQRCode(@RequestBody String barcode) throws Exception {
        return okResponse(QRGenBarcodeGenerator.generateQRCodeImage(barcode));
    }

    @PostMapping(value = "/qrDecode/qrcode", produces = MediaType.TEXT_PLAIN_VALUE)
    public String qrDecodeQRCode(@RequestParam("file") MultipartFile file) throws Exception {
        Path destinationFile = Files.createTempFile("qrcode",null);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile,
                    StandardCopyOption.REPLACE_EXISTING);
        }
        System.out.println("Temp File Name: " + destinationFile.getFileName());
        byte[] fileContent = FileUtils.readFileToByteArray(destinationFile.toFile());
        String encodedQrCode = Base64.getEncoder().encodeToString(fileContent);
        //encodedQrCode = "iVBORw0KGgoAAAANSUhEUgAAAMgAAADIAQAAAACFI5MzAAAA4klEQVR4Xu2VQQ7DIAwEnVOewU8J+SnP4BTXuxSkpu05e2CFItuTg2VjY/5Pdg9MLbIItIgMaRba3M+WCsykRGBGGGe4OqQYwmUPbpsgoSFKGBMkjm7zMg5XhnBKkHK6T8njpKsxMCVCmuXdSxxD28OQIofXg+XMhELE/WKTL6Rco+1C5J21GeqK9JVIrL1qeNDqQVeIdPVuR0W7K0LGdgkDN/FjSh4nyec+briPUqTwpT3xCxuuRpBvDErNX1k/TzAoKCdzFyKOcmLHZIsvpUKikpgS1hUvmxD5qUUWgRbRJi8nqFzsyU2nKQAAAABJRU5ErkJggg==";
        byte[] decodedByte = Base64.getDecoder().decode(encodedQrCode);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(decodedByte));
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        return QRGenBarcodeGenerator.decodeQRCodeImage(bitmap);
    }

    private ResponseEntity<BufferedImage> okResponse(BufferedImage image) {
        return new ResponseEntity<>(image, HttpStatus.OK);
    }
}
