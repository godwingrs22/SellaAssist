package it.sella.assist.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.sella.assist.R;
import it.sella.assist.adapter.PermissionAdapter;
import it.sella.assist.model.Permission;
import it.sella.assist.util.Utility;

/**
 * Created by GodwinRoseSamuel on 22-Jul-16.
 */
public class PermissionActivity extends AppCompatActivity {
    private static final String TAG = PermissionActivity.class.getSimpleName();
    private static final int REQUEST_MULTIPLE_PERMISSION = 0;
    private PermissionAdapter permissionAdapter;
    private List<Permission> permissionList;
    private RecyclerView permissionRecyclerView;
    private TextView exitTextView;
    private TextView continueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        exitTextView = (TextView) findViewById(R.id.permission_exit_textview);
        continueTextView = (TextView) findViewById(R.id.permission_continue_textview);

        permissionRecyclerView = (RecyclerView) findViewById(R.id.permission_item_list);
        permissionList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        permissionRecyclerView.setLayoutManager(layoutManager);

        permissionAdapter = new PermissionAdapter(permissionList);
        permissionRecyclerView.setAdapter(permissionAdapter);
        permissionRecyclerView.setItemAnimator(new DefaultItemAnimator());

        permissionList.clear();
        permissionList.addAll(Utility.addPermissions());
        permissionAdapter.notifyDataSetChanged();

        exitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PermissionActivity.this, "Sella Assit needs certain permissions to work properly", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        continueTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions()) {
                    finish();
                    Intent i = new Intent(getApplicationContext(), SplashActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    private boolean checkAndRequestPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (Permission permission : permissionList) {
            if (ContextCompat.checkSelfPermission(this, permission.getName()) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission.getName());
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_MULTIPLE_PERMISSION);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_MULTIPLE_PERMISSION: {
                Map<String, Integer> perms = new HashMap<>();

                for (Permission permission : permissionList)
                    perms.put(permission.getName(), PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    int counter = 0;
                    for (Map.Entry<String, Integer> perm : perms.entrySet()) {
                        if (perm.getValue() == PackageManager.PERMISSION_GRANTED)
                            counter++;
                    }

                    if (counter == perms.size()) {
                        Log.v(TAG, "All permission granted");
                        finish();
                        Intent i = new Intent(getApplicationContext(), SplashActivity.class);
                        startActivity(i);
                    } else {
                        Log.e(TAG, "Some permissions are not granted ask again ");
                        for (Permission permission : permissionList) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission.getName())) {
                                Log.e(TAG, "Some permissions are not granted");
                                Toast.makeText(this, "Please provide the permissions to all to work properly", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Please go to settings and enable permissions", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        }
    }
}
