package ItemDisplay;

/**
 * Created by srikrishna on 03-04-2015.
 */
public class SubcategoryCardClass {

    private String subcategtitle;

    private int subcategimg;


    public SubcategoryCardClass(String subcategtitle, int subcategimg) {

        this.subcategtitle = subcategtitle;
        this.subcategimg = subcategimg;
    }

    //   public String getItemcateg(){ return itemcateg;}
    public String getSubcategtitle() {
        return subcategtitle;
    }

    public int getSubcategimg() {
        return subcategimg;
    }
}

