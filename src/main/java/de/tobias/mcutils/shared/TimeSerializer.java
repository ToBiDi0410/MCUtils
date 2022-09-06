package de.tobias.mcutils.shared;

@SuppressWarnings("unused")
public class TimeSerializer {

    public final static long ONE_SECOND = 1000;
    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long ONE_DAY = ONE_HOUR * 24;
    public final static long ONE_WEEK = ONE_DAY * 7;

    public String WEEKS = "weeks";
    public String WEEK = "week";
    public String DAYS = "days";
    public String DAY = "day";
    public String HOURS = "hours";
    public String HOUR = "hour";
    public String MINUTES = "minutes";
    public String MINUTE = "minute";
    public String SECONDS = "seconds";
    public String SECOND = "second";
    public String PERMANENT = "PERMANENT";

    long rawValue = 0;
    long seconds = 0;
    long minutes = 0;
    long hours = 0;
    long days = 0;
    long weeks = 0;

    public TimeSerializer(Long millis) {
        rawValue = millis;
        calculateTimes();
    }

    public void calculateTimes() {
        long rawStored = rawValue;
        seconds = 0;
        minutes = 0;
        hours = 0;
        days = 0;
        weeks = 0;

        while(rawStored > 1000) { seconds++; rawStored-=1000; };
        while(seconds > 60) { minutes++; seconds-=60; };
        while(minutes > 60) { hours++; minutes-=60; };
        while(hours > 24) { days++; hours-=24; };
        while(days > 7) { weeks++; days-=7; };
    }

    public String toWDHMS() {
        if(rawValue < 0) return PERMANENT;
        StringBuilder res = new StringBuilder();

        if(weeks > 0) {
            res.append(weeks);
            res.append(" ");
            res.append(weeks > 1 ? WEEKS : WEEK);
            res.append(" ");
        }

        if(days > 0) {
            res.append(days);
            res.append(" ");
            res.append(days > 1 ? DAYS : DAY);
            res.append(" ");
        }

        if(hours > 0) {
            res.append(hours);
            res.append(" ");
            res.append(hours > 1 ? HOURS : HOUR);
            res.append(" ");
        }

        if(minutes > 0) {
            res.append(minutes);
            res.append(" ");
            res.append(minutes > 1 ? MINUTES : MINUTE);
            res.append(" ");
        }

        if(seconds > 0) {
            res.append(seconds);
            res.append(" ");
            res.append(seconds > 1 ? SECONDS : SECOND);
            res.append(" ");
        }

        return res.toString().trim();
    }

    public String toAgo() {
        if(weeks > 0) return weeks + " " + (weeks > 1 ? WEEKS : WEEK);
        if(days > 0) return days + " " + (days > 1 ? DAYS : DAY);
        if(hours > 0) return hours + " " + (hours > 1 ? HOURS : HOUR);
        if(minutes > 0) return minutes + " " + (minutes > 1 ? MINUTES : MINUTE);
        return seconds + " " + (seconds > 1 ? SECONDS : SECOND);
    }
}
