package com.juliana.coolweather.model;

/**
 * Created by JYUAN7 on 1/9/2019.
 */

public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getProvinceName(){
        return provinceName;
    }
    public void setProvinceName(String name){
        this.provinceName = name;
    }
    public String getProvinceCode(){
        return provinceCode;
    }
    public void setProvinceCode(String code){
        this.provinceCode = code;
    }
}