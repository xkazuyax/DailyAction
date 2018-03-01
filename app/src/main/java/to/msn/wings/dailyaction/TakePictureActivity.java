package to.msn.wings.dailyaction;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by kazuya on 2018/02/24.
 */

public class TakePictureActivity extends AppCompatActivity {
    private Camera mCam = null;
    private CameraPreview mCamPreview = null;
    //写真を撮るボタンの2度押し禁止フラグ
    private boolean mIsTake = false;
    String imgPath;
    public double longitude;
    public double latitude;
    SharedPreferences pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.takepicture);

        Intent intent = this.getIntent();
        longitude = intent.getDoubleExtra("longitude",0);
        latitude = intent.getDoubleExtra("latitude",0);
        if (longitude == 0 && latitude == 0) {
            finish();
            Toast.makeText(this,"位置情報が取得できていません",Toast.LENGTH_LONG).show();
        }
        //カメラインスタンスの取得
        try {
            mCam = Camera.open();
            setDisplayOrientation();
        } catch (Exception e) {
            this.finish();
        }

        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        mCamPreview = new CameraPreview(this, mCam);
        Button btn = new Button(this);
        btn.setText("撮影");
        preview.addView(mCamPreview);
        preview.addView(btn);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (!mIsTake) {
                    //撮影2度禁止フラグオン
                    mIsTake = true;
                    mCam.takePicture(null, null, mPicJpgListener);

                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String id = pref.getString("id","-1");
        if (Integer.parseInt(id) == -1) {
           finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.logout:
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
               finish();
        }
        return true;
    }

    public void setDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = (info.orientation - degrees + 360) % 360;
        mCam.setDisplayOrientation(result);
    }

    //JPEGデータ生成完了時のコールバック
    private Camera.PictureCallback mPicJpgListener = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data == null) {
                return;
            }

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;

                case Surface.ROTATION_90:
                    degrees = 90;
                    break;

                case Surface.ROTATION_180:
                    degrees = 180;
                    break;

                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            Matrix m = new Matrix();
            m.setRotate(90 - degrees);
            Bitmap original = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap rotated = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), m, true);

            String saveDir = Environment.getExternalStorageDirectory().getPath() + "/test";
            //SDカードフォルダを取得
            File file = new File(saveDir);

            //フォルダ作成
            if (!file.exists()) {
                if (!file.mkdir()) {
                    Log.e("Debug", "Make Dir Error");
                }
            }

            //画像保存パス
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            imgPath = saveDir + "/" + sf.format(cal.getTime()) + "jpg";

            //ファイル保存
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(imgPath, true);
                rotated.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                //Androidのデータベースへの登録
                registAndroidDB(imgPath);
            } catch (Exception e) {
                Log.e("Debug", e.getMessage());
            }
            //takePictureすると、Previewが停止するので、再度プレビューをスタート
            mCam.startPreview();
            mIsTake = false;
            Intent intent = new Intent(TakePictureActivity.this,TakePictureResult.class);
            intent.putExtra("imgPath",imgPath);
            intent.putExtra("longitude",longitude);
            intent.putExtra("latitude",latitude);
            startActivity(intent);
        }
    };

    //AndroidDBへの画像のパスを登録
    private void registAndroidDB(String path) {
        //AndroidDbへ登録
        //登録しないとすぐにギャラリーに登録されない
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = TakePictureActivity.this.getContentResolver();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put("_data", path);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCam != null) {
            mCam.release();
            mCam = null;
        }

    }
}
