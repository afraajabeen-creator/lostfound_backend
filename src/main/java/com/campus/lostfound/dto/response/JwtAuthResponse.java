package com.campus.lostfound.dto.response;

/**
 * DTO returned upon successful login or registration containing JWT token and user summary.
 */
public class JwtAuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private UserSummaryDto user;

    public JwtAuthResponse() {
    }

    public JwtAuthResponse(String token, UserSummaryDto user) {
        this.token = token;
        this.tokenType = "Bearer";
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UserSummaryDto getUser() {
        return user;
    }

    public void setUser(UserSummaryDto user) {
        this.user = user;
    }
}
