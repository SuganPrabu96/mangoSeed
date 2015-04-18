package ItemDisplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import e_commerce.e_commerce.R;

/**
 * Created by Suganprabu on 18-04-2015.
 */
public class ViewHolderItems extends RecyclerView.ViewHolder{
    public TextView itemname;
    public ImageView itemimg;
    private Context context;


    public ViewHolderItems(Context context, View itemView) {
        super(itemView);
        this.context = context;
        itemname = (TextView) itemView.findViewById(R.id.itemname);
        itemimg = (ImageView) itemView.findViewById(R.id.itemimg);

    }
}
