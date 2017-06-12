package quiettimev1.konamgil.com.quiettime.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import quiettimev1.konamgil.com.quiettime.BroadcastReceiver.MainBroadcast;
import quiettimev1.konamgil.com.quiettime.DB_Helper.PrefDataHelper;
import quiettimev1.konamgil.com.quiettime.PhoneNumber.TimeInfoObject;
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
    public static boolean isServiceRunning = false;
    public static boolean isFirstCall = true;
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
        isServiceRunning = true;
        PrefDataHelper mPrefDataHelper= new PrefDataHelper(getApplicationContext());
        ArrayList<TimeInfoObject> resultTimeList = mPrefDataHelper.selectListInPref();

        mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        audio = new AudioSetting(getApplicationContext());

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

        int startHour = resultTimeList.get(0).getStartHour();
        int startMinute = resultTimeList.get(0).getStartMinute();
        int endHour = resultTimeList.get(0).getEndHour();
        int endMinute = resultTimeList.get(0).getEndMinute();

        AlarmThread mAlarmThread = new AlarmThread(startHour,startMinute,endHour,endMinute);
        mAlarmThread.start();

        return START_REDELIVER_INTENT;
    }

    class AlarmThread extends Thread {
        int sh;
        int sm;
        int eh;
        int em;
        public AlarmThread(int startHour, int startMinute, int endHour, int endMinute) {
            super();
            sh = startHour;
            sm = startMinute;
            eh = endHour;
            em = endMinute;
        }

        @Override
        public void run() {
            super.run();
            startMuteTimeRange(sh,sm); // 시작 시간 무음설정
            endMuteTimeRange(eh,em); // 끝나는 시간 무음해제
            muteWhenCall();
            Log.d(TAG,"서비스 시작");
            Log.d(TAG,"시작시간 = " + sh + ":" + sm+", 끝 시간 = " + eh + ":" + em);
        }
    }

    /**
     * 시작으로 설정한 시간 무음되도록 브로드 캐스트 알람 설정
     */
    public void startMuteTimeRange(int hour, int minute){
        //설정한 시간대는 무음이 되도록 intent 구성
        Intent start = new Intent("Action.START.MuteTime");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,start,0);

        GetTime getTime = new GetTime();
        startTime = getTime.getSelectTimeTypeLong(hour,minute); //예제

        execAlarm(pendingIntent, startTime);
    }

    /**
     * 끝으로 설정한 시간 무음이 풀리도록 브로드 캐스트 알람 설정
     */
    public void endMuteTimeRange(int hour, int minute){
        //설정한 시간대는 무음이 되도록 intent 구성
        Intent end = new Intent("Action.END.MuteTime");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,end,0);

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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {                   //버전이 21이상(마시멜로 이상)
//            mAlarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(settingTime,pendingIntent),pendingIntent);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {       //버전이 19~20(키캣~ 롤리팝)
//            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, settingTime, pendingIntent);
//        } else {
//            mAlarmManager.set(AlarmManager.RTC_WAKEUP, settingTime, pendingIntent); //계속 0초 ~ 3초 오차범위가 생김
//        }
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, settingTime, AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.d(TAG,"알람 설정");
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
//        mTelephonyManager.listen(mListener,PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * 전화 상태 감시 리스너
     */
    private PhoneStateListener mListener = new PhoneStateListener(){
        Handler mHandler = new Handler();
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            if(state == TelephonyManager.CALL_STATE_RINGING){
                Log.i(TAG,"Calling .... Ringing");
                PrefDataHelper mPrefDataHelper = new PrefDataHelper(getApplicationContext());
                final ArrayList<String> checkedNumberList = mPrefDataHelper.selectCheckedNumberList();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0; i<checkedNumberList.size(); i++){
                            if(checkedNumberList.get(i).equals(incomingNumber)){
                                audio.setUpAudioVolume();
                                Log.d(TAG,"걸려온 전화 : " + incomingNumber+", 체크된 번호 수신 : "+checkedNumberList.get(i) );
                                            Toast.makeText(getApplicationContext(),"걸려온 전화 : " + incomingNumber+", 체크된 번호 수신 : "+checkedNumberList.get(i) ,Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                });
                Toast.makeText(getApplicationContext(),incomingNumber + "전화 수신중",Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
        registerRestartAlarm();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        android.os.Debug.waitForDebugger();
        super.onConfigurationChanged(newConfig);
    }
}
