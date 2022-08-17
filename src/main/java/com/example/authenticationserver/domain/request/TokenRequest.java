package com.example.authenticationserver.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

@Getter
@Setter
@Builder
public class TokenRequest {
    private String email;
    private String requesterId;
}
