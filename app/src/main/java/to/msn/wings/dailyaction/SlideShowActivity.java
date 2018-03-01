package to.msn.wings.dailyaction;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmResults;

public class SlideShowActivity extends AppCompatActivity {
    ImageSwitcher imageSwitcher;
    Realm mRealm;
    RealmResults<DailyAction> results;
    int position;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);

        mRealm = Realm.getDefaultInstance();
        results = mRealm.where(DailyAction.class).findAll();
        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory(){
            @Override
            public View makeView(){
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return  imageView;
            }
        });
        File file = new File(results.get(0).getPath());
        try{
            InputStream is = new FileInputStream(file);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            Drawable drawable = new BitmapDrawable(bmp);
            imageSwitcher.setImageDrawable(drawable);
        } catch (IOException e) {
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
        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
               finish();
        }
        return true;
    }

   public void movePosition(int i) {
        position = position + i;
        if (position >= results.size()) {
            position = 0;
        } else if (position < 0) {
            position = results.size()-1;
        }
       File file = new File(results.get(position).getPath());
       try {
           InputStream is = new FileInputStream(file);
           Bitmap bmp = BitmapFactory.decodeStream(is);
           Drawable drawable = new BitmapDrawable(bmp);
           imageSwitcher.setImageDrawable(drawable);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   public void preview(View view) {
        movePosition(-1);
   }

   public void next(View view) {
        movePosition(1);
   }
}
