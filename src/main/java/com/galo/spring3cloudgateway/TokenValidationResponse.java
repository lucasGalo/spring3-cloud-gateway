package com.galo.spring3cloudgateway;

import java.util.List;


public class TokenValidationResponse {
  private boolean valid;
  private String username;
  private List<String> scope;
  private String error;

  public TokenValidationResponse() {
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<String> getScope() {
    return scope;
  }

  public void setScope(List<String> scope) {
    this.scope = scope;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}
