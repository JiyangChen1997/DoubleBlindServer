package com.cjy.doubleblindserver.domain;

import com.cjy.doubleblindserver.cipher.entity.Dto.POKDto;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/12/11 23:49
 */
public class Knowledge {

    private long id;

    private long coinid;

    private String random1;

    private String random2;

    private String random3;

    private String random4;

    private String random5;

    private String value1;

    private String value2;

    private String value3;

    private String value4;

    private String value5;

    private String s1;

    private String s2;

    private String s3;

    private String s4;

    private String s5;

    private String s6;

    private String s7;

    private String s8;

    private String c;

    private String h;

    public Knowledge() {

    }
    public Knowledge(POKDto pokDto, int coinid) {
        this.setCoinid(coinid);
        this.setC(pokDto.c);
        this.setH(pokDto.h);
        this.setRandom1(pokDto.randomValue.get(0));
        this.setRandom2(pokDto.randomValue.get(1));
        this.setRandom3(pokDto.randomValue.get(2));
        this.setRandom4(pokDto.randomValue.get(3));
        this.setRandom5(pokDto.randomValue.get(4));
        this.setValue1(pokDto.value.get(0));
        this.setValue2(pokDto.value.get(1));
        this.setValue3(pokDto.value.get(2));
        this.setValue4(pokDto.value.get(3));
        this.setValue5(pokDto.value.get(4));
        this.setS1(pokDto.s.get(0));
        this.setS2(pokDto.s.get(1));
        this.setS3(pokDto.s.get(2));
        this.setS4(pokDto.s.get(3));
        this.setS5(pokDto.s.get(4));
        this.setS6(pokDto.s.get(5));
        this.setS7(pokDto.s.get(6));
        this.setS8(pokDto.s.get(7));

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCoinid() {
        return coinid;
    }

    public void setCoinid(long coinid) {
        this.coinid = coinid;
    }

    public String getRandom1() {
        return random1;
    }

    public void setRandom1(String random1) {
        this.random1 = random1;
    }

    public String getRandom2() {
        return random2;
    }

    public void setRandom2(String random2) {
        this.random2 = random2;
    }

    public String getRandom3() {
        return random3;
    }

    public void setRandom3(String random3) {
        this.random3 = random3;
    }

    public String getRandom4() {
        return random4;
    }

    public void setRandom4(String random4) {
        this.random4 = random4;
    }

    public String getRandom5() {
        return random5;
    }

    public void setRandom5(String random5) {
        this.random5 = random5;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        this.value4 = value4;
    }

    public String getValue5() {
        return value5;
    }

    public void setValue5(String value5) {
        this.value5 = value5;
    }

    public String getS1() {
        return s1;
    }

    public void setS1(String s1) {
        this.s1 = s1;
    }

    public String getS2() {
        return s2;
    }

    public void setS2(String s2) {
        this.s2 = s2;
    }

    public String getS3() {
        return s3;
    }

    public void setS3(String s3) {
        this.s3 = s3;
    }

    public String getS4() {
        return s4;
    }

    public void setS4(String s4) {
        this.s4 = s4;
    }

    public String getS5() {
        return s5;
    }

    public void setS5(String s5) {
        this.s5 = s5;
    }

    public String getS6() {
        return s6;
    }

    public void setS6(String s6) {
        this.s6 = s6;
    }

    public String getS7() {
        return s7;
    }

    public void setS7(String s7) {
        this.s7 = s7;
    }

    public String getS8() {
        return s8;
    }

    public void setS8(String s8) {
        this.s8 = s8;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }
}
