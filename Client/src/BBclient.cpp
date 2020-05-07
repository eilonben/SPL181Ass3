#include <stdlib.h>
#include "../include/ConnectionHandler.h"
#include <boost/thread.hpp>



static bool flag = true;
class Sender {
private:
    ConnectionHandler* myhandler;
public:
    Sender(ConnectionHandler* handler) :myhandler(handler) {}

    void operator()() {
        while (flag) {
            try {
                const short bufsize = 1024;
                char buf[bufsize];
                std::cin.getline(buf, bufsize);
                std::string line(buf);
                if (!(*myhandler).sendLine(line)) {
                    break;
                }
            } catch (boost::thread_interrupted const &) {
                break;
            }
        }
    }
};
    class Getter {
    private:
        ConnectionHandler* myhandler;
    public:
        Getter(ConnectionHandler* handler) :myhandler(handler) {}

        void operator()() {


            while (flag) {
                const short bufsize = 1024;
                char buf[bufsize];
                std::string answer;
                std::string line(buf);
                if (!(*myhandler).getLine(answer)) {
                    std::cout << "Disconnected. Exiting...\n" << std::endl;
                    break;
                }
                size_t len = answer.length();
                answer.resize(len - 1);
                std::cout << answer << std::endl;
                if (answer == "ACK signout succeeded") {
                    flag=false;
                    break;
                }
            }
        }
    };

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    Sender mySender(&connectionHandler);
    Getter myGetter(&connectionHandler);
    boost::thread sender(mySender);
    boost::thread getter(myGetter);
    getter.join();
    sender.interrupt();
    return 0;
}

