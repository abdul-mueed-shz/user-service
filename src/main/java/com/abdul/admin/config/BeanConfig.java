package com.abdul.admin.config;

import com.abdul.admin.domain.google.datastore.JPADataStoreFactory;
import com.abdul.admin.domain.google.port.out.repository.GoogleCredentialRepository;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    private final OauthProperties oauthProperties;
    private final GoogleCredentialRepository googleCredentialRepository;

    @Bean
    AuthorizationCodeFlow getAuthorizationCodeFlow() throws IOException {
        DataStoreFactory dataStoreFactory = new JPADataStoreFactory(googleCredentialRepository);
        return new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                oauthProperties.getRegistration().getGoogle().getClientId(),
                oauthProperties.getRegistration().getGoogle().getClientSecret(),
                oauthProperties.getRegistration().getGoogle().getScope())
                .setAccessType(oauthProperties.getRegistration().getGoogle().getAccessType())
                .setDataStoreFactory(dataStoreFactory)
                .build();
    }
}
