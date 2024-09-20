package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    void addAddress(AddressBook addressBook);


    @Select("SELECT * FROM address_book WHERE is_default = 1 AND user_id = #{userId}")
    AddressBook getDefaultAddress(Long userId);

    
    @Update("UPDATE address_book SET is_default = 0 WHERE is_default = 1")
    void clearDefaultAddress();

    @Update("UPDATE address_book SET is_default = 1 WHERE id = #{id} AND user_id = #{userId}")
    void setDefaultAddress(Long id, Long userId);

    @Select("SELECT * FROM address_book Where id = #{id} AND user_id = #{userId}")
    AddressBook queryAddressById(Long id, Long userId);

    @Select("SELECT * FROM address_book WHERE user_id = #{userId}")
    List<AddressBook> getAllAddressBook(Long userId);

    void updateAddressById(AddressBook addressBook);

    @Delete("DELETE FROM address_book WHERE id = #{id} AND user_id = #{userId}")
    void deleteById(Long id, Long userId);
}
