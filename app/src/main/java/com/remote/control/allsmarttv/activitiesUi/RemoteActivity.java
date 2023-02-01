package com.remote.control.allsmarttv.activitiesUi;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.remote.control.allsmarttv.appUi.FirstActivity;
import com.remote.control.allsmarttv.appUi.PermActivity;
import com.remote.control.allsmarttv.utils.ImeListenerCall;
import com.remote.control.allsmarttv.Fragments.ConnFrag;
import com.remote.control.allsmarttv.Fragments.DeviceFragment;
import com.remote.control.allsmarttv.Fragments.NavFragment;
import com.remote.control.allsmarttv.Fragments.PairingFragment;
import com.remote.control.allsmarttv.Fragments.RemoteFragment;
import com.remote.control.allsmarttv.R;
import com.remote.control.allsmarttv.utils.BuildInfo;
import com.remote.control.allsmarttv.utils.CapabilitiesClass;
import com.remote.control.allsmarttv.utils.ClientService;
import com.remote.control.allsmarttv.utils.Device;
import com.remote.control.allsmarttv.utils.DevicesInfoUtil;
import com.remote.control.allsmarttv.utils.TvPreferences;
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass;

import java.util.Map;


public class RemoteActivity extends AppCompatActivity implements DeviceFragment.Listener, PairingFragment.Listener, NavFragment.NavigationDrawerCallbacks, RemoteFragment.Listener, ImeListenerCall {

    private static int[] e1 = null;
    private static final String TAG = "AtvRemote.CrRmtActivity";
    private boolean activityVisible = false;
    private boolean bound = false;
    RelativeLayout relativeLayout;
    private ConnFrag connFrag = null;
    private Fragment currentFrag;
    private DeviceFragment deviceFragment = null;
    private int devicesUpdatedEvents = 0;
    private ClientService clientService = null;
    private boolean configured = false;
    private EditorInfo imeEditor;
    private ExtractedText imeExtracted;
    private boolean isOrientChanged;
    private int lastConnectionStatus = 0;
    private int layoutSelected = 0;
    private NavFragment navigationFragment;
    private PairingFragment pairingFragment = null;
    private RemoteFragment remoteFragment = null;
    private View rootView;
    private Intent serviceIntent = null;
    private int status = 7;
    private CharSequence title;
    private boolean help = false;

