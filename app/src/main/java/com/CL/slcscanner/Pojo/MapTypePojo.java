package com.CL.slcscanner.Pojo;

/**
 * Created by vrajesh on 10/9/2018.
 */

public class MapTypePojo {

    String keyLable;
    String value;
    boolean isChecked;
    int position;
    String mapLink;
    boolean isStyle;

    public String getKeyLable() {
        return keyLable;
    }

    public void setKeyLable(String keyLable) {
        this.keyLable = keyLable;
    }

    public boolean isStyle() {
        return isStyle;
    }

    public void setStyle(boolean style) {
        isStyle = style;
    }

    public String getMapLink() {
        return mapLink;
    }

    public void setMapLink(String mapLink) {
        this.mapLink = mapLink;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String maptype) {
        this.value = maptype;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
