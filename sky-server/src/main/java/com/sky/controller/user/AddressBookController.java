package com.sky.controller.user;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName AddressBookController
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/19 11:46
 * @Version 1.0
 **/
@Api("地址相关")
@RestController
@Slf4j
@RequestMapping("/user/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    @ApiOperation("新增地址")
    public Result addAddressBook(@RequestBody AddressBook addressBook) {
        addressBookService.addAddress(addressBook);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询当前登录用户的所有地址信息")
    public Result<List<AddressBook>> getAllAddressBook() {
        List<AddressBook> addressBooks = addressBookService.getAllAddressBook();
        return Result.success(addressBooks);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> queryAddressById(@PathVariable Long id){
        AddressBook addressBook =  addressBookService.queryAddressById(id);
        return Result.success(addressBook);
    }

    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefaultAddress(@RequestBody AddressBook addressBook){
        addressBookService.setDefaultAddress(addressBook.getId());
        return Result.success();
    }

    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> getDefaultAddress(){
        AddressBook addressBook = addressBookService.getDefaultAddress();
        return Result.success(addressBook);
    }


    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result updateAddress(@RequestBody AddressBook addressBook){
        addressBookService.updateAddressById(addressBook);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("根据ip删除地址")
    public Result deleteAddress(Long id){
        addressBookService.deleteById(id);
        return Result.success();
    }
}
