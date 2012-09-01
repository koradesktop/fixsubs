package fixsubs;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author koradesktop
 */
public class FixSubs {

    private static String addTimeToString(String currentTime, long timeOffset) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss,SSS");
        Date convertedDate = dateFormat.parse(currentTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(convertedDate);
        cal.add(Calendar.MILLISECOND, (int) timeOffset);

        return dateFormat.format(cal.getTime());
    }

    public static void userHelp() {
        System.out.println("FixSubs v.0.1 - Open source tool to change subtitle timestamps");
        System.out.println("");
        System.out.println("Syntax : ");
        System.out.println("java -jar FixSubs.jar <subtitle path> <[+/-] subtitle offset [hh:mm:ss,SSS]> ");
        System.out.println("");
        System.out.println("eg. : ");
        System.out.println("java -jar FixSubs.jar /media/movie.srt -01:10:46,877 ");
        System.out.println("java -jar FixSubs.jar C:\\data\\movie.srt 00:00:06,000 ");
        System.out.println("java -jar FixSubs.jar \"C:\\data\\movie part 1.srt\" 00:00:06,000 ");
    }

    public static void main(String[] args) {

        try {

            if (args.length != 2) {
                System.out.println();
                System.out.println();
                userHelp();
                System.out.println();
                System.out.println();
                System.exit(-1);
            }

            File subtitleFile = new File(args[0]);
            String strOffset = args[1];
            long signum = 1;

            if (strOffset.startsWith("-")) {
                signum = -1;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss,SSS");
            Date convertedDate = dateFormat.parse(new String(strOffset.replace("+", "").replace("-", "")));
            Calendar cal = Calendar.getInstance();
            cal.setTime(convertedDate);
            long timeOffset = (cal.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000
                    + cal.get(Calendar.MINUTE) * 60 * 1000
                    + cal.get(Calendar.SECOND) * 1000
                    + cal.get(Calendar.MILLISECOND)) * signum;

            FileInputStream fistream = new FileInputStream(subtitleFile);
            DataInputStream in = new DataInputStream(fistream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(subtitleFile.getParentFile(),
                    subtitleFile.getName().substring(0, subtitleFile.getName().indexOf(".")) + "_fixed"
                    + subtitleFile.getName().substring(subtitleFile.getName().indexOf(".")))));

            String strLine;
            String strLineFixed;
            String part1;
            String part2;
            String ArrayLine[];

            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                System.out.print(strLine);
                if (strLine.indexOf("-->") > 0) {
                    ArrayLine = strLine.split("-->");
                    part1 = ArrayLine[0];
                    part2 = ArrayLine[1];
                    strLineFixed = addTimeToString(part1, timeOffset) + " --> " + addTimeToString(part2, timeOffset);
                    writer.write(strLineFixed);
                    System.out.print("\t\t\t" + strLineFixed);
                } else {
                    writer.write(strLine);
                }
                System.out.println();
                writer.newLine();
            }
            writer.flush();
            //Close the input stream
            in.close();
            
        } catch (Exception e) {
            System.out.println();
            System.out.println();
            System.out.println("////////////////////////////////////////////////////////////////////////////////");
            System.out.println("Error: " + e.getMessage());
            System.out.println("////////////////////////////////////////////////////////////////////////////////");
            System.out.println();
            System.out.println();
            userHelp();
            System.out.println();
            System.out.println();
            e.printStackTrace();
            System.out.println();
            System.out.println();
            System.exit(-2);
        }
    }
}
