package me.noaz.testplugin.messages;

import org.bukkit.ChatColor;

public class TextUtils {
    public static String getRatioAsRedOrGreenString(double currentPositive, double currentTotal, double totalPositive, double totalTotal) {

        String ratioString;
        double ratio = (currentTotal == 0) ? currentPositive : currentPositive/currentTotal;
        double totalRatio = (totalTotal == 0) ? totalPositive : totalPositive/totalTotal;
        if(ratio >= totalRatio || currentTotal == 0) {
            ratioString = ChatColor.GREEN + "";
        } else {
            ratioString = ChatColor.RED + "";
        }


        String ratioAsString = Double.toString(ratio);
        if(ratio >= 100) {
            ratioString += (ratioAsString.length() >= 6) ? ratioAsString.substring(0, 6) : ratioAsString;
        } else if(ratio >= 10) {
            ratioString += (ratioAsString.length() >= 5) ? ratioAsString.substring(0, 5) : ratioAsString;
        } else {
            ratioString += (ratioAsString.length() >= 4) ? ratioAsString.substring(0, 4) : ratioAsString;
        }

        return ratioString;
    }
}
