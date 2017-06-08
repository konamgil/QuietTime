package quiettimev1.konamgil.com.quiettime.UI;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import quiettimev1.konamgil.com.quiettime.DB_Helper.PrefDataHelper;
import quiettimev1.konamgil.com.quiettime.PhoneNumber.ContactsInfoDatas;
import quiettimev1.konamgil.com.quiettime.PhoneNumber.ContactsInfoObject;
import quiettimev1.konamgil.com.quiettime.R;
import quiettimev1.konamgil.com.quiettime.Service.AudioService;
import quiettimev1.konamgil.com.quiettime.Util.GetTime;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK;
import static android.app.AlertDialog.THEME_HOLO_LIGHT;
import static android.app.AlertDialog.THEME_TRADITIONAL;

public class MainActivity extends AppCompatActivity {

    //퍼미션 리퀘스트 코드
    private final int MY_PERMISSION_REQUEST = 100;

    private Context mContext;

    //시간 설정 관련
    private GetTime mGetTime;
    private TimePickerDialog dialog;

    //전역위젯
    private TextView tvStartTime;
    private TextView tvEndTime;

    //시간 설정 데이터
    private HashMap<String,Integer> setTimeMap;

    //프리퍼런스
    private PrefDataHelper mPrefDataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();//퍼미션 요청
        }
        mContext = this;
        init(); // 위젯 초기화
        initWidget();
//        ContactsInfoDatas a = new ContactsInfoDatas(mContext);
//        ArrayList<ContactsInfoObject> B = a.getContacts();
    }

    public void init(){
        setTimeMap= new HashMap<>();
        mPrefDataHelper = new PrefDataHelper(mContext);
    }
    public void initWidget(){

        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Be Quite");
        toolbar.setTitleTextColor(Color.WHITE);

        //버튼 위젯 초기화
        Button btnSetTime = (Button)findViewById(R.id.btnSetTime);
        Button btnMoveContactsActivity = (Button)findViewById(R.id.btnMoveContactsActivity);
        Button btnUpdate = (Button)findViewById(R.id.btnUpdate);

        //버튼 리스너 연결
        btnSetTime.setOnClickListener(mOnClickListener);
        btnMoveContactsActivity.setOnClickListener(mOnClickListener);
        btnUpdate.setOnClickListener(mOnClickListener);

        //텍스트뷰 위젯 초기화
        tvStartTime = (TextView)findViewById(R.id.tvStartTime);
        tvEndTime = (TextView)findViewById(R.id.tvEndTime);

    }

    /**
     *  버튼 리스너
     */
    private Button.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnSetTime:
                    showEndTimePickerDialog();
                    showStartTimePickerDialog();
                    break;
                case R.id.btnMoveContactsActivity:
                    break;
                case R.id.btnUpdate:
                    successSettingUpdateTime();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 시작 시간 설정 다이얼로그 보여주기
     */
    private void showStartTimePickerDialog(){
        mGetTime = new GetTime();
        dialog = new TimePickerDialog(mContext, THEME_TRADITIONAL,startTimePickerlistener, mGetTime.getCurrentTime(), mGetTime.getCurrentMinute(), false);
        dialog.setMessage("무음설정 - 시작 시간");
        dialog.show();

    }

    /**
     * 끝 시간 설정 다이얼로그 보여주기
     */
    private void showEndTimePickerDialog(){
        mGetTime = new GetTime();
        dialog = new TimePickerDialog(mContext, endTimePickerlistener, mGetTime.getCurrentTime(), mGetTime.getCurrentMinute(), true);
        dialog.setMessage("무음설정 - 끝 시간");
        dialog.show();
    }

    /**
     * 시작 타임 피커 리스너
     */
    private TimePickerDialog.OnTimeSetListener startTimePickerlistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tvStartTime.setText(hourOfDay + "시 " + minute + "분");
            setTimeMap.put("startHour",hourOfDay);
            setTimeMap.put("startMinute",minute);
            Toast.makeText(getApplicationContext(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 끝 타임 피커 리스너
     */
    private TimePickerDialog.OnTimeSetListener endTimePickerlistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tvEndTime.setText(hourOfDay + "시 " + minute + "분");
            setTimeMap.put("endHour",hourOfDay);
            setTimeMap.put("endMinute",minute);
            Toast.makeText(getApplicationContext(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 완료 버튼 눌럿을때
     */
    private void successSettingUpdateTime(){
        Intent intent = new Intent(mContext, AudioService.class);
//        intent.putExtra("resultTimeMap",setTimeMap);
        HashMap<String,Integer> resultSettingTime = mPrefDataHelper.selectMapInPref();
        mPrefDataHelper.insertMapInPref(setTimeMap);
        startService(intent);

        Toast.makeText(mContext,"완료",Toast.LENGTH_SHORT).show();

    }

    /**
     * 퍼미션 체크
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.MODIFY_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                )
                    {requestPermissions(new String[]{
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.MODIFY_PHONE_STATE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    }, MY_PERMISSION_REQUEST);
        } else {
            Toast.makeText(mContext,"권한이 허용된 상태입니다",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * onResume에서 서비스 실행
     */
    @Override
    protected void onResume() {
        super.onResume();

    }




}
