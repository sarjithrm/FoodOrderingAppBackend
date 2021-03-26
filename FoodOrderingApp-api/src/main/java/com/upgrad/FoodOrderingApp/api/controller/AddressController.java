package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
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

@RestController
@RequestMapping("/")
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressService addressService;

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

        final AddressEntity address = new AddressEntity();

        address.setFlatBuilNumber(saveAddressRequest.getFlatBuildingName());
        address.setCity(saveAddressRequest.getCity());
        address.setLocality(saveAddressRequest.getLocality());
        address.setPincode(saveAddressRequest.getPincode());
        address.setState(null);

        final AddressEntity newAddress = addressService.saveAddress(accessToken, address, saveAddressRequest.getStateUuid());

        SaveAddressResponse saveAddressResponse = new SaveAddressResponse().id(newAddress.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
    }

    /**
     * Fetch All saved addresses
     * @param authorization
     * @return ResponseEntity<AddressListResponse>
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/address/customer", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAddress(@RequestHeader(name = "authorization") final String authorization) throws AuthorizationFailedException{
        String accessToken = authorization.split("Bearer ")[1];

        List<AddressEntity> addresses   =   addressService.getSavedAddresses(accessToken);
        List<AddressList> addressesList =   new ArrayList<>();

        ListIterator<AddressEntity> listIterator = addresses.listIterator();
        while (listIterator.hasNext()){
            AddressEntity address = listIterator.next();
            AddressListState stateList = new AddressListState().id(UUID.fromString(address.getState().getUuid()))
                    .stateName(address.getState().getStateName());
            AddressList addressList = new AddressList().id(UUID.fromString(address.getUuid())).flatBuildingName(address.getFlatBuilNumber())
                    .city(address.getCity()).locality(address.getLocality()).pincode(address.getPincode())
                    .state(stateList);
            addressesList.add(addressList);
        }

        AddressListResponse addressListResponse = new AddressListResponse().addresses(addressesList);
        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);
    }

    /**
     * Delete Address
     * @param addressUuid
     * @param authorization
     * @return ResponseEntity<DeleteAddressResponse>
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/address/delete/{address_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@PathVariable("address_id") final String addressUuid, @RequestHeader(name = "authorization") final String authorization) throws AuthorizationFailedException, AddressNotFoundException{
        String accessToken = authorization.split("Bearer ")[1];

        AddressEntity address = addressService.deleteAddress(addressUuid, accessToken);

        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse().id(UUID.fromString(address.getUuid()))
                .status("ADDRESS DELETED SUCCESSFULLY");

        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);
    }

    /**
     * Fetch states
     * @return ResponseEntity<StatesListResponse>
     */
    @RequestMapping(method = RequestMethod.GET, path = "/states", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getStates(){
        List<StateEntity> states   =   addressService.getStates();
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
