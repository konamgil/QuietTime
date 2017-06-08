package quiettimev1.konamgil.com.quiettime.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import quiettimev1.konamgil.com.quiettime.Service.AudioService;
import quiettimev1.konamgil.com.quiettime.Util.AudioSetting;

/**
 * Created by konamgil on 2017-06-08.
 */

public class MainBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("RestartService" , "RestartService called : " + intent.getAction());

        AudioSetting audio = new AudioSetting(context);
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
            audio.MuteAudio(); //무음
            Toast.makeText(context,"무음",Toast.LENGTH_SHORT).show();

        }

        /**
         * 끝 시간때 장비 다시 소리나도록 설정
         */
        if(intent.getAction().equals("Action.END.MuteTime")){
            audio.UnMuteAudio(); //무음 해제
            Toast.makeText(context,"무음 해제",Toast.LENGTH_SHORT).show();
        }

    }
}















