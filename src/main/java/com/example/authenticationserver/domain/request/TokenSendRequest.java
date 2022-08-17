package com.example.authenticationserver.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenSendRequest {
    private String email;
    private String token;
}
