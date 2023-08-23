package rw.gov.sacco.stockmis.v1.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utility {
    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String NUM = "0123456789";
    private static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random rng = new SecureRandom();

    private static final Logger logger = LoggerFactory.getLogger(Utility.class);


    static char randomChar() {
        return ALPHANUM.charAt(rng.nextInt(ALPHANUM.length()));
    }

    static char randomNum() {
        return NUM.charAt(rng.nextInt(NUM.length()));
    }

    static char randomStr() {
        return ALPHA.charAt(rng.nextInt(ALPHA.length()));
    }

    public static String randomUUID(int length, int spacing, char returnType) {
        StringBuilder sb = new StringBuilder();
        char spacerChar = '-';
        int spacer = 0;
        while (length > 0) {
            if (spacer == spacing && spacing > 0) {
                spacer++;
                sb.append(spacerChar);
            }
            length--;
            spacer++;

            switch (returnType) {
                case 'A':
                    sb.append(randomChar());
                    break;
                case 'N':
                    sb.append(randomNum());
                    break;
                case 'S':
                    sb.append(randomStr());
                    break;
                default:
                    logger.error("");
                    break;
            }
        }
        return sb.toString();
    }


    public static boolean isCodeValid(String activationCode, String sentCode) {
        return activationCode.trim().equalsIgnoreCase(sentCode.trim());
    }

    public static Integer getMonthNumber(Month month) {
        if (month == Month.JANUARY)
            return 1;
        if (month == Month.FEBRUARY)
            return 2;
        if (month == Month.MARCH)
            return 3;
        if (month == Month.APRIL)
            return 4;
        if (month == Month.MAY)
            return 5;
        if (month == Month.JUNE)
            return 6;
        if (month == Month.JULY)
            return 7;
        if (month == Month.AUGUST)
            return 8;
        if (month == Month.SEPTEMBER)
            return 9;
        if (month == Month.NOVEMBER)
            return 10;
        if (month == Month.OCTOBER)
            return 11;
        if (month == Month.DECEMBER)
            return 12;

        return 0;
    }


    private static Month getMonthName(int m) {
        if (m == 1)
            return Month.JANUARY;
        if (m == 2)
            return Month.FEBRUARY;
        if (m == 3)
            return Month.MARCH;
        if (m == 4)
            return Month.APRIL;
        if (m == 5)
            return Month.MAY;
        if (m == 6)
            return Month.JUNE;
        if (m == 7)
            return Month.JULY;
        if (m == 8)
            return Month.AUGUST;
        if (m == 9)
            return Month.SEPTEMBER;
        if (m == 10)
            return Month.OCTOBER;
        if (m == 11)
            return Month.NOVEMBER;
        if (m == 12)
            return Month.DECEMBER;

        return null;
    }

    public static List<Month> getMonthsFromRange(int start, int end) {
        List<Month> months = new ArrayList<>();

        for (int i = start; i <= end; i++) {
            months.add(getMonthName(i));
        }
        return months;
    }
}
