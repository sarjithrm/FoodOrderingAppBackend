package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;


    /**
     * signUp - Register Customer
     * @param customerEntity
     * @return CustomerEntity
     * @throws SignUpRestrictedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(final CustomerEntity customerEntity) throws SignUpRestrictedException {

        String emailRegex           =   "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]{2,5}$";
        String contactNumberRegex   =   "^[0-9]{10}$";
        String passwordRegex        =   "([A-Z]+[0-9]+[a-z]*[#@$%&*!^]+){8,}";

        CustomerEntity customer = customerDao.getContactNumber(customerEntity.getContactNumber());

        if(customerEntity.getFirstName().equals("") || customerEntity.getEmail().equals("") || customerEntity.getContactNumber().equals("") || customerEntity.getPassword().equals("")){
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
        if(customer != null){
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number");
        }
        if(Pattern.compile(emailRegex).matcher(customerEntity.getEmail()).equals("false")){
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }
        if(Pattern.compile(contactNumberRegex).matcher(customerEntity.getContactNumber()).equals("false")){
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }
        if(Pattern.compile(passwordRegex).matcher(customerEntity.getPassword()).equals("false")){
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }

        String password = customerEntity.getPassword();

        String[] encryptedText = cryptographyProvider.encrypt(password);
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);

        return customerDao.createCustomer(customerEntity);
    }


    /**
     * authenticate - Customer Authentication and Access-Token generation
     * @param username, password
     * @return CustomerAuthEntity
     * @throws AuthenticationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        CustomerEntity customer = customerDao.getContactNumber(username);
        if(customer == null){
            throw new AuthenticationFailedException("ATH-001", "this contact number has not been registered!");
        }

        String encryptedPassword = PasswordCryptographyProvider.encrypt(password, customer.getSalt());
        if(encryptedPassword.equals(customer.getPassword())){
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuthToken = new CustomerAuthEntity();
            customerAuthToken.setCustomer(customer);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expireAt = now.plusHours(6);

            customerAuthToken.setAccessToken(jwtTokenProvider.generateToken(customer.getUuid(), now, expireAt));

            customerAuthToken.setUuid(UUID.randomUUID().toString());
            customerAuthToken.setLoginAt(now);
            customerAuthToken.setExpiresAt(expireAt);

            customerDao.createAuthToken(customerAuthToken);

            return customerAuthToken;
        }else{
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }
    }


    /**
     * authorize the customer
     * @param accessToken
     * @return CustomerAuthEntity
     * @throws AuthorizationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity getCustomer(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAccessToken(accessToken);
        if(customerAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        if(customerAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if(now.isAfter(customerAuthEntity.getExpiresAt())){
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        return customerAuthEntity.getCustomer();
    }

    /**
     * logout - Customer Logout
     * @param accessToken
     * @return CustomerAuthEntity
     * @throws AuthorizationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(final String accessToken) throws AuthorizationFailedException {
        CustomerEntity customerEntity = getCustomer(accessToken);

        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAccessToken(accessToken);
        final ZonedDateTime now = ZonedDateTime.now();
        customerAuthEntity.setLogoutAt(now);
        customerDao.updateCustomerAuth(customerAuthEntity);

        return customerAuthEntity;
    }

    /**
     * update Customer details
     * @param customer
     * @return CustomerEntity
     * @throws AuthorizationFailedException
     * @throws UpdateCustomerException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(final CustomerEntity customer) throws AuthorizationFailedException, UpdateCustomerException{
        if(customer.getFirstName().equals("")){
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }

        customerDao.updateCustomer(customer);

        return customer;
    }

    /**
     * Change Customer Password
     * @param customer
     * @param oldPassword
     * @param newPassword
     * @return CustomerEntity
     * @throws AuthorizationFailedException
     * @throws UpdateCustomerException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(final String oldPassword, final String newPassword, final CustomerEntity customer) throws AuthorizationFailedException, UpdateCustomerException{
        String passwordRegex        =   "([A-Z]+[0-9]+[a-z]*[#@$%&*!^]+){8,}";

        if(oldPassword.equals("") || newPassword.equals("")){
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }

        if(Pattern.compile(passwordRegex).matcher(newPassword).equals("false")){
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }
        String encryptedPassword = PasswordCryptographyProvider.encrypt(oldPassword, customer.getSalt());
        if(!encryptedPassword.equals(customer.getPassword())){
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }

        String[] encryptedText = cryptographyProvider.encrypt(newPassword);
        customer.setSalt(encryptedText[0]);
        customer.setPassword(encryptedText[1]);
        customerDao.updateCustomer(customer);

        return customer;
    }

    /**
     * get customer by Id
     * @param customerId
     * @return customer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity getCustomerByUUID(final String customerId){
        CustomerEntity customer = customerDao.getCustomerByUUID(customerId);
        return customer;
    }
}
