package quiettimev1.konamgil.com.quiettime.Util;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

/**
 * Created by konamgil on 2017-06-08.
 */

public class AudioSetting {
    /**
     * 오디오 무음처리
     */
    private Context mContext;
    private AudioManager mAlramMAnager;

    /**
     * 생성자
     * @param mContext
     */
    public AudioSetting(Context mContext) {
        this.mContext = mContext;
        mAlramMAnager= (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 현재 상태의 볼륨 기준에서 무음으로 바꿈
     */
    public void MuteAudio(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //마시멜로버전 이상
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
        } else {
            mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_ALARM, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }

    /**
     * 무음 상태가 되기 직전 상태로 볼륨을 되돌림
     */
    public void UnMuteAudio(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //마시멜로버전 이상
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE,0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);

            if (mAlramMAnager.getStreamVolume(AudioManager.STREAM_RING) == 0){
                mAlramMAnager.setStreamVolume(AudioManager.STREAM_RING,10,0);
            }
        } else {
            mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_ALARM, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        }
    }

    /**
     * 벨소리 최대볼륨의 절반으로 만듭니다
     */
    public void setUpAudioVolume(){
        int maxVoulume = mAlramMAnager.getStreamMaxVolume(AudioManager.STREAM_RING);
        mAlramMAnager.setStreamVolume(AudioManager.STREAM_RING, maxVoulume/2, AudioManager.FLAG_PLAY_SOUND);
        mAlramMAnager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, maxVoulume/2, AudioManager.FLAG_PLAY_SOUND);
        mAlramMAnager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVoulume/2, AudioManager.FLAG_PLAY_SOUND);
        mAlramMAnager.setStreamVolume(AudioManager.STREAM_SYSTEM, maxVoulume/2, AudioManager.FLAG_PLAY_SOUND);
        mAlramMAnager.setStreamVolume(AudioManager.STREAM_ALARM, maxVoulume/2, AudioManager.FLAG_PLAY_SOUND);
        mAlramMAnager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    /**
     * 벨소리 볼륨을 0으로 만듭니다
     */
    public void setDownAudioVolume(){
        mAlramMAnager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//        mAlramMAnager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_SHOW_UI);
//        mAlramMAnager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.FLAG_SHOW_UI);
//        mAlramMAnager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
//        mAlramMAnager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, AudioManager.FLAG_SHOW_UI);
//        mAlramMAnager.setStreamVolume(AudioManager.STREAM_ALARM, 0, AudioManager.FLAG_SHOW_UI);
    }
}
