package com.zebra.rfidreader.demo.nongshanghang;

import java.util.Objects;

public class EpcBean {
    private String epc;
    private boolean isInved;

    public EpcBean(String epc) {
        this.epc = epc;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public boolean isInved() {
        return isInved;
    }

    public void setInved(boolean inved) {
        isInved = inved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EpcBean)) return false;
        EpcBean epcBean = (EpcBean) o;
        return getEpc().equals(epcBean.getEpc());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEpc());
    }
}
