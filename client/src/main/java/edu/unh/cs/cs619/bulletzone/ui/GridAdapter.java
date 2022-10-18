package edu.unh.cs.cs619.bulletzone.ui;

import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

        if (convertView instanceof ImageView) {
            synchronized (monitor) {

                ImageView imageView = (ImageView) convertView;
                if (position % 5 == 0) {
                    imageView.setImageResource(R.drawable.tank);
                } else if (position % 5 == 1) {
                    imageView.setImageResource(R.drawable.stonewall);
                } else if (position % 5 == 2) {
                    imageView.setImageResource(R.drawable.ironwall);
                } else if (position % 5 == 3) {
                    imageView.setImageResource(R.drawable.bullet);
                } else if (position % 5 == 4) {
                    imageView.setImageResource(R.drawable.road);
                }
                imageView.setAdjustViewBounds(true);

            }
        }


        return convertView;
    }
}


