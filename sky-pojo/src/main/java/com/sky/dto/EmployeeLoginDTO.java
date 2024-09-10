package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "员工登录时传递的数据模型") //接口测试的注解
public class EmployeeLoginDTO implements Serializable {

    @ApiModelProperty("用户名") //接口测试
    private String username;

    @ApiModelProperty("密码")
    private String password;

}
