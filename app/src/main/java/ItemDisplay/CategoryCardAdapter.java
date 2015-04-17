package ItemDisplay;

import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import e_commerce.e_commerce.Master;
import e_commerce.e_commerce.R;

/**
 * Created by srikrishna on 03-04-2015.
 */
public class CategoryCardAdapter extends RecyclerView.Adapter<ViewHoldercateg> {

    private ArrayList<CategoryCardClass> items;
    private Context context;

    public CategoryCardAdapter(ArrayList<CategoryCardClass> items, Context context) {
        this.items = items;

        this.context = context;
    }

    @Override
    public ViewHoldercateg onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.category_card, parent, false);
        ViewHoldercateg vH = new ViewHoldercateg(context, v);
        return vH;
    }

    @Override
    public void onBindViewHolder(final ViewHoldercateg viewHolder, final int position) {
        final CategoryCardClass item = items.get(position);


        viewHolder.categname.setText(item.getCategtitle());
        viewHolder.categimg.setImageResource(item.getCategimg());

        viewHolder.itemView.findViewById(R.id.card_view2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Message categoryMsg = new Message();
                categoryMsg.arg1 = 1;
                categoryMsg.arg2 = position;
                Master.ProductsFragment.categoryMsgHandler.sendMessage(categoryMsg);
            }
        });

/*
        viewHolder.itemView.findViewById(R.id.card_view2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(context,"Testing" , Toast.LENGTH_LONG).show();

                LayoutInflater inflater = (LayoutInflater) context.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);

                final ListView listView = Master.ProductsFragment.subcategoryListView;

                Message msg = new Message();
                msg.arg1 = 0;
                msg.arg2 = 1;
                Master.ProductsFragment.mHandler.sendMessage(msg);

                if (position == 0) {
                    ArrayAdapter<String> adapterbev = new ArrayAdapter<String>(context,
                            android.R.layout.simple_list_item_1, android.R.id.text1, beverages);
                    listView.setAdapter(adapterbev);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position,
                                                long id) {

                            // ListView Clicked item index
                            int itemPosition = position;

                            // ListView Clicked item value
                            String itemValue = (String) listView.getItemAtPosition(position);

                            if (itemPosition == 0) {
                                ArrayAdapter<String> subbev1 = new ArrayAdapter<String>(context,
                                        android.R.layout.simple_list_item_1, android.R.id.text1, subcategbev1);
                                listView.setAdapter(subbev1);
                            }


                        }

                    });
                }

            }});
*/
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

}
