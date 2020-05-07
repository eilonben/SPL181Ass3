package bgu.spl181.net.impl.database;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RentedMovie {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;

    public RentedMovie(String id, String name){
        this.id=id;
        this.name=name;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


}
