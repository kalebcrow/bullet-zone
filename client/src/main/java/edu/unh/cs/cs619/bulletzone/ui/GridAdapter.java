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

    /**
     * updateList: updates the stored list of tiles via the new
     * array of BlankTiles
     * @param tiles
     */
    public void updateList(BlankTile[] tiles) {
        synchronized (monitor) {

            mEntities = tiles;
            this.notifyDataSetChanged();
        }
    }

    /**
     * getCount: returns the number of tiles on the board
     * (always 256, the nuble of tiles in the grid)
     * @return
     */
    @Override
    public int getCount() {
        return 16 * 16;
    }

    /**
     * getItem: returns the tile at the requested location
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return mEntities[position];
    }

    /**
     * returns the
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * getView: Sets the image and orientation of said image of the tile at the
     * location specified by index.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
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


