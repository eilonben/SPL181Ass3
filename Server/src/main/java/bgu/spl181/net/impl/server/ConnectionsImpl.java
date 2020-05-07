package bgu.spl181.net.impl.server;

import bgu.spl181.net.api.bidi.ConnectionHandler;
import bgu.spl181.net.api.bidi.Connections;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {
    Map<Integer,ConnectionHandler<T>> clients;


   public ConnectionsImpl(){
       clients = new ConcurrentHashMap<>();

    }
    @Override
    public boolean send (int connectionId, T msg){
       if (!clients.containsKey(connectionId))
           return false;
       clients.get(connectionId).send(msg);
       return true;
    }

    @Override
    public void broadcast(T msg) {
        for (Map.Entry<Integer,ConnectionHandler<T>> client :clients.entrySet()) {
            client.getValue().send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        clients.remove(connectionId);
    }

    public void connect(int connectionId, ConnectionHandler<T> handler){
       clients.put(new Integer(connectionId), handler);
    }
}
