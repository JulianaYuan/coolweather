package com.juliana.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.juliana.coolweather.R;
import com.juliana.coolweather.db.CoolWeatherDB;
import com.juliana.coolweather.model.City;
import com.juliana.coolweather.model.County;
import com.juliana.coolweather.model.Province;
import com.juliana.coolweather.util.HttpCallbackListener;
import com.juliana.coolweather.util.HttpUtil;
import com.juliana.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JYUAN7 on 1/10/2019.
 */

public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    /**
     *province list
     */
    private List<Province> provinceList;
    /**
     * city list
     */
    private List<City> cityList;
    /**
     * county list
     */
    private List<County> countyList;
    /**
     * choose province
     */
    private Province selectedProvince;
    /**
     * choose city
     */
    private City selectedCity;
    /**
     * choose level
     */
    private int currentLevel;
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity",false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected",false)&& !isFromWeatherActivity){
            Intent intent = new Intent(this,WheatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(index);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(index);
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    String countyCode = countyList.get(index).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this,WheatherActivity.class);
                    Log.i("mainActivity","ChooseAreaActivity onCreate countyCode "+countyCode);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvince();
    }

    /**
     * query all province, first query from database,then from server
     */
    private void queryProvince(){
        provinceList = coolWeatherDB.loadProvinces();
        Log.i("mainActivity","queryProvince ");
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("China");
            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer(null,"province");
        }
    }

    /**
     * query all cities, first query from database,then from server
     */
    private void queryCities(){
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        Log.i("mainActivity","queryCities ");
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }
    /**
     * query all counties, first query from database,then from server
     */
    private void queryCounties(){
        countyList = coolWeatherDB.loadCounties(selectedCity.getId());
        Log.i("mainActivity","queryCounties "+countyList.size());
        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    /**
     * query from server
     */
    private void queryFromServer(final String code,final String type){
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
            //address = "http://m.weather.com.cn/data/"+code+".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
            //address = "http://m.weather.com.cn/data.xml";
        }
        showProgressDialog();
        Log.i("mainActivity","queryFromServer address "+address+"type "+type);
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(coolWeatherDB,response);
                }else if ("city".equals(type)){
                    result = Utility.handleCitiesResponse(coolWeatherDB,response,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountiesResponse(coolWeatherDB,response,selectedCity.getId());
                }
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                Log.i("mainActivity","queryFromServer result province");
                                queryProvince();
                            }else if ("city".equals(type)){
                                Log.i("mainActivity","queryFromServer result city");
                                queryCities();
                            }else if ("county".equals(type)){
                                Log.i("mainActivity","queryFromServer result county");
                                queryCounties();
                            }
                        }
                    });
                }
                else{
                    Log.i("mainActivity","queryFromServer result "+result);
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog(){
        if (progressDialog==null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载......");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed(){
        if (currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvince();
        }else {
            if (isFromWeatherActivity){
                Intent intent = new Intent(this,WheatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
