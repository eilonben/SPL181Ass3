package bgu.spl181.net.impl.database;


import bgu.spl181.net.api.SharedDataBase;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MoviesSharedProtocolData implements SharedDataBase {
    private MoviesData mDataBase;
    private UsersData uDataBase;
    private ConcurrentHashMap<Integer, String> loggedInUsers;
    private ConcurrentHashMap<String, UserJson> usersMap;
    private ConcurrentHashMap<String, MovieJson> moviesMap;
    private Gson gson;
    private int currID;
    private String userAddr;
    private String movieAddr;


    public MoviesSharedProtocolData(String usersFileAddress, String moviesFileAddress) {
        gson = new Gson();
        userAddr = usersFileAddress;
        movieAddr = moviesFileAddress;
        try {
            FileReader usersFile = new FileReader(usersFileAddress);
            FileReader moviesFile = new FileReader(moviesFileAddress);
            mDataBase = gson.fromJson(moviesFile, MoviesData.class);
            uDataBase = gson.fromJson(usersFile, UsersData.class);
            usersFile.close();
            moviesFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.usersMap = new ConcurrentHashMap<>();
        this.moviesMap = new ConcurrentHashMap<>();
        this.loggedInUsers = new ConcurrentHashMap<>();
        List<UserJson> usersList = uDataBase.getUsers();
        List<MovieJson> moviesList = mDataBase.getMovies();
        for (UserJson user : usersList) {
            usersMap.put(user.getUsername(), user);
        }
        for (MovieJson movie : moviesList) {
            moviesMap.put(movie.getName(), movie);
        }
        currID = moviesList.size();
    }

    public ConcurrentHashMap<Integer, String> getLoggedUsers() {
        return loggedInUsers;
    }

    public synchronized boolean login(int ConnectionID, String userName, String password) {
        if (!usersMap.containsKey(userName) || loggedInUsers.containsKey(ConnectionID) || loggedInUsers.contains(userName)) {
                return false; //checks if the user is not register/ already logged in in this acount / other acount
            }
            if (!usersMap.get(userName).getPassword().equals(password)) {
                return false; // checks if the password is correct
            }
            loggedInUsers.put(ConnectionID, userName); // adds the client to the logged in users
            return true;
      


    }

    public synchronized boolean register(int ConnectionID, String userName, String password, String country) {


            if (usersMap.containsKey(userName) || loggedInUsers.containsKey(ConnectionID)) { // checks if the user is already logged in or register
                return false;
            }
            UserJson userJson = new UserJson(userName, password, country);
            uDataBase.getUsers().add(userJson);
            usersMap.put(userName, userJson);
            writeToUsers();
            return true;
    }

    public synchronized boolean signout(int ConnectionID) {

            if (!loggedInUsers.containsKey(ConnectionID)) {
                return false; // checks if logged in
            }
            loggedInUsers.remove(ConnectionID); // removes from logged in users

            return true;

    }

    //write to json file
    private void writeToUsers() {
        try (FileWriter fw = new FileWriter(userAddr)) { //updates the user json
            gson.toJson(uDataBase, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  void writeToMovies() {
        try (FileWriter fw = new FileWriter(movieAddr)) { //updates the movie json
            gson.toJson(mDataBase, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String getBalance(int connectionID) { //returns the current balance
            if (!loggedInUsers.containsKey(connectionID)) { //checks if logged in
                return null;
            }
            String userid = loggedInUsers.get(connectionID);
            return usersMap.get(userid).getBalance(); // get his balance

    }

    public synchronized String addBalance(int connectionID, String amount) { //returns the new balance
            if (!loggedInUsers.containsKey(connectionID)) {
                return null;        //checks if logged in
            }
            String userid = loggedInUsers.get(connectionID);
            int toAdd = Integer.parseInt(amount); //amount to add
            int current = Integer.parseInt(usersMap.get(userid).getBalance());
            usersMap.get(userid).setBalance(Integer.toString(toAdd + current));
            writeToUsers();
            return Integer.toString(toAdd + current);
    }


    public synchronized String returnAllMovies(int connectionID) {
            if (!loggedInUsers.containsKey(connectionID)) { //if logged in
                return null;
            }
            String output = "";
            for (MovieJson movie : mDataBase.getMovies()) { // returns all movies name
                output += "\"" + movie.getName() + "\" ";
            }
            if (mDataBase.getMovies().size() >= 1)
                output = output.substring(0, output.length() - 1);
            return output;
    }

    public synchronized String returnMovieInfo(int connectionID, String movieName) { //returns movie info by name
            if (!loggedInUsers.containsKey(connectionID) || !moviesMap.containsKey(movieName)) {
                return null;
            }// checks if logged in
            String output = "";
            MovieJson currMovie = moviesMap.get(movieName);
            output += currMovie.toString();
            if (moviesMap.get(movieName).getBannedCountries() != null) {
                List<String> bannedCountries = moviesMap.get(movieName).getBannedCountries();
                for (String country : bannedCountries) {
                    output += " \"" + country + "\"";
                }
            }

            return output;

    }

    public synchronized String rentMovie(int connectionID, String movieName) {
            if (!loggedInUsers.containsKey(connectionID))
                return null;
            if (!moviesMap.containsKey(movieName)){
                return null;
            }
            String userName = loggedInUsers.get(connectionID);  //gets user name
            int balance = Integer.parseInt(usersMap.get(userName).getBalance());
            int price = Integer.parseInt(moviesMap.get(movieName).getPrice());
            int copies = Integer.parseInt((moviesMap).get(movieName).getAvailableAmount());
            List<String> bannedCountries = moviesMap.get(movieName).getBannedCountries();
            if (bannedCountries != null && bannedCountries.contains(usersMap.get(userName).getCountry()))
                return null;
            if (balance < price || copies < 1) { //checks if has enough money
                return null;
            }
            if (usersMap.get(userName).containsMovie(movieName))
                return null;
            balance = balance - price;
            usersMap.get(userName).setBalance(Integer.toString(balance));
            usersMap.get(userName).addRentedMovie(moviesMap.get(movieName).getId(), moviesMap.get(movieName).getName());
            copies--;
            moviesMap.get(movieName).setAvailableAmount(Integer.toString(copies));
            writeToMovies();
            writeToUsers();
            return moviesMap.get(movieName).toString();
    }

    public synchronized String returnMovie(int connectionID, String movieName) {
            if (!loggedInUsers.containsKey(connectionID)) //checks if logged in
                return null;

            if (!moviesMap.containsKey(movieName)) // checks if the movie exists
                return null;
            String userName = loggedInUsers.get(connectionID);
            if (usersMap.get(userName).returnRentedMovie(moviesMap.get(movieName).getId())) {
                int copies = Integer.parseInt(moviesMap.get(movieName).getAvailableAmount());
                copies++;
                moviesMap.get(movieName).setAvailableAmount(Integer.toString(copies));
                writeToMovies();
                writeToUsers();
                return moviesMap.get(movieName).toString();
            }
            return null;



    }
//adds movie to the movie list
    public synchronized String addMovie(int connectionID, String moviename, String amount, String price, ArrayList<String> countries) {
            if (!loggedInUsers.containsKey(connectionID) || !usersMap.get(loggedInUsers.get(connectionID)).getType().equals("admin")) {
                return null;
            }
            int price1 = Integer.parseInt(price);
            int amount1 = Integer.parseInt(amount);
            if (moviesMap.containsKey(moviename) || price1 < 1 | amount1 < 1) { // checks if valid
                return null;
            }
            currID++;
            MovieJson movieJson = new MovieJson(Integer.toString(currID), moviename, price, amount, amount, countries);
            moviesMap.put(moviename, movieJson);
            mDataBase.addMovie(movieJson);
            writeToMovies();
            return movieJson.toString();
    }

    public synchronized String removeMovie(int connectionID, String movieName) {
            if (!loggedInUsers.containsKey(connectionID) || !usersMap.get(loggedInUsers.get(connectionID)).getType().equals("admin")) {
                return null;
            }
            if (!moviesMap.containsKey(movieName)) // checks if the movie exists
                return null;
            String total = moviesMap.get(movieName).getTotalAmount();
            String available = moviesMap.get(movieName).getAvailableAmount();
            int dif = Integer.parseInt(total) - Integer.parseInt(available);
            if (dif > 0) {
                return null;
            }
            moviesMap.remove(movieName);
            mDataBase.remmovie(movieName);
            writeToMovies();
            writeToUsers();
            return movieName;
    }

    public synchronized String changePrice(int connectionID, String movieName, String price) {
            //checks if the user is logged in and an admin
            if (!loggedInUsers.containsKey(connectionID) || !usersMap.get(loggedInUsers.get(connectionID)).getType().equals("admin")) {
                return null;
            }
            if (!moviesMap.containsKey(movieName)) //checks if the movie exists
                return null;
            int price1 = Integer.parseInt(price);
            if (price1 < 1) {
                return null;
            }
            moviesMap.get(movieName).setPrice(price);
            writeToMovies();
            return moviesMap.get(movieName).toString();
    }
}