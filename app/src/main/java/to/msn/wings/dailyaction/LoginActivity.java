package to.msn.wings.dailyaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

public class LoginActivity extends AppCompatActivity {
    Realm realm;
    EditText id;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String id = pref.getString("id","-1");
        if (Integer.parseInt(id) != -1) {
            Intent intent = new Intent(this,TopActivity.class);
            startActivity(intent);
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    public void new_add(View view) {
        Intent intent = new Intent(this,NewUserRegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        realm = Realm.getDefaultInstance();
        id = (EditText)findViewById(R.id.id);
        password = (EditText)findViewById(R.id.password);
        RealmResults<UserInfo>  results = realm.where(UserInfo.class).equalTo("login_ID",id.getText().
                toString()).equalTo("password",password.getText().toString()).findAll();
        if (results.size() > 0) {
            UserInfo userInfo = results.first();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("id",userInfo.getId().toString());
            editor.putString("name",userInfo.getName());
            editor.commit();
            Intent intent = new Intent(this,TopActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this,"IDまたはPasswordが異なります",Toast.LENGTH_LONG).show();
        }
    }
}
