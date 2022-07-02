package com.telepathy.labs.assignment;

import com.telepathy.labs.exception.TimeException;
import com.telepathy.labs.exception.UserInputException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionOne {

    public static Integer covertToInteger(String meetingTime) throws TimeException {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        try {

            dateTimeFormatter.parse(meetingTime);
            return Integer.parseInt(meetingTime.replace(":",""));
        } catch (DateTimeParseException ex) {
            throw new TimeException("INVALID TIME EXCEPTION");
        }
    }

    public static Integer getRequiredMeetingRoom(List<Integer> startTimes, List<Integer> endTimes) {

        int start = 1;
        int end = 0;

        int minMeetingRoomsRequired = 1;
        int numberOngoingMeetings = 1;

        while (start < startTimes.size() && end < endTimes.size()) {
 //           System.out.println("End Time:" + endTimes.get(end) + " -> StartTime: " + startTimes.get(start) + " : " + minMeetingRoomsRequired);
            if (endTimes.get(end) > startTimes.get(start)) {
                minMeetingRoomsRequired++;
                if (minMeetingRoomsRequired < numberOngoingMeetings)
                    minMeetingRoomsRequired = numberOngoingMeetings;
            } else {
                numberOngoingMeetings--;
                end++;
            }
            start++;
        }
        return minMeetingRoomsRequired;
    }

    public static void main(String[] args) throws UserInputException {

        String strLine;
        List<Integer> meetingStartTimes =  new ArrayList<>();
        List<Integer> meetingEndTimes =  new ArrayList<>();

        try {

            String fileName = args[0];

            String filePath = System.getProperty("user.dir") + File.separator + fileName;
            System.out.println("Example File Path:" + filePath);
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            while ((strLine = br.readLine()) != null) {
                String [] timeList = strLine.split("-");
                meetingStartTimes.add(covertToInteger(timeList[0]));
                meetingEndTimes.add(covertToInteger(timeList[1]));
            }
        }   catch(IOException ex) {
                throw new UserInputException("FILE NOT FOUND OR FILE NOT READABLE");
        }   catch(ArrayIndexOutOfBoundsException ex) {
                throw new UserInputException("FILE PARAMETER MISSING");
        }   catch(Exception ex) {
                ex.printStackTrace();
        }

        Collections.sort(meetingStartTimes);
        Collections.sort(meetingEndTimes);

        System.out.println("Minimum Meeting Rooms: " + getRequiredMeetingRoom(meetingStartTimes,meetingEndTimes));
    }
}
