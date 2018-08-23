package com.example.tangjie.news;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class LocationActivity extends AppCompatActivity {
    private TextView locationTextView;
    private LocationManager locationManager;
    private String provider;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        locationTextView = (TextView) findViewById(R.id.location);

        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //获取所有可用的位置提供器
            List<String> providerList = locationManager.getProviders(true);
            if (providerList.contains(LocationManager.GPS_PROVIDER)){
                provider = LocationManager.GPS_PROVIDER;
            }else if (providerList.contains(LocationManager.NETWORK_PROVIDER)){
                provider = LocationManager.GPS_PROVIDER;
            }else {
                //当前没有可用的位置提供器
                Toast.makeText(LocationActivity.this,"未找到位置提供器",Toast.LENGTH_SHORT).show();
                return;
            }
            locationManager.requestLocationUpdates(provider,5000,1,locationListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            if (locationManager != null) {
                locationManager.removeUpdates(locationListener);
            }
        }
    }

    private void showLocation(final Location location){
        String currentPosition = "纬度：" + location.getLatitude() + "\n" + "经度：" + location.getLongitude();
        locationTextView.setText(currentPosition);
    }

}
