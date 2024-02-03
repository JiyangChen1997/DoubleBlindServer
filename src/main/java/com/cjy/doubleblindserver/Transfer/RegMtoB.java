package com.cjy.doubleblindserver.Transfer;

import com.cjy.doubleblindserver.cipher.entity.Dto.POKDto;

//签名阶段商家向银行发送的数据
public class RegMtoB {
    public String name;
    public String password;
    public String card;
    public POKDto zkpok;
}
