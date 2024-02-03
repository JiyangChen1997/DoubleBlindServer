package com.cjy.doubleblindserver.Transfer;


import com.cjy.doubleblindserver.cipher.entity.Dto.POKDto;

//注册阶段用户向银行发送的数据
public class RegUtoB {
    public POKDto zkpok;
    public String name;
    public String password;
    public String card;
    public String pubkey;
}
