
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Patterns {


    public static void main(String[] args) throws UnknownHostException{

        String hostName = InetAddress.getLocalHost().getHostName();
        Pattern pattern = Pattern.compile("vqa(.*?)agg");
        Matcher matcher = pattern.matcher(hostName);
        String laneId =  matcher.group(1);
    }
}
