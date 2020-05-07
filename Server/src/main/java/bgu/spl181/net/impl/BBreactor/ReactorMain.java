package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.api.Server;
import bgu.spl181.net.impl.database.MoviesSharedProtocolData;
import bgu.spl181.net.impl.server.LineMessageEncoderDecoder;
import bgu.spl181.net.impl.protocol.MovieRentalProtocol;
public class ReactorMain {

    public static void main(String[] args){
        String userFileAddress="Database/Users.json";
        String movieFileAddress="Database/Movies.json";
        MoviesSharedProtocolData db = new MoviesSharedProtocolData(userFileAddress,movieFileAddress);
        Server.reactor(
                5,
                Integer.parseInt(args[0]), //port
                () -> new MovieRentalProtocol(db), //protocol factory
                LineMessageEncoderDecoder::new //message encoder decoder factory
        ).serve();
    }
}
