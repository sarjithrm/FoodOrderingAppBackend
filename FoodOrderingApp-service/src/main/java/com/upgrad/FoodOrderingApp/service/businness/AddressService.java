package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.regex.Pattern;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private CustomerService customerService;

    /**
     * Address Registration
     * @param accessToken
     * @param address
     * @param stateId
     * @return AddressEntity
     * @throws AuthorizationFailedException
     * @throws SaveAddressException
     * @throws AddressNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(final String accessToken, final AddressEntity address, final String stateId) throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException{
        String pincodeRegex =   "^[0-9]{6}$";
        if(address.getFlatBuilNumber().equals("") || address.getCity().equals("") || address.getLocality().equals("") || address.getPincode().equals("") || stateId.equals("")){
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }
        if(Pattern.compile(pincodeRegex).matcher(address.getPincode()).equals("false")){
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }
        StateEntity state = addressDao.getState(stateId);
        if(state == null){
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        CustomerAuthEntity customerAuthEntity = customerService.authorize(accessToken);
        CustomerEntity customer = customerAuthEntity.getCustomer();
        address.setState(state);

        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setAddress(address);
        customerAddressEntity.setCustomer(customer);

        return addressDao.saveAddress(address, customerAddressEntity);
    }

    /**
     * Get saved addresses
     * @param accessToken
     * @return ArrayList<AddressEntity>
     * @throws AuthorizationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public ArrayList<AddressEntity> getSavedAddresses(final String accessToken) throws AuthorizationFailedException{
        CustomerAuthEntity customerAuthEntity = customerService.authorize(accessToken);

        return addressDao.getAddresses();
    }

    /**
     * Delete Address
     * @param addressUuid
     * @param accessToken
     * @return AddressEntity
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(final String addressUuid, final String accessToken) throws AuthorizationFailedException, AddressNotFoundException{
        if(addressUuid.equals("")){
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }
        CustomerAuthEntity customerAuthEntity = customerService.authorize(accessToken);
        CustomerEntity customer = customerAuthEntity.getCustomer();

        AddressEntity address = addressDao.getAddress(addressUuid);
        if(address == null){
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }
        CustomerAddressEntity customerAddressEntity = addressDao.getCustomerAddressMapping(address);
        if(customer.getId() != customerAddressEntity.getCustomer().getId()){
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }

        return addressDao.deleteAddress(address);
    }

    /**
     * Get states
     * @return ArrayList<StateEntity>
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public ArrayList<StateEntity> getStates(){
        return addressDao.getStates();
    }

}
