package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.api.Server;
import bgu.spl181.net.impl.database.MoviesSharedProtocolData;
import bgu.spl181.net.impl.server.LineMessageEncoderDecoder;
import bgu.spl181.net.impl.protocol.MovieRentalProtocol;


public class TPCMain {

    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);
        String userFileAddress="Database/Users.json";
        String movieFileAddress="Database/Movies.json";
        MoviesSharedProtocolData db = new MoviesSharedProtocolData(userFileAddress,movieFileAddress);
        Server.threadPerClient(
                port, //port
                () -> new MovieRentalProtocol(db), //protocol factory
                LineMessageEncoderDecoder::new //message encoder decoder factory
        ).serve();
    }
}
