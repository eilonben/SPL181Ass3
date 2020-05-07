package bgu.spl181.net.impl.database;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UsersData {

    @SerializedName("users")
    @Expose
    private List<UserJson> users = null;

    public List<UserJson> getUsers() {
        return users;
    }

}