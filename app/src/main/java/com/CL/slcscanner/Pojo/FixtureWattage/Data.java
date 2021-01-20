
package com.CL.slcscanner.Pojo.FixtureWattage;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("fixture_wattage")
    @Expose
    private List<String> fixtureWattage = null;

    public List<String> getFixtureWattage() {
        return fixtureWattage;
    }

    public void setFixtureWattage(List<String> fixtureWattage) {
        this.fixtureWattage = fixtureWattage;
    }
}
