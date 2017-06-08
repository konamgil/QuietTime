package quiettimev1.konamgil.com.quiettime.PhoneNumber;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by konamgil on 2017-06-08.
 */

public class ContactsInfoDatas {
    private String TAG = getClass().getSimpleName();
    private ArrayList<ContactsInfoObject> mContacts;
    private Context mContext;
    private ContentResolver cr;

    public ContactsInfoDatas(Context mContext) {
        this.mContext = mContext;
        getContactsDatas(); //데이터 가져오기
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
                    tempContacts.put(count++, new ContactsInfoObject(name, numNo));
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
