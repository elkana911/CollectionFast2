package id.co.ppu.collectionfast2.component;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;

/**
 * Created by Eric on 27-Sep-16.
 */

public class RVBAdapter extends ArrayAdapter<TrnRVB> {
    private Context ctx;
    private List<TrnRVB> list;

    public RVBAdapter(Context context, int resource, List<TrnRVB> objects) {
        super(context, resource, objects);
        this.ctx = context;
        this.list = objects;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public TrnRVB getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = new TextView(this.ctx);
//            TextView tv = (TextView) convertView.findViewById(R.id.nama);
        tv.setPadding(10, 10, 10, 10);
        tv.setTextColor(Color.BLACK);
        tv.setText(list.get(position).getRvbNo());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        return tv;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(this.ctx);
        label.setPadding(10, 10, 10, 10);
        label.setText(list.get(position).getRvbNo());
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        return label;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