    private final ClientService.Listener clientListener = new ClientService.Listener() {

        private boolean hasSentSound = false;

        @Override
        public void onConnecting(Device device) {
            RemoteActivity.this.configured = false;
            RemoteActivity.this.updateState();
        }

        @Override
        public void onConnected(Device device) {
            RemoteActivity.this.updateState();
        }

        @Override
        public void onConnectFailed(Device device) {
            Log.e(RemoteActivity.TAG, "Client failed to connect... " + device);
            RemoteActivity.this.forgetDevice();
            RemoteActivity.this.updateState();
        }

        @Override
        public void onDisconnected(Device device) {
            RemoteActivity.this.configured = false;
            if (TvPreferences.getDeviceInfo(RemoteActivity.this) != null && RemoteActivity.this.activityVisible) {
                RemoteActivity.this.runOnUiThread(new Runnable() {

                    public void run() {
                        RemoteActivity.this.reconnect();
                    }
                });
            }
            if (RemoteActivity.this.activityVisible) {
                RemoteActivity.this.updateState();
            }
        }

        @Override
        public void onPairingRequired(Device device) {
            if (RemoteActivity.this.activityVisible) {
                RemoteActivity.this.generatePairingFragment();
            } else {
                RemoteActivity.this.forgetDevice();
                if (RemoteActivity.this.clientService != null) {
                    RemoteActivity.this.clientService.cancelPairing();
                }
            }
        }

        @Override
        public void onShowIme(Device device, EditorInfo editorInfo, boolean z, ExtractedText extractedText) {
            if (!RemoteActivity.this.isOrientChanged) {
                RemoteActivity.this.imeExtracted = extractedText;
                RemoteActivity.this.imeEditor = editorInfo;
            }
            RemoteActivity.this.isOrientChanged = false;
            if (RemoteActivity.this.isAttached(RemoteActivity.this.remoteFragment) && RemoteActivity.this.currentFrag == RemoteActivity.this.remoteFragment && RemoteActivity.this.activityVisible) {
                RemoteActivity.this.remoteFragment.showIme();
            }
        }

        @Override
        public void onHideIme(Device device) {
            if (RemoteActivity.this.isAttached(RemoteActivity.this.remoteFragment) && RemoteActivity.this.currentFrag == RemoteActivity.this.remoteFragment) {
                RemoteActivity.this.remoteFragment.hideIme();
            }
        }

        @Override
        public void onStartVoice(Device device) {
            this.hasSentSound = false;
            if ((RemoteActivity.this.isAttached(RemoteActivity.this.remoteFragment)
                    && RemoteActivity.this.currentFrag == RemoteActivity.this.remoteFragment)
                    || (RemoteActivity.this.isAttached(RemoteActivity.this.connFrag)
                    && RemoteActivity.this.currentFrag == RemoteActivity.this.connFrag)) {
                RemoteActivity.this.remoteFragment.showVoiceAnimation();
            }
        }

        @Override
        public void onVoiceSoundLevel(Device device, int i) {
            if ((RemoteActivity.this.isAttached(RemoteActivity.this.remoteFragment)
                    && RemoteActivity.this.currentFrag == RemoteActivity.this.remoteFragment)
                    || (RemoteActivity.this.isAttached(RemoteActivity.this.connFrag)
                    && RemoteActivity.this.currentFrag == RemoteActivity.this.connFrag)) {
                RemoteActivity.this.remoteFragment.setSoundLevel(i);
            }
            if (!this.hasSentSound) {
                this.hasSentSound = true;
            }
        }

        @Override
        public void onStopVoice(Device device) {
            this.hasSentSound = false;
            if ((RemoteActivity.this.isAttached(RemoteActivity.this.remoteFragment)
                    && RemoteActivity.this.currentFrag == RemoteActivity.this.remoteFragment)
                    || (RemoteActivity.this.isAttached(RemoteActivity.this.connFrag)
                    && RemoteActivity.this.currentFrag == RemoteActivity.this.connFrag)) {
                RemoteActivity.this.remoteFragment.stopVoiceSearchAnimation();
            }
        }

        @Override
        public void onCompletionInfo(Device device, CompletionInfo[] completionInfoArr) {
            if ((RemoteActivity.this.isAttached(RemoteActivity.this.remoteFragment)
                    && RemoteActivity.this.currentFrag == RemoteActivity.this.remoteFragment)
                    || (RemoteActivity.this.isAttached(RemoteActivity.this.connFrag)
                    && RemoteActivity.this.currentFrag == RemoteActivity.this.connFrag)) {
                RemoteActivity.this.remoteFragment.updateCompletionInfo(completionInfoArr);
            }
        }

        @Override
        public void onConfigureSuccess(Device device) {
            RemoteActivity.this.configured = true;
            if (RemoteActivity.this.activityVisible) {
                RemoteActivity.this.active();
            }
            logAtvBuildInfo(device);
        }

        private void logAtvBuildInfo(Device device) {
            if (device == null) {
                Log.w(RemoteActivity.TAG, "trying to log but device is null");
                return;
            }
            BuildInfo buildInfo = device.getBuildInfo();
            if (buildInfo == null) {
                Log.w(RemoteActivity.TAG, "trying to log but build info is null");
                return;
            }
        }

        @Override
        public void onConfigureFailure(Device device, int i) {
            RemoteActivity.this.configured = false;
            RemoteActivity.this.forgetDevice();
            RemoteActivity.this.updateStateOnStatus(RemoteActivity.this.getTheStatus());
        }

        @Override
        public void onException(Device device, Exception exc) {
            Log.e(RemoteActivity.TAG, String.format("Received exception; %s ", exc.getMessage()));
            if (exc instanceof Device.WaitTimedOutException) {
            }
        }

        @Override
        public void onServiceDeath() {
            RemoteActivity.this.serviceConnection.onServiceDisconnected(null);
        }

        @Override
        public void onDeveloperStatus(Device device, boolean z) {
        }

        @Override
        public void onAsset(Device device, String str, Map<String, String> map, byte[] bArr) {
        }

        @Override
        public void onBundle(Device device, int i, Bundle bundle) {
        }

        @Override
        public void onCapabilities(Device device, CapabilitiesClass capabilitiesClass) {
            if (!capabilitiesClass.secondScreenSetup() || RemoteActivity.this.clientService != null) {
            }
        }

        @Override
        public void onBugReportStatus(Device device, int i) {
            switch (i) {
                case 1:
                case 3:
                    return;
                case 2:
                default:
                    return;
            }
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
                if (!intent.getBooleanExtra("noConnectivity", false)) {
                }
            } else if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(action) && 12 == intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1)) {
            }
            if (RemoteActivity.this.getTheStatus() != 1 && !RemoteActivity.this.reconnect()) {
                RemoteActivity.this.updateStateOnStatus(RemoteActivity.this.getTheStatus());
            }
        }
    };

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    if (RemoteActivity.this.clientService != null && RemoteActivity.this.configured) {
                        RemoteActivity.this.clientService.interactive(true);
                        return;
                    }
                    return;
                case 2:
                    if (RemoteActivity.this.clientService != null && RemoteActivity.this.configured) {
                        RemoteActivity.this.clientService.interactive(false);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            try {
                if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                    if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                    } else {
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName componentName) {
            RemoteActivity.this.clientService = null;
            RemoteActivity.this.bound = false;
            RemoteActivity.this.status = 7;
            if (RemoteActivity.this.remoteFragment != null) {
                RemoteActivity.this.remoteFragment.attachRemoteInterface(null);
            }
            RemoteActivity.this.updateState();
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            RemoteActivity.this.clientService = ((ClientService.LocalBinder) iBinder).getService();
            RemoteActivity.this.clientService.setRemoteListener(RemoteActivity.this.clientListener);
            RemoteActivity.this.bound = true;
            RemoteActivity.this.status = 6;
            if (RemoteActivity.this.remoteFragment != null) {
                RemoteActivity.this.remoteFragment.attachRemoteInterface(RemoteActivity.this.clientService);
            }
            RemoteActivity.this.updateState();
        }
    };

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SupportedClass.loadLangLocale(getBaseContext());
        setContentView(R.layout.activity_remote);


        SharedPreferences sharedPreferences = RemoteActivity.this.getSharedPreferences("TVRemote", Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("connect", false))
        {

        Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.remote_update_dialog);
            dialog.getWindow().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.remote_update_bg));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);

        TextView btnClose = dialog.findViewById(R.id.button_exit);
            btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox checkBox = dialog.findViewById(R.id.check);

                if (checkBox.isChecked()) {
                    SharedPreferences.Editor editor = RemoteActivity.this.getSharedPreferences("TVRemote", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("connect", true);
                    editor.apply();
                }

                dialog.dismiss();
            }
        });

            dialog.show();
    }


        relativeLayout = findViewById(R.id.androidBack_lay);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RemoteActivity.this, FirstActivity.class);
                startActivity(intent);
                finish();
            }
        });


        registerReceiver();

        this.layoutSelected = TvPreferences.getControl(this);
        this.navigationFragment = (NavFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drawer);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.rootView = drawerLayout;
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);

        this.navigationFragment.set(drawerLayout, getSupportActionBar(), this.help);
        this.currentFrag = getSupportFragmentManager().findFragmentById(R.id.content_layout);
        if (this.currentFrag != null) {
            updateTheActionBar();
            if (this.currentFrag instanceof DeviceFragment) {
                this.deviceFragment = (DeviceFragment) this.currentFrag;
            } else if (this.currentFrag instanceof RemoteFragment) {
                this.remoteFragment = (RemoteFragment) this.currentFrag;
            } else if (this.currentFrag instanceof ConnFrag) {
                this.connFrag = (ConnFrag) this.currentFrag;
            } else if (this.currentFrag instanceof PairingFragment) {
                this.pairingFragment = (PairingFragment) this.currentFrag;
            }
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().addFlags(4718592);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rem_menu, menu);
        resetAB();
        return super.onCreateOptionsMenu(menu);
    }


    public void resetAB() {
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        supportActionBar.setDisplayShowTitleEnabled(true);

        supportActionBar.setTitle(this.title);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.currentFrag instanceof ConnFrag) {
            return super.onPrepareOptionsMenu(menu);
        }
