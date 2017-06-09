package quiettimev1.konamgil.com.quiettime.UI;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;

import quiettimev1.konamgil.com.quiettime.DB_Helper.ContactsAdapter;
import quiettimev1.konamgil.com.quiettime.DB_Helper.PrefDataHelper;
import quiettimev1.konamgil.com.quiettime.PhoneNumber.ContactsInfoDatas;
import quiettimev1.konamgil.com.quiettime.PhoneNumber.ContactsInfoObject;
import quiettimev1.konamgil.com.quiettime.PhoneNumber.TimeInfoObject;
import quiettimev1.konamgil.com.quiettime.R;
import quiettimev1.konamgil.com.quiettime.Service.AudioService;
import quiettimev1.konamgil.com.quiettime.Util.GetTime;

public class MainActivity extends AppCompatActivity {

    //퍼미션 리퀘스트 코드
    private final int MY_PERMISSION_REQUEST = 100;

    private Context mContext;

    //시간 설정 관련
    private GetTime mGetTime;
    private TimePickerDialog dialog;

    //전역위젯
    private TextView tvStartHour;
    private TextView tvStartMinute;
    private TextView tvEndHour;
    private TextView tvEndMinute;

    //프리퍼런스
    private PrefDataHelper mPrefDataHelper;

    //타임정보 리스트
    private ArrayList<TimeInfoObject> mTimeList;

    //전화번호 인원 리스트
    private ArrayList<ContactsInfoObject> mContactsInfoObjectArrayList;

    //체크된 전화번호 리스트
    private ArrayList<String> checkedNumberList;

    //리스트 어댑터
    private ContactsAdapter mContactsAdapter;

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
        allowMuteMode();
    }

    /**
     * 방해금지 허용해야 볼륨조절 사용 가능
     */
    public void allowMuteMode(){
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            Toast.makeText(mContext,"방해금지모드를 해제해주세요", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }
    /**
     * 컬렉션 및 스토어 초기화
     */
    public void init(){
        mContactsAdapter = new ContactsAdapter(mContext);
        mPrefDataHelper = new PrefDataHelper(mContext);
        mContactsInfoObjectArrayList = mContactsAdapter.getAllData();
        mTimeList = new ArrayList<>();
        checkedNumberList = new ArrayList<>();
    }

    /**
     * 위젯 초기화
     */
    public void initWidget(){

        //툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Be Quite");
        toolbar.setTitleTextColor(Color.WHITE);

        //버튼 위젯 초기화
        Button btnStartSetTime = (Button)findViewById(R.id.btnStartSetTime);
        Button btnEndSetTime = (Button)findViewById(R.id.btnEndSetTime);
        Button btnUpdate = (Button)findViewById(R.id.btnUpdate);

        //버튼 리스너 연결
        btnStartSetTime.setOnClickListener(mOnClickListener);
        btnEndSetTime.setOnClickListener(mOnClickListener);
        btnUpdate.setOnClickListener(mOnClickListener);

        //텍스트뷰 위젯 초기화
        tvStartHour = (TextView)findViewById(R.id.tvStartHour);
        tvStartMinute = (TextView)findViewById(R.id.tvStartMinute);
        tvEndHour = (TextView)findViewById(R.id.tvEndHour);
        tvEndMinute = (TextView)findViewById(R.id.tvEndMinute);

        //리스트뷰
        ListView listContacts = (ListView)findViewById(R.id.listContacts);

        listContacts.setAdapter(mContactsAdapter);
        listContacts.setOnItemClickListener(mOnItemClickListener);
        listContacts.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
    }

    /**
     * 리스트 아이템 리스너
     */
    private ListView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mContactsAdapter.setCheckBox(position);
        }
    };

    /**
     * 체크된 사람의 전화번호 저장
     */
    public void checkedNumber(){
        checkedNumberList.clear();
        for (ContactsInfoObject object : mContactsInfoObjectArrayList){
            if (object.isCheckbox()){
                checkedNumberList.add(object.getTeleNumber());
            }
        }
        mPrefDataHelper.insertCheckedNumberList(checkedNumberList);
//        for(int i=0; i<checkedNumberList.size(); i++){
//            Log.d("MAIN",checkedNumberList.get(i).toString());
//        }
    }
    /**
     *  버튼 리스너
     */
    private Button.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnStartSetTime:
                    showStartTimePickerDialog();
                    break;
                case R.id.btnEndSetTime:
                    showEndTimePickerDialog();
                    break;
                case R.id.btnUpdate:
                    successSettingUpdateTime();
                    checkedNumber();
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
        dialog = new TimePickerDialog(mContext, startTimePickerlistener, mGetTime.getCurrentTime(), mGetTime.getCurrentMinute(), false);
        dialog.setMessage("무음설정 - 시작 시간");
        dialog.show();

    }

    /**
     * 끝 시간 설정 다이얼로그 보여주기
     */
    private void showEndTimePickerDialog(){
        mGetTime = new GetTime();
        dialog = new TimePickerDialog(mContext, endTimePickerlistener, mGetTime.getCurrentTime(), mGetTime.getCurrentMinute(), false);
        dialog.setMessage("무음설정 - 끝 시간");
        dialog.show();
    }

    /**
     * 시작 타임 피커 리스너
     */
    private TimePickerDialog.OnTimeSetListener startTimePickerlistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tvStartHour.setText(String.valueOf(hourOfDay));
            tvStartMinute.setText(String.valueOf(minute));
            int h = hourOfDay;
            int m = minute;
            Toast.makeText(getApplicationContext(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 끝 타임 피커 리스너
     */
    private TimePickerDialog.OnTimeSetListener endTimePickerlistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tvEndHour.setText(String.valueOf(hourOfDay));
            tvEndMinute.setText(String.valueOf(minute));
            int h = hourOfDay;
            int m = minute;
            Toast.makeText(getApplicationContext(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();
        }
    };



    /**
     * 완료 버튼 눌럿을때
     */
    private void successSettingUpdateTime(){
        try {
            int startHour = Integer.parseInt(tvStartHour.getText().toString());
            int startMinute = Integer.parseInt(tvStartMinute.getText().toString());
            int endHour = Integer.parseInt(tvEndHour.getText().toString());
            int endMinute = Integer.parseInt(tvEndMinute.getText().toString());

            if (!mTimeList.isEmpty()) {
                mTimeList.set(0, new TimeInfoObject(startHour, startMinute, endHour, endMinute));
            } else {
                mTimeList.add(0, new TimeInfoObject(startHour, startMinute, endHour, endMinute));
            }

            Log.d("AudioService", tvStartHour.getText().toString() + tvStartMinute.getText().toString() + tvEndHour.getText().toString() + tvEndMinute.getText().toString());
            mPrefDataHelper.insertListInPref(mTimeList);

            Intent intent = new Intent(mContext, AudioService.class);
            startService(intent);

            Toast.makeText(mContext, "완료", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(mContext, "시간을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
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
