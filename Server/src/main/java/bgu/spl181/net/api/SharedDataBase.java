package bgu.spl181.net.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface SharedDataBase {

    boolean login(int ConnectionID , String userName, String password);

    boolean register(int ConnectionID ,String userName, String password,String dataBlock);

    boolean signout(int ConnectionID );

    ConcurrentHashMap<Integer, String> getLoggedUsers();
}
