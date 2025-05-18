import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.print("Enter number of exams: ");
        int numExams = scanner.nextInt();
        scanner.nextLine();
        String[] examNames = new String[numExams];
        LocalDate[] examDates = new LocalDate[numExams];
        int[] topics = new int[numExams];
        System.out.println("Enter each exam on a new line in the format: <name> <yyyy-MM-dd> <topics>");
        for (int i = 0; i < numExams; i++) {
            String line = scanner.nextLine();
            String[] parts = line.trim().split(" ");
            examNames[i] = parts[0];
            examDates[i] = LocalDate.parse(parts[1], formatter);
            topics[i] = Integer.parseInt(parts[2]);
        }
        LocalDate today = LocalDate.now();
        int maxDays = 0;
        for (int i = 0; i < numExams; i++) {
            int days = (int)java.time.temporal.ChronoUnit.DAYS.between(today, examDates[i]);
            if (days > maxDays) maxDays = days;
        }
        // Prepare date headers
        List<LocalDate> allDates = new ArrayList<>();
        for (int d = 0; d <= maxDays; d++) {
            allDates.add(today.plusDays(d));
        }
        // Prepare schedule: first row is header
        List<String[]> schedule = new ArrayList<>();
        String[] header = new String[allDates.size() + 1];
        header[0] = "Exam";
        for (int d = 0; d < allDates.size(); d++) {
            header[d + 1] = allDates.get(d).toString();
        }
        schedule.add(header);
        // For each exam, fill in chapters per day
        for (int i = 0; i < numExams; i++) {
            int totalTopics = topics[i];
            int daysToExam = (int)java.time.temporal.ChronoUnit.DAYS.between(today, examDates[i]);
            String[] row = new String[allDates.size() + 1];
            row[0] = examNames[i];
            int chapter = 1;
            int topicsLeft = totalTopics;
            for (int d = 0; d < allDates.size(); d++) {
                if (d <= daysToExam && topicsLeft > 0) {
                    int daysRemaining = daysToExam - d + 1;
                    int topicsToday = (int)Math.ceil((double)topicsLeft / daysRemaining);
                    if (topicsToday > topicsLeft) topicsToday = topicsLeft;
                    // Build chapter labels for this day
                    StringBuilder chapters = new StringBuilder();
                    for (int c = 0; c < topicsToday; c++) {
                        if (c > 0) chapters.append(", ");
                        chapters.append("Chapter ").append(chapter++);
                    }
                    row[d + 1] = chapters.toString();
                    topicsLeft -= topicsToday;
                } else {
                    row[d + 1] = "";
                }
            }
            schedule.add(row);
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter("schedule.csv"))) {
            for (String[] row : schedule) {
                pw.println(String.join(",", row));
            }
        }
        System.out.println("Schedule generated in schedule.csv");
    }
}
