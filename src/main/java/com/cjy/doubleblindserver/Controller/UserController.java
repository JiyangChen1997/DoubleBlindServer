package com.cjy.doubleblindserver.Controller;

import com.cjy.doubleblindserver.Service.impl.UserService;
import com.cjy.doubleblindserver.Transfer.*;
import com.cjy.doubleblindserver.common.baseclass.customresponse.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/27 23:17
 */
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService service;
    @GetMapping("/getSystemData")
    public ServerResponse getSystemData(HttpServletRequest request) {
        return ServerResponse.success(service.getSystemData());
    }

    @PostMapping("/walletRegister")
    public ServerResponse walletRegister(@RequestBody RegUtoB regUtoB) {
        return ServerResponse.success(service.walletRegister(regUtoB));
    }

    @PostMapping("/certifateRegister")
    public ServerResponse certificateRegister(@RequestBody RegMtoB regMtoB) {
        return ServerResponse.success(service.certificateRegister(regMtoB));
    }

    @PostMapping("/getCer")
    public ServerResponse getAnnoyCertificate(@RequestBody SpendMtoU spendMtoU) {
        service.getAnnoyCertificate(spendMtoU);
        return ServerResponse.success();
    }

    @GetMapping("/sendCer")
    public ServerResponse sendAnnoyCertificate(HttpServletRequest request) {
        return ServerResponse.success(service.sendAnnoyCertificate());
    }

    @PostMapping("/getCoin")
    public ServerResponse getElectronicCoin(@RequestBody SpendUtoM spendUtoM) {
        service.getElectronicCoin(spendUtoM);
        return ServerResponse.success();
    }

    @GetMapping("/sendCoin")
    public ServerResponse sendElectronicCoin(HttpServletRequest request) {
        return ServerResponse.success(service.sendElectronicCoin());
    }

    @PostMapping("/deposit")
    public ServerResponse deposit(@RequestBody DepositMtoB depositMtoB) {
        return ServerResponse.success(service.Deposit(depositMtoB));
    }

    @PostMapping("/transfer")
    public ServerResponse transfer(@RequestBody TransferMtoB transferMtoB) {
        return ServerResponse.success(service.transfer(transferMtoB));
    }

}
