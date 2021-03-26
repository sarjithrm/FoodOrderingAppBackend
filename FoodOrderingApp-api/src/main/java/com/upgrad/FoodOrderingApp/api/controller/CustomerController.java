package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * signUp - Register Customer
     * @param signupCustomerRequest
     * @return ResponseEntity<SignupCustomerResponse>
     * @throws SignUpRestrictedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signUp(@RequestBody(required = false) final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException{

        final CustomerEntity customerEntity = new CustomerEntity();

        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstname(signupCustomerRequest.getFirstName());
        customerEntity.setLastname(signupCustomerRequest.getLastName());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setSalt(RandomStringUtils.randomAlphanumeric(10));

        final CustomerEntity newCustomerEntity = customerService.saveCustomer(customerEntity);
        SignupCustomerResponse customerResponse = new SignupCustomerResponse().id(newCustomerEntity.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(customerResponse, HttpStatus.CREATED);
    }


    /**
     * login - Customer Login
     * @param authorization
     * @return ResponseEntity<LoginResponse>
     * @throws AuthenticationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/login", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader(name = "authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decode;
        decode = Base64.getDecoder().decode(authorization);
        String decodedText = new String(decode);

        CustomerAuthEntity customerAuthEntity = customerService.authenticate(decodedText);
        CustomerEntity customer = customerAuthEntity.getCustomer();

        LoginResponse loginResponse = new LoginResponse().id(customer.getUuid()).firstName(customer.getFirstname()).lastName(customer.getLastname())
                .contactNumber(customer.getContactNumber()).emailAddress(customer.getEmail()).message("LOGGED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", customerAuthEntity.getAccessToken());

        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }


    /**
     * logout - Customer logout
     * @param accessToken
     * @return ResponseEntity<LogoutResponse>
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/logout", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader(name = "authorization") final String accessToken) throws AuthorizationFailedException{

        CustomerAuthEntity customerAuthEntity = customerService.logout(accessToken);
        CustomerEntity customer = customerAuthEntity.getCustomer();

        LogoutResponse logoutResponse = new LogoutResponse().id(customer.getUuid()).message("LOGGED OUT SUCCESSFULLY");

        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
    }


    /**
     * update - Customer Details updation
     * @param authorization
     * @param updateCustomerRequest
     * @return ResponseEntity<UpdateCustomerResponse>
     * @throws AuthorizationFailedException
     * @throws UpdateCustomerException
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/customer", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> update(@RequestHeader(name = "authorization") final String authorization, @RequestBody(required = false) final UpdateCustomerRequest updateCustomerRequest) throws AuthorizationFailedException, UpdateCustomerException{
        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customerEntity = customerService.updateCustomer(accessToken, updateCustomerRequest.getFirstName(), updateCustomerRequest.getLastName());

        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse().id(customerEntity.getUuid())
                .firstName(customerEntity.getFirstname()).lastName(customerEntity.getLastname()).status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }

    /**
     * Change Customer Password
     * @param authorization
     * @param updatePasswordRequest
     * @return ResponseEntity<UpdatePasswordResponse>
     * @throws AuthorizationFailedException
     * @throws UpdateCustomerException
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/customer/password", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> changePassword(@RequestHeader(name = "authorization") final String authorization, @RequestBody(required = false) final UpdatePasswordRequest updatePasswordRequest) throws AuthorizationFailedException, UpdateCustomerException{
        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customer = customerService.changePassword(accessToken, updatePasswordRequest.getOldPassword(), updatePasswordRequest.getNewPassword());

        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse().id(customer.getUuid()).status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
    }

}
