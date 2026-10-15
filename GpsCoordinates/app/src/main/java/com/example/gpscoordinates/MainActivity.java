package com.example.gpscoordinates;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements LocationListener {
    private LocationManager manager;
    private TextView status, latitude, longitude, details;
    private Button action;
    private boolean listening;
    private Location latest;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable freshness = new Runnable() {
        @Override public void run() {
            if (latest != null && listening && SystemClock.elapsedRealtimeNanos() - latest.getElapsedRealtimeNanos() > 30_000_000_000L)
                status.setText("GPS signal stale · waiting for a fresh fix");
            handler.postDelayed(this, 5000);
        }
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.rgb(240,245,247));
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28,64,28,40);
        scroll.addView(root);
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(28, insets.getSystemWindowInsetTop()+40,28,insets.getSystemWindowInsetBottom()+32);
            return insets;
        });
        text(root,"SATELLITE LOCATION",13,Color.rgb(20,110,95));
        TextView title = text(root,"Your coordinates",32,Color.rgb(20,40,53));
        title.setTypeface(null,Typeface.BOLD);
        text(root,"Live position from your device’s GPS",16,Color.DKGRAY);
        status = text(root,"Ready to connect",16,Color.rgb(20,110,95));
        latitude = coordinate(root,"LATITUDE");
        longitude = coordinate(root,"LONGITUDE");
        details = text(root,"No GPS fix yet. Coordinates appear after a fresh satellite fix.",15,Color.DKGRAY);
        action = new Button(this);
        root.addView(action);
        action.setOnClickListener(v -> {
            if (!precise()) {
                if (getPreferences(MODE_PRIVATE).getBoolean("asked",false) && !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:"+getPackageName())));
                } else {
                    getPreferences(MODE_PRIVATE).edit().putBoolean("asked",true).apply();
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},7);
                }
            } else if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            } else startGps();
        });
        text(root,"GPS only • Updates while this screen is open\nYour location stays on this device.",13,Color.GRAY);
        setContentView(scroll);
    }

    private TextView text(LinearLayout parent,String value,int size,int color) {
        TextView view = new TextView(this);
        view.setText(value); view.setTextSize(size); view.setTextColor(color);
        view.setPadding(0,12,0,18); parent.addView(view); return view;
    }
    private TextView coordinate(LinearLayout root,String label) {
        LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(24,16,24,16);
        GradientDrawable bg = new GradientDrawable(); bg.setColor(Color.WHITE); bg.setCornerRadius(22); card.setBackground(bg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1,-2); params.setMargins(0,12,0,12); root.addView(card,params);
        text(card,label,12,Color.GRAY);
        TextView value = text(card,"—",32,Color.rgb(20,40,53)); value.setTypeface(Typeface.MONOSPACE); return value;
    }
    private boolean precise() { return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED; }
    @Override protected void onResume() { super.onResume(); startGps(); handler.post(freshness); }
    @Override protected void onPause() {
        super.onPause(); manager.removeUpdates(this); listening=false; handler.removeCallbacks(freshness);
    }
    private void startGps() {
        if (!precise()) {
            latitude.setText("—"); longitude.setText("—"); latest=null;
            status.setText("Precise location permission required");
            details.setText("Allow precise location to read GPS coordinates. Approximate access is insufficient for this app.");
            action.setText("Allow precise location / settings"); return;
        }
        if (!manager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            status.setText("GPS is unavailable on this device"); action.setEnabled(false); return;
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            status.setText("GPS is switched off"); action.setText("Open location settings"); return;
        }
        action.setEnabled(true); action.setText("Refresh GPS");
        status.setText("Searching for GPS satellites…");
        details.setText("For the best signal, go outdoors. The first fix can take a minute. Any displayed coordinates are the last received fix until updated.");
        try {
            manager.removeUpdates(this);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000L,0f,this,Looper.getMainLooper());
            listening=true;
        } catch (SecurityException error) { listening=false; status.setText("Location permission was revoked"); }
    }
    @Override public void onLocationChanged(Location location) {
        latest=location;
        latitude.setText(String.format(Locale.US,"%.6f°",location.getLatitude()));
        longitude.setText(String.format(Locale.US,"%.6f°",location.getLongitude()));
        status.setText("GPS fix received");
        details.setText("Provider: GPS\nAccuracy: " + (location.hasAccuracy()?String.format(Locale.US,"± %.1f m",location.getAccuracy()):"unavailable")
            + "\nUpdated: " + DateFormat.getTimeInstance().format(new Date(location.getTime()))
            + (location.isFromMockProvider()?"\nSimulated location (test provider)":""));
    }
    @Override public void onProviderDisabled(String provider) {
        if (LocationManager.GPS_PROVIDER.equals(provider)) { status.setText("GPS switched off · displayed fix may be old"); action.setText("Open location settings"); }
    }
    @Override public void onProviderEnabled(String provider) { if (LocationManager.GPS_PROVIDER.equals(provider)) startGps(); }
    @Override public void onStatusChanged(String provider,int status,Bundle extras) { }
    @Override public void onRequestPermissionsResult(int code,String[] permissions,int[] results) {
        super.onRequestPermissionsResult(code,permissions,results);
        if(code==7) startGps();
    }
}

