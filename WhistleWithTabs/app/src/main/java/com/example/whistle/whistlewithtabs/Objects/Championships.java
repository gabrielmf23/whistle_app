package com.example.whistle.whistlewithtabs.Objects;

public class Championships extends BaseEntity{

    private int idCountry;

    @Override
    public String toString() {
        return getName();
    }

    public int getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(int idCountry) {
        this.idCountry = idCountry;
    }
}