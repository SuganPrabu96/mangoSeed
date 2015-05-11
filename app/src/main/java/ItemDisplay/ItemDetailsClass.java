package ItemDisplay;

/**
 * Created by srikrishna on 06-03-2015.
 */
public class ItemDetailsClass {

    private String itemtitle;

    private String itemimgurl = "none";

    private Double itemprice;

    private Double itemMRP;


    public ItemDetailsClass(String itemtitle, String itemimgurl, Double itemprice, Double itemMRP) {

        this.itemtitle = itemtitle;
        this.itemimgurl = itemimgurl;
        this.itemprice = itemprice;
        this.itemMRP = itemMRP;
    }

    //   public String getItemcateg(){ return itemcateg;}
    public String getItemtitle() {
        return itemtitle;
    }

    public String getItemimgurl() {
        return itemimgurl;
    }

    public Double getItemprice() { return itemprice; }

    public Double getItemMRP() { return itemMRP; }
}
