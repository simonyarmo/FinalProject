import Library.Library;
import Network.Server;
import User.UserManager;

public class ServerDriver {
    public static void main(String[] args) {
        UserManager userManager =new UserManager();
        Library library =new Library();
        Server server = new Server(3100, userManager, library);
    }
}
