package ItemDisplay;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;

import e_commerce.e_commerce.Master;
import e_commerce.e_commerce.R;

/**
 * Created by Suganprabu on 18-04-2015.
 */
public class ItemsCardAdapter extends RecyclerView.Adapter<ViewHolderItems>{

    private ArrayList<ItemDetailsClass> items;
    private Context context;
    private Button addBtn;
    private FrameLayout itemCardFrame;

    public ItemsCardAdapter(ArrayList<ItemDetailsClass> items, Context context) {
        this.items = items;

        this.context = context;
    }

    @Override
    public ViewHolderItems onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.items_card_front, parent, false);
        final ViewHolderItems vH = new ViewHolderItems(context, v);

        itemCardFrame = (FrameLayout) v.findViewById(R.id.item_card_frame);
        addBtn = (Button) v.findViewById(R.id.buttonAdd);

        return vH;
    }

    @Override
    public void onBindViewHolder(final ViewHolderItems viewHolder, final int position) {
        final ItemDetailsClass item = items.get(position);

        viewHolder.itemname.setText(item.getItemtitle());
        viewHolder.itemimg.setImageResource(Integer.parseInt(item.getItemimgurl()));
        viewHolder.selPrice.setText(item.getItemprice().toString());

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Master.addtocart_fn(item);
            }
        });

        itemCardFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.arg1 = 1;
                Bundle b = new Bundle();
                b.putString("name",item.getItemtitle());
                b.putString("image",item.getItemimgurl());
                b.putDouble("price",item.getItemprice());
                b.putDouble("MRP",item.getItemMRP());
                msg.setData(b);
                Master.itemDetailsHandler.sendMessage(msg);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

}

