package bgu.spl181.net.impl.database;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserJson {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("movies")
    @Expose
    private List<RentedMovie> movies = null;
    @SerializedName("balance")
    @Expose
    private String balance;

    public UserJson(String username,String password, String country){
        this.username= username;
        this.type="normal";
        this.password= password;
        this.country= country;
        this.movies=new ArrayList<>() ;
        this.balance="0";
    }
    public String getUsername() {
        return username;
    }


    public String getType() {
        return type;
    }


    public String getPassword() {
        return password;
    }


    public String getCountry() {
        return country;
    }


    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public void addRentedMovie(String id, String name) {
        RentedMovie toAdd = new RentedMovie(id, name);
        if (movies == null) {
            movies = new ArrayList<>();
        }
        movies.add(toAdd);
    }

    public boolean returnRentedMovie(String id) {
        RentedMovie toReturn = null;
        for (RentedMovie r: movies){
            if (r.getId().equals(id)) {
                toReturn = r;
                break;
            }
        }
        if (toReturn!=null) {
            movies.remove(toReturn);
            return true;
        }
        return false;
    }

    public boolean containsMovie(String moviename){
        for(RentedMovie r : movies){
            if (r.getName().equals(moviename))
                return true;
        }
        return false;
    }
}

