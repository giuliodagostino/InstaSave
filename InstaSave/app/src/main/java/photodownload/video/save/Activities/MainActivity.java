package photodownload.video.save.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import photodownload.video.save.Adapters.RecyclerViewAdapter;
import photodownload.video.save.Intro.IntroActivity;
import photodownload.video.save.Model.Files;
import photodownload.video.save.R;
import photodownload.video.save.Utils.Constant;
import photodownload.video.save.Utils.Constants;
import photodownload.video.save.Utils.SettingsActivity;
import photodownload.video.save.Utils.Source;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class MainActivity extends AppCompatActivity {

    RequestQueue queue;
    Toolbar mToolbar;
    String htmlSource = "";
    private String videoUrl = "";
    private String imageUrl = "";
    private String description = "";
    private ImageView mainImageView;
    private ProgressBar progressBar, downloadProgress;
    SwipeRefreshLayout recyclerLayout;
    RelativeLayout placeholderLayout;
    private static final String PREFS_NAME = "preferenceName";
    private ImageView playIcon;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    Intent serviceIntent;
    private static String URL = "";
    Drawer resultDrawer;
    AccountHeader headerResult;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private TextView noImageText;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    TextView downloadPercentage;
    private int mProgressStatus;
    private Handler mHandler = new Handler();
    String resultDesc;
    ProgressDialog mProgressDialog;
    String fileN = null;
    String urlDesc, htmlResponse;
    private String desc, resultz;
    TextView captionText;
    private HashTagHelper mTextHashTagHelper;
    private Button repost, copyTextBtn, copyHashtagsBtn;
    private ClipboardManager myClipboard, hashtagClipboard;
    private ClipData myClip, hashtagClipData;
    File[] files;
    String path;
    Uri uri;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    Parcelable listState;
    private Boolean doubleBackToExitPressedOnce = false;
    private AdView mAdView;
    InterstitialAd mInterstitialAd;
    private int counter = 0;
    SharedPreferences preferences, saveCounter, loadCounter;
    AdRequest adRequestint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Constant.theme);
        setContentView(R.layout.activity_main);
        if (getAppIntro(this)) {
            Intent i = new Intent(this, IntroActivity.class);
            startActivity(i);
        }
        initComponents();
        //ADS
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        NativeExpressAdView adNativeView = (NativeExpressAdView) findViewById(R.id.nativeAD);

        AdRequest request = new AdRequest.Builder()
                .build();
        adNativeView.loadAd(request);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(Constant.color);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null) {
            try{
                ClipData.Item item = clip.getItemAt(0);
                URL = Source.getURL(item.getText().toString(), "(http(s)?:\\/\\/(.+?\\.)?[^\\s\\.]+\\.[^\\s\\/]{1,9}(\\/[^\\s]+)?)");
            }catch(Exception e){
                e.printStackTrace();
            }
            Log.d("URL: ", URL);
        } else {
            Toast.makeText(this, "Empty ClipBoard!", Toast.LENGTH_SHORT).show();
        }

        queue = Volley.newRequestQueue(this);
        boolean result = checkPermission();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "sintony-regular.otf");

        if (result) {
            checkFolder();
            if (checkForEmptyUrl(URL)) {
                viewPageSource();
                setUpRecyclerView();
            } else {
                Toast.makeText(this, "Please Copy Instagram Share URL", Toast.LENGTH_SHORT).show();
                setUpRecyclerView();
                placeholderLayout.setVisibility(View.INVISIBLE);
                recyclerLayout.setVisibility(View.VISIBLE);
            }
        }
        ColorDrawable cd = new ColorDrawable(Constant.color);
        //Navigation Drawer
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(cd)
                .withSelectionListEnabledForSingleProfile(false)
                .withAlternativeProfileHeaderSwitching(false)
                .withCompactStyle(false)
                .withDividerBelowHeader(false)
                .withProfileImagesVisible(true)
                .withTypeface(typeface)
                .addProfiles(new ProfileDrawerItem().withIcon(R.mipmap.ic_launcher).withName(getResources().getString(R.string.app_name)))
                .build();
        resultDrawer = new DrawerBuilder()
                .withActivity(this)
                .withSelectedItem(-1)
                .withFullscreen(true)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggle(true)
                .withCloseOnClick(true)
                .withMultiSelect(false)
                .withTranslucentStatusBar(true)
                .withToolbar(mToolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withSelectable(false).withName(R.string.app_name).withTypeface(typeface),
                        new PrimaryDrawerItem().withSelectable(false).withName(R.string.gallery).withIcon(R.drawable.ic_home_black_24dp).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                if (placeholderLayout.getVisibility() == View.VISIBLE) {
                                    placeholderLayout.setVisibility(View.GONE);
                                    recyclerLayout.setVisibility(View.VISIBLE);
                                    setUpRecyclerView();
                                }
                                return false;
                            }
                        }).withTypeface(typeface),
                        new PrimaryDrawerItem().withSelectable(false).withName(R.string.rtf).withIcon(R.drawable.ic_share_black_24dp).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                final String shareappPackageName = getPackageName();
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out InstaKeep App at: https://play.google.com/store/apps/details?id=" + shareappPackageName);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                return false;
                            }
                        }).withTypeface(typeface),
                        new PrimaryDrawerItem().withSelectable(false).withName(R.string.ru).withIcon(R.drawable.ic_thumb_up_black_24dp).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                final String appPackageName = getPackageName();
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                                return false;
                            }
                        }).withTypeface(typeface),
                        new PrimaryDrawerItem().withSelectable(false).withName(R.string.setting).withIcon(R.drawable.ic_settings_applications_black_24dp).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                return false;
                            }
                        }).withTypeface(typeface),
                        new PrimaryDrawerItem().withSelectable(false).withName(R.string.feed).withIcon(R.drawable.ic_feedback_black_24dp).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                DisplayMetrics displaymetrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                                int height = displaymetrics.heightPixels;
                                int width = displaymetrics.widthPixels;
                                PackageManager manager = getApplicationContext().getPackageManager();
                                PackageInfo info = null;
                                try {
                                    info = manager.getPackageInfo(getPackageName(), 0);
                                } catch (PackageManager.NameNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                String version = info.versionName;
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.developer_email)});
                                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + version);
                                i.putExtra(Intent.EXTRA_TEXT,
                                        "\n" + " Device :" + getDeviceName() +
                                                "\n" + " System Version:" + Build.VERSION.SDK_INT +
                                                "\n" + " Display Height  :" + height + "px" +
                                                "\n" + " Display Width  :" + width + "px" +
                                                "\n\n" + "Have a problem? Please share it with us and we will do our best to solve it!" +
                                                "\n");
                                startActivity(Intent.createChooser(i, "Send Email"));
                                return false;
                            }
                        }).withTypeface(typeface),
                        new PrimaryDrawerItem().withSelectable(false).withName(R.string.exit).withIcon(R.drawable.ic_exit_to_app_black_24dp).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                finish();
                                return false;
                            }
                        }).withTypeface(typeface)
                ).
                        withSavedInstance(savedInstanceState)
                .build();
        loadInterstitialAd();
    }

    private void loadInterstitialAd() {
        adRequestint = new AdRequest.Builder().build();
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_full_screen));
        mInterstitialAd.loadAd(adRequestint);
    }
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, getData());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private ArrayList<Files> getData() {
        ArrayList<Files> filesList = new ArrayList<>();
        Files f;
        String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FOLDER_NAME;
        File targetDirector = new File(targetPath);
        files = targetDirector.listFiles();
        if (files == null) {
//            noImageText.setVisibility(View.INVISIBLE);
        }
        try {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {

                    if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                        return -1;
                    } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                        return +1;
                    } else {
                        return 0;
                    }
                }

            });

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                f = new Files();
                String filename = file.getName();
                String start = StringUtils.substringBefore(filename, "+");
                f.setName(start);
                f.setUri(Uri.fromFile(file));
                f.setFilename(files[i].getAbsolutePath());
                filesList.add(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filesList;
    }

    private void getpostText(String textField) {
        if (Source.getURL(textField, "(http(s)?:\\/\\/(.+?\\.)?[^\\s\\.]+\\.[^\\s\\/]{1,9}(\\/[^\\s]+)?)").isEmpty()) {
            Log.d("incorrect", "Url is not Correct");
        } else {
            urlDesc = Source.getURL(textField, "(http(s)?:\\/\\/(.+?\\.)?[^\\s\\.]+\\.[^\\s\\/]{1,9}(\\/[^\\s]+)?)");
            AsyncHttpClient client = new AsyncHttpClient();
            client.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36");
            client.get(textField, new TextHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    htmlResponse = responseString;
                    Log.d("HtML: ", htmlResponse);
                    try {
                        int start = htmlResponse.indexOf("\"text\":") + 9;
                        String starting = htmlResponse.substring(start);
                        int end = starting.indexOf("\"}}]}");
                        desc = starting.substring(0, end);
                        resultz = StringEscapeUtils.unescapeJava(desc);
                        Log.d("End", resultz);
                        if (resultz.contains("created_at") || resultz.contains("href")) {
                            resultz = "";
                            copyTextBtn.setVisibility(View.GONE);
                        }
                        if (!resultz.contains("#")) {
                            copyHashtagsBtn.setVisibility(View.GONE);
//                            btn_caption.setVisibitlity(View.GONE);
                        }
                        try {
                            captionText.setText(resultz);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        desc = "";
                    }
                }
            });
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkFolder();
                    if (checkForEmptyUrl(URL)) {
                        viewPageSource();
                        setUpRecyclerView();
                    } else {
                        setUpRecyclerView();
                        placeholderLayout.setVisibility(View.INVISIBLE);
                        recyclerLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    //code for deny
                    checkAgain();
                }
                break;
        }
    }

    public void checkAgain() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }

    public Boolean getAppIntro(Context context) {

        SharedPreferences preferences;
        preferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean("AppIntro", true);
    }

    public void initComponents() {
        mainImageView = (ImageView) findViewById(R.id.imageDownloadedID);
        recyclerLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRecyclerView);
        placeholderLayout = (RelativeLayout) findViewById(R.id.relativeContent);
        playIcon = (ImageView) findViewById(R.id.playIcon);
        progressBar = (ProgressBar) findViewById(R.id.mProgressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        downloadPercentage = (TextView) findViewById(R.id.downloadPercent);
        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.download_dialog, null);
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        mProgressDialog = new ProgressDialog(MainActivity.this);
        captionText = (TextView) findViewById(R.id.captionText);
        repost = (Button) findViewById(R.id.repostButton);
        copyHashtagsBtn = (Button) findViewById(R.id.copyHashtag);
        copyTextBtn = (Button) findViewById(R.id.copyTextButton);
        copyTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String copiedtext = captionText.getText().toString();
                myClip = ClipData.newPlainText("text", copiedtext);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), "Text Copied",
                        Toast.LENGTH_SHORT).show();
            }
        });
        mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.accent), null);
        mTextHashTagHelper.handle(captionText);
        copyHashtagsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> allHashTags = mTextHashTagHelper.getAllHashTags();
                String tmpString = allHashTags.toString().replace('[', '#');
                String tmpString1 = tmpString.replace(']', ' ');
                String replaceBlank = tmpString1.replaceAll("\\s+", "");
                String tmpString2 = replaceBlank.replaceAll(",", " #");
                Log.d("hashtags", tmpString2);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text label", tmpString2);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Hashtags Copied",
                        Toast.LENGTH_SHORT).show();
            }
        });

        repost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final File file = new File(path);
                    Uri mainUri = Uri.fromFile(file);
                    if (path.endsWith(".jpg")) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("image/*");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Downloaded using " + getResources().getString(R.string.app_name) + " android application");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        sharingIntent.setPackage("com.instagram.android");
                        try {
                            startActivity(Intent.createChooser(sharingIntent, "Share Image using"));
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(getApplicationContext(), "No application found to open this file.", Toast.LENGTH_LONG).show();
                        }
                    } else if (path.endsWith(".mp4")) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("video/*");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Downloaded using " + getResources().getString(R.string.app_name) + " android application");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        sharingIntent.setPackage("com.instagram.android");
                        try {
                            startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(getApplicationContext(), "No application found to open this file.", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        recyclerLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerLayout.setRefreshing(true);
                setUpRecyclerView();
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerLayout.setRefreshing(false);
                    }
                }, 2000);

            }
        });
    }

    public void viewPageSource() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response: ", response.toString());
                if (response != null) {

                    recyclerLayout.setVisibility(View.INVISIBLE);
                    placeholderLayout.setVisibility(View.VISIBLE);

                    htmlSource = response.toString();
                    videoUrl = Source.getURL(htmlSource, "property=\"og:video\" content=\"([^\"]+)\"");
                    imageUrl = Source.getURL(htmlSource, "property=\"og:image\" content=\"([^\"]+)\"");
                    description = Source.getURL(htmlSource, "property=\"og:description\" content=\"([^\"]+)\"");

                    Log.d("Image URl: ", imageUrl);
                    Log.d("Description: ", description);
                    Log.d("Video URL:", videoUrl);

                    try {
                        resultDesc = description.substring(description.indexOf("@") + 1, description.indexOf("Instagram") - 5);
                        Log.d("Profile Name: ", resultDesc);
                        if (resultDesc == null) {
                            resultDesc = "Profile Name";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (videoUrl.isEmpty()) {
                        String prefsImage = getPreference(getApplicationContext(), "link");
                        if (!prefsImage.equals(imageUrl)) {
                            progressBar.setVisibility(View.VISIBLE);
                            playIcon.setVisibility(View.INVISIBLE);
                            Glide.with(getApplicationContext())
                                    .load(imageUrl)
                                    .into(mainImageView);
//                            dwndImage(imageUrl);
                            newDownload(imageUrl);
                            getpostText(URL);
                            setPreference(getApplicationContext(), "link", imageUrl);
                        } else {
                            recyclerLayout.setVisibility(View.VISIBLE);
                            placeholderLayout.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    } else if (videoUrl.endsWith(".mp4")) {
                        String prefsVal = getPreference(getApplicationContext(), "link");
                        if (!prefsVal.equals(videoUrl)) {
                            playIcon.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            Glide.with(getApplicationContext())
                                    .load(imageUrl)
                                    .into(mainImageView);
//                            dwndImage(videoUrl);
                            newDownload(videoUrl);
                            getpostText(URL);
                            setPreference(getApplicationContext(), "link", videoUrl);
                        } else {
                            recyclerLayout.setVisibility(View.VISIBLE);
                            placeholderLayout.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Failed to Fetch Data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please Check If Your Url is Correct!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                recyclerLayout.setVisibility(View.VISIBLE);
                placeholderLayout.setVisibility(View.INVISIBLE);
            }
        });
        queue.add(stringRequest);
    }

    private boolean checkForEmptyUrl(String url) {
        Pattern p = Pattern.compile("instagram.com");
        Matcher m = p.matcher(url);//replace with string to compare
        if (m.find()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ic_insta) {
            Intent launchIntent = MainActivity.this.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
            startActivity(launchIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void dwndImage(String url) {

        String fileName = url.substring(url.lastIndexOf('/') + 1);
        DownloadManager dm = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("InstaKeep")
                .setDescription("Downloading")
                .setDestinationInExternalPublicDir("/InstaKeep", fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        Toast.makeText(this, "Download Started", Toast.LENGTH_SHORT).show();
        dm.enqueue(request);
    }

    public void checkFolder() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/InstaKeep";
        File dir = new File(path);
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdir();
        }
        if (isDirectoryCreated) {
            // do something\
            Log.d("Folder", "Already Created");
        }
    }


    public static boolean setPreference(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getPreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, "defaultValue");
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        if (recyclerLayout.getVisibility() == View.VISIBLE) {
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        } else if (placeholderLayout.getVisibility() == View.VISIBLE) {
            placeholderLayout.setVisibility(View.GONE);
            recyclerLayout.setVisibility(View.VISIBLE);
            showInterstitial();
            setUpRecyclerView();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();
                if (url.toString().endsWith(".mp4")) {
                    fileN = resultDesc + "+instakeep_" + UUID.randomUUID().toString().substring(0, 5) + ".mp4";
                    File filename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            Constants.FOLDER_NAME, fileN);
                    output = new FileOutputStream(filename);

                } else {
                    fileN = resultDesc + "+instakeep_" + UUID.randomUUID().toString().substring(0, 5) + ".jpg";
                    File filename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            Constants.FOLDER_NAME, fileN);
                    output = new FileOutputStream(filename);
                }
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            MediaScannerConnection.scanFile(MainActivity.this,
                    new String[]{Environment.getExternalStorageDirectory().getAbsolutePath() +
                            Constants.FOLDER_NAME + fileN}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String newpath, Uri newuri) {
                            Log.i("ExternalStorage", "Scanned " + newpath + ":");
                            Log.i("ExternalStorage", "-> uri=" + newuri);
                            path = newpath;
                            uri = newuri;
                        }
                    });

        }
    }

    public void newDownload(String url) {
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("Downloading..");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
        downloadTask.execute(url);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save RecyclerView state
        if (mBundleRecyclerViewState != null && recyclerView != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            if (recyclerView.getLayoutManager() != null) {
                recyclerView.getLayoutManager().onRestoreInstanceState(listState);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }
}
