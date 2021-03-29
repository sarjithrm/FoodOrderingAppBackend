package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;
import sun.util.resources.cldr.ext.CurrencyNames_zh_Hans_SG;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * createCustomer
     * @param customerEntity
     * @return CustomerEntity
     */
    public CustomerEntity createCustomer(CustomerEntity customerEntity){
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    /**
     * getContactNumber - Check contact number is used or not
     * @param contactNumber
     * @return 1 if used else 0
     */
    public CustomerEntity getContactNumber(final String contactNumber){
        try{
            return entityManager.createNamedQuery("getContactNumber", CustomerEntity.class)
                    .setParameter("contactNumber", contactNumber)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    /**
     * Create Customer Authentication Token
     * @param customerAuthEntity
     * @return Customer Authentication Token
     */
    public CustomerAuthEntity createAuthToken(final CustomerAuthEntity customerAuthEntity){
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }


    /**
     * getCustomerAccessToken - Fetch the access token of customer
     * @param accessToken
     * @return CustomerAuthEntity
     */
    public CustomerAuthEntity getCustomerAccessToken(final String accessToken){
        try{
            return entityManager.createNamedQuery("getAccessToken", CustomerAuthEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    /**
     * update Customer logout details
     * @param customerAuthEntity
     */
    public void updateCustomerAuth(final CustomerAuthEntity customerAuthEntity){
        entityManager.merge(customerAuthEntity);
    }


    /**
     * update Customer details
     * @param customerEntity
     */
    public void updateCustomer(final CustomerEntity customerEntity){
        entityManager.merge(customerEntity);
    }

    /**
     * get customer by Id
     * @param customerId
     * @return customer
     */
    public CustomerEntity getCustomerByUUID(final String customerId){
        try{
            CustomerEntity customer = entityManager.createNamedQuery("getCustomerById", CustomerEntity.class)
                    .setParameter("uuid", customerId)
                    .getSingleResult();
            return customer;
        }catch (NoResultException nre){
            return null;
        }
    }
}
