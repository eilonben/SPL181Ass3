package bgu.spl181.net.impl.protocol;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.api.SharedDataBase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class UserTextProtocol implements BidiMessagingProtocol<String> {
    protected Connections<String> connections;
    protected int connectionID;
    private SharedDataBase db;
    private boolean shouldTerminate;

    public UserTextProtocol(SharedDataBase db) {
        this.db = db; //shared data base
        this.shouldTerminate = false;
    }

    public void start(int connectionId, Connections<String> connections) {
        this.connections = connections;
        this.connectionID = connectionId;


    }

    public void process(String msg) { // handling the msg recived
        boolean succeeded;
        String ackResponse;
        String errorResponse;
        String[] cmd = msg.split(" ", 4);
        switch (cmd[0]) {
            case ("REGISTER"):
                register(msg); // abstract function
                break;
            case ("LOGIN"):
                if (cmd.length>2){
                    succeeded = db.login(connectionID, cmd[1], cmd[2]); // sends a login request to the database
                    ackResponse = "ACK login succeeded";
                    errorResponse = "ERROR login failed";
                    if (succeeded) { // ACK if succeeded
                        connections.send(connectionID, ackResponse);
                    } else {           //Erore if not
                    connections.send(connectionID, errorResponse);
                    }
                }
                break;
            case ("SIGNOUT"):
                ackResponse = "ACK signout succeeded";
                errorResponse = "ERROR signout failed";
                boolean signout = db.signout(connectionID); //sends signout request to the database
                if (signout) {
                    connections.send(connectionID, ackResponse);
                    shouldTerminate = true;   //now should terminate
                    connections.disconnect(connectionID); // sends Ack response
                } else {
                    connections.send(connectionID, errorResponse);
                }
                break;

            case ("REQUEST"): // execute request = abstract function
                String response = executeRequest(msg);
                connections.send(connectionID, response);
                break;

        }


    }

    protected void sendBroadcast(String message) {
        ConcurrentHashMap<Integer, String> logged = db.getLoggedUsers(); //sends broadcast message to all logged in users
        for (Map.Entry<Integer, String> entry : logged.entrySet()) {
            connections.send(entry.getKey(), "BROADCAST " + message);
        }
    }

    public abstract void register(String msg);

    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public abstract String executeRequest(String command);
}
