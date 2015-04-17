package ItemDisplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import e_commerce.e_commerce.R;

//import com.example.srikrishna.startupapp.FirstPageAfterLogin;

/**
 * Created by srikrishna on 07-02-2015.
 */
public class CardAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ArrayList<ItemDetailsClass> items;

    private Context context;

    public CardAdapter(ArrayList<ItemDetailsClass> items, Context context) {
        this.items = items;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_layout, parent, false);
        ViewHolder vH = new ViewHolder(context, v);
        return vH;

//

    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final ItemDetailsClass item = items.get(position);

        final String[] actions = new String[]{"Amount",
                "100g",
                "250g",
                "500g"};


//        /** Create an array adapter to populate dropdownlist */
//        ArrayAdapter<String> adapter;
//        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, actions);
//
//
//        /** Enabling dropdown list navigation for the action bar */
//      .setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//
//        /** Defining Navigation listener */
//        ActionBar.OnNavigationListener navigationListener = new ActionBar.OnNavigationListener() {
//
//
//            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
//               // functioncall(actions[itemPosition]);
//                 Toast.makeText(this, "You selected : " + actions[itemPosition], Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        };
//
//        /** Setting dropdown items and item navigation listener for the actionbar */
//        .setListNavigationCallbacks(adapter, navigationListener);
        //       findViewById(R.id.card_view);

//        Spinner spinner = (Spinner) viewHolder.itemView.findViewById(R.id.spinner);
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
//                R.array.amount_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);

//
//        viewHolder.itemView.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
//
//
//            @Override
//            public void onClick(View v) {
////Pos starts from zero ; where zero is 1st card
//
//
//                //Toast.makeText(context, "Item clicked", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(context, StickyHeaderScrollViewActivity.class);
//                intent.putExtra("pos",position );
//                intent.putExtra("categ",events.getEventcateg());
//                intent.putExtra("schedule",events.getEventsch());
//                intent.putExtra("title",events.getEventtitle());
//                intent.putExtra("description",events.getShortdesc());
//                intent.putExtra("format",events.getEventformat());
//                intent.putExtra("faq",events.getEventfaqfull());
//                intent.putExtra("coord",events.getCoorddetails());
//
//
//
//                context.startActivity(intent);
//            }
//        });


//        viewHolder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        // String[] loc;
//        loc = new String[]{"#EF5350","#2196F3","#FF5722","#AB47BC","#1B5E20"};
//        Random r = new Random();
//        int col = r.nextInt(5);
//        String s = loc[col];
//        viewHolder.back.setBackgroundColor(Color.parseColor(s));


        viewHolder.itemname.setText(item.getItemtitle());


        viewHolder.itemimg.setImageResource(R.drawable.goldwinner);
        //       LightingColorFilter lcf = new LightingColorFilter( 0xFFFFFFFF , 0x0000FF00 );


//        viewHolder.itemimg.setColorFilter(lcf);

        //   setitemimage check how to do that;

    }


    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}