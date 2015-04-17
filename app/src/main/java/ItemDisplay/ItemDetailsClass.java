package ItemDisplay;

/**
 * Created by srikrishna on 06-03-2015.
 */
public class ItemDetailsClass {

    private String itemtitle;

    private String itemimgurl = "none";


    public ItemDetailsClass(String itemtitle, String itemimgurl) {

        this.itemtitle = itemtitle;
        this.itemimgurl = itemimgurl;
    }

    //   public String getItemcateg(){ return itemcateg;}
    public String getItemtitle() {
        return itemtitle;
    }

    public String getItemimgurl() {
        return itemimgurl;
    }
}
