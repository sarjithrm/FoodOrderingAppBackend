package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.common.ItemType;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    @RequestMapping(method = RequestMethod.POST, path = "/order", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader(name = "authorization") final String authorization, @RequestBody(required = false) final SaveOrderRequest saveOrderRequest) throws AuthorizationFailedException, CouponNotFoundException, AddressNotFoundException, PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException{
        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customer = customerService.getCustomer(accessToken);

        AddressEntity address       = addressService.getAddressByUUID(saveOrderRequest.getAddressId(), customer);
        CouponEntity coupon         = orderService.getCouponByCouponId(saveOrderRequest.getCouponId().toString());
        PaymentEntity payment       = paymentService.getPaymentByUUID(saveOrderRequest.getPaymentId().toString());
        RestaurantEntity restaurant = restaurantService.restaurantByUUID(saveOrderRequest.getRestaurantId().toString());

        OrderEntity order = new OrderEntity();

        order.setUuid(UUID.randomUUID().toString());
        order.setBill(saveOrderRequest.getBill());
        order.setCoupon(coupon);
        order.setDiscount(saveOrderRequest.getDiscount());
        order.setPayment(payment);
        order.setAddress(address);
        order.setCustomer(customer);
        order.setRestaurant(restaurant);

        Date now = new Date();
        order.setDate(now);

        OrderItemEntity orderItem = new OrderItemEntity();
        for(ItemQuantity item: saveOrderRequest.getItemQuantities()){

            orderItem.setOrder(order);
            ItemEntity menuItem = itemService.getItem(item.getItemId().toString());
            orderItem.setItem(menuItem);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());
        }

        OrderEntity newOrder = orderService.saveOrder(order);
        OrderItemEntity orderItemEntities = orderService.saveOrderItem(orderItem);
        SaveOrderResponse saveOrderResponse = new SaveOrderResponse().id(newOrder.getUuid())
                .status("ORDER SUCCESSFULLY PLACED");

        return new ResponseEntity<SaveOrderResponse>(saveOrderResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCoupon(@RequestHeader(name = "authorization") final String authorization, @PathVariable final String coupon_name) throws AuthorizationFailedException, CouponNotFoundException{
        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customer = customerService.getCustomer(accessToken);

        CouponEntity coupon = orderService.getCouponByCouponName(coupon_name);

        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse().id(UUID.fromString(coupon.getUuid()))
                .couponName(coupon.getCouponName()).percent(coupon.getPercent());
        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getOrders(@RequestHeader(name = "authorization") final String authorization) throws AuthorizationFailedException{
        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customer = customerService.getCustomer(accessToken);

        List<OrderList> ordersList = new ArrayList<>();

        List<OrderEntity> orders = orderService.getOrdersByCustomers(customer.getUuid());
        for(OrderEntity order: orders){
            OrderListAddressState state = new OrderListAddressState().id(UUID.fromString(order.getAddress().getState().getUuid()))
                    .stateName(order.getAddress().getState().getStateName());

            OrderListAddress address = new OrderListAddress().id(UUID.fromString(order.getAddress().getUuid()))
                    .flatBuildingName(order.getAddress().getFlatBuilNo())
                    .locality(order.getAddress().getLocality())
                    .city(order.getAddress().getCity())
                    .pincode(order.getAddress().getPincode())
                    .state(state);

            OrderListCoupon coupon = new OrderListCoupon().id(UUID.fromString(order.getCoupon().getUuid()))
                    .couponName(order.getCoupon().getCouponName())
                    .percent(order.getCoupon().getPercent());

            OrderListCustomer orderCustomer = new OrderListCustomer().id(UUID.fromString(order.getCustomer().getUuid()))
                    .firstName(order.getCustomer().getFirstName())
                    .lastName(order.getCustomer().getLastName())
                    .emailAddress(order.getCustomer().getEmail())
                    .contactNumber(order.getCustomer().getContactNumber());

            OrderListPayment payment = new OrderListPayment().id(UUID.fromString(order.getPayment().getUuid()))
                    .paymentName(order.getPayment().getPaymentName());

            List<OrderItemEntity> orderItems = orderService.getOrderItems(order);
            List<ItemQuantityResponse> orderItemList = new ArrayList<>();
            for(OrderItemEntity item: orderItems){
                ItemQuantityResponseItem itemDetails = new ItemQuantityResponseItem().id(UUID.fromString(item.getItem().getUuid()))
                        .itemName(item.getItem().getItemName())
                        .itemPrice(item.getItem().getPrice())
                        .type(ItemQuantityResponseItem.TypeEnum.valueOf(ItemType.getItemType(item.getItem().getType().toString())));

                ItemQuantityResponse itemResponse = new ItemQuantityResponse().item(itemDetails)
                        .quantity(item.getQuantity())
                        .price(item.getPrice());

                orderItemList.add(itemResponse);
            }

            OrderList orderDetails = new OrderList().id(UUID.fromString(order.getUuid()))
                    .bill(order.getBill())
                    .coupon(coupon)
                    .discount(order.getDiscount())
                    .date(String.valueOf(order.getDate()))
                    .payment(payment)
                    .customer(orderCustomer)
                    .address(address)
                    .itemQuantities(orderItemList);

            ordersList.add(orderDetails);
        }

        CustomerOrderResponse response = new CustomerOrderResponse().orders(ordersList);
        return new ResponseEntity<CustomerOrderResponse>(response, HttpStatus.OK);
    }
}
