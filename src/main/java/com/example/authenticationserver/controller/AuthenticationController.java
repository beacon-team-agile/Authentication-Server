package com.example.authenticationserver.controller;

import com.example.authenticationserver.dao.impl.UserDAOImpl;
import com.example.authenticationserver.domain.entity.RegistrationToken;
import com.example.authenticationserver.domain.entity.User;
import com.example.authenticationserver.domain.entity.UserRole;
import com.example.authenticationserver.domain.request.*;
import com.example.authenticationserver.domain.response.LoginResponse;
import com.example.authenticationserver.security.AuthUserDetail;
import com.example.authenticationserver.security.JwtFilter;
import com.example.authenticationserver.security.JwtProvider;
import com.example.authenticationserver.security.JwtTokenGenerator;
import com.example.authenticationserver.service.RegTokenService;
import com.example.authenticationserver.service.UserRoleService;
import com.example.authenticationserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/credential")
public class AuthenticationController {
    private AuthenticationManager authenticationManager;
    private JwtProvider jwtProvider;
    private JwtFilter jwtFilter;

    private UserService userService;
    private UserRoleService userRoleService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserRoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }


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
    @PreAuthorize("hasAuthority('hr')")
    public ResponseEntity<String> generateToken(@RequestBody TokenRequest request) {
        //Generate token with expiration date
        String tokenGenerateInput = request.getRequesterId();
        String token = jwtTokenGenerator.generateToken(tokenGenerateInput);

        //send request to Email service
        String emailTokenURI = "http://localhost:9000/email-service/email/send_reg_token";
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
    public LoginResponse loginRequest(@RequestBody LoginRequest request,
                                      HttpServletResponse response) {
        Authentication authentication = null;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword())
            );
        } catch(BadCredentialsException badCredentialsException) {
            //badCredentialsException.printStackTrace();
            return LoginResponse.builder()
                    .message("User not found")
                    .token("")
                    .build();
        } catch (AuthenticationException e){
            //e.printStackTrace();
            return LoginResponse.builder()
                    .message("User not found")
                    .token("")
                    .build();
        }

        //Check user status
        User user = userService.getUserByName(request.getUsername());
        if (user == null) {
            return LoginResponse.builder()
                    .message("Unregistered user")// + authUserDetail.getUsername()
                    .token("")
                    .build();
        } else if (!user.isActiveFlag()) {
            return LoginResponse.builder()
                    .message("Please wait for HR to review your application")// + authUserDetail.getUsername()
                    .token("")
                    .build();
        }

        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal();

        Set<String> roles = authUserDetail.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        String authority = roles.stream().findAny().orElse("");
        String token = jwtProvider.createToken(authUserDetail);

        if (authority.equals("hr")) {
            //Redirect to show status tracking page

            return LoginResponse.builder()
                    .message("Welcome HR!")// + authUserDetail.getUsername()
                    .token(token)
                    .build();
        } else if (authority.equals("employee")) {
            return LoginResponse.builder()
                    .message("Welcome HR!")// + authUserDetail.getUsername()
                    .token(token)
                    .build();
        } else {

            return LoginResponse.builder()
                    .message("Unauthorized")// + authUserDetail.getUsername()
                    .token(null)
                    .build();
        }
    }

    @PostMapping("/register")
    public String registerRequest(@RequestParam(("token")) String token,
                                  @RequestBody RegisterFormRequest registerFormRequest,
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
            if (currentDateTimeInMillis - 10800000 > registeredDateTimeInMillis) { //pass
                return "Expired Token";
            }
        }

        Date currentTimeStamp = new java.util.Date();

        //Get user token
        Authentication authentication;

        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            registerFormRequest.getUsername(),
                            registerFormRequest.getPassword())
            );
        } catch (AuthenticationException e){
            throw new BadCredentialsException("Provided credential is invalid.");
        }

        //We can add duplicate user identifier

        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal();
        String jwtToken = jwtProvider.createToken(authUserDetail);

        //Add user to the database
        User newUser = User.builder()
                .username(registerFormRequest.getUsername())
                .password(registerFormRequest.getPassword())
                .email(registerFormRequest.getEmail())
                .createDate(currentTimeStamp.toString())
                .lastModificationDate(currentTimeStamp.toString())
                .build();

        Integer userId = userService.creatUser(newUser);

        UserRole userRole = UserRole.builder()
                .userId(userId)
                .roleId(1)
                .activeFlag(true)
                .createDate(currentTimeStamp.toString())
                .lastModificationDate(currentTimeStamp.toString())
                .build();

        userRoleService.addUserRole(userRole);

        //Redirect to composite onborad form
        return jwtToken;
    }
}

