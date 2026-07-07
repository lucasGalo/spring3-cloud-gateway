package com.galo.spring3cloudgateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

  @Value("${services.users}")
  private String usersServiceUrl;
  @Value("${services.auth}")
  private String authServiceUrl;
  @Bean
  public RouteLocator customRouteLocator(RouteLocatorBuilder builder, AuthFilter authFilter) {
    return builder.routes()
            .route("users", r -> r.path("/users/**")
                    .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                    .uri(usersServiceUrl))
            .route("auth", r -> r.path("/auth/**")
                    .uri(authServiceUrl))
            .build();
  }
}
