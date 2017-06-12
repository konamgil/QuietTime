package quiettimev1.konamgil.com.quiettime.PhoneNumber;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import quiettimev1.konamgil.com.quiettime.DB_Helper.PrefDataHelper;

/**
 * Created by konamgil on 2017-06-08.
 */

public class ContactsInfoDatas {
    private String TAG = getClass().getSimpleName();
    private ArrayList<ContactsInfoObject> mContacts;
    private ArrayList<String> mCheckedNumber;
    private Context mContext;
    private ContentResolver cr;
    private boolean isChecked;

    //체크된 사람들 정보 가져오기위해서 프리퍼런스에서 가져온다
    private PrefDataHelper mPrefDataHelper;

    public ContactsInfoDatas(Context mContext) {
        this.mContext = mContext;
        mPrefDataHelper = new PrefDataHelper(mContext);
        getCheckedNumberList();
        getContactsDatas(); //데이터 가져오기
    }

    /**
     * 체크된 번호 가져오기
     */
    private void getCheckedNumberList(){
        mCheckedNumber = mPrefDataHelper.selectCheckedNumberList();
    }
    /**
     * 전화번호부 목록 가져오기
     */
    private void getContactsDatas(){

        HashMap<Integer, ContactsInfoObject> tempContacts = new LinkedHashMap<>();
        mContacts = new ArrayList<>();
        cr = mContext.getContentResolver(); //컨텐츠 리졸버
        int count = 1;

        //가져올 컬럼
        String[] PROJECTION = new String[]{
                ContactsContract.Data.DISPLAY_NAME, //이름
                ContactsContract.Data.DATA1, //전화번호
                ContactsContract.Data.MIMETYPE //타입
        };

        Cursor cursor = cr.query(
                ContactsContract.Data.CONTENT_URI,
                PROJECTION,
                ContactsContract.Data.MIMETYPE + " = ?" + " OR " + ContactsContract.Data.MIMETYPE + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                "lower(" + ContactsContract.Data.DISPLAY_NAME + ")"
        );

        try {
            final int namePos = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
            final int numNoPos = cursor.getColumnIndex(ContactsContract.Data.DATA1);
            final int mimePos = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);

            while (cursor.moveToNext()){
                String name = cursor.getString(namePos);
                String numNo = cursor.getString(numNoPos);
                String mime = cursor.getString(mimePos);

                if (mime.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                   if(mCheckedNumber!=null) {
                       Loop1:
                       for (int i = 0; i < mCheckedNumber.size(); i++) {
                           if (numNo.equals(mCheckedNumber.get(i))) {
                               isChecked = true;
                               break Loop1;
                           } else {
                               isChecked = false;
                           }
                       }
                   }
                    tempContacts.put(count++, new ContactsInfoObject(name, numNo, isChecked));
                }
            }
        } finally {
            cursor.close();
        }

        for (Map.Entry<Integer, ContactsInfoObject> numberList : tempContacts.entrySet()) {
            mContacts.add(numberList.getValue());
        }
    }

    public ArrayList getContacts() {
        return mContacts;
    }

}
