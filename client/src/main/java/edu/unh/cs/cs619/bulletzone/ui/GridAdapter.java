package edu.unh.cs.cs619.bulletzone.ui;

import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;

@EBean
public class GridAdapter extends BaseAdapter {

    private final Object monitor = new Object();
    @SystemService
    protected LayoutInflater inflater;
    private BlankTile[] mEntities = new BlankTile[256];

    public void updateList(BlankTile[] tiles) {
        synchronized (monitor) {

            mEntities = tiles;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return 16 * 16;
    }

    @Override
    public Object getItem(int position) {
        return mEntities[position];
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

        if (convertView instanceof ImageView) {
            synchronized (monitor) {
                ImageView imageView = (ImageView) convertView;
                if (mEntities[position] != null) {
                    imageView.setImageResource(mEntities[position].getResourceID());
                    imageView.setRotation(mEntities[position].getOrientation()/2 * 90);

                } else {
                    imageView.setImageResource(R.drawable.blank);
                }
            }
        }

        return convertView;
    }
}


