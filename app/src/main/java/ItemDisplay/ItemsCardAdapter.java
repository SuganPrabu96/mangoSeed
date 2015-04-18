package ItemDisplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import e_commerce.e_commerce.R;

/**
 * Created by Suganprabu on 18-04-2015.
 */
public class ItemsCardAdapter extends RecyclerView.Adapter<ViewHolderItems>{

    private ArrayList<ItemDetailsClass> items;
    private Context context;

    public ItemsCardAdapter(ArrayList<ItemDetailsClass> items, Context context) {
        this.items = items;

        this.context = context;
    }

    @Override
    public ViewHolderItems onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.items_card, parent, false);
        ViewHolderItems vH = new ViewHolderItems(context, v);
        return vH;
    }

    @Override
    public void onBindViewHolder(final ViewHolderItems viewHolder, final int position) {
        final ItemDetailsClass item = items.get(position);


        viewHolder.itemname.setText(item.getItemtitle());
        viewHolder.itemimg.setImageResource(Integer.parseInt(item.getItemimgurl()));


    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

}

