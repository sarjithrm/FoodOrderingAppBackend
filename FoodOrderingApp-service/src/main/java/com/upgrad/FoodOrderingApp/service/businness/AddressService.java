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
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;


    /**
     * get State By UUID
     * @param stateId
     * @return state
     * @throws AddressNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public StateEntity getStateByUUID(final String stateId) throws AddressNotFoundException{
        StateEntity state = addressDao.getState(stateId);
        if(state == null){
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        return state;
    }

    /**
     * Address Registration
     * @param address
     * @param customer
     * @return AddressEntity
     * @throws AuthorizationFailedException
     * @throws SaveAddressException
     * @throws AddressNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(final AddressEntity address, final CustomerEntity customer) {

        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setAddress(address);
        customerAddressEntity.setCustomer(customer);

        return addressDao.saveAddress(address, customerAddressEntity);
    }

    /**
     * get all address
     * @param customer
     * @return List<AddressEntity>
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<AddressEntity> getAllAddress(final CustomerEntity customer) {

        List<CustomerAddressEntity> customerAddresses = addressDao.getAddresses(customer);
        List<AddressEntity> addresses = new ArrayList<>();
        for(CustomerAddressEntity address: customerAddresses){
            addresses.add(address.getAddress());
        }
        return addresses;
    }

    /**
     * get address by uuid
     * @param addressUuid
     * @param customer
     * @return address
     * @throws AddressNotFoundException
     * @throws AuthorizationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity getAddressByUUID(final String addressUuid, final CustomerEntity customer) throws AddressNotFoundException, AuthorizationFailedException{
        if(addressUuid.equals("")){
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }
        AddressEntity address = addressDao.getAddress(addressUuid);
        if(address == null){
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }

        CustomerAddressEntity customerAddressEntity = addressDao.getCustomerAddressMapping(address);
        if(customer.getId() != customerAddressEntity.getCustomer().getId()){
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }

        return address;
    }

    /**
     * delete address
     * @param address
     * @return address
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(final AddressEntity address){
        return addressDao.deleteAddress(address);
    }

    /**
     * get all states
     * @return list of states
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<StateEntity> getAllStates(){
        return addressDao.getStates();
    }

}
