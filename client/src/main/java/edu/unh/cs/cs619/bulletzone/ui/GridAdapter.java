package edu.unh.cs.cs619.bulletzone.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import edu.unh.cs.cs619.bulletzone.R;

@EBean
public class GridAdapter extends BaseAdapter {

    private final Object monitor = new Object();
    @SystemService
    protected LayoutInflater inflater;
    private int[][] mEntities = new int[16][16];

    public void updateList(int[][] entities) {
        synchronized (monitor) {
            this.mEntities = entities;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return 16 * 16;
    }

    @Override
    public Object getItem(int position) {
        return mEntities[(int) position / 16][position % 16];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.field_item, null);
        }

        int row = position / 16;
        int col = position % 16;

        int val = mEntities[row][col];
// TODO tank widget adjustment here
        if (convertView instanceof TextView) {
            synchronized (monitor) {
                if (val > 0) {
                    if (val == 1000 || (val>1000&&val<=2000)) {
                        ((TextView) convertView).setText("W");
                    } else if (val >= 2000000 && val <= 3000000) {
                        ((TextView) convertView).setText("B");
                    } else if (val >= 10000000 && val <= 20000000) {
                        // we have a tank
                        // next two lines stolen from red's GridAdapter
                        // int passedTankId = (val % 10000000) / 10000;
                        int passedDirection = val % 10;
                        if (passedDirection == 0) {
                            ((TextView) convertView).setText("^");
                        }
                        else if (passedDirection == 2) {
                            ((TextView) convertView).setText(">");
                        }
                        else if (passedDirection == 4) {
                            ((TextView) convertView).setText("v");
                        }
                        else if (passedDirection == 6) {
                            ((TextView) convertView).setText("<");
                        }
                        else {
                            ((TextView) convertView).setText("?");
                        }
                    }
                } else {
                    ((TextView) convertView).setText("");
                }
            }
        }

        return convertView;
    }
}


