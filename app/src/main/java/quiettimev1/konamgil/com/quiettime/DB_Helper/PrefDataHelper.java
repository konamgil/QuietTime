package quiettimev1.konamgil.com.quiettime.DB_Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

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
        mPrefs = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = mPrefs.edit();
        gson = new Gson();

    }

    /**
     *  프리퍼런스에 json 로 바꾸어 저장한다
     */
    public void insertMapInPref(HashMap<String,Integer> hashMap){
        Gson gson = new Gson();
        String hashMapString = gson.toJson(hashMap);
        editor.putString("settingTime",hashMapString);
        editor.commit();
    }

    public HashMap<String, Integer> selectMapInPref(){
        String storedHashMapString = mPrefs.getString("settingTime", "");
        Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
        HashMap<String, Integer> didSettingTime = gson.fromJson(storedHashMapString, type);
        return didSettingTime;
    }

    /**
     * id를 이용한 프리프 삭제
     * @param _id
     */
    public void deleteItemFromInPref(int _id){

//        String strPref = mPrefs.getString("MyJson", null);
//        Gson gson = new Gson();
//
//        List<phoneBookItemObject> sectionlist = gson.fromJson(strPref, new TypeToken<List<phoneBookItemObject>>(){}.getType());
//        ArrayList<phoneBookItemObject> itemArrayList = new ArrayList<phoneBookItemObject>(sectionlist);
//
//        int len = itemArrayList.size();
//        for(int i=0; i<len; i++){
//            if(itemArrayList.get(i).get_id() == _id){
//                itemArrayList.remove(i);
//            }
//        }
//
//        String json = gson.toJson(itemArrayList);
//        editor.putString("MyJson", json);
//        editor.commit();

    }

    public void updateItemFromInPref(int _id, String name,  String addr, String telNumber, String dataStore){
//        String strPref = mPrefs.getString("MyJson", null);
//        Gson gson = new Gson();
//
//        List<phoneBookItemObject> sectionlist = gson.fromJson(strPref, new TypeToken<List<phoneBookItemObject>>(){}.getType());
//        ArrayList<phoneBookItemObject> itemArrayList = new ArrayList<phoneBookItemObject>(sectionlist);
//
//        int len = itemArrayList.size();
//        for(int i=0; i<len; i++){
//            if(itemArrayList.get(i).get_id() == _id){
//                itemArrayList.set(i,new phoneBookItemObject(_id,name,telNumber,addr, dataStore));
//            }
//        }
//
//        String json = gson.toJson(itemArrayList);
//        editor.putString("MyJson", json);
//        editor.commit();
    }


//    public ArrayList getJsonFileFromPref(){
//        String strPref = mPrefs.getString("MyJson", "");
//        Gson gson = new Gson();
//
////        if(strPref != null) {
//        if(!strPref.equals("")||!strPref.equals("[]")) {
//            // do some thing
//
//            String result = mPrefs.getString("MyJson","");
//            if(result.equals("")){
//                return new ArrayList<phoneBookItemObject>();
//            }
//            List<phoneBookItemObject> sectionlist = gson.fromJson(strPref, new TypeToken<List<phoneBookItemObject>>(){}.getType());
//            ArrayList<phoneBookItemObject> itemArrayList = new ArrayList<phoneBookItemObject>(sectionlist);
//
//            return itemArrayList;
//        }
//        List<phoneBookItemObject> sectionlist = gson.fromJson(strPref, new TypeToken<List<phoneBookItemObject>>(){}.getType());
//        ArrayList<phoneBookItemObject> itemArrayList = new ArrayList<phoneBookItemObject>(sectionlist);
//        return itemArrayList;

//    }
}
