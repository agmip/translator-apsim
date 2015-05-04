package org.agmip.translators.apsim.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.agmip.translators.apsim.core.DailyWeather;
import org.agmip.translators.apsim.core.Weather;
import org.agmip.translators.apsim.util.Util;

public class WeatherReader {

    public Weather read(String path) throws Exception {

        File file = new File(path);
        String name = file.getName();
        InputStream fis = new FileInputStream(path);
        return read(fis, name);

    }

    public Weather read(InputStream fis, String name) throws Exception {
        
        Weather weather = new Weather();

        // determine the weather name from the file path.
        int indexLastDot = name.lastIndexOf('.');
        if (indexLastDot != -1) {
            name = name.substring(0, indexLastDot);
        }
        weather.setName(name);
        weather.setId(name);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(fis,
                Charset.forName("UTF-8")));
        String line;

        String[] headings = null;
        String[] units = null;
        ArrayList<DailyWeather> records = new ArrayList<DailyWeather>();
        boolean constantsFound = false;

        while ((line = br.readLine()) != null) {
            // remove comments from line.
            if (line.contains("!")) {
                if (line.startsWith("!")) {
                    String[] words = line.substring(line.indexOf('!') + 1).trim().split("\\s+");
                    if (words.length >= 3 && words[1].equals(":")) {
                        if (words[0].equalsIgnoreCase("name")) {
                            weather.setName(words[2]);
                        } else if (words[0].equalsIgnoreCase("id")) {
                            weather.setId(words[2]);
                        } else if (words[0].equalsIgnoreCase("source")) {
                            weather.setSource(words[2]);
                        } else if (words[0].equalsIgnoreCase("site")) {
                            weather.setSite(words[2]);
                        } else if (words[0].equalsIgnoreCase("dist")) {
                            weather.setDist(words[2]);
                        } else if (words[0].equalsIgnoreCase("elevation")) {
                            try {
                                double value = Double.parseDouble(words[2]);
                                weather.setElevation(value);
                            } catch (Exception e) {
                            }
                        } else if (words[0].equalsIgnoreCase("climate")) {
                            weather.setClimateId(words[2]);
                        }
                    }
                }
                line = line.substring(0, line.indexOf('!'));
            }

            String[] words = line.trim().split("\\s+");

            if (words.length >= 3 && words[1].equals("=")) {
                // weather constant.
                if (words[0].equalsIgnoreCase("latitude")) {
                    weather.setLatitude(Double.valueOf(words[2]));
                } else if (words[0].equalsIgnoreCase("longitude")) {
                    weather.setLongitude(Double.valueOf(words[2]));
                } else if (words[0].equalsIgnoreCase("tav")) {
                    weather.setTav(Double.valueOf(words[2]));
                } else if (words[0].equalsIgnoreCase("amp")) {
                    weather.setAmp(Double.valueOf(words[2]));
                } else if (words[0].equalsIgnoreCase("co2")) {
                    weather.setCO2Y(words[2]);
                }
                constantsFound = true;
            } else if (constantsFound && line.length() > 0) {

                // data - look for headings first, then units, then data.
                if (headings == null) {
                    headings = words;
                } else if (units == null) {
                    units = words;
                } else {
                    // Must be data.
                    if (words.length == headings.length) {
                        DailyWeather dailyWeather = new DailyWeather();

                        Date date = getDateFromWords(headings, words);
                        dailyWeather.setDate(DailyWeather.apsimWeather.format(date));

                        for (int i = 0; i < headings.length; i++) {
                            if (headings[i].equalsIgnoreCase("radn")) {
                                dailyWeather.setSolarRadiation(Double.valueOf(words[i]));
                            } else if (headings[i].equalsIgnoreCase("maxt")) {
                                dailyWeather.setMaxTemperature(Double.valueOf(words[i]));
                            } else if (headings[i].equalsIgnoreCase("mint")) {
                                dailyWeather.setMinTemperature(Double.valueOf(words[i]));
                            } else if (headings[i].equalsIgnoreCase("rain")) {
                                dailyWeather.setRainfall(Double.valueOf(words[i]));
                            }
                        }

                        records.add(dailyWeather);
                    }

                }

            }

        }
        weather.setRecords(records);

        br.close();
        
        String climId = weather.getClimateId();
        String wstId = weather.getId();
        if (!climId.equals("") && wstId.endsWith(climId)) {
            weather.setId(wstId.replaceAll(climId, ""));
        }

        return weather;
    }

    private Date getDateFromWords(String[] headings, String[] words)
            throws Exception {
        int year = 0;
        int month = -1;
        int day = 0;
        for (int i = 0; i != words.length; i++) {
            if (headings[i].equalsIgnoreCase("date")) {
                return Util.apsim.parse(words[i]);
            } else if (headings[i].equalsIgnoreCase("year")) {
                year = Integer.valueOf(words[i]);
            } else if (headings[i].equalsIgnoreCase("month")) {
                month = Integer.valueOf(words[i]);
            } else if (headings[i].equalsIgnoreCase("day")) {
                day = Integer.valueOf(words[i]);
            }
        }
        if (year > 0) {

            if (day > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, 0, 1);
                calendar.add(Calendar.DAY_OF_YEAR, day - 1);
                return calendar.getTime();
            } else {
                day = 1;
            }
            if (month < 0) {
                month = 0;
            }
            return new GregorianCalendar(year, month, day).getTime();
        }
        return null;
    }

}
