package com.zebra.rfidreader.nonghang.nongshanghang.shverity;

import java.util.Objects;

public class BoxBean {
    private String boxName;
    private boolean checked;

    public BoxBean(String boxName, boolean checked) {
        this.boxName = boxName;
        this.checked = checked;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoxBean)) return false;
        BoxBean boxBean = (BoxBean) o;
        return getBoxName().equals(boxBean.getBoxName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoxName());
    }
}
