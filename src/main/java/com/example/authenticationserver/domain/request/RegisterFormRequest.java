package com.example.authenticationserver.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterFormRequest {
    private String username;
    private String password;
    private String email;
}
