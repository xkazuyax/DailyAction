package to.msn.wings.dailyaction;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import io.realm.RealmResults;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleApiClient client;
    private LocationRequest request;
    private FusedLocationProviderApi api;
    private GoogleMap mMap;
    public double longitude;
    public double latitude;
    private Realm mRealm;
    public SharedPreferences pref;
    public OnMarkerDialogFragment dialog;
    public MyInfoFragment myInfo;
    public Marker user_marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mRealm = Realm.getDefaultInstance();

        //Permissionの確認と要求
        if( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //位置情報のリクエスト情報を取得
        request=LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(1000).setFastestInterval(15);
        api= LocationServices.FusedLocationApi;
        //GooglePlayへの接続クライアント
        client=new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
               finish();
        }
        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String id = pref.getString("id","-1");
        RealmResults<UserInfo> results = mRealm.where(UserInfo.class).equalTo("id",Integer.parseInt(id)).findAll();
        if (results.size() == 1) {
            UserInfo userInfo = results.first();
            //デフォルトの位置セット
            LatLng first_place = new LatLng(userInfo.getLatitude(), userInfo.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first_place, 16f));
        } else {
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this, "エラーが発生したためログイン画面に戻ります", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String id = pref.getString("id","-1");
        if (Integer.parseInt(id) == -1) {
           finish();
        }
        //Google Play への接続
        if(client != null){
            client.connect();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        //位置情報リクエストの解除 and Google Playへの接続
        if (client != null && client.isConnected()) {
            api.removeLocationUpdates(client,this);
        }
        client.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //ACCESS_FINE_LOCATIONへのPermissionの確認
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        RealmResults<DailyAction> results = mRealm.where(DailyAction.class).findAll();
        if (results.size() != 0 ) {
            for (int i = 0; i<results.size();i++) {
                LatLng takenPicturePlace = new LatLng(results.get(i).getLatitude(), results.get(i).getLongitude());
                //青色のアイコン(写真を撮った場所)
                BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);

                mMap.addMarker(new MarkerOptions().position(takenPicturePlace).title(results.get(i).getSubject()).icon(icon)).setTag(results.get(i).getId());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(takenPicturePlace, 16f));
            }
        }

        //位置情報の監視
        api.requestLocationUpdates(client,request,this);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag() != "user_marker") {
                    dialog = new OnMarkerDialogFragment();
                    Bundle args = new Bundle();
                    args.putString("id", marker.getTag().toString());
                    dialog.setArguments(args);
                    dialog.show(getFragmentManager(), "dialog_marker");
                } else {
                    pref = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
                    String id = pref.getString("id", "-1");
                    if (Integer.parseInt(id) != -1) {
                        myInfo = new MyInfoFragment();
                        Bundle args = new Bundle();
                        args.putLong("id", Long.parseLong(id));
                        myInfo.setArguments(args);
                        myInfo.show(getFragmentManager(), "dialog_myInfo");
                    }
                }
                return false;
            }
        });
    }

    //GooglePlayへの接続クライアントが切れたときの処理
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //位置情報が更新されたとき、カメラ位置を移動
    @Override
    public void onLocationChanged(Location location) {
        if (mMap == null) {
            return;
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        final LatLng latLng = new LatLng(latitude,longitude);
        String id = pref.getString("id","-1");
        RealmResults<UserInfo> results = mRealm.where(UserInfo.class).equalTo("id",Integer.valueOf(id)).findAll();
        if (results.size() == 1) {
            final UserInfo userInfo = results.first();
            mRealm.executeTransaction(new Realm.Transaction(){
                @Override
                public void execute(Realm realm) {
                    userInfo.setLatitude(latitude);
                    userInfo.setLogitude(longitude);
                    if (user_marker != null) {
                        user_marker.remove();
                    }
                    user_marker = mMap.addMarker(new MarkerOptions().position(latLng));
                    user_marker.setTag("user_marker");
                }
            });
        } else {
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this, "エラーが発生したためログイン画面に戻ります", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i){

    }

    public void btnSatellite(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void btnNormal(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void btn_takePicture(View view) {
        Intent intent = new Intent(MapsActivity.this,TakePictureActivity.class);
        intent.putExtra("latitude",latitude);
        intent.putExtra("longitude",longitude);
        startActivity(intent);
    }
}
