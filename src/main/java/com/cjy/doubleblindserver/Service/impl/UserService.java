package com.cjy.doubleblindserver.Service.impl;

import com.cjy.doubleblindserver.Transfer.*;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/27 23:20
 */

public interface UserService {
    //获取系统参数
    BeginBtoU getSystemData();

    //注册电子钱包
    RegBtoU walletRegister (RegUtoB regUtoB);
    //注册匿名证书
    RegBtoM certificateRegister(RegMtoB regMtoB);

    //接收随机化处理后的匿名证书
    void getAnnoyCertificate (SpendMtoU spendMtoU);
    //发送随机化处理后的匿名证书
    SpendMtoU sendAnnoyCertificate();
    //接收电子货币
    void getElectronicCoin (SpendUtoM spendUtoM);
    //发送电子货币
    SpendUtoM sendElectronicCoin();
    //清算
    boolean Deposit (DepositMtoB depositMtoB);
    //转换
    TransferBtoM transfer(TransferMtoB transferMtoB);
}
