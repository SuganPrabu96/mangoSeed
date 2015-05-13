package ItemDisplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import e_commerce.e_commerce.R;

/**
 * Created by srikrishna on 03-04-2015.
 */
public class ViewHolderSubcateg extends RecyclerView.ViewHolder {
    private Context context;
    public TextView subcategname;
    public ImageView subcategimg;




    public ViewHolderSubcateg(Context context, View itemView) {
        super(itemView);
        this.context = context;
        subcategname = (TextView) itemView.findViewById(R.id.subcatname);
        subcategimg = (ImageView) itemView.findViewById(R.id.subcatimg);

    }}

