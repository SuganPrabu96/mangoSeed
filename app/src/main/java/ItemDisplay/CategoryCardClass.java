package ItemDisplay;

/**
 * Created by srikrishna on 03-04-2015.
 */
public class CategoryCardClass {

    private String categtitle;

    private int categimg;


    public CategoryCardClass(String categtitle, int categimg) {

        this.categtitle = categtitle;
        this.categimg = categimg;
    }

    //   public String getItemcateg(){ return itemcateg;}
    public String getCategtitle() {
        return categtitle;
    }

    public int getCategimg() {
        return categimg;
    }
}

