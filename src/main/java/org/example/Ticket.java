package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Properties;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Ticket {
    private final String origin;
    private final String origin_name;
    private final String destination;
    private final String destination_name;
    private final String departure_date;
    private final String departure_time;
    private final String arrival_date;
    private final String arrival_time;
    private final String carrier;
    private final String stops;
    private final String price;
    private Duration duration;

    public Ticket(String origin, String originName, String destination, String destinationName, String departureDate, String departureTime, String arrivalDate, String arrivalTime, String carrier, Number stops, Number price) {
        this.origin = origin;
        origin_name = originName;
        this.destination = destination;
        destination_name = destinationName;
        departure_date = departureDate;
        departure_time = departureTime;
        arrival_date = arrivalDate;
        arrival_time = arrivalTime;
        this.carrier = carrier;
        this.stops = stops.toString();
        this.price = price.toString();
    }
    //статический метод
    //получаем контейнер из входящего JSON
    public static ArrayList<Ticket> getContainer(){
        ArrayList<Ticket> container = new ArrayList<>();
        Properties prop = new Properties();
        String path = "";
        try {
            prop.load(new FileInputStream("config.properties"));

            path = prop.getProperty("path");

            // Используйте полученные значения
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        File file = new File(path);
        JSONParser parser = new JSONParser();
        try {
            FileInputStream fis = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);

            JSONObject root = (JSONObject) parser.parse(isr);
            JSONArray tickets = (JSONArray) root.get("tickets");

            for (Object ticket: tickets){
                JSONObject oTicket = (JSONObject) ticket;
                if(oTicket.get("origin_name").equals("Владивосток") && oTicket.get("destination_name").equals("Тель-Авив")){
                    Ticket newTicket = new Ticket(
                            (String) oTicket.get("origin"),
                            (String) oTicket.get("origin_name"),
                            (String) oTicket.get("destination"),
                            (String) oTicket.get("destination_name"),
                            (String) oTicket.get("departure_date"),
                            (String) oTicket.get("departure_time"),
                            (String) oTicket.get("arrival_date"),
                            (String) oTicket.get("arrival_time"),
                            (String) oTicket.get("carrier"),
                            (Number) oTicket.get("stops"),
                            (Number) oTicket.get("price"));
                    container.add(newTicket);
                }
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        getDuration(container);
        return container;
    }
    //статический метод
    //на вход готовый контейнер
    //на выход ничего, но добавляет для каждого билета в контейнере значение поля duration
    private static void getDuration(List<Ticket> container){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");
        Duration duration;
        for (Ticket ticket: container){
//            System.out.println(ticket.getCarrier());
            String departure = ticket.getDeparture_date() + " " +  ticket.getDeparture_time();
            String arrival = ticket.getArrival_date() + " " + ticket.getArrival_time();
//            System.out.println("dep: " + departure);
//            System.out.println("arr: " + arrival);
            try {
                Date dateTimeDeparture = format.parse(departure);
                Date dateTimeArrival = format.parse(arrival);
//                System.out.println("departure " + dateTimeDeparture);
//                System.out.println("arrival " + dateTimeArrival);
                long diffInMillisecond = dateTimeArrival.getTime() - dateTimeDeparture.getTime();
                duration = Duration.ofMillis(diffInMillisecond);
                ticket.duration = duration;
//                System.out.println(duration.toHours() + ":" + duration.toMinutes()%60 + ":" + duration.getSeconds()%60);
            } catch (java.text.ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //статический метод
    //на вход готовый контейнер
    //на выходе список авиаперевозчиков
    public static List<String> getCarriers(List<Ticket> container){
        List<String> carriers = new ArrayList<>();
        for (Ticket ticket: container){
            if (carriers.isEmpty()){
                carriers.add(ticket.getCarrier());
            } else if (!carriers.contains(ticket.getCarrier())){
                carriers.add(ticket.getCarrier());
            }
        }
        return carriers;
    }

    public static HashMap<String, Duration> getMinDurations(List<Ticket> container){
        List<String> carriers = getCarriers(container);
        //создаем мапу,где ключ - название самолета, значение - лист длительностей полетов
        HashMap<String, List<Duration>> durations = new HashMap<>();
        for (String carrier: carriers){
            List<Duration> durationsForOneCarrier = new ArrayList<>();
            for (Ticket ticket: container){
                if (ticket.getCarrier().equals(carrier)){
                    durationsForOneCarrier.add(ticket.getDuration());
                }
            }
            durations.put(carrier, durationsForOneCarrier);
        }
        //Ищем для каждлго самолета минимальную длительность и кладем в мапу
        //На выходе получем нужную для задания мапу
        HashMap<String, Duration> minDurations = new HashMap<>();
        for (String carrier: carriers){
//            System.out.println(carrier);
            List<Duration> durationList = durations.get(carrier);
            Duration minDuration = null;
            for (Duration duration: durationList){
//                System.out.println(duration.toHours() + ":" + duration.toMinutes() % 60 + ":" + duration.getSeconds() % 60);
                if (minDuration == null){
                    minDuration = duration;
                } else if (duration.compareTo(minDuration) < 0) {
                    minDuration = duration;
                }
            }
            minDurations.put(carrier, minDuration);
        }
        return minDurations;
    }
    //Ищем среднее значение
    public static double getAveragePrice(List<Ticket> container){
        double averagePrice = -1;
        int count = 0;
        double sumOfPrice = 0;
        for (Ticket ticket: container){
            count++;
            sumOfPrice += Double.parseDouble(ticket.getPrice());
        }
        averagePrice = sumOfPrice/count;
        return averagePrice;
    }
    //Ищем медианное значение
    public static double getMedianPrice(List<Ticket> container){
        double medianPrice = -1;
        //формируем лист цен
        List<Double> prices = new ArrayList<>();
        for (Ticket ticket: container){
            prices.add(Double.valueOf(ticket.getPrice()));
        }
        //сортируем наш лист
        prices = prices.stream().sorted().collect(Collectors.toList());
        //если число цен в листе нечетное то просто берем значениие по середине
        if (prices.size() % 2 != 0){
            medianPrice = prices.get((prices.size() - 1) / 2);
        } else {
            //если число цен четное то берем среднее от двух цен посередине
            medianPrice = (prices.get((int) ((prices.size() - 1) / 2)) + prices.get((int) ((prices.size() - 1) / 2 + 1))) / 2;
        }
//        System.out.println("Prices list");
//        prices.stream().forEach(System.out::println);
//        System.out.println("End");
        return medianPrice;
    }

    public String getOrigin() {
        return origin;
    }

    public String getOrigin_name() {
        return origin_name;
    }

    public String getDestination() {
        return destination;
    }

    public String getDestination_name() {
        return destination_name;
    }

    public String getDeparture_date() {
        return departure_date;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public String getArrival_date() {
        return arrival_date;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getStops() {
        return stops;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public String toString(){
        return "origin: " + origin + ", origin_name: " + origin_name + ", destination: " + destination + ", destination_name: " + destination_name + ", " +
                "departure_date: " + departure_date + ", departure_time: " + departure_time + ", arrival_date: " + arrival_date + ", arrival_time: " + arrival_time + ", " +
                "carrier: " + carrier + ", stops: " + stops + ", price: " + price +
                ", duration: " + duration.toHours() + ":" + duration.toMinutes() % 60 + ":" + duration.getSeconds() % 60;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
