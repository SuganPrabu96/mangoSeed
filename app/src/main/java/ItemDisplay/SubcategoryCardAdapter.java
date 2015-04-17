package ItemDisplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import e_commerce.e_commerce.R;

/**
 * Created by srikrishna on 03-04-2015.
 */
public class SubcategoryCardAdapter extends RecyclerView.Adapter<ViewHolderSubcateg> {

    private ArrayList<SubcategoryCardClass> items;
    private Context context;

    public SubcategoryCardAdapter(ArrayList<SubcategoryCardClass> items, Context context) {
        this.items = items;

        this.context = context;
    }

    @Override
    public ViewHolderSubcateg onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.subcategory_card, parent, false);
        ViewHolderSubcateg vH = new ViewHolderSubcateg(context, v);
        return vH;
    }

    @Override
    public void onBindViewHolder(final ViewHolderSubcateg viewHolder, final int position) {
        final SubcategoryCardClass item = items.get(position);


        viewHolder.subcategname.setText(item.getSubcategtitle());
        viewHolder.subcategimg.setImageResource(item.getSubcategimg());
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

}