package quiettimev1.konamgil.com.quiettime.DB_Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import quiettimev1.konamgil.com.quiettime.PhoneNumber.ContactsInfoDatas;
import quiettimev1.konamgil.com.quiettime.PhoneNumber.ContactsInfoObject;
import quiettimev1.konamgil.com.quiettime.R;

/**
 * Created by konamgil on 2017-06-09.
 */

public class ContactsAdapter extends BaseAdapter {
    private Context mContext;
    private ContactsInfoDatas mContactsInfoDatas;
    private ArrayList<ContactsInfoObject> mArrayList;
    private View vi;
    private ViewHolder viewHolder;

    public ContactsAdapter(Context mContext) {
        this.mContext = mContext;
        mContactsInfoDatas = new ContactsInfoDatas(mContext);
        mArrayList = mContactsInfoDatas.getContacts();

    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        vi = convertView;
        ContactsInfoObject contact_item = mArrayList.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.item_contact, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.name = (TextView) vi.findViewById(R.id.tvName);
            viewHolder.tel = (TextView) vi.findViewById(R.id.tvPhoneNumber);
            viewHolder.checkBox = (CheckBox) vi.findViewById(R.id.cbSelect);
            vi.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
            viewHolder.name.setText(contact_item.getName());
            viewHolder.tel.setText(contact_item.getTeleNumber());
            if (contact_item.isCheckbox()) {
                viewHolder.checkBox.setChecked(true);
            } else {
                viewHolder.checkBox.setChecked(false);
            }

            return vi;
    }

    public ArrayList<ContactsInfoObject> getAllData(){
        return mArrayList;
    }

    public void setCheckBox(int position){
        //Update status of checkbox
        ContactsInfoObject items = mArrayList.get(position);
        items.setCheckbox(!items.isCheckbox());
        notifyDataSetChanged();
    }

    public class ViewHolder{
        TextView name;
        TextView tel;
        CheckBox checkBox;
    }
}
