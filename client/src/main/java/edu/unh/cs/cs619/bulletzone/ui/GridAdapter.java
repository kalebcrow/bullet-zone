package edu.unh.cs.cs619.bulletzone.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.game.TileFactory;
import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;

@EBean
public class GridAdapter extends BaseAdapter {

    private final Object monitor = new Object();
    @SystemService
    protected LayoutInflater inflater;
    private GroundTile[][] mEntities = new GroundTile[256][2];

    /**
     * updateList: updates the stored list of tiles via the new
     * array of BlankTiles
     * @param tiles
     */
    public void updateList(GroundTile[][] tiles) {
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

        if (convertView instanceof RelativeLayout) { //ImageView) {
            //GroundTile terrainTile =
            synchronized (monitor) {
                // making it so there are multiple images in the cells
                RelativeLayout relativeLayout = (RelativeLayout) convertView;
                relativeLayout.setPadding(0,0,0,0);
                ImageView terrain = (ImageView) relativeLayout.getChildAt(0);
                ImageView item = (ImageView) relativeLayout.getChildAt(1);
                //ImageView vehicle = (ImageView) relativeLayout.getChildAt(2);

                if (mEntities[position][0] != null) {
                    // check cell is not null then set terrain
                    terrain.setImageResource(mEntities[position][0].getResourceID());//terrain.setBackgroundResource(mEntities[position].getTerrain());
                    // check for improvements (just walls right now)
                    item.setImageResource(mEntities[position][1].getResourceID());

                    //terrain.setRotation(mEntities[position][0].getOrientation()/2 * 90);
                    item.setRotation(mEntities[position][1].getOrientation()/2 * 90);

                } else {
                    // somethings wrong
                    terrain.setImageResource(R.drawable.blank);
                }
            }
        }

        return convertView;
    }
}


