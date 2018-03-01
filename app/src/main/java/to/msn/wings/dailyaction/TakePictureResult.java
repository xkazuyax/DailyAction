package to.msn.wings.dailyaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import io.realm.Realm;

public class TakePictureResult extends AppCompatActivity {
    private ImageView imageView;
    private String path;
    private EditText subject;
    private EditText comment;
    private Realm mRealm;
    private Double longitude;
    private Double latitude;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture_result);

        subject = (EditText)findViewById(R.id.subject);
        comment = (EditText)findViewById(R.id.comment);
        Intent intent = this.getIntent();
        path = intent.getStringExtra("imgPath");
        longitude = intent.getDoubleExtra("longitude",0);
        latitude = intent.getDoubleExtra("latitude",0);
        if (longitude ==0 && latitude == 0) {
            finish();
        }
        File picture = new File(path);
        try {
            InputStream is = new FileInputStream(picture);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            imageView = (ImageView)findViewById(R.id.takenPicture);
            imageView.setImageBitmap(bmp);
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.logout:
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
               finish();
        }
        return true;
    }

    public void save(View view) {
        mRealm = Realm.getDefaultInstance();
        final Date date = new Date();
       mRealm.executeTransaction(new Realm.Transaction(){
           @Override
           public void execute(Realm realm) {
               Number maxId = realm.where(DailyAction.class).max("id");
               long nextId=0;
               if (maxId != null) {
                   nextId = maxId.longValue() + 1;
               }
                   DailyAction dailyAction = realm.createObject(DailyAction.class,new Long(nextId));
                   dailyAction.setDate(date);
                   dailyAction.setPath(path);
                   dailyAction.setLatitude(latitude);
                   dailyAction.setLongitude(longitude);
                   dailyAction.setSubject(subject.getText().toString());
                   dailyAction.setComment(comment.getText().toString());
           }
       });
        Toast.makeText(this, "保存しました", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(TakePictureResult.this,MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
