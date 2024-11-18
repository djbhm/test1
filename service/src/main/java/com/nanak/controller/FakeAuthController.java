package com.nanak.controller;

import com.nanak.pojo.dto.IsLoginDTO;
import com.nanak.result.Result;
import com.nanak.utils.JwtTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
*@Author: nanak
*@CreateTime: 2024-11-18
*@Description: 
*@Version: 1.0
*/
@RestController
@RequestMapping("/auth")
public class FakeAuthController {
    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @PostMapping("login")
    public Result login() {
        String token = jwtTokenHelper.generateAccessToken("nanak");
        return Result.ok(token);
    }

    @PostMapping("isLogin")
    public Result isLogin(@RequestBody IsLoginDTO isLoginDTO) {
        Boolean verified = jwtTokenHelper.verifyToken(isLoginDTO.getToken());
        return Result.ok(verified);
    }
}
