package quiettimev1.konamgil.com.quiettime.DB_Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import quiettimev1.konamgil.com.quiettime.PhoneNumber.TimeInfoObject;

/**
 * Created by konamgil on 2017-05-19.
 */

// 프리퍼런스를 사용할 수 있게 도와주는 헬퍼 클래스이다
public class PrefDataHelper {
    private Context mContext;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor editor;
    private Gson gson;

    /**
     * 생성자
     * @param context
     */
    public PrefDataHelper(Context context) {
        this.mContext = context;
        openPrefEditor();
    }

    /**
     * 쉐어드프리퍼런스 에디터 열기
     */
    private void openPrefEditor(){
        //프리퍼런스 열고 edit을 초기화한다
        mPrefs = mContext.getSharedPreferences("data", Context.MODE_MULTI_PROCESS);
        editor = mPrefs.edit();
        gson = new Gson();

    }

    /**
     *  시간 정보를 프리퍼런스에 json 으로 바꾸어 저장한다
     */
    public void insertListInPref(ArrayList list){
        String timeListString = gson.toJson(list);
        editor.putString("settingTime",timeListString);
        editor.commit();
    }

    /**
     * 설정된 번호 정보를 프리퍼런스에 json으로 바꾸어 저장한다
     */
    public void insertCheckedNumberList(ArrayList list){
        String checkdListString = gson.toJson(list);
        editor.putString("checkedNumber",checkdListString);
        editor.commit();
    }

    /**
     * 설정된 번호 정보를 가져온다
     */
    public ArrayList selectCheckedNumberList(){
        String storedCheckedNumberListString = mPrefs.getString("checkedNumber","");
        Log.d("AudioService",storedCheckedNumberListString);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<TimeInfoObject> didCheckedNumberList = gson.fromJson(storedCheckedNumberListString, type);
        return didCheckedNumberList;
    }

    /**
     * 설정된 시간 값 가져오기
     * @return ArrayList
     */
    public ArrayList selectListInPref(){
        String storedTimeListString = mPrefs.getString("settingTime", "");
        Log.d("AudioService",storedTimeListString);
        Type type = new TypeToken<ArrayList<TimeInfoObject>>(){}.getType();
        ArrayList<TimeInfoObject> didSettingTimeList = gson.fromJson(storedTimeListString, type);
        return didSettingTimeList;
    }


}
