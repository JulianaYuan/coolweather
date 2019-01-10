package com.juliana.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.juliana.coolweather.db.CoolWeatherDB;
import com.juliana.coolweather.model.City;
import com.juliana.coolweather.model.County;
import com.juliana.coolweather.model.Province;

/**
 * Created by JYUAN7 on 1/9/2019.
 */

public class Utility {
    /**
     *analysis and handle the provinces data return by server
     */
    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length>0){
                for (String p:allProvinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    Log.i("mainActivity","handleProvinceResponse "+province.getProvinceName());
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * analysis and handle the city data return by server
     */
    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities !=null && allCities.length>0){
                for (String c:allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    Log.i("mainActivity","handleCitiesResponse "+city.getCityName());
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * analysis and handle the county data return by server
     */
    public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length>0){
                for (String c:allCounties){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    Log.i("mainActivity","handleCountiesResponse "+county.getCountyName());
                    coolWeatherDB.saveCounty(county);
                }
                Log.i("mainActivity","handleCountiesResponse return true");
                return true;
            }
        }
        Log.i("mainActivity","handleCountiesResponse return false");
        return false;
    }
}
