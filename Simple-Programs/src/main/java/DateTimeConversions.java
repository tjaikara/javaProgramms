
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeConversions {

    public static void main(String[] args) throws ParseException{

        Date date = new Date();

        System.out.println(date);


        String dateString = "2017-09-22 00:35:42";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date2 = simpleDateFormat.parse(dateString);
        System.out.println(date2);


        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(utc);

        String val = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date1 = simpleDateFormat1.parse(val);

        System.out.println(date1);
    }
}
