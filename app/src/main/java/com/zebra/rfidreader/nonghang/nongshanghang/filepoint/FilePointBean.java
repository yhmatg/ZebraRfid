package com.zebra.rfidreader.nonghang.nongshanghang.filepoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilePointBean {
    private String bagCode;;
    private List<String> epcs = new ArrayList<>();

    public String getBagCode() {
        return bagCode;
    }

    public void setBagCode(String bagCode) {
        this.bagCode = bagCode;
    }

    public List<String> getEpcs() {
        return epcs;
    }

    public void setEpcs(List<String> epcs) {
        this.epcs = epcs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilePointBean)) return false;
        FilePointBean that = (FilePointBean) o;
        return Objects.equals(getBagCode(), that.getBagCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBagCode());
    }
}
