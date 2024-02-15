package com.qooke.levelrunproject;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.qooke.levelrunproject.config.Config;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottom_menu;
    Fragment missionFragment;
    Fragment rankingFragment;
    Fragment mainFragment;
    Fragment socialFragment;
    Fragment profileFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 토큰 확인하고 없으면 로그인 액티비티 띄우기
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        Log.i("getKeyHash", ""+getKeyHash(MainActivity.this));
        Log.i("MainActivity_tag", "token : " + token);
        if (token.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        bottom_menu = findViewById(R.id.bottom_menu);
        missionFragment = new RecordFragment();
        rankingFragment = new RankingFragment();
        mainFragment = new MainFragment();
        socialFragment = new SocialFragment();
        profileFragment = new ProfileFragment();
        loadFragment(mainFragment);

        // 바텀 네비게이션뷰 눌렀을때
        bottom_menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Fragment fragment = null;
                if (itemId == R.id.recordFragment) {
                    fragment = missionFragment;
                } else if (itemId == R.id.rankingFragment) {
                    fragment = rankingFragment;
                } else if (itemId == R.id.mainFragment) {
                    fragment = mainFragment;
                } else if (itemId == R.id.socialFragment) {
                    fragment = socialFragment;
                } else if (itemId == R.id.profileFragment) {
                    fragment = profileFragment;
                }
                return loadFragment(fragment);
            }
        });
    }
    boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
            return true;
        } else {
            return false;
        }
    }
    // 해쉬 값 받기
    public static String getKeyHash(final Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if(packageInfo == null)
                return null;
            for(Signature signature : packageInfo.signatures) {
                try{
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return android.util.Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}