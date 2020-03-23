package com.example.whistle.whistlewithtabs.Objects;

public class Countries extends BaseEntity{

    private int idConfederation;

    public int getIdConfederation() {
        return idConfederation;
    }

    public void setIdConfederation(int idConfederation) {
        this.idConfederation = idConfederation;
    }

    @Override
    public String toString() {
        return getName();
    }
}
