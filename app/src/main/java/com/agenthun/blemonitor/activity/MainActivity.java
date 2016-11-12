package com.agenthun.blemonitor.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.agenthun.blemonitor.App;
import com.agenthun.blemonitor.R;
import com.agenthun.blemonitor.adapter.SectionsPagerAdapter;
import com.agenthun.blemonitor.bean.AllDynamicDataByContainerId;
import com.agenthun.blemonitor.bean.base.Detail;
import com.agenthun.blemonitor.bean.base.HistoryData;
import com.agenthun.blemonitor.connectivity.manager.RetrofitManager;
import com.agenthun.blemonitor.connectivity.service.PathType;
import com.agenthun.blemonitor.utils.ApiLevelHelper;
import com.agenthun.blemonitor.view.BottomSheetDialogView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity
   /*     implements NavigationView.OnNavigationItemSelectedListener*/ {

    private static final String TAG = "MainActivity";
    private final int SDK_PERMISSION_REQUEST = 127;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected() returned: " + position);
                if (position == 0) {
                    fab.setVisibility(View.VISIBLE);
                    ViewCompat.animate(fab).scaleX(1).scaleY(1)
                            .setInterpolator(new LinearOutSlowInInterpolator())
                            .setListener(new ViewPropertyAnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(View view) {
                                    if (isFinishing() || (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.JELLY_BEAN_MR1) && isDestroyed())) {
                                        return;
                                    }
                                    fab.setVisibility(View.VISIBLE);
                                }
                            })
                            .start();
                } else {
                    if (fab.isShown()) {
                        ViewCompat.animate(fab).scaleX(0).scaleY(0)
                                .setInterpolator(new FastOutSlowInInterpolator())
                                .setStartDelay(100)
                                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(View view) {
                                        if (isFinishing() || (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.JELLY_BEAN_MR1) && isDestroyed())) {
                                            return;
                                        }
                                        fab.setVisibility(View.GONE);
                                    }
                                })
                                .start();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnFABClickListener != null) {
                    mOnFABClickListener.OnFABClickListener(view);
                }
            }
        });

        getPersimmions();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setCancelable(false);
//            builder.setTitle(getResources().getString(R.string.device_alarm_title))
//                    .setMessage(R.string.error_ble_not_supported)
//                    .setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Log.d(TAG, "AlertDialog ble not supported");
//                            finish();
//                        }
//                    }).show();
//        }
//
//        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
//        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
//        if (bluetoothAdapter == null) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setCancelable(false);
//            builder.setTitle(getResources().getString(R.string.device_alarm_title))
//                    .setMessage(R.string.error_ble_not_supported)
//                    .setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Log.d(TAG, "AlertDialog ble not supported");
//                            finish();
//                            return;
//                        }
//                    }).show();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPersimmions();
    }

    //FABClick interface
    public interface OnFABClickListener {
        void OnFABClickListener(View view);
    }

    private OnFABClickListener mOnFABClickListener;

    public void setOnItemClickListener(OnFABClickListener mOnFABClickListener) {
        this.mOnFABClickListener = mOnFABClickListener;
    }

    private void getPersimmions() {
        List<String> permissions = new ArrayList<>();

        // 读写外设为必须权限，用户如果禁止，则每次进入都会申请
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissions.size() > 0) {
            ActivityCompat.requestPermissions(MainActivity.this, permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
