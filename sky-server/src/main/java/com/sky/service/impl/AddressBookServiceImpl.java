package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName AdressBookServiceImpl
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/19 12:26
 * @Version 1.0
 **/
@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public void addAddress(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        if (addressBook.getIsDefault() == null || addressBook.getIsDefault() == 0){
            addressBook.setIsDefault(0);
            addressBookMapper.addAddress(addressBook);
        } else {
            addressBookMapper.clearDefaultAddress();
            addressBookMapper.addAddress(addressBook);
        }

    }

    @Override
    public void setDefaultAddress(Long id) {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getDefaultAddress(userId);
        if (addressBook == null || addressBook.getId() != id){
            addressBookMapper.clearDefaultAddress();
            addressBookMapper.setDefaultAddress(id, userId);
        }
    }

    @Override
    public AddressBook getDefaultAddress() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getDefaultAddress(userId);
        return addressBook;
    }

    @Override
    public AddressBook queryAddressById(Long id) {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.queryAddressById(id, userId);
        return addressBook;
    }

    @Override
    public List<AddressBook> getAllAddressBook() {
        Long userId = BaseContext.getCurrentId();
        List<AddressBook> addressBooks = addressBookMapper.getAllAddressBook(userId);
        return addressBooks;
    }

    @Override
    public void updateAddressById(AddressBook addressBook) {
        addressBookMapper.updateAddressById(addressBook);
    }

    @Override
    public void deleteById(Long id) {
        Long userId = BaseContext.getCurrentId();
        addressBookMapper.deleteById(id, userId);
    }
}
