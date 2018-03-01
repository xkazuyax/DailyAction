package to.msn.wings.dailyaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;

import io.realm.Realm;

public class NewUserRegisterActivity extends AppCompatActivity {
    Realm mRealm;
    EditText newId;
    EditText newPass;
    EditText newName;
    ImageView imageView;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_register);

        newId = (EditText)findViewById(R.id.new_id);
        newPass = (EditText)findViewById(R.id.new_pass);
        newName = (EditText)findViewById(R.id.new_name);
        imageView = (ImageView)findViewById(R.id.new_image);
        mRealm = Realm.getDefaultInstance();
    }

    public void upload(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent resultData) {
        if (requestCode == 1 && resultCode == RESULT_OK ) {
            if (resultData.getData() != null) {
                ParcelFileDescriptor pfDescriptor = null;
                try{
                    uri = resultData.getData();
                    pfDescriptor = getContentResolver().openFileDescriptor(uri,"r");
                    if (pfDescriptor != null) {
                        FileDescriptor fileDescriptor = pfDescriptor.getFileDescriptor();
                        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        pfDescriptor.close();
                        imageView.setImageBitmap(bitmap);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if (pfDescriptor != null) {
                            pfDescriptor.close();
                        }
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void newUserSave(View view) {
        mRealm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                Number maxId = realm.where(UserInfo.class).max("id");
                long nextId =0;
                if (maxId != null) {
                    nextId = maxId.longValue() + 1;
                }
                UserInfo userInfo = realm.createObject(UserInfo.class,new Long(nextId));
                userInfo.setLoginId(newId.getText().toString());
                userInfo.setPassword(newPass.getText().toString());
                userInfo.setName(newName.getText().toString());
                userInfo.setPicturePass(uri.toString());
                userInfo.setLatitude(35.6700);
                userInfo.setLogitude(139.7730);
                Toast.makeText(NewUserRegisterActivity.this, "登録完了しました", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
