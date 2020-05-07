package bgu.spl181.net.impl.database;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MoviesData {

    @SerializedName("movies")
    @Expose
    private List<MovieJson> movies = null;

    public List<MovieJson> getMovies() {
        return movies;
    }


    public void addMovie(MovieJson movieJson){
        movies.add(movieJson);
    } //adds movie to the list

    public void remmovie(String movieName){ //removes a specific movie from the list
        int index=0;
        for(int i=0; i<movies.size(); i++){
            if(movies.get(i).getName().equals(movieName))
                index=i;
        }
        movies.remove(index);
    }

}