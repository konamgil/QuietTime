package quiettimev1.konamgil.com.quiettime.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import quiettimev1.konamgil.com.quiettime.DB_Helper.PrefDataHelper;
import quiettimev1.konamgil.com.quiettime.Service.AudioService;
import quiettimev1.konamgil.com.quiettime.Util.AudioSetting;

/**
 * Created by konamgil on 2017-06-08.
 */

public class MainBroadcast extends BroadcastReceiver {
    private String TAG = getClass().getSimpleName();
    private String telNum = "";
    private AudioManager am;
    private RingtoneManager mRingtoneManager;
    private Uri alertUri;
    private  Ringtone r;

    @Override
    public void onReceive(final Context context, Intent intent) {
        mRingtoneManager = new RingtoneManager(context);
        Log.d("RestartService", "RestartService called : " + intent.getAction());
        final Handler mHandler = new Handler();
        final AudioSetting audio = new AudioSetting(context);
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);




        /**
         * 서비스 죽일때 알람으로 다시 서비스 등록
         */
        if (intent.getAction().equals("ACTION.RESTART.AudioService")) {
            Intent i = new Intent(context, AudioService.class);
            context.startService(i);
            Log.d("RestartService", "ACTION.RESTART.AudioService ");
            Toast.makeText(context, "서비스 재시작", Toast.LENGTH_SHORT).show();
        }

        /**
         * 폰 재시작 할때 서비스 등록
         */
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, AudioService.class);
            context.startService(i);
            Log.d("RestartService", "ACTION_BOOT_COMPLETED");
            Toast.makeText(context, "재부팅 후 시작", Toast.LENGTH_SHORT).show();
        }

        /**
         * 시작 시간때 장비 무음으로 설정
         */
        if (intent.getAction().equals("Action.START.MuteTime")) {
//            audio.setDownAudioVolume();
//            audio.MuteAudio();
            AudioService.isServiceRunning = true;
            am.setRingerMode(0x00000000); // no sound and no vibration
            Log.d(TAG, "무음");
            Toast.makeText(context, "무음", Toast.LENGTH_SHORT).show();
        }

        /**
         * 끝 시간때 장비 다시 소리나도록 설정
         */
        if (intent.getAction().equals("Action.END.MuteTime")) {
            audio.setUpAudioVolume();
            AudioService.isServiceRunning = false;
            r.stop();
            Log.d(TAG, "무음 해제");
            Toast.makeText(context, "무음 해제", Toast.LENGTH_SHORT).show();
        }


        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            r = RingtoneManager.getRingtone(context, alertUri);
            final PrefDataHelper mPrefDataHelper = new PrefDataHelper(context);
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            final ArrayList<String> checkedNumberList = mPrefDataHelper.selectCheckedNumberList();
            if (tm.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
                Bundle bundle = intent.getExtras();
                telNum = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.d(TAG, "전화왔음");
                Log.d(TAG, "걸려온 번호 : " + telNum);
                if(!AudioService.isServiceRunning){
                    return;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < checkedNumberList.size(); i++) {
                            if (checkedNumberList.get(i).replaceAll("-", "").equals(telNum)) {
//                                audio.UnMuteAudio();
//                                audio.setUpAudioVolume();
//                                audio.setUpAudioVolume();
                                if(AudioService.isFirstCall){
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            r.play();
                                            AudioService.isFirstCall = false;
                                            Log.d(TAG,"첫번째 전화입니다");
                                        }
                                    });
                                }
                                if (AudioService.isServiceRunning) {
                                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                    am.setStreamVolume(AudioManager.STREAM_RING, 3, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
                                    Log.d(TAG, "걸려온 전화 : " + telNum + ", 체크된 번호 수신 : " + checkedNumberList.get(i));
                                    Toast.makeText(context, "걸려온 전화 : " + telNum + ", 체크된 번호 수신 : " + checkedNumberList.get(i), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                    }
                });
            }


            if (tm.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
                if (AudioService.isServiceRunning) {
//                    am.setRingerMode(0x00000000); // no sound and no vibration
                    r.stop();
                    Log.d(TAG,"r 종료");
                    mRingtoneManager.stopPreviousRingtone();
                    audio.setDownAudioVolume();
                    Log.d(TAG,"현재 재생상태 : " + r.isPlaying());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            if (r.isPlaying()) {
                            try {
//                                r.stop();
//                                Log.d(TAG,"r 종료");
//                                mRingtoneManager.stopPreviousRingtone();
//                                audio.setDownAudioVolume();
//                                Log.d(TAG,"현재 재생상태 : " + r.isPlaying());
                            }catch (Exception e){
                                e.printStackTrace();
                            }

//                            }
                        }
                    });


                    Toast.makeText(context, "서비스중" + r.isPlaying(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            return;
        }
    }
}















