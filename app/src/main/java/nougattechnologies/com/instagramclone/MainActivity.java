package nougattechnologies.com.instagramclone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nougattechnologies.com.instagramclone.Fragment.HomeFragment;
import nougattechnologies.com.instagramclone.Fragment.NotificationFragment;
import nougattechnologies.com.instagramclone.Fragment.ProfileFragment;
import nougattechnologies.com.instagramclone.Fragment.SearchFragment;
import nougattechnologies.com.instagramclone.Model.User;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

Bundle intent = getIntent().getExtras();
if (intent!=null){
    String publisher = intent.getString("publisherid");

    SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
    editor.putString("profileid",publisher);
    editor.apply();

    getSupportFragmentManager().beginTransaction().
            replace(R.id.fragment_container,new ProfileFragment()).commit();


}else {
    getSupportFragmentManager().beginTransaction().
            replace(R.id.fragment_container,new HomeFragment()).commit();

}

//        getSupportFragmentManager().beginTransaction().
//                replace(R.id.fragment_container,new HomeFragment()).commit();
//

        }
        private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.nav_home:
                                selectedFragment =new HomeFragment();
                                break;

                            case R.id.nav_search:
                                selectedFragment =new SearchFragment();
                                break;

                            case R.id.nav_add:
                                selectedFragment =null;
                                startActivity(new Intent(MainActivity.this,PostActivity.class));
                                break;

                            case R.id.nav_heart:
                                selectedFragment =new NotificationFragment();
                                break;

                            case R.id.nav_profie:
                                SharedPreferences.Editor editor =getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                                editor.putString("profileid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                editor.apply();


//                                SharedPreferences pref =getSharedPreferences("PICSPREFS",Context.MODE_PRIVATE);
//                                String id = pref.getString("facebook_id", "empty");
//
//                                Bitmap myBitmap = id;
//                                        menuItem.setIcon(new BitmapDrawable(getResources(), myBitmap));
//



                                selectedFragment =new ProfileFragment();

                                break;
                        }

                        if (selectedFragment!=null){
                            getSupportFragmentManager().beginTransaction().
                                    replace(R.id.fragment_container,selectedFragment).commit();
                        }


                        return true;
                    }
                };

}
