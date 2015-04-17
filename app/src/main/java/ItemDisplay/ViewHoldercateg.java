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
public class ViewHoldercateg extends RecyclerView.ViewHolder {
    public TextView categname;
    public ImageView categimg;
    private Context context;


    public ViewHoldercateg(Context context, View itemView) {
        super(itemView);
        this.context = context;
        categname = (TextView) itemView.findViewById(R.id.catname);
        categimg = (ImageView) itemView.findViewById(R.id.catimg);

    }
}

