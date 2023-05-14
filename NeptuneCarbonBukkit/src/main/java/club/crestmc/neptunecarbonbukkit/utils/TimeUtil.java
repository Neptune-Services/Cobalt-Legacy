package club.crestmc.neptunecarbonbukkit.utils;

import javafx.util.Pair;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    public static long getCurrentTimeSeconds() {
        return Instant.ofEpochMilli(System.currentTimeMillis()).getEpochSecond();
    }

    /**
     * Formats the punishment duration as "[DURATION] [TIME UNIT]" or "30 days"
     * @param args The arguments to get the punishment duration from
     * @return The formatted punishment duration
     */
    public static String getDurationFrom(String[] args) {
        Pair<String[], Pair<ChronoUnit, Long>> argumentPair = TimeUtil.getUnitTimePair(args);

        if (argumentPair == null) {
            return null;
        }

        Pair<ChronoUnit, Long> timeDataPair = argumentPair.getValue();
        ChronoUnit unit = timeDataPair.getKey();
        String unitString = unit.toString().toLowerCase();
        long duration = timeDataPair.getValue();

        return duration + " " + unitString;
    }

    /**
     * Translates arguments into a valid date time, then returns a unix epoch time that translates to said date
     * @param args The argumnets to be parsed
     * @return A pair containing the punishment expire time (in unix epoch seconds) and the parsed arguments
     */
    public static Pair<Long, String[]> getExpireTime(String[] args) {
        Pair<String[], Pair<ChronoUnit, Long>> argumentPair = TimeUtil.getUnitTimePair(args);

        if (argumentPair == null) {
            return null;
        }

        long currentTime = TimeUtil.getCurrentTimeSeconds();
        String[] parsedArgs = argumentPair.getKey();

        Pair<ChronoUnit, Long> timeDataPair = argumentPair.getValue();
        ChronoUnit unit = timeDataPair.getKey();
        Long duration = timeDataPair.getValue();

        // Some time units aren't supported by Java's instant calculations, in those cases, we'll convert them here
        if (unit == ChronoUnit.YEARS) {
            unit = ChronoUnit.DAYS;
            duration *= 365;
        } else if (unit == ChronoUnit.MONTHS) {
            unit = ChronoUnit.DAYS;
            duration *= 30;
        } else if (unit == ChronoUnit.WEEKS) {
            unit = ChronoUnit.DAYS;
            duration *= 7;
        }

        Long time = Instant.ofEpochSecond(currentTime).plus(duration, unit).getEpochSecond();

        return new Pair<>(time, parsedArgs);
    }

    /**
     * Converts unix time stamps into text, readable by humans
     * @param epochSeconds The time stamp with the date to convert
     * @return The formatted text containing the remaining time until the specified date
     */
    public static String getHowLongUntil(long epochSeconds) {
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("America/New_York"));

        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/New_York"));
        Period period = Period.between(now.toLocalDate(), dateTime.toLocalDate());
        Duration duration = Duration.between(dateTime, now).abs();

        long years = period.getYears();
        long months = period.getMonths();
        long days = period.getDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        String result = "";

        if (years > 0) {
            result += years + " year" + (years > 1 ? "s" : "") + " ";
        } else if (months > 0) {
            result += months + " month" + (months > 1 ? "s" : "") + " ";
        } else if (days > 0) {
            result += days + " day" + (days > 1 ? "s" : "") + " ";
        } else if (hours > 0) {
            result += hours + " hour" + (hours > 1 ? "s" : "") + " ";
        } else if (minutes > 0) {
            result += minutes + " minute" + (minutes > 1 ? "s" : "") + " ";
        } else if (seconds > 0) {
            result += seconds + " second" + (seconds > 1 ? "s" : "") + " ";
        }

        if (result.equals("")) {
            result = "0 seconds";
        }

        return result.trim();
    }

    /**
     * Takes command arguments and converts them into a Pair consisting of a TimeUnit and a long
     * Anything duration longer than a day (ex. 30years) will be converted into days due to Java's TimeUnit limitations.
     * @param args The command arguments to parsed
     * @return A Pair consisting of a TimeUnit and a long which was parsed from the string provided.
     */
    public static Pair<String[], Pair<ChronoUnit, Long>> getUnitTimePair(String[] args) {
        for (int arg = 0; arg < args.length; arg++) {
            String currentArg = args[arg];

            try {
                // Check if the current argument is a number, if it is, check if the next argument is a time unit
                Long.parseLong(currentArg);
                String nextArg;

                if (args.length <= arg + 1) {
                    continue;
                }

                nextArg = args[arg + 1];
                Units unit = Units.getByName(nextArg);

                if (unit == null) {
                    continue;
                }

                String[] parsedArgs = new String[args.length - 2];

                for (int oldIndex = 0, newIndex = 0; oldIndex < args.length; oldIndex++) {
                    // If the argument is the current argument, or the index argument (time period and unit) remove them from the parsed arguments
                    if (oldIndex == arg || oldIndex == arg + 1) {
                        continue;
                    }

                    parsedArgs[newIndex++] = args[oldIndex];
                }

                return new Pair<>(parsedArgs, getUnitTimePair(currentArg + nextArg));
            } catch (NumberFormatException ignored) {
                Pair<ChronoUnit, Long> unitLongPair = getUnitTimePair(currentArg);

                if (unitLongPair == null) {
                    continue;
                }

                String[] parsedArgs = new String[args.length - 1];

                for (int oldIndex = 0, newIndex = 0; oldIndex < args.length; oldIndex++) {
                    // If the argument is the current argument remove them from the parsed arguments
                    if (oldIndex == arg) {
                        continue;
                    }

                    parsedArgs[newIndex++] = args[oldIndex];
                }

                return new Pair<>(parsedArgs, unitLongPair);
            }
        }

        return null;
    }

    /**
     * Takes a duration (ex. 30d) and converts it into a Pair consisting of a TimeUnit and a long
     * Anything duration longer than a day (ex. 30years) will be converted into days due to Java's TimeUnit limitations.
     * @param duration The duration to be parsed. This could be any duration string. Examples: 30d, 30weeks, 100year, and so on
     * @return javafx.util.Pair consisting of a TimeUnit and a long which was parsed from the string provided.
     */
    public static Pair<ChronoUnit, Long> getUnitTimePair(String duration) {
        String units = String.join("|", Units.getAllUnits());

        Pattern timePattern = Pattern.compile("(\\d+)(" + units + ")");
        Matcher matcher = timePattern.matcher(duration);

        long period;
        Units timeUnit;

        if (matcher.matches()) {
            period = Long.parseLong(matcher.group(1));
            timeUnit = Units.getByName(matcher.group(2));
        } else {
            return null;
        }

        return new Pair<>(timeUnit.getTimeUnit(), period);
    }

    private enum Units {

        SECONDS() {
            @Override
            public String[] getUnits() {
                return new String[] { "s", "sec", "secs", "second", "seconds" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.SECONDS;
            }
        },
        MINUTES {
            @Override
            public String[] getUnits() {
                return new String[] { "m", "min", "mins", "minute", "minutes" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.MINUTES;
            }
        },
        HOURS {
            @Override
            public String[] getUnits() {
                return new String[] { "h", "hour", "hours" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.HOURS;
            }
        },
        DAYS {
            @Override
            public String[] getUnits() {
                return new String[] { "d", "day", "days" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.DAYS;
            }
        },
        WEEKS {
            @Override
            public String[] getUnits() {
                return new String[] { "w", "week", "weeks" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.WEEKS;
            }
        },
        MONTHS {
            @Override
            public String[] getUnits() {
                return new String[] { "mo", "month", "months" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.MONTHS;
            }
        },
        YEARS {
            @Override
            public String[] getUnits() {
                return new String[] { "y", "year", "years" };
            }

            @Override
            public ChronoUnit getTimeUnit() {
                return ChronoUnit.YEARS;
            }
        };

        private static final Map<String, Units> units;

        static {
            units = new HashMap<>();

            for (Units unit : values()) {
                for (String unitString : unit.getUnits()) {
                    units.put(unitString, unit);
                }
            }
        }

        public abstract String[] getUnits();
        public abstract ChronoUnit getTimeUnit();

        public static Set<String> getAllUnits() {
            return new HashSet<>(units.keySet());
        }

        public static Units getByName(String name) {
            return units.entrySet().stream()
                    .filter(unitEntry -> unitEntry.getKey().equals(name))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);
        }

    }

}
