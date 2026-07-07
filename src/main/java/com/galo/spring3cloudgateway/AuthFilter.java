package com.galo.spring3cloudgateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

  private final WebClient webClient;

  public AuthFilter(WebClient webClient) {super(Config.class);
    this.webClient = webClient;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      String token = exchange.getRequest().getHeaders().getFirst("Authorization");

      return webClient.get()
              .uri("/validate")
              .header("Authorization", token)
              .retrieve()
              .bodyToMono(TokenValidationResponse.class)
              .flatMap(response -> {
                if (!response.isValid()) {
                  // Token inválido
                  exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                  exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

                  String body = "{\"error\":\"usuario não logado\"}";
                  DataBuffer buffer = exchange.getResponse()
                          .bufferFactory()
                          .wrap(body.getBytes());

                  return exchange.getResponse().writeWith(Mono.just(buffer));
                } else if (!response.getScope().contains("ADMIN")) {
                  // ‘Token’ válido, mas sem escopo correto
                  exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                  exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

                  String body = "{\"error\":\"usuario sem permissão\"}";
                  DataBuffer buffer = exchange.getResponse()
                          .bufferFactory()
                          .wrap(body.getBytes());

                  return exchange.getResponse().writeWith(Mono.just(buffer));
                } else {
                  // Token válido e escopo correto
                  return chain.filter(exchange);
                }
              })
              .onErrorResume(e -> {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

                String body = "{\"error\":\"usuario não logado\"}";
                DataBuffer buffer = exchange.getResponse()
                        .bufferFactory()
                        .wrap(body.getBytes());

                return exchange.getResponse().writeWith(Mono.just(buffer));
              });

    };
  }

  public static class Config {
    // Pode adicionar configs extras se precisar
  }
}