//        if (this.currentFrag instanceof RemoteFragment) {
//            menu.findItem(R.id.menu_keyboard).setVisible(false);
//        }
        if (this.currentFrag instanceof DeviceFragment) {
            menu.findItem(R.id.menu_refresh).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    private void startClientListenerService() {
        this.serviceIntent = new Intent(this, ClientService.class);
        startService(this.serviceIntent);
        if (!this.bound) {
            bindService(this.serviceIntent, this.serviceConnection, Context.BIND_AUTO_CREATE);
        }
        if (this.clientService == null) {
            this.status = 5;
        }
        updateState();
    }


    private void active() {
        this.handler.removeCallbacksAndMessages(null);
        this.handler.sendEmptyMessage(1);
    }

    private void inactive() {
        this.handler.removeCallbacksAndMessages(null);
        this.handler.sendEmptyMessageDelayed(2, 1000);
    }


    @Override
    public void onNavigationDrawerItemSelected(int i) {
        switch (i) {
            case 0:
                generateRemoteFragment(0);
                return;
            case 1:
                generateRemoteFragment(1);
                return;
            case 2:
                generateRemoteFragment(2);
                return;
            case 3:
                if (TvPreferences.getDeviceInfo(this) != null) {
                    generateConnectionFragment(false);
                    return;
                } else {
                    generateHubListFragment(false);
                    return;
                }
            case 4:

            case 5:
                return;
            default:
                return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_refresh:
                if (isAttached(this.deviceFragment) && RemoteActivity.this.currentFrag == RemoteActivity.this.deviceFragment) {

                    RemoteActivity.this.deviceFragment.showNoDeviceLay(RemoteActivity.this);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (activityVisible) {
                                RemoteActivity.this.deviceFragment.restartSearching();
                            }
                        }
                    },2000);

                }
                return true;
