package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception {
        User user = userRepository2.findById(userId).get();

        if (user.getConnected()) {
            throw new Exception("Already connected");
        }

        if (countryName.equalsIgnoreCase(user.getOriginalCountry().getCountryName().toString())) {
            return user;
        }

        if (user.getServiceProviderList() == null) {
            throw new Exception("Unable to connect");
        }

        List<ServiceProvider> providers = user.getServiceProviderList();
        int min = Integer.MAX_VALUE;
        ServiceProvider serviceProvider1 = null;
        Country country1 = null;

        for (ServiceProvider serviceProvider : providers) {
            List<Country> countryList = serviceProvider.getCountryList();

            for (Country country : countryList) {

                if (countryName.equalsIgnoreCase(country.getCountryName().toString()) && min > serviceProvider.getId()) {
                    min = serviceProvider.getId();
                    serviceProvider1 = serviceProvider;
                    country1 = country;
                }
            }
        }
        if (serviceProvider1 != null) {
            Connection connection = new Connection();
            connection.setUser(user);
            connection.setServiceProvider(serviceProvider1);

            String countryCode = country1.getCode();
            int providerId = serviceProvider1.getId();
            String maskedip = countryCode + "." + providerId + "." + userId;
            user.setConnected(true);
            user.setMaskedIp(maskedip);
            user.getConnectionList().add(connection);
            serviceProvider1.getConnectionList().add(connection);

            userRepository2.save(user);
            serviceProviderRepository2.save(serviceProvider1);

            return user;
        } else
            throw new Exception("Unable to connect");

    }

    @Override
    public User disconnect(int userId) throws Exception {

        User user = userRepository2.findById(userId).get();
        if (!user.getConnected()) {
            throw new Exception("Already disconnected");
        }
        user.setConnected(false);
        user.setMaskedIp(null);
        userRepository2.save(user);
        return user;
    }

    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender = userRepository2.findById(senderId).get();
        User reciver = userRepository2.findById(receiverId).get();

        CountryName reciverCountryName = null;
        if(reciver.getConnected()){
            String reciverCountryCode;
            String[] arr = reciver.getMaskedIp().split("\\.");
            reciverCountryCode = arr[0];
            for(CountryName countryName : CountryName.values()){
                if(countryName.toCode().equals(reciverCountryCode)){
                    reciverCountryName = countryName;
                    break;
                }
            }
        }else{
            reciverCountryName = reciver.getOriginalCountry().getCountryName();
        }

        if(reciverCountryName.equals(sender.getOriginalCountry().getCountryName())){
            return sender;
        }

        try {
            sender = connect(senderId, reciverCountryName.name());
        }catch (Exception e){
            throw new Exception("Cannot establish communication");
        }

        return sender;
    }
    }