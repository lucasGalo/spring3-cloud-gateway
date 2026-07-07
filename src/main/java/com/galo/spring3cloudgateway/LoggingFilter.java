package com.galo.spring3cloudgateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingFilter implements GatewayFilter {
  private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    log.info("Request path: {}", exchange.getRequest().getURI().getPath());
    log.info("Headers: {}", exchange.getRequest().getHeaders());
    return chain.filter(exchange)
            .then(Mono.fromRunnable(() -> {
              log.info("Response status: {}", exchange.getResponse().getStatusCode());
            }));
  }
}
