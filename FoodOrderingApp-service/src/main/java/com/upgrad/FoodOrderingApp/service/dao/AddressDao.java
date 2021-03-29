package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetch the state details by it's ID
     * @param stateId
     * @return StateEntity
     */
    public StateEntity getState(final String stateId){
        try{
            return entityManager.createNamedQuery("getState", StateEntity.class)
                    .setParameter("uuid", stateId)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    /**
     * Save Address
     * @param address
     * @return AddressEntity
     */
    public AddressEntity saveAddress(final AddressEntity address, final CustomerAddressEntity customerAddressEntity){
        entityManager.persist(address);
        entityManager.persist(customerAddressEntity);
        return address;
    }

    /**
     * get Addresses of customer
     * @param customer
     * @return List<CustomerAddressEntity>
     */
    public List<CustomerAddressEntity> getAddresses(final CustomerEntity customer){
        List<CustomerAddressEntity> addresses =  entityManager.createNamedQuery("getAddressOfCustomer", CustomerAddressEntity.class)
                .setParameter("customer", customer)
                .getResultList();
        return addresses;
    }

    /**
     * get Address By Uuid
     * @param addressUuid
     * @return AddressEntity
     */
    public AddressEntity getAddress(final String addressUuid){
        try{
            return entityManager.createNamedQuery("getAddressByUuid", AddressEntity.class)
                    .setParameter("uuid", addressUuid)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    /**
     * get Customer Address Map
     * @param address
     * @return CustomerAddressEntity
     */
    public CustomerAddressEntity getCustomerAddressMapping(final AddressEntity address){
        try{
            return entityManager.createNamedQuery("getCustomerAddressMap", CustomerAddressEntity.class)
                    .setParameter("address", address)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    /**
     * delete Address
     * @param address
     * @return AddressEntity
     */
    public AddressEntity deleteAddress(final AddressEntity address){
        entityManager.remove(address);
        return address;
    }

    /**
     * get all states
     * @return ArrayList<StateEntity>
     */
    public ArrayList<StateEntity> getStates(){
        ArrayList<StateEntity> states = (ArrayList<StateEntity>) entityManager.createNamedQuery("getStates", StateEntity.class)
                .getResultList();
        return states;
    }
}
