import java.util.Arrays;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.BufferedReader;	

/**
 * Die Uhrzeiten werden alle eingelesen und als Minuten-Timestamps (also Stunden werden in Minuten umgewandelt) abgespeichert.
 * Alle Termine werden dann chronologisch sortiert. Der ganze Tag wird dann "abgespult" und wenn jeweils eine grösse Pause gefunden wird, 
 * diese abgelegt und schlussendlich ausgegeben.
 *
 * 10191 - The longest nap
 * Runtime: 0.130
 * uva run number: 24028437
 * @date 10.10.2019
 * @author Samuel Keusch
 */
public class Main {

    public static class Appointment {
        public int fromMinute;
        public int toMinute;

        public Appointment(int fromMinute, int toMinute) {
            this.fromMinute = fromMinute;
            this.toMinute = toMinute;
        }
    }

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            int dayCounter = 1;
            while (reader.ready()) {
                int numOfAppointments = Integer.parseInt(reader.readLine());
                Appointment[] appointments = new Appointment[numOfAppointments];

                for (int i = 0; i < numOfAppointments; i++) {
					String[] line = reader.readLine().split(" ", 3);
					String[] start = line[0].split(":");
					String[] end = line[1].split(":");
                    appointments[i] = new Appointment(Integer.parseInt(start[0]) * 60 + Integer.parseInt(start[1]), Integer.parseInt(end[0]) * 60 + Integer.parseInt(end[1]));
                }
                System.out.println("Day #" + (dayCounter++) + ": " + showNapInfo(appointments));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String showNapInfo(Appointment[] appointments) {
        Arrays.sort(appointments, (Appointment o1, Appointment o2) -> {
            return o1.fromMinute - o2.fromMinute;
        });

        int maxGap = 0;
        int maxStart = 10 * 60; // starts at 10:00
        int lastOccupiedMin = 10 * 60;
        for (int i = 0; i < appointments.length; i++) {
            int gap = appointments[i].fromMinute - lastOccupiedMin;
            if (gap > maxGap) {
                maxStart = lastOccupiedMin;
                maxGap = gap;
            }
            lastOccupiedMin = appointments[i].toMinute;
        }

        // possible gap before 18:00
        if (lastOccupiedMin != 18 * 60) {
            int gap = 18 * 60 - lastOccupiedMin;
            if (gap > maxGap) {
                maxStart = lastOccupiedMin;
                maxGap = gap;
            }
        }

        return "the longest nap starts at " + formatTime(maxStart) + " and will last for " + formatDuration(maxGap)
                + ".";
    }

    public static String formatDuration(int minutes) {
        if (minutes >= 60) {
            return (minutes / 60) + " hours and " + (minutes % 60) + " minutes";
        } else {
            return minutes + " minutes";
        }
    }

    public static String formatTime(int minutes) {
        return ((minutes / 60 < 10) ? "0" : "") + (minutes / 60) + ":"
                + ((minutes % 60 < 10 ? "0" : "") + (minutes % 60));
    }
}