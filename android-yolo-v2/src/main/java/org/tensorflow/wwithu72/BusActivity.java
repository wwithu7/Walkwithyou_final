package org.tensorflow.wwithu72;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.tensorflow.wwithu72.view.ClassifierActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.odsay.odsayandroidsdk.ODsayService;

public class BusActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {
    //
    private GestureLibrary gestureLib;

    //
    private Button thisStation;
    //private TextView tv_data;
    private Button button_input;
    private TextView text_this;
    private Button thisBus;
    private TextView text_thisBus;
    private TextView text_prev;
    private TextView text_next;
    private TextView text_time,text_view,text_view3, text_view4;

    public double longitude, latitude;
    LocationManager locationManager;
    private Context context;
    private String spinnerSelectedName;
    String s="";
    AppLocationService appLocationService;
    private ODsayService odsayService;
    private JSONObject jsonObject;


    public static String lat = "";
    public static String lon = "";

    private EditText edit_busno;

    private String busNo;

    int flag=0;
    int index;
    int turn_on=0;
    ArrayList<String> busstop = new ArrayList<String>();

    //record play
    final int REQUEST_PERMISSION_CODE = 1000;
    public static final String TAG = "RecordPlayActivity";
    public boolean exitState;
    public boolean first = true;
    //earphone
    public static int lock=-1;
    private MusicIntentReceiver myReceiver;
    public static String x="126.95584930";
    public static String y="37.53843986";
    String firststation="";
    boolean arsId;
    boolean dist;
    boolean stationId;
    boolean stationNm;
    boolean arrmsg1;
    boolean arrmsg2;
    boolean nxtStn;
    boolean adirection;
    boolean rtNm;
    // boolean stationNm1;
    boolean sectnm;
    boolean flagg=false;
    int armidt=0;

