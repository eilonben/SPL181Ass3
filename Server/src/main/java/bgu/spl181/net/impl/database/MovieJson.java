package bgu.spl181.net.impl.database;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieJson {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("bannedCountries")
    @Expose
    private List<String> bannedCountries = null;
    @SerializedName("availableAmount")
    @Expose
    private String availableAmount;
    @SerializedName("totalAmount")
    @Expose
    private String totalAmount;

    public MovieJson (String id, String name, String price, String availableAmount, String totalAmount, List<String>bannedCountries){
        this.price=price;
        this.id=id;
        this.name=name;
        this.totalAmount= totalAmount;
        this.availableAmount= availableAmount;
        this.bannedCountries= bannedCountries;
    }
    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getBannedCountries() {
        return bannedCountries;
    }


    public String getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(String availableAmount) {
        this.availableAmount = availableAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String toString(){
        return "\""+name+"\" "+availableAmount+" "+price;
    }

}