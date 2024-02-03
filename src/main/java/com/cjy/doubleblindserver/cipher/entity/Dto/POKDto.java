package com.cjy.doubleblindserver.cipher.entity.Dto;

import java.util.List;
//零知识证明的银行传输形式
public class POKDto {

    public List<String> randomValue;
    public List<String> value;
    public List<String> s;
    public String c;
    public String h;
}
