package com.leosanqing.controller;

import com.leosanqing.pojo.UserAddress;
import com.leosanqing.pojo.bo.AddressBO;
import com.leosanqing.service.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Author: leosanqing
 * @Date: 2019-12-12 07:59
 */
@RestController
@Api(value = "地址相关接口api", tags = {"查询地址相关"})
@Slf4j
@Validated
public class AddressController {
    @Autowired
    private AddressService addressService;

    @GetMapping("/api/v1/address/list")
    @ApiOperation(value = "查询所有收货地址", notes = "查询所有收货地址", httpMethod = "POST")
    public List<UserAddress> queryAll(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam @NotBlank String userId
    ) {
        return addressService.queryAll(userId);
    }

    @PostMapping("/api/v1/address/add")
    @ApiOperation(value = "添加收货地址", notes = "添加收货地址", httpMethod = "POST")
    public void add(
            @ApiParam(name = "addressBO", value = "收货地址BO")
            @RequestBody AddressBO addressBO
    ) {
        addressService.addNewUserAddress(addressBO);
    }


    @PostMapping("/api/v1/address/update")
    @ApiOperation(value = "添加收货地址", notes = "添加收货地址", httpMethod = "POST")
    public void update(
            @ApiParam(name = "addressBO", value = "收货地址BO")
            @RequestBody @Validated AddressBO addressBO
    ) {
        addressService.updateUserAddress(addressBO);
    }


    @PostMapping("/api/v1/address/delete")
    @ApiOperation(value = "删除收货地址", notes = "删除收货地址", httpMethod = "POST")
    public void del(
            @ApiParam(name = "userId", value = "用户Id")
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "addressId", value = "收货地址Id")
            @RequestParam @NotBlank String addressId
    ) {
        addressService.deleteUserAddress(userId, addressId);
    }


    @PostMapping("/api/v1/address/setDefault")
    @ApiOperation(value = "删除收货地址", notes = "删除收货地址", httpMethod = "POST")
    public void setDefault(
            @ApiParam(name = "userId", value = "用户Id")
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "addressId", value = "收货地址Id")
            @RequestParam @NotBlank String addressId
    ) {
        addressService.updateToBeDefault(userId, addressId);
    }
}
