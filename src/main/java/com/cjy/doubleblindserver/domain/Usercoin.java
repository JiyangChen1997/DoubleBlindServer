package com.cjy.doubleblindserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.cjy.doubleblindserver.cipher.entity.Dto.CoinDto;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/12/11 23:42
 */
public class Usercoin {
    @TableId(type = IdType.AUTO)
    private long id;

    private String R;

    private String S;

    private String T;

    private String J;

    public Usercoin () {

    }
    public Usercoin(CoinDto coinDto) {

        this.R = coinDto.R;

        this.S = coinDto.S;

        this.T = coinDto.T;

        this.J = coinDto.J;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getR() {
        return R;
    }

    public void setR(String r) {
        R = r;
    }

    public String getS() {
        return S;
    }

    public void setS(String s) {
        S = s;
    }

    public String getT() {
        return T;
    }

    public void setT(String t) {
        T = t;
    }

    public String getJ() {
        return J;
    }

    public void setJ(String j) {
        J = j;
    }
}
