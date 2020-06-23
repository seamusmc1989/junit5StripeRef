package com.ref.testing.customer;

import com.ref.testing.utils.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository,
                                       PhoneNumberValidator phoneNumberValidator) {
        this.customerRepository = customerRepository;
        this.phoneNumberValidator = phoneNumberValidator;
    }

    public UUID registerNewCustomer(CustomerRegistrationRequest request) {
        String phoneNumber = request.getCustomer().getPhoneNumber();

        if (!phoneNumberValidator.test(phoneNumber)) {
            throw new IllegalStateException("Phone Number " + phoneNumber + " is not valid");
        }

        Optional<Customer> customerOptional = customerRepository
                .selectCustomerByPhoneNumber(phoneNumber);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            if (customer.getPhoneNumber().equals(request.getCustomer().getPhoneNumber())) {
                throw new IllegalStateException(String.format("phone number [%s] is taken", phoneNumber));
            }

            if (customer.getName().equals(request.getCustomer().getName())) {
                throw new IllegalStateException(String.format("name [%s] is taken", phoneNumber));
            }
        }


//        if (customerOptional.isPresent()) {
//            Customer customer = customerOptional.get();
//            System.out.println("customer is... " + customer.toString());
//            if (customer.getName().equals(request.getCustomer().getName())) {
//                return;
//            }
//            throw new IllegalStateException(String.format("phone number [%s] is taken", phoneNumber));
//        }

        if(request.getCustomer().getId() == null) {
            UUID uuid = UUID.randomUUID();
            request.getCustomer().setId(uuid);
        }

        customerRepository.save(request.getCustomer());

        return request.getCustomer().getId();
    }
}