    SharedPreferences recPlay;
    ArrayList<String> arsIds;
    ArrayList<String> dists;
    ArrayList<String> stationIds;
    ArrayList<String> stationNms;
    // ArrayList<String> stationNm1s;
    ArrayList<String> arrmsg1s;
    ArrayList<String> arrmsg2s;
    ArrayList<String> nxtStns;
    ArrayList<String> adirections;
    ArrayList<String> rtNms;
    String sectnms;
    InputMethodManager imm;
    public void onBackPressed() {
        SharedPreferences tuto =
                getSharedPreferences("Tutorial", Activity.MODE_PRIVATE);
        SharedPreferences spref = getSharedPreferences("isFirsts", Activity.MODE_PRIVATE);
        if(tuto.getInt("tutorial", 0)==1){
            //on
            FirstActivity.button2.setContentDescription(getResources().getString(R.string.Tmap_button));
            FirstActivity.button1.setContentDescription(getResources().getString(R.string.Tcamera_button));
            FirstActivity.button4.setContentDescription(getResources().getString(R.string.Tsetting_button));
            FirstActivity.button3.setContentDescription(getResources().getString(R.string.Tbus_button));
            FirstActivity.fcontainer.setContentDescription(getResources().getString(R.string.Tfirstscreen));
        }else{
            //off
            FirstActivity.button2.setContentDescription(getResources().getString(R.string.map_button));
            FirstActivity.button1.setContentDescription(getResources().getString(R.string.camera_button));
            FirstActivity.button4.setContentDescription(getResources().getString(R.string.setting_button));
            FirstActivity.button3.setContentDescription(getResources().getString(R.string.bus_button));
            FirstActivity.fcontainer.setContentDescription(getResources().getString(R.string.firstscreen));
        }
        if(!spref.getBoolean("isFirsts", false)){ FirstActivity.button2.setContentDescription(getResources().getString(R.string.Tmap_button));
            FirstActivity.button1.setContentDescription(getResources().getString(R.string.Tcamera_button));
            FirstActivity.button4.setContentDescription(getResources().getString(R.string.Tsetting_button));
            FirstActivity.button3.setContentDescription(getResources().getString(R.string.Tbus_button));
            FirstActivity.fcontainer.setContentDescription(getResources().getString(R.string.Tfirstscreen));
        }
        super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("aaa","enter bus create");
        super.onCreate(savedInstanceState);
        //keyboard
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        appLocationService = new AppLocationService(BusActivity.this);
        SharedPreferences gesture =
                getSharedPreferences("Gesture", Activity.MODE_PRIVATE);
        if(gesture.getInt("gesture", 0)==1) {
            GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
            View inflate = getLayoutInflater().inflate(R.layout.activity_bus, null);
            gestureOverlayView.addView(inflate);
            gestureOverlayView.addOnGesturePerformedListener(this);
            gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
            if (!gestureLib.load())
                finish();
            setContentView(gestureOverlayView);
        }else{
            setContentView(R.layout.activity_bus);
        }
        recPlay = getSharedPreferences("Sound", Activity.MODE_PRIVATE);
        //record play setting
        first = false;
        exitState = true;

        myReceiver = new MusicIntentReceiver();
        View bcontainer = findViewById(R.id.bcontainer);
        Button sett = (Button) findViewById(R.id.bus_set);
        Button map = (Button)findViewById(R.id.bus_map);
        Button cam = (Button)findViewById(R.id.bus_cam);
        Button bus = (Button)findViewById(R.id.bus_bus);
        thisStation = (Button) findViewById(R.id.thisStation);
        //tv_data = (TextView) findViewById(R.id.tv_data);
        text_this = (TextView) findViewById(R.id.text_thisStation);
        //textview scroll event
        text_this.setMovementMethod(ScrollingMovementMethod.getInstance());

        thisBus = (Button) findViewById(R.id.thisBus);
        thisBus.setEnabled(false);
        text_thisBus = (TextView) findViewById(R.id.text_thisBus);
        //    text_prev = (TextView) findViewById(R.id.text_prev);
        text_next = (TextView) findViewById(R.id.text_next);
        //   text_view = (TextView)findViewById(R.id.textView);
        text_view3 = (TextView)findViewById(R.id.textView3);
        text_view4 = (TextView)findViewById(R.id.textview4);
        button_input = (Button) findViewById(R.id.bt_input);
        button_input.setEnabled(false);
        edit_busno = (EditText) findViewById(R.id.edit_busno);


        text_time=(TextView)findViewById(R.id.text_time);
        bus.setEnabled(false);
        SharedPreferences tuto =
                getSharedPreferences("Tutorial", Activity.MODE_PRIVATE);
        SharedPreferences spref = getSharedPreferences("isFirsts", Activity.MODE_PRIVATE);
        SharedPreferences soundPref = getSharedPreferences("isFirstSound", Activity.MODE_PRIVATE);
        if(tuto.getInt("tutorial", 0)==1){
            sett.setContentDescription(getResources().getString(R.string.Tsetting_button));
            map.setContentDescription(getResources().getString(R.string.Tmap_button));
            cam.setContentDescription(getResources().getString(R.string.Tcamera_button));
            bus.setContentDescription(getResources().getString(R.string.Tbus_button));
            thisStation.setContentDescription(getResources().getString(R.string.TthisStation));
            thisBus.setContentDescription(getResources().getString(R.string.TbusList));
            edit_busno.setContentDescription(getResources().getString(R.string.write));
            button_input.setContentDescription(getResources().getString(R.string.Tinsert));
            //    text_view.setContentDescription(getResources().getString(R.string.Tdirection));
            text_view3.setContentDescription(getResources().getString(R.string.Tnextstop));
            text_view4.setContentDescription(getResources().getString(R.string.Tremaintime));
            bcontainer.setContentDescription(getResources().getString(R.string.Tbusscreen));
        }else{
            sett.setContentDescription(getResources().getString(R.string.setting_button));
            map.setContentDescription(getResources().getString(R.string.map_button));
            cam.setContentDescription(getResources().getString(R.string.camera_button));
            bus.setContentDescription(getResources().getString(R.string.bus_button));
            thisStation.setContentDescription(getResources().getString(R.string.thisStation));
            thisBus.setContentDescription(getResources().getString(R.string.busList));
            edit_busno.setContentDescription(getResources().getString(R.string.write));
            button_input.setContentDescription(getResources().getString(R.string.insert));
            //      text_view.setContentDescription(getResources().getString(R.string.direction));
            text_view3.setContentDescription(getResources().getString(R.string.nextstop));
            text_view4.setContentDescription(getResources().getString(R.string.remaintime));
            bcontainer.setContentDescription(getResources().getString(R.string.busscreen));
        }
        if(!spref.getBoolean("isFirsts", false)){
            sett.setContentDescription(getResources().getString(R.string.Tsetting_button));
            map.setContentDescription(getResources().getString(R.string.Tmap_button));
            cam.setContentDescription(getResources().getString(R.string.Tcamera_button));
            bus.setContentDescription(getResources().getString(R.string.Tbus_button));
            thisStation.setContentDescription(getResources().getString(R.string.TthisStation));
            thisBus.setContentDescription(getResources().getString(R.string.TbusList));
            edit_busno.setContentDescription(getResources().getString(R.string.write));
            button_input.setContentDescription(getResources().getString(R.string.Tinsert));
            //    text_view.setContentDescription(getResources().getString(R.string.Tdirection));
            text_view3.setContentDescription(getResources().getString(R.string.Tnextstop));
            text_view4.setContentDescription(getResources().getString(R.string.Tremaintime));
            bcontainer.setContentDescription(getResources().getString(R.string.Tbusscreen));
        }
        if(!soundPref.getBoolean("isFirstSound", false)){
            Log.d("Is first?", "first");
        }
        sett.setOnClickListener(new View.OnClickListener() { //button1을 눌렀을때~~
            @Override
            public void onClick(View view) { //여기에 눌렀을때의 일어날 일을 적는다.
                Intent intent = new Intent(BusActivity.this,SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("ACTIVITY","bus");
                exitState = false;
                startActivity(intent);
                //Toast.makeText(getApplicationContext(), "버튼이 눌러졌나 ?", Toast.LENGTH_SHORT).show();
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BusActivity.this,MapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                exitState = false;
                startActivity(intent);
            }
        });
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BusActivity.this,ClassifierActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                exitState = false;
                startActivity(intent);
            }
        });
        init();
    }


    public void init() {
        context = this;

        thisBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    public void run() {
                        s = "";
                        //checkGyungibus();

                            getbuslist();

                            for (int i = 0; i < rtNms.size(); i++) {
                                s += rtNms.get(i) + "   ";
                            }
                            text_thisBus.post(new Runnable() {
                                public void run() {
                                    text_thisBus.setText(s);
                                }
                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    button_input.setEnabled(true);
                                }
                            });



                    }


                }.start();



            }
            private String checkGyungibus(){

                String myurl = "http://openapi.gbis.go.kr/ws/rest/busarrivalservice/station?serviceKey=/*your service key*/%3D%3D&stationId="+stationIds.get(0);
                Log.d("url",myurl);

                HttpURLConnection con = null;
                InputStreamReader isr = null;
                BufferedReader br = null;

                try{
                    URL url = new URL(myurl);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-type", "application/xml");
                    con.setConnectTimeout(100000);
                    con.setReadTimeout(1000000);
                    BufferedReader rd;

                    XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = parserCreator.newPullParser();

                    parser.setInput(url.openStream(), null);

                    int parserEvent = parser.getEventType();
                    flagg=false;

                    while (parserEvent != XmlPullParser.END_DOCUMENT){
                        switch(parserEvent){
                            case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                                if(parser.getName().equals("flag")){ //title 만나면 내용을 받을수 있게 하자
                                    flagg=true;
                                }
                                break;
                        }
                        Log.d("flag","flag?");
                        parserEvent = parser.next();
                    }

                }
                catch(Exception e){
                    Log.d("!!!!!!!!!!!!!!!!!!!!","삐에러가났습니다!!");
                }
                finally{
                    if(con != null){
                        try{con.disconnect();}catch(Exception e){}
                    }

                    if(isr != null){
                        try{isr.close();}catch(Exception e){}
                    }

                    if(br != null){
                        try{br.close();}catch(Exception e){}
                    }
                }
                return "";
            }
            private String getbuslist(){

                String myurl = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?serviceKey=/*yourservicekey*/%3D%3D&arsId="+arsIds.get(0);
                Log.d("url",myurl);

                HttpURLConnection con = null;
                InputStreamReader isr = null;
                BufferedReader br = null;

                try{
                    URL url = new URL(myurl);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-type", "application/xml");
                    con.setConnectTimeout(100000);
                    con.setReadTimeout(1000000);
                    BufferedReader rd;

                    XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = parserCreator.newPullParser();

                    parser.setInput(url.openStream(), null);

                    int parserEvent = parser.getEventType();
                    arrmsg1s=new ArrayList<String>();
                    arrmsg2s=new ArrayList<String>();
                    rtNms=new ArrayList<String>();
                    adirections=new ArrayList<String>();
                    nxtStns=new ArrayList<String>();

                    while (parserEvent != XmlPullParser.END_DOCUMENT){
                        switch(parserEvent){
                            case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                                if(parser.getName().equals("arrmsg1")){ //title 만나면 내용을 받을수 있게 하자
                                    arrmsg1 = true;
                                }
                                if(parser.getName().equals("arrmsg2")){ //address 만나면 내용을 받을수 있게 하자
                                    arrmsg2 = true;
                                }
                                if(parser.getName().equals("rtNm")){ //mapx 만나면 내용을 받을수 있게 하자
                                    rtNm = true;
                                }
                                if(parser.getName().equals("adirection")){ //mapy 만나면 내용을 받을수 있게 하자
                                    adirection= true;
                                }
                                if(parser.getName().equals("nxtStn")){ //mapy 만나면 내용을 받을수 있게 하자
                                    nxtStn = true;
                                }
                                if(parser.getName().equals("sectNm")){
                                    sectnm=true;
                                }


                                break;

                            case XmlPullParser.TEXT://parser가 내용에 접근했을때
                                if(arrmsg1){ //isTitle이 true일 때 태그의 내용을 저장.
                                    arrmsg1s.add(parser.getText());
                                    arrmsg1= false;
                                }
                                if(arrmsg2){ //isAddress이 true일 때 태그의 내용을 저장.
                                    arrmsg2s.add (parser.getText());
                                    arrmsg2 = false;

                                }
                                if(rtNm){ //isMapx이 true일 때 태그의 내용을 저장.
                                    rtNms.add(parser.getText());
                                    rtNm = false;
                                }
                                if(nxtStn){ //isMapy이 true일 때 태그의 내용을 저장.
                                    nxtStns.add(parser.getText());
                                    nxtStn = false;
                                }
                                if(adirection){ //isMapy이 true일 때 태그의 내용을 저장.
                                    adirections.add(parser.getText());
                                    adirection = false;
                                }


                                break;
                            case XmlPullParser.END_TAG:
                                break;
                        }
                        parserEvent = parser.next();
                    }
                }
                catch(Exception e){
                    Log.d("!!!!!!!!!!!!!!!!!!!!","삐에러가났습니다!!");
                }
                finally{
                    if(con != null){
                        try{con.disconnect();}catch(Exception e){}
                    }

                    if(isr != null){
                        try{isr.close();}catch(Exception e){}
                    }

                    if(br != null){
                        try{br.close();}catch(Exception e){}
                    }
                }

                return "";

            }
        });
        button_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(edit_busno.getWindowToken(),0);
