package com.wealthtrack.api.infra.storage;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    public String uploadFile(MultipartFile file) {
        // 1. Trata a Opcionalidade: Se não mandou arquivo, retorna nulo e vida que segue.
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            String contentType = file.getContentType();

            // Array que vai guardar o arquivo final (comprimido ou original)
            byte[] fileBytes;

            // 2. Verifica se é uma imagem. Se for, aplica a compressão!
            if (contentType != null && contentType.startsWith("image/")) {

                // Abre um "tubo" na memória RAM para guardar a imagem comprimida
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                // Mágica do Thumbnailator: Pega o arquivo original, mantém o tamanho original (scale 1.0),
                // mas corta a qualidade das cores/pixels pela metade (0.5), e joga no "tubo".
                Thumbnails.of(file.getInputStream())
                        .scale(1.0)
                        .outputQuality(0.5)
                        .toOutputStream(outputStream);

                // Transforma o tubo em um array de bytes pronto para subir
                fileBytes = outputStream.toByteArray();

            } else {
                // Se for PDF ou outro tipo, pega os bytes originais sem mexer
                fileBytes = file.getBytes();
            }

            // 3. Faz o upload para o Firebase usando nossos bytes otimizados
            Blob blob = bucket.create(fileName, fileBytes, contentType);

            return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    bucket.getName(), fileName);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar/comprimir e fazer upload do arquivo", e);
        }
    }
}