package com.wealthtrack.api.infra.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    // O @PostConstruct diz ao Spring: "Rode este método sozinho logo após a aplicação iniciar"
    @PostConstruct
    public void initialize() {
        try {
            // Busca o arquivo JSON que colocamos na pasta resources
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-key.json");

            if (serviceAccount == null) {
                throw new RuntimeException("Arquivo firebase-key.json não encontrado!");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("wealthtrack-ai.appspot.com") // Coloque a URL do SEU bucket aqui (sem o gs://)
                    .build();

            // Evita erro de inicializar duas vezes
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}