package quiettimev1.konamgil.com.quiettime.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import quiettimev1.konamgil.com.quiettime.BroadcastReceiver.MainBroadcast;
import quiettimev1.konamgil.com.quiettime.DB_Helper.PrefDataHelper;
import quiettimev1.konamgil.com.quiettime.R;
import quiettimev1.konamgil.com.quiettime.Util.AudioSetting;
import quiettimev1.konamgil.com.quiettime.Util.GetTime;

/**
 * Created by konamgil on 2017-06-08.
 */

public class AudioService extends Service {

    final String TAG = getClass().getSimpleName();

    private long startTime;
    private long endTime;

    private TelephonyManager mTelephonyManager;
    private AlarmManager mAlarmManager;
    private AudioSetting audio;
    private Handler mHandler;
    private HashMap<String,Integer> resultSettingTime;
    private PrefDataHelper mPrefDataHelper;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        unregisterRestartAlarm();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        audio = new AudioSetting(getApplicationContext());
        mPrefDataHelper = new PrefDataHelper(getApplicationContext());

        startForeground(1,new Notification());

        /**
         * startForeground 를 사용하면 notification 을 보여주어야 하는데 없애기 위한 코드
         */
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification;

        notification = new Notification.Builder(getApplicationContext())
        .setContentTitle("")
        .setContentText("")
        .setPriority(Notification.PRIORITY_MIN)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setWhen(System.currentTimeMillis())
        .build();

        nm.notify(startId, notification);
        nm.cancel(startId);

//        Bundle item = intent.getExtras();
//        resultSettingTime = (HashMap<String, Integer>)intent.getSerializableExtra("resultTimeMap");
        resultSettingTime = mPrefDataHelper.selectMapInPref();


        mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int startHour = resultSettingTime.get("startHour");
                int startMinute = resultSettingTime.get("startMinute");
                int endHour = resultSettingTime.get("endHour");
                int endMinute = resultSettingTime.get("endMinute");
                muteWhenCall(); // 전화 상태 감시
                startMuteTimeRange(startHour,startMinute); // 시작 시간 무음설정
                endMuteTimeRange(endHour,endMinute); // 끝나는 시간 무음해제
                Log.d(TAG,"서비스 시작");
                Log.d(TAG,"시작시간 : " + startHour + ":" + startMinute+" 끝 시간" + endHour + ":" + endMinute);

            }
        });
        //MuteAudio();
        return START_REDELIVER_INTENT;
    }

    /**
     * 시작으로 설정한 시간 무음되도록 브로드 캐스트 알람 설정
     */
    public void startMuteTimeRange(int hour, int minute){
        //설정한 시간대는 무음이 되도록 intent 구성
        Intent intent = new Intent("Action.START.MuteTime");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);

        GetTime getTime = new GetTime();
        startTime = getTime.getSelectTimeTypeLong(hour,minute); //예제

        execAlarm(pendingIntent, startTime);
    }

    /**
     * 끝으로 설정한 시간 무음이 풀리도록 브로드 캐스트 알람 설정
     */
    public void endMuteTimeRange(int hour, int minute){
        //설정한 시간대는 무음이 되도록 intent 구성
        Intent intent = new Intent("Action.END.MuteTime");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);

        GetTime getTime = new GetTime();
        endTime = getTime.getSelectTimeTypeLong(hour,minute); //예제

        execAlarm(pendingIntent, endTime);
    }

    /**
     * 지정한 시간에 알람이 울리도록 설정
     * @param pendingIntent
     * @param settingTime
     */
    public void execAlarm(PendingIntent pendingIntent, long settingTime){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {                   //버전이 21이상(마시멜로 이상)
            mAlarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(settingTime,pendingIntent),pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {       //버전이 19~20(키캣~ 롤리팝)
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, settingTime, pendingIntent);
        } else {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, settingTime, pendingIntent); //계속 0초 ~ 3초 오차범위가 생김
        }
    }
    /**
     * 알람 매니져에 서비스 등록
     */
    private void registerRestartAlarm(){

        Log.i("AudioService" , "registerRestartAlarm" );
        Intent intent = new Intent(AudioService.this,MainBroadcast.class);
        intent.setAction("ACTION.RESTART.AudioService");
        PendingIntent sender = PendingIntent.getBroadcast(AudioService.this,0,intent,0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 1*1000;

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        /**
         * 알람 등록
         */
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,firstTime,1*1000,sender);

    }

    /**
     * 알람 매니져에 서비스 해제
     */
    private void unregisterRestartAlarm(){

        Log.i("000 PersistentService" , "unregisterRestartAlarm" );

        Intent intent = new Intent(AudioService.this,MainBroadcast.class);
        intent.setAction("ACTION.RESTART.AudioService");
        PendingIntent sender = PendingIntent.getBroadcast(AudioService.this,0,intent,0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        /**
         * 알람 취소
         */
        alarmManager.cancel(sender);
    }

    /**
     * 전화 리슨 등록
     */
    public void muteWhenCall(){
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mTelephonyManager.listen(mListener,PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * 전화 상태 감시 리스너
     */
    private PhoneStateListener mListener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if(state == TelephonyManager.CALL_STATE_RINGING){
                //MuteAudio();
                //선택된 번호는 특정시간에도 벨소리가 울려야한다
                //특정 시간에는 무음이 된다
                if(incomingNumber.equals("01051374420")){
                    audio.UnMuteAudio();
                }
                Toast.makeText(getApplicationContext(),incomingNumber + "전화왔어요",Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerRestartAlarm();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        android.os.Debug.waitForDebugger();
        super.onConfigurationChanged(newConfig);
    }
}
