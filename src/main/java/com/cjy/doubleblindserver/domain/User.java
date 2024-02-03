package com.cjy.doubleblindserver.domain;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/27 22:39
 */
public class User {

    private Long id;

    private String name;

    private String card;

    private String password;

    private Long rest;

    private String pubkey;

    @Override
    public String toString() {
        return "User{" +
                "id = " + id  +
                ", name = '" + name + '\'' +
                ", card = '" + card + '\'' +
                ", password = '" + password + '\'' +
                ", rest = '" + rest + '\'' +
                ", pubkey = '" + pubkey + '\'' + '}' + '\n';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRest() {
        return rest;
    }

    public void setRest(Long rest) {
        this.rest = rest;
    }

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }
}
