package uhh_lt.webserver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Datendifferenzberechner {

    public static void main(String[] args) {

        Differenz("2019-02-20 16:29:03", "2019-02-20 17:23:31");
    }

    public static long Differenz(String aDate, String tDate)
    {
        try
        {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(aDate);  //2019-02-20 16:29:03
            Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tDate);

            long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
            long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
            System.out.println(diff);
            return diff;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
}
