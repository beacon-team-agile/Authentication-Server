package com.example.authenticationserver.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterResponse {
    private String status;
    private String message;
}
