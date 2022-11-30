package com.remote.control.allsmarttv.Fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.remote.control.allsmarttv.R;


public class NavFragment extends Fragment {

    private static final float DISABLED_ALPHA = 0.3f;
    private static final float ENABLED_ALPHA = 1.0f;
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private ListView actionListView;
    private NavigationDrawerCallbacks callbacks;
    private int currentSelectedPosition = 3;
    private int currentStatus = 7;
    private DrawerLayout drawerLay;
    private ActionBarDrawerToggle drawerToggle;
    private ListView feedbackHelpListView;
    private boolean fromSavedInstanceState;
    private ListView modeListView;
    private ListView settingListView;
    private boolean takingBugReport = false;
    private boolean userLearnedDrawer;

    public interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int i);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.userLearnedDrawer = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(PREF_USER_LEARNED_DRAWER, false);
        if (bundle != null) {
            this.currentSelectedPosition = bundle.getInt(STATE_SELECTED_POSITION);
            this.fromSavedInstanceState = true;
        }
        selectItem(this.currentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_nav, viewGroup, false);
    }

    public void set(DrawerLayout drawerLayout, ActionBar actionBar, boolean z) {
        int i;
        View view = getView();
        this.modeListView = (ListView) view.findViewById(R.id.mode);
        this.modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                NavFragment.this.selectItem(i + 0);
            }
        });
        this.modeListView.setAdapter((ListAdapter) new ArrayAdapter<String>(actionBar.getThemedContext(), R.layout.drawer_item, R.id.text, new String[]{getString(R.string.dpad), getString(R.string.touchpad_second)}) {


            public View getView(int i, View view, ViewGroup viewGroup) {
                View view2 = super.getView(i, view, viewGroup);
                ImageView imageView = (ImageView) view2.findViewById(R.id.icon);
                switch (i) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
                View findViewById = view2.findViewById(R.id.text);
                switch (NavFragment.this.currentStatus) {
                    case 1:
                        imageView.setAlpha(NavFragment.ENABLED_ALPHA);
                        findViewById.setAlpha(NavFragment.ENABLED_ALPHA);
                        break;
                    default:
                        imageView.setAlpha(NavFragment.DISABLED_ALPHA);
                        findViewById.setAlpha(NavFragment.DISABLED_ALPHA);
                        break;
                }
                return view2;
            }

            public boolean isEnabled(int i) {
                return NavFragment.this.currentStatus == 1;
            }
        });
        this.actionListView = (ListView) view.findViewById(R.id.remote);
        this.actionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (i == 0) {
                    NavFragment.this.takingBugReport = true;
                    NavFragment.this.actionListView.setItemChecked(i, false);
                    if (NavFragment.this.drawerLay != null) {
                        NavFragment.this.drawerLay.closeDrawer(GravityCompat.START);
                    }
                }
            }
        });
        this.actionListView.setAdapter((ListAdapter) new ArrayAdapter<String>(actionBar.getThemedContext(), R.layout.drawer_item, R.id.text, new String[]{getString(R.string.bug_report)}) {

            public View getView(int i, View view, ViewGroup viewGroup) {
                View view2 = super.getView(i, view, viewGroup);
                ImageView imageView = (ImageView) view2.findViewById(R.id.icon);
                switch (i) {
                    case 0:
                        break;
                }
                return view2;
            }

            public boolean isEnabled(int i) {
                return !NavFragment.this.takingBugReport;
            }
        });
        this.settingListView = (ListView) view.findViewById(R.id.setting);
        this.settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                NavFragment.this.selectItem(i + 3);
            }
        });
        this.settingListView.setAdapter((ListAdapter) new ArrayAdapter<String>(actionBar.getThemedContext(), R.layout.drawer_item, R.id.text, new String[]{getString(R.string.manage)}) {

            public View getView(int i, View view, ViewGroup viewGroup) {
                View view2 = super.getView(i, view, viewGroup);
                ImageView imageView = (ImageView) view2.findViewById(R.id.icon);
                switch (i) {
                    case 0:
                        break;
                }
                return view2;
            }
        });
        if (z) {
            i = R.string.help;
        } else {
            i = R.string.feedback;
        }
        String[] strArr = z ? new String[]{getString(i)} : new String[]{getString(i), getString(R.string.about)};
        this.feedbackHelpListView = (ListView) view.findViewById(R.id.feedback);
        this.feedbackHelpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                NavFragment.this.selectItem(i + 4);
            }
        });
        this.feedbackHelpListView.setAdapter((ListAdapter) new ArrayAdapter<String>(actionBar.getThemedContext(), R.layout.drawer_item, R.id.text, strArr) {


            public View getView(int i, View view, ViewGroup viewGroup) {
                View view2 = super.getView(i, view, viewGroup);
                ImageView imageView = (ImageView) view2.findViewById(R.id.icon);
                switch (i) {
                    case 0:
                        break;
                    case 1:
                        break;
                }
                return view2;
            }
        });
        if (this.currentSelectedPosition < 3) {
            this.modeListView.setItemChecked(this.currentSelectedPosition, true);
        } else if (this.currentSelectedPosition == 3) {
            this.settingListView.setItemChecked(0, true);
        } else if (this.currentSelectedPosition == 5) {
            this.feedbackHelpListView.setItemChecked(1, true);
        }
        this.drawerLay = drawerLayout;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        this.drawerToggle = new ActionBarDrawerToggle(getActivity(), this.drawerLay, R.string.open, R.string.close) {


            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (NavFragment.this.isAdded()) {
                    NavFragment.this.getActivity().supportInvalidateOptionsMenu();
                }
            }

            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                if (NavFragment.this.isAdded()) {
                    if (!NavFragment.this.userLearnedDrawer) {
                        NavFragment.this.userLearnedDrawer = true;
                        PreferenceManager.getDefaultSharedPreferences(NavFragment.this.getActivity()).edit().putBoolean(NavFragment.PREF_USER_LEARNED_DRAWER, true).apply();
                    }
                    NavFragment.this.getActivity().supportInvalidateOptionsMenu();
                }
            }
        };
        if (!this.userLearnedDrawer && !this.fromSavedInstanceState) {
            this.drawerLay.openDrawer(GravityCompat.START);
        }
        this.drawerLay.post(new Runnable() {

            public void run() {
                NavFragment.this.drawerToggle.syncState();
            }
        });
        this.drawerLay.setDrawerListener(this.drawerToggle);
    }

    public void selectItem(int i) {
        this.currentSelectedPosition = i;
        if (!(this.modeListView == null || this.settingListView == null)) {
            if (this.currentSelectedPosition < 3) {
                this.modeListView.setItemChecked(this.currentSelectedPosition, true);
                this.settingListView.setItemChecked(0, false);
                this.feedbackHelpListView.setItemChecked(1, false);
            } else if (this.currentSelectedPosition == 3) {
                this.modeListView.setItemChecked(0, false);
                this.modeListView.setItemChecked(1, false);
                this.modeListView.setItemChecked(2, false);
                this.settingListView.setItemChecked(0, true);
                this.feedbackHelpListView.setItemChecked(1, false);
            } else if (this.currentSelectedPosition == 5) {
                this.modeListView.setItemChecked(0, false);
                this.modeListView.setItemChecked(1, false);
                this.modeListView.setItemChecked(2, false);
                this.settingListView.setItemChecked(0, false);
                this.feedbackHelpListView.setItemChecked(1, true);
            }
            this.feedbackHelpListView.setItemChecked(0, false);
        }
        if (this.drawerLay != null) {
            this.drawerLay.closeDrawer(GravityCompat.START);
        }
        if (this.callbacks != null) {
            this.callbacks.onNavigationDrawerItemSelected(i);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.callbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.callbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(STATE_SELECTED_POSITION, this.currentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.drawerToggle.onConfigurationChanged(configuration);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (this.drawerToggle.onOptionsItemSelected(menuItem)) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public boolean isDrawerOpen() {
        return this.drawerLay.isDrawerOpen(GravityCompat.START);
    }

    public void setDrawerState(boolean z) {
        if (z) {
            this.drawerLay.openDrawer(GravityCompat.START);
        } else {
            this.drawerLay.closeDrawer(GravityCompat.START);
        }
    }

    public void onDeveloperStatus(boolean z) {
        this.actionListView.setVisibility(z ? View.VISIBLE : View.GONE);
    }
}
