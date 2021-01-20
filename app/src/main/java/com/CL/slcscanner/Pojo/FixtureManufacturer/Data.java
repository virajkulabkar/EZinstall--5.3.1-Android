
package com.CL.slcscanner.Pojo.FixtureManufacturer;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("fixture_manufacturer")
    @Expose
    private List<String> fixtureManufacturer = null;

    public List<String> getFixtureManufacturer() {
        return fixtureManufacturer;
    }

    public void setFixtureManufacturer(List<String> fixtureManufacturer) {
        this.fixtureManufacturer = fixtureManufacturer;
    }

}
