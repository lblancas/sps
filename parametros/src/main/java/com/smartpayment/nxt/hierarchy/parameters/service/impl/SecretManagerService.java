
package com.smartpayment.nxt.hierarchy.parameters.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartpayment.nxt.hierarchy.parameters.dto.DatabaseConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Slf4j
@Service
public class SecretManagerService {

  public DatabaseConfigDTO getDatabaseConfig(String secretName) throws JsonProcessingException {

    SecretsManagerClient secretManager = SecretsManagerClient.builder()
        .region(Region.US_EAST_1)
        .build();

    GetSecretValueRequest request = GetSecretValueRequest.builder()
        .secretId(secretName)
        .build();

    GetSecretValueResponse response = secretManager.getSecretValue(request);

    if (log.isDebugEnabled()) {
      log.debug("Secret content: {}",response.secretString());
    }

    return new ObjectMapper().readValue(response.secretString(), DatabaseConfigDTO.class);
  }

}