//
//            case R.id.menu_keyboard:
//                if (isAttached(this.remoteFragment)) {
//                    this.remoteFragment.toggleKeyboard();
//                }
//                return true;
//            case R.id.menu_action_connection:
//                if (currentFrag instanceof RemoteFragment) {
//                    createAndShowConnectionFragment(false);
//                }
//                return true;
            default:
                return false;
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.currentFrag instanceof RemoteFragment) {
            switch (i) {
                case 24:
                case 25:
                    if (this.clientService == null || !this.configured) {
                        return true;
                    }
                    this.clientService.sendKeyEvent(i, 0);
                    return true;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (this.currentFrag instanceof RemoteFragment) {
            switch (i) {
                case 24:
                case 25:
                    if (this.clientService != null && this.configured) {
                        this.clientService.sendKeyEvent(i, 1);
                    }
                    return true;
            }
        }
        return super.onKeyUp(i, keyEvent);
    }

    @Override
    public void onDevicesUpdated() {  updateActionBarTitle(getString(R.string.android_tv));

        if (this.devicesUpdatedEvents % 10 == 0) {
        }
        this.devicesUpdatedEvents++;
    }

    @Override
    public void onNoConnectivity() {  updateActionBarTitle(getString(R.string.android_tv));
    }

    @Override
    public void onNoDevices() { updateActionBarTitle(getString(R.string.android_tv));
    }

    @Override
    public void onDeviceSelected(DevicesInfoUtil devicesInfoUtil) {

        TvPreferences.saveDeviceInfo(this, devicesInfoUtil);
        startClientListenerService();

    }

    @Override
    public void onPairingCompleted(String str) {

            if (this.clientService != null) {
            this.clientService.setPairingSecret(str);
        }

        generateConnectionFragment(false);
    }

    @Override
    public void onPairingCancelled() {

        if (this.clientService != null) {
            this.clientService.cancelPairing();
        }
                forgetDevice();
                cancelDisconnect();
    }

    @Override
    public void onStoreExtractedText(ExtractedText extractedText) {
        this.imeExtracted = extractedText;
    }

    @Override
    public void onMicPermissionDenied() {
        Snackbar action = Snackbar.make(this.rootView, (int) R.string.allow_permission, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.setting, new View.OnClickListener() {
            public void onClick(View view) {
                RemoteActivity.this.startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:" + RemoteActivity.this.getPackageName())));
            }
        });
        ((TextView) action.getView().findViewById(R.id.snackbar_text)).setMaxLines(getResources().getInteger(R.integer.snack_lines));
        action.show();
    }

    @Override
    public ExtractedText getImeExtracted() {
        return this.imeExtracted;
    }

    @Override
    public EditorInfo getImeEditor() {
        return this.imeEditor;
    }

    public void forgetDevice() {
        TvPreferences.saveDeviceInfo(getApplicationContext(), null);
    }

    public void cancelDisconnect() {

            this.imeExtracted = null;
            this.imeEditor = null;
            this.isOrientChanged = false;
            if (this.clientService != null) {
                this.clientService.disconnect();
            }
            this.navigationFragment.selectItem(3);

    }

    public boolean reconnect() {
        if (TvPreferences.getDeviceInfo(this) == null) {
            return false;
        }
        startClientListenerService();
        return true;
    }

    public int getTheStatus() {
        if (this.status != 6) {
            return this.status;
        }
        if (this.clientService == null) {
            this.status = 7;
            return this.status;
        }
        switch (m1()[this.clientService.getStatus().ordinal()]) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            default:
                return 7;
        }
    }

    private boolean isAttached(Fragment fragment) {
        if (fragment == null || fragment.isRemoving() || fragment.isDetached() || fragment.getView() == null) {
            return false;
        }
        return true;
    }

    private void generatePairingFragment() {
        if (!(this.currentFrag instanceof PairingFragment)) {
            if (this.pairingFragment == null) {
                this.pairingFragment = new PairingFragment();
            }
            switchFragment(this.pairingFragment);
        }
    }

    public void generateHubListFragment(boolean z) {
        if (!(this.currentFrag instanceof DeviceFragment) || z) {
            if (this.deviceFragment == null) {
                this.deviceFragment = new DeviceFragment();
            }
            this.devicesUpdatedEvents = 0;
            switchFragment(this.deviceFragment);
        }
    }

    public void generateConnectionFragment(boolean z) {
        if (!(this.currentFrag instanceof ConnFrag) || z) {
            if (this.connFrag == null) {
                this.connFrag = new ConnFrag();
            }
            inactive();
            switchFragment(this.connFrag);
            return;
        }
        updateConnectionFragmentUI();
    }

    public void generateRemoteFragment(int i) {
        this.layoutSelected = i;
        TvPreferences.setControl(this, this.layoutSelected);
        if (this.remoteFragment != null) {
        } else {
            this.remoteFragment = new RemoteFragment();
        }
        this.remoteFragment.attachRemoteInterface(this.clientService);
        active();
        switchFragment(this.remoteFragment);
    }

    private void updateState() {
        int status = getTheStatus();
        if (this.lastConnectionStatus != status) {
            this.lastConnectionStatus = status;
            updateStateOnStatus(status);
        }
    }


    private void updateStateOnStatus(int i) {
        switch (i) {
            case 1:
                switch (this.layoutSelected) {
                    case 0:
                        selectNavigationDrawerFragmentItem(0);
                        break;
                    case 1:
                        selectNavigationDrawerFragmentItem(1);
                        break;
                    case 2:
                        selectNavigationDrawerFragmentItem(2);
                        break;
                }
                if (this.activityVisible && this.configured) {
                    active();
                    break;
                }
            case 2:
            default:
                break;
            case 3:
                this.configured = false;
                if (!(this.currentFrag instanceof RemoteFragment)) {
                    forgetDevice();
                    cancelDisconnect();
                    break;
                } else {
                    break;
                }
            case 4:
            case 7:
                this.configured = false;
                cancelDisconnect();
                break;
            case 5:
            case 6:
                break;
        }
        if (this.currentFrag instanceof ConnFrag) {
            updateConnectionFragmentUI();
        }
       updateActionBarTitle();
    }

    private void updateTheActionBar() {
        updateActionBarTitle();
        invalidateOptionsMenu();
    }

    private void updateActionBarTitle() {

        DevicesInfoUtil devices1;
        DevicesInfoUtil devices2;
        String string = getString(R.string.android_tv);
        if ((this.currentFrag instanceof RemoteFragment) && (devices2 = TvPreferences.getDeviceInfo(this)) != null) {
            string = getString(R.string.android_tv)/*, new Object[]{deviceInfo2.getName()})*/;
        }
        if (this.currentFrag instanceof DeviceFragment) {
            string = getString(R.string.android_tv);
        }
        if (this.currentFrag instanceof ConnFrag) {
            if (getTheStatus() == 2) {
                string = getString(R.string.android_tv);
            } else if (getTheStatus() == 1 && (devices1 = TvPreferences.getDeviceInfo(this)) != null) {
                string = getString(R.string.android_tv)/*, new Object[]{deviceInfo.getName()})*/;
            }
        }

        updateActionBarTitle(string);
    }

    private void updateActionBarTitle(String str) {
        if (!TextUtils.equals(str, this.title)) {
            this.title = str;
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setTitle(str);
            }
        }
    }

    private void updateConnectionFragmentUI() {
        if (this.activityVisible && isAttached(this.connFrag) && (this.currentFrag instanceof ConnFrag)) {
            this.connFrag.uiState(getTheStatus());
        }
    }

    private void selectNavigationDrawerFragmentItem(int i) {
        if (this.activityVisible && isAttached(this.navigationFragment)) {
            this.navigationFragment.selectItem(i);
        }
    }

    private void switchFragment(Fragment fragment) {
        if (!this.activityVisible) {
            Log.w(TAG, "Called switchFragment() when activity is not visible.");
            this.currentFrag = null;
            return;
        }
        this.currentFrag = fragment;

        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, fragment).commitAllowingStateLoss();
        updateTheActionBar();
    }


    private boolean currentFragmentSupportsLandscape() {
        if ((this.currentFrag instanceof ConnFrag)) {
            return true;
        }
        return this.currentFrag instanceof DeviceFragment;
    }

    private void recreateLandscapeFragment() {
        if (this.currentFrag instanceof ConnFrag) {
            this.connFrag = null;
            generateConnectionFragment(true);
        } else if (this.currentFrag instanceof DeviceFragment) {
            this.deviceFragment = null;
            generateHubListFragment(true);
        } else {
            Log.e(TAG, "Should not recreate current fragment for landscape.");
        }
    }

    private void checkLocPermission() {
        boolean z = false;
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
            z = true;
        }
        if (!z) {
            startActivity(new Intent(this, PermActivity.class));
            finish();
        }
    }


    private static int[] m1() {
        if (e1 != null) {
            return e1;
        }
        int[] iArr = new int[ClientService.Status.values().length];
        try {
            iArr[ClientService.Status.CONNECTED.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ClientService.Status.CONNECTING.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ClientService.Status.DISCONNECTED.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ClientService.Status.NO_CONNECTION.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        e1 = iArr;
        return iArr;
    }

    public void registerReceiver()
    {
        try {

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
            registerReceiver(this.receiver, intentFilter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void unRegister(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                unregisterReceiver(receiver);
            }catch (Exception ignored){
            }
        }else {
            try {
                unregisterReceiver(receiver);
            }catch (Exception e){
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        forgetDevice();
        cancelDisconnect();
        unRegister();
    }

    @Override
    public void onResume() {
        boolean z = true;
        super.onResume();
        checkLocPermission();
        this.activityVisible = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        registerReceiver(this.broadcastReceiver, intentFilter);

        this.lastConnectionStatus = 0;
        if (this.clientService != null) {
            active();
        }
        if (!z && !reconnect() && this.currentFrag == null) {
            updateStateOnStatus(getTheStatus());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.currentFrag instanceof RemoteFragment) {
            this.remoteFragment = null;
            this.isOrientChanged = true;
            generateRemoteFragment(this.layoutSelected);
        } else if (currentFragmentSupportsLandscape()) {
            recreateLandscapeFragment();
        }
    }

    @Override
    public void onPause() {
        this.activityVisible = false;
        if (isAttached(this.remoteFragment) && this.remoteFragment.isRecording()) {
            this.remoteFragment.stopVoice();
        }
        if (this.clientService != null) {
            inactive();
            if (this.bound) {
                unbindService(this.serviceConnection);
                this.bound = false;
            }
        }
        if (TvPreferences.RETAIN_SERVICE_INSTANCE) {

        } else {
            stopService(this.serviceIntent);
        }
        unregisterReceiver(this.broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (this.navigationFragment.isDrawerOpen() && currentFrag instanceof ConnFrag) {
            this.navigationFragment.setDrawerState(false);
            generateRemoteFragment(0);
        } else if (this.currentFrag instanceof PairingFragment) {
            generateConnectionFragment(false);
        } else if (this.currentFrag instanceof ConnFrag) {
            if (getTheStatus() == 2) {

                        Intent intent = new Intent(RemoteActivity.this, FirstActivity.class);
                        startActivity(intent);
                        finish();




            } else {
                updateStateOnStatus(getTheStatus());
            }
        } else if (this.currentFrag instanceof DeviceFragment) {


                    Intent intent = new Intent(RemoteActivity.this, FirstActivity.class);
                    startActivity(intent);
                    finish();
        }

        else if(this.currentFrag instanceof RemoteFragment)
        {


                    Intent intent = new Intent(RemoteActivity.this, FirstActivity.class);
                    startActivity(intent);
                    finish();
        }
    }

}
