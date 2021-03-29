package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/")
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private CustomerService customerService;

    /**
     * Address Registration
     * @param authorization
     * @param saveAddressRequest
     * @return ResponseEntity<SaveAddressResponse>
     * @throws AuthorizationFailedException
     * @throws SaveAddressException
     * @throws AddressNotFoundException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader(name = "authorization") final String authorization, @RequestBody(required = false) final SaveAddressRequest saveAddressRequest) throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException{
        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customer = customerService.getCustomer(accessToken);

        String pincodeRegex =   "^[0-9]{6}$";

        if(saveAddressRequest.getFlatBuildingName().equals("") || saveAddressRequest.getCity().equals("") || saveAddressRequest.getLocality().equals("") || saveAddressRequest.getPincode().equals("") || saveAddressRequest.getStateUuid().equals("")){
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }
        if(Pattern.compile(pincodeRegex).matcher(saveAddressRequest.getPincode()).equals("false")){
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }

        StateEntity state = addressService.getStateByUUID(saveAddressRequest.getStateUuid());

        final AddressEntity address = new AddressEntity();

        address.setUuid(UUID.randomUUID().toString());
        address.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        address.setCity(saveAddressRequest.getCity());
        address.setLocality(saveAddressRequest.getLocality());
        address.setPincode(saveAddressRequest.getPincode());
        address.setState(state);

        final AddressEntity newAddress = addressService.saveAddress(address, customer);

        SaveAddressResponse saveAddressResponse = new SaveAddressResponse().id(newAddress.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
    }

    /**
     * get address of customer
     * @param authorization
     * @return ResponseEntity<AddressListResponse>
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/address/customer", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAddress(@RequestHeader(name = "authorization") final String authorization) throws AuthorizationFailedException{
        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customer = customerService.getCustomer(accessToken);

        List<AddressEntity> addresses   =   addressService.getAllAddress(customer);
        List<AddressList> addressesList =   new ArrayList<>();

        ListIterator<AddressEntity> listIterator = addresses.listIterator();
        while (listIterator.hasNext()){
            AddressEntity address = listIterator.next();
            AddressListState stateList = new AddressListState().id(UUID.fromString(address.getState().getUuid()))
                    .stateName(address.getState().getStateName());
            AddressList addressList = new AddressList().id(UUID.fromString(address.getUuid())).flatBuildingName(address.getFlatBuilNo())
                    .city(address.getCity()).locality(address.getLocality()).pincode(address.getPincode())
                    .state(stateList);
            addressesList.add(addressList);
        }

        AddressListResponse addressListResponse = new AddressListResponse().addresses(addressesList);
        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);
    }

    /**
     * delete address
     * @param addressUuid
     * @param authorization
     * @return ResponseEntity<DeleteAddressResponse>
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@PathVariable("address_id") final String addressUuid, @RequestHeader(name = "authorization") final String authorization) throws AuthorizationFailedException, AddressNotFoundException{
        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customer = customerService.getCustomer(accessToken);

        AddressEntity address = addressService.getAddressByUUID(addressUuid, customer);

        AddressEntity deletedAddress = addressService.deleteAddress(address);

        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse().id(UUID.fromString(deletedAddress.getUuid()))
                .status("ADDRESS DELETED SUCCESSFULLY");

        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);
    }

    /**
     * get states
     * @return ResponseEntity<StatesListResponse>
     */
    @RequestMapping(method = RequestMethod.GET, path = "/states", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getStates(){
        List<StateEntity> states   =   addressService.getAllStates();
        List<StatesList> statesList =   new ArrayList<>();

        ListIterator<StateEntity> listIterator = states.listIterator();
        while (listIterator.hasNext()){
            StateEntity state = listIterator.next();
            StatesList stateList = new StatesList().id(UUID.fromString(state.getUuid()))
                    .stateName(state.getStateName());
            statesList.add(stateList);
        }

        StatesListResponse statesListResponse = new StatesListResponse().states(statesList);
        return new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);
    }
}
