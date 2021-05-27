package com.zebra.rfidreader.nonghang.zhongyou.model;

public class Post {
    private String epcCipher;
    private String epcPlant;
    private String postCode;
    private String postalCode;

    public String getEpcCipher() {
        return epcCipher;
    }

    public void setEpcCipher(String epcCipher) {
        this.epcCipher = epcCipher;
    }

    public String getEpcPlant() {
        return epcPlant;
    }

    public void setEpcPlant(String epcPlant) {
        this.epcPlant = epcPlant;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getPostalcode() {
        return postalCode;
    }

    public void setPostalcode(String postalcode) {
        this.postalCode = postalcode;
    }

    @Override
    public String toString() {
        return "Post{" +
                "epcCipher='" + epcCipher + '\'' +
                ", epcPlant='" + epcPlant + '\'' +
                ", postCode='" + postCode + '\'' +
                ", postalcode='" + postalCode + '\'' +
                '}';
    }
}
