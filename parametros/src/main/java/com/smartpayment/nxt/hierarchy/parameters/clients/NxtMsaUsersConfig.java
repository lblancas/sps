package com.smartpayment.nxt.hierarchy.parameters.clients;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class NxtMsaUsersConfig {

  public static String USER_PATH = "/user";

  @Bean("nxtMsaUsersWebClient")
  public WebClient webClient(
      @Value("${webclient.client.nxt-msa-users.base-url}") String baseUrl,
      @Value("${webclient.client.nxt-msa-users.connect-timeout}") Integer connectTimeout,
      @Value("${webclient.client.nxt-msa-users.read-timeout}") Integer readTimeout,
      @Value("${webclient.client.nxt-msa-users.write-timeout}") Integer writeTimeout
  ) {
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
        .responseTimeout(Duration.ofMillis(connectTimeout))
        .doOnConnected(conn ->
            conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS)));


    return WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  public static String getResponseMessage(JsonNode jsonResponse) {
    String nxtMsaUsersMessage;

    if (jsonResponse.has("message")) {

      //Prioridad a los mensajes del framework, si usan la estructura de smart entonces no existe este nodo
      nxtMsaUsersMessage = jsonResponse.get("message").toString();

    } else if (jsonResponse.has("class") && jsonResponse.get("class").has("message")) {

      //Algunos servicios dan el detalle del error en este nodo y no en el de la estructura smart
      nxtMsaUsersMessage = jsonResponse.get("class").get("message").toString();

    } else if (jsonResponse.has("responseCode") && jsonResponse.get("responseCode").has("message")) {

      //Estructura smart
      nxtMsaUsersMessage = jsonResponse.get("responseCode").get("message").toString();

    } else {

      nxtMsaUsersMessage = "Mensaje no identificado en respuesta de nxtMsaUsers";
      log.error(nxtMsaUsersMessage + ", jsonResponse: {}", jsonResponse.toString());
    }

    return nxtMsaUsersMessage;
  }

}
