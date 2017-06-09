package quiettimev1.konamgil.com.quiettime.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import quiettimev1.konamgil.com.quiettime.DB_Helper.PrefDataHelper;
import quiettimev1.konamgil.com.quiettime.Service.AudioService;
import quiettimev1.konamgil.com.quiettime.Util.AudioSetting;

/**
 * Created by konamgil on 2017-06-08.
 */

public class MainBroadcast extends BroadcastReceiver {
    private String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        PrefDataHelper mPrefDataHelper = new PrefDataHelper(context);
        final ArrayList<String> checkedNumberList = mPrefDataHelper.selectCheckedNumberList();

        Log.d("RestartService" , "RestartService called : " + intent.getAction());
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final Handler mHandler = new Handler();
        final AudioSetting audio = new AudioSetting(context);

        /**
         * 서비스 죽일때 알람으로 다시 서비스 등록
         */
        if(intent.getAction().equals("ACTION.RESTART.AudioService")){
            Intent i = new Intent(context,AudioService.class);
            context.startService(i);
            Log.d("RestartService" ,"ACTION.RESTART.AudioService " );
            Toast.makeText(context,"서비스 재시작",Toast.LENGTH_SHORT).show();
        }

        /**
         * 폰 재시작 할때 서비스 등록
         */
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent i = new Intent(context,AudioService.class);
            context.startService(i);
            Log.d("RestartService" , "ACTION_BOOT_COMPLETED" );
            Toast.makeText(context,"재부팅 후 시작",Toast.LENGTH_SHORT).show();
        }

        /**
         * 시작 시간때 장비 무음으로 설정
         */
        if(intent.getAction().equals("Action.START.MuteTime")){
//            audio.MuteAudio(); //무음
            audio.setDownAudioVolume();
            Toast.makeText(context,"무음",Toast.LENGTH_SHORT).show();

        }

        /**
         * 끝 시간때 장비 다시 소리나도록 설정
         */
        if(intent.getAction().equals("Action.END.MuteTime")){
//            audio.UnMuteAudio(); //무음 해제
            audio.setUpAudioVolume();
            Toast.makeText(context,"무음 해제",Toast.LENGTH_SHORT).show();
        }


        tm.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, final String incomingNumber) {
                // 전화 수신 반응.
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE :   // 폰이 울리거나 통화중이 아님.
                        Log.i("TelephonyTestActivity","STATE_IDLE" + "서비스 상태 : " + AudioService.isServiceRunning);
                        if(AudioService.isServiceRunning){
//                            audio.MuteAudio();
                            audio.setDownAudioVolume();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING :   // 폰이 울린다.
                        Log.i("TelephonyTestActivity","Calling .... Ringing");

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                for(int i = 0; i<checkedNumberList.size(); i++){
                                    if(checkedNumberList.get(0) == incomingNumber){
                                        audio.setUpAudioVolume();
                                        Log.d(TAG,incomingNumber+ " 체크된 번호 수신");
                                        return;
                                    }
                                }
                            }
                        });
                        Toast.makeText(context,incomingNumber + "전화 수신중",Toast.LENGTH_SHORT).show();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK :   // 폰이 현재 통화 중.
                        Log.i("TelephonyTestActivity","STATE_OFFHOOK");
                        break;
                    default: break;
                }

                super.onCallStateChanged(state, incomingNumber);
            }
        },PhoneStateListener.LISTEN_CALL_STATE);
    }
}















