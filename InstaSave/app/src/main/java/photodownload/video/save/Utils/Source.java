package photodownload.video.save.Utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Source {

    public static String getURL(String html, String regex){

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {

            return matcher.group(1);

        }
        return "";
    }


    public static ArrayList<String> getArrayURL(String html, String regex){

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        ArrayList<String> stringArrayList = new ArrayList<>();

        while (matcher.find()) {

            stringArrayList.add(matcher.group(1));
        }
        return stringArrayList;
    }

}

