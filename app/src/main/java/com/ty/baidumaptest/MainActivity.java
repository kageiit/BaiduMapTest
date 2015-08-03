package com.ty.baidumaptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private BMapManager bMapManager;

    private MapView mapView;

    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bMapManager = new BMapManager(this);
        bMapManager.init("4LNngxkGV7Knrn1QdYBXXAr0", null);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.bmapView);
        mapView.setSatellite(false);
        mapView.setBuiltInZoomControls(true);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        Toast.makeText(MainActivity.this, providerList.toString(), Toast.LENGTH_SHORT).show();
        for(String provider:providerList)
        {
            if (provider.equals(LocationManager.PASSIVE_PROVIDER)){
                continue;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                navigateTo(location);
                break;
            }

        }

    }

    private void navigateTo(Location location) {
        MapController mapController = mapView.getController();
        mapController.setZoom(16);
        GeoPoint point = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
        mapController.setCenter(point);

        PopupOverlay popupOverlay = new PopupOverlay(mapView, new PopupClickListener() {
            @Override
            public void onClickedPopup(int i) {
                Toast.makeText(MainActivity.this, "You clicked button" + i, Toast.LENGTH_SHORT).show();
            }
        });

        Bitmap[] bitmaps = new Bitmap[3];

        try {
            bitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.crop_handle_x);
            bitmaps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.ic_btn_selection_checked_lrg);
            bitmaps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.crop_handle_y);
        } catch (Exception e) {
            e.printStackTrace();
        }
        popupOverlay.showPopup(bitmaps, point, 18);


        MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mapView);
        LocationData locationData = new LocationData();
        locationData.latitude = location.getLatitude();
        locationData.longitude = location.getLongitude();
        myLocationOverlay.setData(locationData);
        mapView.getOverlays().add(myLocationOverlay);
        mapView.refresh();

    }

    @Override
    protected void onResume() {
        mapView.onResume();
        if (bMapManager != null) {
            bMapManager.start();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        if (bMapManager!=null) {
            bMapManager.stop();
        }
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        mapView.destroy();
        if (bMapManager!=null){
            bMapManager.stop();
            bMapManager.destroy();

        }
        super.onDestroy();
    }
}