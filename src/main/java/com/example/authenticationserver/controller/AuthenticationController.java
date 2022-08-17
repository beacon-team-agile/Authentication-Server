package com.example.authenticationserver.controller;

import com.example.authenticationserver.domain.entity.RegistrationToken;
import com.example.authenticationserver.domain.request.LoginRequest;
import com.example.authenticationserver.domain.request.TokenRequest;
import com.example.authenticationserver.domain.request.TokenSendRequest;
import com.example.authenticationserver.domain.response.LoginResponse;
import com.example.authenticationserver.security.AuthUserDetail;
import com.example.authenticationserver.security.JwtFilter;
import com.example.authenticationserver.security.JwtProvider;
import com.example.authenticationserver.security.JwtTokenGenerator;
import com.example.authenticationserver.service.RegTokenService;
import com.example.authenticationserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

@RestController
@RequestMapping("/credential")
public class AuthenticationController {
    private AuthenticationManager authenticationManager;
    private JwtProvider jwtProvider;
    private JwtFilter jwtFilter;

    private final RegTokenService regTokenService;

    @Autowired
    public AuthenticationController (RegTokenService regTokenService) {
        this.regTokenService = regTokenService;
    }


    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Autowired
    public void setJwtFilter(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * TokenRequest contains:
     *      Manual input email
     *      Requester (HR)
     * @param @RequestBody request
     * @return
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateToken(@RequestBody TokenRequest request) {
        //Generate token with expiration date
        String tokenGenerateInput = request.getRequesterId();
        String token = jwtTokenGenerator.generateToken(tokenGenerateInput);

        //send request to Email service
        String emailTokenURI = "http://localhost:8091/email-service/email/send_reg_token";
        TokenSendRequest sendRequestBody = TokenSendRequest.builder().email(request.getEmail()).token(token).build();

        //Form rest template and HTTP headers
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<TokenSendRequest> requestEntity = RequestEntity
                .post(emailTokenURI, "")
                .accept(MediaType.APPLICATION_JSON)
                .body(sendRequestBody);

        //Request action to the email service
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        //Get current server time
        Date date = new java.util.Date();

        //Save it into the RegistrationToken table in Authentication database
        Integer id = Integer.valueOf(request.getRequesterId());
        RegistrationToken registrationToken
                = RegistrationToken.builder()
                    .token(token)
                    .email(request.getEmail())
                    .expirationDate(date.toString())
                    .createBy(id)
                    .build();

        regTokenService.addRegisterToken(registrationToken);

        return response;
    }

    @PostMapping("/login")
    public LoginResponse loginRequest(@RequestBody LoginRequest request) {
        Authentication authentication = null;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getPassword())
            );
        } catch (AuthenticationException e){
            e.printStackTrace();
            return LoginResponse.builder()
                    .message("User not found ")
                    .token("")
                    .build();
        }

        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal();

        String token = jwtProvider.createToken(authUserDetail);

        return LoginResponse.builder()
                .message("Welcome ")// + authUserDetail.getUsername()
                .token(token)
                .build();
    }

    @GetMapping("/register")
    public String registerRequest(@RequestParam(("token")) String token,
                                  HttpServletResponse response) throws ParseException, IOException {
        //Validate token
        RegistrationToken registrationToken = regTokenService.getExistingToken(token);

        //Invalid token
        if (registrationToken == null) {
            return "Invalid token";
        }

        //Expired token
        if (registrationToken.getExpirationDate() == null) {
            return "Invalid token setup";
        } else {
            String registeredDateString = registrationToken.getExpirationDate();
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            Date registeredDate = formatter.parse(registeredDateString);
            Date currentDate = new java.util.Date();

            long registeredDateTimeInMillis = registeredDate.getTime();
            long currentDateTimeInMillis = currentDate.getTime();
            System.out.println(currentDateTimeInMillis);
            System.out.println(registeredDateTimeInMillis);
            if (currentDateTimeInMillis - 10800000 > registeredDateTimeInMillis) { //pass
                return "Expired Token";
            }
        }


        //Redirect to composite
        response.sendRedirect("http://localhost:8095/composite-service");
        return "redirected";
    }
}

