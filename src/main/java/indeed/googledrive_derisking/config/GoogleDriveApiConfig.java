package indeed.googledrive_derisking.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Configuration
public class GoogleDriveApiConfig {
    @Bean
    public GoogleCredentials googleCredentials(
            final GoogleDriveCredentialProperties googleDriveCredentialProperties)
            throws IOException {
        // Construct Google Drive credential json format
        final String googleDriveCredential = """
                {
                  "type": "%s",
                  "project_id": "%s",
                  "private_key_id": "%s",
                  "private_key": "%s",
                  "client_email": "%s",
                  "client_id": "%s",
                  "auth_uri": "%s",
                  "token_uri": "%s",
                  "auth_provider_x509_cert_url": "%s",
                  "client_x509_cert_url": "%s",
                  "universe_domain": "%s"
                }
                """.formatted(googleDriveCredentialProperties.getType(),
                googleDriveCredentialProperties.getProjectId(),
                googleDriveCredentialProperties.getProjectKeyId(),
                googleDriveCredentialProperties.getPrivateKey(),
                googleDriveCredentialProperties.getClientEmail(),
                googleDriveCredentialProperties.getClientId(),
                googleDriveCredentialProperties.getAuthUri(),
                googleDriveCredentialProperties.getTokenUri(),
                googleDriveCredentialProperties.getAuthProviderX509CertUrl(),
                googleDriveCredentialProperties.getClientX509CertUrl(),
                googleDriveCredentialProperties.getUniverseDomain());
        // Construct Google Drive Credential input stream
        final InputStream credentialInputStream = new ByteArrayInputStream(
                googleDriveCredential.getBytes(StandardCharsets.UTF_8));
        // Another Solution: reading from credential json file
        /**
         * {@code
         *       final InputStream credentialInputStream = Optional
         *              .ofNullable(GoogleDriveApiConfig.class.getClassLoader()
         *                      .getResourceAsStream("googledrivecredential.json"))
         *              .orElseThrow(() -> new IllegalStateException(
         *                      "Failed to read google drive credential file"));
         * }
         */
        return GoogleCredentials.fromStream(credentialInputStream)
                .createScoped(DriveScopes.DRIVE);
    }

    @Bean
    public HttpRequestInitializer httpRequestInitializer(
            final GoogleCredentials googleCredentials) {
        return new HttpCredentialsAdapter(googleCredentials);
    }

    @Bean
    public Drive googleDrive(final HttpRequestInitializer httpRequestInitializer) {
        return new Drive.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(),
                httpRequestInitializer).setApplicationName("BLT M3").build();
    }
}
