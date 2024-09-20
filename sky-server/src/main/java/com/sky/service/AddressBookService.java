package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void addAddress(AddressBook addressBook);

    void setDefaultAddress(Long id);

    AddressBook getDefaultAddress();

    AddressBook queryAddressById(Long id);

    List<AddressBook> getAllAddressBook();

    void updateAddressById(AddressBook addressBook);

    void deleteById(Long id);
}
