package Cart;

/**
 * Created by srikrishna on 19-04-2015.
 */
public class CartItemsClass {


    private String cartitemname;
    private int itemquantity;





    public CartItemsClass(String itemtitle) {

        this.cartitemname = itemtitle;

    }

    //   public String getItemcateg(){ return itemcateg;}
    public String getcartItemname() {
        return cartitemname;
    }
    public int getQuantity(){return itemquantity;
    }


}



