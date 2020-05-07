package bgu.spl181.net.impl.protocol;

import bgu.spl181.net.impl.database.MoviesSharedProtocolData;

import java.util.ArrayList;

public class MovieRentalProtocol extends UserTextProtocol {
    private MoviesSharedProtocolData database;

    public MovieRentalProtocol(MoviesSharedProtocolData database) {
        super(database);
        this.database = database;
    }

    @Override
    public void register(String msg) { //should have name password and country
        boolean succeeded;
        String ackResponse;
        String errorResponse;
        String[] cmd = msg.split(" ", 4);
        if (cmd.length < 4 ) //not enough variables
            succeeded = false;
        else if ( !cmd[3].contains("country="))
            succeeded = false;
        else if (cmd.length == 4) {
            String country = cmd[3].substring(cmd[3].indexOf("\"") + 1, cmd[3].length() - 1);
            succeeded = database.register(connectionID, cmd[1], cmd[2], country);
        } else
            succeeded = false;
        ackResponse = "ACK registration succeeded";
        errorResponse = "ERROR registration failed";
        if (succeeded) { //if login succeeded
            connections.send(connectionID, ackResponse);
        } else {
            connections.send(connectionID, errorResponse);
        }
    }

    public String executeRequest(String msg) { //each data base has its own requests
        String response = null;
        String moviename = "";
        String message = "";
        String[] cmd = msg.split(" ", 3);
        switch (cmd[1]) {
            case ("balance"):
                if (cmd[2].equals("info")) {
                    response = database.getBalance(connectionID); //sends balance request to the DB
                    if (response == null)
                        message = "ERROR request balance failed";
                    else
                        message = "ACK balance " + response;
                    break;
                }
                if (cmd[2].substring(0, cmd[2].indexOf(" ")).equals("add")) { // checks if the request if type balance add
                    response = database.addBalance(connectionID, cmd[2].substring(cmd[2].indexOf(" ") + 1));
                    if (response == null)
                        message = "ERROR request balance failed";
                    else
                        message = "ACK balance " + response + " added" + cmd[2].substring(cmd[2].indexOf(" "));
                    break;
                }
            case ("info"):
                if (cmd.length < 3) { // checks if asked about all movies
                    response = database.returnAllMovies(connectionID);
                    if (response == null)
                        message = "ERROR request info failed";
                    else
                        message = "ACK info " + response;
                } else { //specific movie info
                    response = database.returnMovieInfo(connectionID, cmd[2].substring(1, cmd[2].length() - 1));
                    if (response == null)
                        message = "ERROR request info failed";
                    else
                        message = "ACK info " + response;
                }
                break;

            case ("rent"):
                response = database.rentMovie(connectionID, cmd[2].substring(1, cmd[2].length() - 1));
                if (response == null)
                    message = "ERROR request rent failed";
                else {
                    message = "ACK rent " + cmd[2] + " success";
                    sendBroadcast("movie " + response);
                }
                break;

            case ("return"):
                response = database.returnMovie(connectionID, cmd[2].substring(1, cmd[2].length() - 1));
                if (response == null)
                    message = "ERROR request return failed";
                else {
                    message = "ACK return " + cmd[2] + " success";
                    sendBroadcast("movie " + response);
                }
                break;

            case ("addmovie"):
                cmd[2] = cmd[2].substring(1); // first " off
                moviename = cmd[2].substring(0, cmd[2].indexOf("\"")); //removing " " from movie name
                cmd[2] = cmd[2].substring(cmd[2].indexOf("\"") + 2); // there is "+space after the first "
                String[] params = cmd[2].split(" ", 3);
                if (params.length == 2) { // there are no banned countries
                    response = database.addMovie(connectionID, moviename, params[0], params[1], null);
                } else {
                    ArrayList<String> countries = getCountries(params[2]);
                    response = database.addMovie(connectionID, moviename, params[0], params[1], countries);
                }
                if (response == null)
                    message = "ERROR request addmovie failed";
                else {
                    message = "ACK addmovie \"" + moviename + "\" success";
                    sendBroadcast("movie " + response);
                }
                break;
            case ("remmovie"): // remove movie only by admin
                response = database.removeMovie(connectionID, cmd[2].substring(1, cmd[2].length() - 1));
                if (response == null)
                    message = "ERROR request remmovie failed";
                else {
                    message = "ACK remmovie " + cmd[2] + " success";
                    sendBroadcast("movie " + cmd[2] + " removed");
                }
                break;
            case ("changeprice"): //change price of movie - only be admin
                cmd[2] = cmd[2].substring(1);
                moviename = cmd[2].substring(0, cmd[2].indexOf("\""));

                cmd[2] = cmd[2].substring(cmd[2].indexOf("\"") + 2);
                response = database.changePrice(connectionID, moviename, cmd[2]);
                if (response == null)
                    message = "ERROR request changeprice failed";
                else {
                    message = "ACK changeprice \"" + moviename + "\" success";
                    sendBroadcast("movie " + response);
                }
        }
        return message;
    }

    private ArrayList<String> getCountries(String toParse) { //returns a list of banned countries
        if (toParse.length() == 0) {
            return null;
        }
        ArrayList<String> countries = new ArrayList<>();
        while (!toParse.contains("\"")) {
            toParse = toParse.substring(1);
            String country = toParse.substring(0, toParse.indexOf("\""));
            countries.add(country.substring(0, country.length() - 1));
            if (toParse.indexOf("\"") == toParse.length() - 1)
                break;
            toParse = toParse.substring(toParse.indexOf("\"") + 2);

        }
        return countries;
    }
}

