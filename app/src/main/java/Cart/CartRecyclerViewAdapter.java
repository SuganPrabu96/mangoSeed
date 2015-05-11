package Cart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

import e_commerce.e_commerce.Master;
import e_commerce.e_commerce.R;


/**
 * Created by srikrishna on 19-04-2015.
 */
public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartCardViewHolder> {


    private ArrayList<CartItemsClass> listitems;
    private Context context;
    private ImageButton removeFromCart;

    public CartRecyclerViewAdapter(ArrayList<CartItemsClass> items,Context context){
        this.listitems = items;
        this.context = context;
    }

    public CartCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cart_card, parent, false);
        CartCardViewHolder vH = new CartCardViewHolder(context, v);

        removeFromCart = (ImageButton) v.findViewById(R.id.cart_removebutton);

        return vH;
    }
    @Override
    public void onBindViewHolder(final CartCardViewHolder viewHolder,final int position) {
        final CartItemsClass item = listitems.get(position);

        viewHolder.itemname.setText(item.getcartItemname());

        removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Master.removefrom_cart(position);

            }
        });

    }

    public void add(CartItemsClass item){
        listitems.add(item);
        notifyItemInserted(listitems.indexOf(item));
    }

    public void remove(int position){
        listitems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listitems.size()-position+1);//TODO have to modify this line

    }

    public int getItemCount() {
        return listitems == null ? 0 : listitems.size();
    }

}

