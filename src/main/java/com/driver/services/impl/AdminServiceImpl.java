package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
    Admin admin = new Admin(username,password);

    adminRepository1.save(admin);
    return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
    Admin admin = adminRepository1.findById(adminId).get();
    ServiceProvider serviceProvider = new ServiceProvider(providerName);

    serviceProvider.setAdmin(admin);
    admin.getServiceProviderList().add(serviceProvider);

    adminRepository1.save(admin);
    return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        Country country = new Country();
        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();





        return serviceProvider;
    }
}
