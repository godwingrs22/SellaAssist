package it.sella.assist.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import it.sella.assist.R;
import it.sella.assist.core.SOSManager;

/**
 * Created by GodwinRoseSamuel on 22-Aug-16.
 */
public class SOSActivity extends AppCompatActivity {
    private static final String TAG = SOSActivity.class.getSimpleName();
    private Button helpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.md_white_1000));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        helpButton = (Button) findViewById(R.id.help_button);

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SOSManager sosManager = new SOSManager();
                sosManager.requestHelp(SOSActivity.this);
                Toast.makeText(SOSActivity.this, "Help Requested Successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