//                tv_data.post(new Runnable(){
//                    public void run(){
//                        tv_data.setText("");
//                    }
//                });

                new Thread() {
                    public void run() {
                        s="";
                        getbuslist();
                        for(int i=0;i<rtNms.size();i++){
                            s+=rtNms.get(i)+"   ";
                        }
                        text_thisBus.post(new Runnable(){
                            public void run(){
                                text_thisBus.setText(s);
                            }
                        });

                    }
                }.start();
                button_input.setEnabled(true);
                index=-1;
                busNo = edit_busno.getText().toString();
                if(busNo!=""){
                    button_input.setEnabled(true);
                }
                for(int i=0;i<rtNms.size();i++){
                    if(busNo.equals(rtNms.get(i))){
                        index=i;
                    }
                }

                // text_prev.post(new Runnable(){
                //   public void run(){
                //      text_prev.setText("");
                // }
                // });
                text_next.post(new Runnable(){
                    public void run(){
                        text_next.setText("");
                    }
                });
                text_time.post(new Runnable(){
                    public void run(){
                        text_time.setText("");
                    }
                });
                if(index==-1){
                    //  text_prev.post(new Runnable(){
                    //     public void run(){
                    //        text_prev.setText("해당정류장에 정차하지 않는 버스입니다.");
                    //   }
                    //});
                    text_next.post(new Runnable(){
                        public void run(){
                            text_next.setText("해당정류장에 정차하지 않는 버스입니다.");
                        }
                    });
                    text_time.post(new Runnable(){
                        public void run(){
                            text_time.setText("");
                        }
                    });
                }
                else {
                    text_next.post(new Runnable(){
                        public void run(){
                            text_next.setText(nxtStns.get(index)+" <"+adirections.get(index)+" 방면>");
                        }
                    });
                    text_time.post(new Runnable(){
                        public void run(){
                            text_time.setText("첫번째 도착 버스: " +arrmsg1s.get(index) + "\n" + "두번째 도착 버스: " +arrmsg2s.get(index));
                        }
                    });

                }
            }
            private String getbuslist(){
                String myurl = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?serviceKey=ZMeRmHtFEEPYni3c2T3soeWma3dcMQhR8ezpjWKhCE3hNJ9VlFronNmDUgen1D62Z1cHByNxNMCCzcUiNgG12g%3D%3D&arsId="+arsIds.get(0);
                Log.d("url",myurl);

                HttpURLConnection con = null;
                InputStreamReader isr = null;
                BufferedReader br = null;

                try{
                    URL url = new URL(myurl);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-type", "application/xml");
                    con.setConnectTimeout(100000);
                    con.setReadTimeout(1000000);
                    BufferedReader rd;

                    XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = parserCreator.newPullParser();

                    parser.setInput(url.openStream(), null);

                    int parserEvent = parser.getEventType();
                    arrmsg1s=new ArrayList<String>();
                    arrmsg2s=new ArrayList<String>();
                    rtNms=new ArrayList<String>();
                    adirections=new ArrayList<String>();
                    nxtStns=new ArrayList<String>();

                    while (parserEvent != XmlPullParser.END_DOCUMENT){
                        switch(parserEvent){
                            case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                                if(parser.getName().equals("arrmsg1")){ //title 만나면 내용을 받을수 있게 하자
                                    arrmsg1 = true;
                                }
                                if(parser.getName().equals("arrmsg2")){ //address 만나면 내용을 받을수 있게 하자
                                    arrmsg2 = true;
                                }
                                if(parser.getName().equals("rtNm")){ //mapx 만나면 내용을 받을수 있게 하자
                                    rtNm = true;
                                }
                                if(parser.getName().equals("adirection")){ //mapy 만나면 내용을 받을수 있게 하자
                                    adirection= true;
                                }
                                if(parser.getName().equals("nxtStn")){ //mapy 만나면 내용을 받을수 있게 하자
                                    nxtStn = true;
                                }

                                break;

                            case XmlPullParser.TEXT://parser가 내용에 접근했을때
                                if(arrmsg1){ //isTitle이 true일 때 태그의 내용을 저장.
                                    arrmsg1s.add(parser.getText());
                                    arrmsg1= false;
                                }
                                if(arrmsg2){ //isAddress이 true일 때 태그의 내용을 저장.
                                    arrmsg2s.add (parser.getText());
                                    arrmsg2 = false;

                                }
                                if(rtNm){ //isMapx이 true일 때 태그의 내용을 저장.
                                    rtNms.add(parser.getText());
                                    rtNm = false;
                                }
                                if(nxtStn){ //isMapy이 true일 때 태그의 내용을 저장.
                                    nxtStns.add(parser.getText());
                                    nxtStn = false;
                                }
                                if(adirection){ //isMapy이 true일 때 태그의 내용을 저장.
                                    adirections.add(parser.getText());
                                    adirection = false;
                                }

                                break;
                            case XmlPullParser.END_TAG:
                                break;
                        }
                        parserEvent = parser.next();
                    }
                }
                catch(Exception e){
                    Log.d("!!!!!!!!!!!!!!!!!!!!","삐에러가났습니다!!");
                }
                finally{
                    if(con != null){
                        try{con.disconnect();}catch(Exception e){}
                    }

                    if(isr != null){
                        try{isr.close();}catch(Exception e){}
                    }

                    if(br != null){
                        try{br.close();}catch(Exception e){}
                    }
                }

                return "";

            }



        });

        thisStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arsId = false;
                dist = false;
                stationId = false;
                stationNm = false;
                arrmsg1 = false;
                arrmsg2 = false;
                nxtStn = false;
                adirection = false;
                rtNm = false;
                thisBus.setEnabled(false);
                button_input.setEnabled(false);

                text_time.post(new Runnable() {
                    public void run() {
                        text_time.setText("");
                    }
                });
                //text_prev.post(new Runnable(){
                //   public void run(){
                //      text_prev.setText("");
                // }
                // });
                text_next.post(new Runnable() {
                    public void run() {
                        text_next.setText("");
                    }
                });
                text_this.post(new Runnable() {
                    public void run() {
                        text_this.setText("");
                    }
                });
                text_thisBus.post(new Runnable() {
                    public void run() {
                        text_thisBus.setText("");
                    }
                });

                edit_busno.post(new Runnable() {
                    public void run() {
                        edit_busno.setText("");
                    }
                });
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
                try {
                    Location location = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);/////////////////////////////////////////
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        lat = Double.toString(latitude);
                        lon = Double.toString(longitude);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
                new Thread() {
                    public void run() {
                        s = "";
                        getarmID();
                        for (int i = 0; i < 2; i++) {
                            if (stationIds.size() <= i)
                                break;
                            else {
                                s = arsIds.get(i) + "  " + stationNms.get(i) + "\n";
                                turn_on = 1;
                            }
                        }
                        if (stationIds.size() == 0) {
                            s = "근방에 정류장이 없습니다.";
                        }
                        text_this.post(new Runnable() {
                            public void run() {
                                text_this.setText(s);
                                if (turn_on == 1) {
                                    thisBus.setEnabled(true);
                                    turn_on = 0;
                                }
                            }
                        });
                    }
                }.start();

            }


            private String getarmID(){
                String getresult="";
                armidt=1;
                String myurl = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos?ServiceKey=/*service key*/%3D%3D&tmX="+lon+"&tmY="+lat+"&radius="+"300";
                Log.d("myurl", myurl);
                HttpURLConnection con = null;
                InputStreamReader isr = null;
                BufferedReader br = null;

                try{
                    URL url = new URL(myurl);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-type", "application/xml");
                    con.setConnectTimeout(100000);
                    con.setReadTimeout(1000000);
                    BufferedReader rd;

                    XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = parserCreator.newPullParser();

                    parser.setInput(url.openStream(), null);

                    int parserEvent = parser.getEventType();
                    arsIds=new ArrayList<String>();
                    dists=new ArrayList<String>();
                    stationIds=new ArrayList<String>();
                    stationNms=new ArrayList<String>();

                    while (parserEvent != XmlPullParser.END_DOCUMENT){
                        switch(parserEvent){
                            case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                                if(parser.getName().equals("arsId")){ //title 만나면 내용을 받을수 있게 하자
                                    arsId = true;
                                }
                                if(parser.getName().equals("stationId")){ //address 만나면 내용을 받을수 있게 하자
                                    stationId = true;
                                }
                                if(parser.getName().equals("dist")){ //mapx 만나면 내용을 받을수 있게 하자
                                    dist = true;
                                }
                                if(parser.getName().equals("stationNm")){ //mapy 만나면 내용을 받을수 있게 하자
                                    stationNm = true;
                                }
                                if(parser.getName().equals("flag")){
                                    flagg=true;
                                }

                                break;

                            case XmlPullParser.TEXT://parser가 내용에 접근했을때
                                if(arsId){ //isTitle이 true일 때 태그의 내용을 저장.
                                    arsIds.add(parser.getText());
                                    arsId= false;
                                }
                                if(dist){ //isAddress이 true일 때 태그의 내용을 저장.
                                    dists.add (parser.getText());
                                    dist = false;

                                }
                                if(stationId){ //isMapx이 true일 때 태그의 내용을 저장.
                                    stationIds.add(parser.getText());
                                    stationId = false;
                                }
                                if(stationNm){ //isMapy이 true일 때 태그의 내용을 저장.
                                    stationNms.add(parser.getText());
                                    stationNm = false;
                                }

                                break;
                            case XmlPullParser.END_TAG:
                                break;
                        }
                        parserEvent = parser.next();
                    }
                }
                catch(Exception e){
                    Log.d("!!!!!!!!!!!!!!!!!!!!","삐에러가났습니다!!");
                }
                finally{
                    if(con != null){
                        try{con.disconnect();}catch(Exception e){}
                    }

                    if(isr != null){
                        try{isr.close();}catch(Exception e){}
                    }

                    if(br != null){
                        try{br.close();}catch(Exception e){}
                    }
                }

                return "";

            }
        });
    }

    @Override
    protected void onResume() {
        Log.d("aaa","enter bus pause");
        //Log.d("DONG","enter bus resume");
        if(FirstActivity.checkRecplay()){
            if(FirstActivity.playLock){
                FirstActivity.playLock = false;
                //
                FirstActivity.init();
                FirstActivity.flag=true;
                FirstActivity.record = new Thread(new FirstActivity.recordSound());
                FirstActivity.play = new Thread(new FirstActivity.playRecord());
                FirstActivity.record.start();
                FirstActivity.play.start();
            }
        }else{

        }
        //earphone
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);

        super.onResume();
    }
    @Override
    protected void onPause() {
        Log.d("aaa","enter map pause");
        //Log.d("DONG","enter map Pause");

        if(exitState){
            if(FirstActivity.mIsRecording){
                FirstActivity.playLock = true;
                FirstActivity.flag = false;
                FirstActivity.m_in_rec.stop();
                FirstActivity.m_in_rec = null;
                FirstActivity.m_out_trk.stop();
                FirstActivity.m_out_trk  = null;
                FirstActivity.init();
            }
            else{

            }
        }else{
            Log.d("exit","go to other activity");
        }

        //earphone
        unregisterReceiver(myReceiver);

        super.onPause();
    }

    public void startLocationService(){
        long minTime = 5000;
        long minDistance = 0;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,       //NETWORK provider로 바꾸니까 실행됨.
                minTime,
                minDistance,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        lat = Double.toString(latitude);
                        lon = Double.toString(longitude);
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {
                    }

                    @Override
                    public void onProviderEnabled(String s) {
                    }
                    @Override
                    public void onProviderDisabled(String s) {
                    }
                }
        );
    }


    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        for(Prediction prediction : predictions){
            if(prediction.score>1.0){
                if(prediction.name.equals("leftSwipe")){
                    Toast.makeText(this,"현재위치",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BusActivity.this,MapActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                } else if(prediction.name.equals("Circle")){
                    Toast.makeText(this,"횡단보도",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BusActivity.this,ClassifierActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                } else if(prediction.name.equals("rightSwipe")){
                    Toast.makeText(this,"현재 페이지 입니다",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d(TAG, "Headset is unplugged");
                        FirstActivity.lock=0;
                        lock = 0;
                        break;
                    case 1:
                        Log.d(TAG, "Headset is plugged");
                        FirstActivity.lock=1;
                        lock = 1;
                        break;
                    default:
                        Log.d(TAG, "I have no idea what the headset state is");
                }
            }
        }
    }

}

