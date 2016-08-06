package it.sella.sellaassist.ui;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.SystemRequirementsChecker;
import com.squareup.picasso.Picasso;

import it.sella.sellaassist.R;
import it.sella.sellaassist.model.User;
import it.sella.sellaassist.sync.SellaAssistSyncAdapter;
import it.sella.sellaassist.util.SellaCache;
import it.sella.sellaassist.util.Utility;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 201;

    private Boolean doubleBackToExitPressedOnce = false;

    private static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.sella.BancaSella";
    private static final String FACEBOOK_URL = "https://www.facebook.com/bancasella";
    private static final String TWITTER_URL = "https://twitter.com/bancasella";
    private static final String WEBSITE_URL = "https://www.sella.it";
    private static final String CONTACT_US_URL = "https://www.sella.it/ita/contatti/index.jsp";
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            navigationView.getMenu().performIdentifierAction(R.id.item_navigation_drawer_feeds, 0);

        }

        user = getIntent().getParcelableExtra(Utility.USER_GBS_ID_KEY);
        if (user != null) {
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.navigation_header_name)).setText(user.getName());
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.navigation_header_gbsid)).setText(user.getGbsID());
            Picasso.with(getApplicationContext())
                    .load(Uri.parse(user.getProfilePic()))
                    .placeholder(R.drawable.ic_account_circle_white_48dp)
                    .error(R.drawable.ic_account_circle_white_48dp)
                    .into((ImageView) navigationView.getHeaderView(0).findViewById(R.id.navigation_header_profile_image));
        }

        SellaCache.putCache(Utility.USER_GBS_ID_KEY, user.getGbsID(), this);

        SellaAssistSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Exit", Toast.LENGTH_SHORT).show();
            doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        if (id == R.id.action_logout) {
            finish();
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra(Utility.USER_GBS_ID_KEY, user);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (item.getItemId()) {
            case R.id.item_navigation_drawer_feeds:
                fragmentClass = FeedsFragment.class;
                break;
            case R.id.item_navigation_drawer_biometric:
                fragmentClass = BiometricFragment.class;
                break;
            case R.id.item_navigation_drawer_events:
                fragmentClass = EventsFragment.class;
                break;
            case R.id.item_navigation_drawer_lms:
                fragmentClass = LmsFragment.class;
                break;
            case R.id.item_navigation_drawer_about:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("About SellaAssist")
                        .setMessage("SellaAssist\n------------------------\nVersion:1.0.2\n\nDeveloped By:\nBanca Sella-Chennai Branch\nwww.sella.it"
                                + "\n\nSupport by Email:\ninfo@sella.it"
                                + "\n\nDISCLAIMER:\n"
                                + "The user uses the application it on own and sole responsibility."
                                + "The information and datas appearing in the application serve exclusively "
                                + "as guidance and the creator is not liable for their correctness"
                                + "\n\nGood Luck!!\nBanca Sella")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
                break;
            case R.id.item_navigation_drawer_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                share.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_description) + "\n" +
                        "Website : " + WEBSITE_URL + "\n" +
                        "FaceBook Page : " + FACEBOOK_URL + "\n" +
                        "Download from: " + PLAY_STORE_URL);
                startActivity(Intent.createChooser(share, getString(R.string.app_name)));
                break;
            case R.id.item_navigation_drawer_rateus:
                break;
            case R.id.item_navigation_drawer_device_settings:
                break;
            case R.id.item_navigation_drawer_contact_us:
                try {
                    Intent contactusIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CONTACT_US_URL));
                    startActivity(contactusIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No browser is installed", Toast.LENGTH_LONG);
                }
                break;
            case R.id.item_navigation_drawer_help_and_feedback:
                try {
                    Intent i = new Intent("android.intent.action.MAIN");
                    i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
                    i.addCategory("android.intent.category.LAUNCHER");
                    i.setData(Uri.parse(WEBSITE_URL));
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(WEBSITE_URL));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
                break;
            default:
                fragmentClass = FeedsFragment.class;
        }

        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        item.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    public User getUser() {
        return user;
    }
}
