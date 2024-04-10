package org.example;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Ticket> container = Ticket.getContainer();
        List<String> carriers = Ticket.getCarriers(container);
        HashMap<String, Duration> minDurations = Ticket.getMinDurations(container);
        //просто вывод на консоль результата
        //преобразования в дату и обратно сделано для корректного отображения времени
        //было 8:5:0 стало 08:05:00 и т.д.
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        System.out.println("Minimum flight time between the cities of Vladivostok and Tel Aviv for each air carrier:");
        for(String carrier: carriers){
            Duration duration = minDurations.get(carrier);
            Date date = new Date();
            date.setHours((int) duration.toHours());
            date.setMinutes((int) duration.toMinutes() % 60);
            date.setSeconds((int) duration.getSeconds() % 60);
            String str = formatter.format(date);
            System.out.println(carrier + ": " + str);
        }

        double averagePrice = Ticket.getAveragePrice(container);
        double medianPrice = Ticket.getMedianPrice(container);
        System.out.println("Average price: " + averagePrice);
        System.out.println("Median price: " + medianPrice);
        System.out.println("Difference: " + Math.abs(averagePrice - medianPrice));
    }
}