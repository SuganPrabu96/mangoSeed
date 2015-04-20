package Cart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import e_commerce.e_commerce.R;

/**
 * Created by srikrishna on 19-04-2015.
 */
public class CartCardViewHolder extends RecyclerView.ViewHolder{

    public TextView itemname;
    private Context context;


    public CartCardViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        itemname = (TextView) itemView.findViewById(R.id.itemname);

    }}
