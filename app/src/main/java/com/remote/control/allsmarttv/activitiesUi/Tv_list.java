package com.remote.control.allsmarttv.activitiesUi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.remote.control.allsmarttv.R;
import com.remote.control.allsmarttv.appUi.FirstActivity;
import com.remote.control.allsmarttv.utils.Remote_model;
import com.remote.control.allsmarttv.utils.RemoteAdapter;
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass;

import java.util.ArrayList;

public class Tv_list extends AppCompatActivity {

    ArrayList<Remote_model> arrayList;
    RecyclerView recyclerView;
    RemoteAdapter tv_list_adapter;
    EditText searchView;
    ImageView back_button, search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportedClass.loadLangLocale(getBaseContext());
        setContentView(R.layout.activity_remote_list);

        recyclerView = findViewById(R.id.select_remote_recycler);
        searchView = findViewById(R.id.searchView);
        back_button = findViewById(R.id.back_button_list);
        search = findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        arrayList = new ArrayList<>();
        arrayList.add(new Remote_model("SONY Bravia TV - Android"));
        arrayList.add(new Remote_model("SONY Bravia"));
        arrayList.add(new Remote_model("SONY Bravia TV"));
        arrayList.add(new Remote_model("Android TV Remote"));
        arrayList.add(new Remote_model("Roku TV Remote"));
        arrayList.add(new Remote_model("LG Smart TV - WebOS"));
        arrayList.add(new Remote_model("TCL TV - Android"));
        arrayList.add(new Remote_model("TCL TV - Roku"));
        arrayList.add(new Remote_model("SHARP Aquos - Android"));
        arrayList.add(new Remote_model("SHARP Aquos - Roku"));
        arrayList.add(new Remote_model("AOC TV - Roku"));
        arrayList.add(new Remote_model("Hisense TV - Roku"));
        arrayList.add(new Remote_model("Insignia TV - Roku"));
        arrayList.add(new Remote_model("Roku Express + Roku Media Player"));
        arrayList.add(new Remote_model("PHILIPS TV - Android"));
        arrayList.add(new Remote_model("Arcelik TV - Android"));
        arrayList.add(new Remote_model("Vestel TV - Android"));
        arrayList.add(new Remote_model("Sanyo TV - Roku"));
        arrayList.add(new Remote_model("Element TV - Roku"));
        arrayList.add(new Remote_model("JVC TV - Roku"));
        arrayList.add(new Remote_model("RCA TV - Roku"));
        arrayList.add(new Remote_model("Magnavox TV - Roku"));
        arrayList.add(new Remote_model("Haier TV - Roku"));
        arrayList.add(new Remote_model("PHILIPS TV - Roku"));
        arrayList.add(new Remote_model("Razor Forge TV - Android"));
        arrayList.add(new Remote_model("LeEco - Android"));
        arrayList.add(new Remote_model("Google Nexus - Android"));
        arrayList.add(new Remote_model("Xiaomi Mi Box - Android"));
        arrayList.add(new Remote_model("LMT TV iekārta - Android"));
        arrayList.add(new Remote_model("Nvidia Shield - Android"));
        arrayList.add(new Remote_model("LEONET LifeStick - Android"));
        arrayList.add(new Remote_model("Toshiba TV - Android"));
        arrayList.add(new Remote_model("Sanyo TV - Android"));
        arrayList.add(new Remote_model("Skyworth TV - Android"));
        arrayList.add(new Remote_model("Westinghouse TV - Roku"));
        arrayList.add(new Remote_model("Westinghouse TV - Android"));
        arrayList.add(new Remote_model("Thomson TV - Android"));
        arrayList.add(new Remote_model("BAUHN TV - Android"));
        arrayList.add(new Remote_model("Infomir MAGic Box - Android"));
        arrayList.add(new Remote_model("Vodafone TV - Android"));
        arrayList.add(new Remote_model("KAON 4K - Android"));
        arrayList.add(new Remote_model("FreeBox Mini 4K - Android"));
        arrayList.add(new Remote_model("Tsuyata Stick - Android"));
        arrayList.add(new Remote_model("1und1 - Android"));
        arrayList.add(new Remote_model("Aconatic - Android"));
        arrayList.add(new Remote_model("Aiwa TV - Android"));
        arrayList.add(new Remote_model("ANAM - Android"));
        arrayList.add(new Remote_model("Anker - Android"));
        arrayList.add(new Remote_model("ASANZO - Android"));
        arrayList.add(new Remote_model("Asus - Android"));
        arrayList.add(new Remote_model("Ayonz - Android"));
        arrayList.add(new Remote_model("BenQ - Android"));
        arrayList.add(new Remote_model("Blaupunkt - Android"));
        arrayList.add(new Remote_model("Casper - Android"));
        arrayList.add(new Remote_model("CG - Android"));
        arrayList.add(new Remote_model("Changhong - Android"));
        arrayList.add(new Remote_model("Chimei - Android"));
        arrayList.add(new Remote_model("CHiQ - Android"));
        arrayList.add(new Remote_model("Condor - Android"));
        arrayList.add(new Remote_model("Dish TV - Android"));
        arrayList.add(new Remote_model("Eko - Android"));
        arrayList.add(new Remote_model("Elsys - Android"));
        arrayList.add(new Remote_model("Ematic - Android"));
        arrayList.add(new Remote_model("ENTV - Android"));
        arrayList.add(new Remote_model("EPSON - Android"));
        arrayList.add(new Remote_model("ESTLA - Android"));
        arrayList.add(new Remote_model("Foxcom - Android"));
        arrayList.add(new Remote_model("FPT Play - Android"));
        arrayList.add(new Remote_model("Funai - Android"));
        arrayList.add(new Remote_model("Globe Telecom - Android"));
        arrayList.add(new Remote_model("Haier - Android"));
        arrayList.add(new Remote_model("Hansung - Android"));
        arrayList.add(new Remote_model("Hisense - Android"));
        arrayList.add(new Remote_model("HORIZON - Android"));
        arrayList.add(new Remote_model("iFFalcon - Android"));
        arrayList.add(new Remote_model("Infinix - Android"));
        arrayList.add(new Remote_model("Iriver - Android"));
        arrayList.add(new Remote_model("Itel - Android"));
        arrayList.add(new Remote_model("JBL - Android"));
        arrayList.add(new Remote_model("JVC - Android"));
        arrayList.add(new Remote_model("KIVI - Android"));
        arrayList.add(new Remote_model("KODAK - Android"));
        arrayList.add(new Remote_model("Kogan - Android"));
        arrayList.add(new Remote_model("KOODA - Android"));
        arrayList.add(new Remote_model("Linsar - Android"));
        arrayList.add(new Remote_model("Llyod - Android"));
        arrayList.add(new Remote_model("LUCOMS - Android"));
        arrayList.add(new Remote_model("Marcel - Android"));
        arrayList.add(new Remote_model("MarQ - Android"));
        arrayList.add(new Remote_model("Mediabox - Android"));
        arrayList.add(new Remote_model("Micromax - Android"));
        arrayList.add(new Remote_model("Motorola - Android"));
        arrayList.add(new Remote_model("MyBox - Android"));
        arrayList.add(new Remote_model("Nokia - Android"));
        arrayList.add(new Remote_model("OnePlus - Android"));
        arrayList.add(new Remote_model("Orange - Android"));
        arrayList.add(new Remote_model("Panasonic - Android"));
        arrayList.add(new Remote_model("PIXELA - Android"));
        arrayList.add(new Remote_model("Polaroid - Android"));
        arrayList.add(new Remote_model("PRISM Korea - Android"));
        arrayList.add(new Remote_model("RCA - Android"));
        arrayList.add(new Remote_model("RFL Electronics - Android"));
        arrayList.add(new Remote_model("Robi Axiata - Android"));
        arrayList.add(new Remote_model("Sceptre - Android"));
        arrayList.add(new Remote_model("Seiki - Android"));
        arrayList.add(new Remote_model("SFR - Android"));
        arrayList.add(new Remote_model("SMARTEVER - Android"));
        arrayList.add(new Remote_model("SONIQ Australia - Android"));
        arrayList.add(new Remote_model("Syinix - Android"));
        arrayList.add(new Remote_model("Telekom Malaysia - Android"));
        arrayList.add(new Remote_model("Tempo - Android"));
        arrayList.add(new Remote_model("theham - Android"));
        arrayList.add(new Remote_model("TPV (Philips EMEA) - Android"));
        arrayList.add(new Remote_model("Truvii - Android"));
        arrayList.add(new Remote_model("Turbo-X - Android"));
        arrayList.add(new Remote_model("UMAX - Android"));
        arrayList.add(new Remote_model("Videostrong - Android"));
        arrayList.add(new Remote_model("VinSmart - Android"));
        arrayList.add(new Remote_model("VU Television - Android"));
        arrayList.add(new Remote_model("Walton - Android"));
        arrayList.add(new Remote_model("Witooth - Android"));
        arrayList.add(new Remote_model("XGIMI Technology - Android"));
        arrayList.add(new Remote_model("ATVIO - Roku"));
        arrayList.add(new Remote_model("InFocus - Roku"));
        arrayList.add(new Remote_model("Element - Roku"));
        arrayList.add(new Remote_model("Hitachi - Roku"));
        arrayList.add(new Remote_model("Onn - Roku"));
        arrayList.add(new Remote_model("Polaroid - Roku"));
        arrayList.add(new Remote_model("Daewoo - Android"));
        arrayList.add(new Remote_model("Kalley - Android"));
        arrayList.add(new Remote_model("Ecostar - Android"));
        arrayList.add(new Remote_model("Coocaa - Android"));
        arrayList.add(new Remote_model("Hathway - Android"));
        arrayList.add(new Remote_model("HQ - Android"));
        arrayList.add(new Remote_model("Konka - Android"));
        arrayList.add(new Remote_model("Premier - Android"));
        arrayList.add(new Remote_model("Riviera - Android"));
        arrayList.add(new Remote_model("EON Smart Box - Android"));
        arrayList.add(new Remote_model("B UHD - Android"));
        arrayList.add(new Remote_model("Artel - Android"));
        arrayList.add(new Remote_model("Metz - Android"));
        arrayList.add(new Remote_model("Orient - Android"));
        arrayList.add(new Remote_model("Mystery - Android"));
        arrayList.add(new Remote_model("ELENBERG - Android"));
        arrayList.add(new Remote_model("Prestigio - Android"));
        arrayList.add(new Remote_model("TIM Vision Box - Android"));
        arrayList.add(new Remote_model("Philco - Android"));
        arrayList.add(new Remote_model("Hi Level - Android"));
        arrayList.add(new Remote_model("Ghia - Android"));
        arrayList.add(new Remote_model("Iris - Android"));
        arrayList.add(new Remote_model("Sunny - Android"));
        arrayList.add(new Remote_model("Nasco - Android"));
        arrayList.add(new Remote_model("Caixun - Android"));
        arrayList.add(new Remote_model("Prestiz - Android"));
        arrayList.add(new Remote_model("Axen - Android"));
        arrayList.add(new Remote_model("Noblex - Android"));
        arrayList.add(new Remote_model("Indurama - Android"));
        arrayList.add(new Remote_model("Sansui - Android"));
        arrayList.add(new Remote_model("Stream - Android"));
        arrayList.add(new Remote_model("Onida - Android"));
        arrayList.add(new Remote_model("Sinotec - Android"));
        arrayList.add(new Remote_model("Polytron - Android"));
        arrayList.add(new Remote_model("RealMe - Android"));
        arrayList.add(new Remote_model("Vitron - Android"));

        tv_list_adapter = new RemoteAdapter(this, this);
        tv_list_adapter.setList(arrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(tv_list_adapter);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                tv_list_adapter.getFilter().filter(s);
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Tv_list.this, FirstActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Tv_list.this, FirstActivity.class);
        startActivity(intent);
        finish();
    }
}