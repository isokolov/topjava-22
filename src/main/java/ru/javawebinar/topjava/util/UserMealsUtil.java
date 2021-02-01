package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summingInt;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }



    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles
        List<UserMealWithExcess> listWithExcess = new ArrayList<>();
        int caloriesPerDayFromList = 0;
        Set<LocalDate> localDates = new HashSet<>();
        LocalDate localDate = null;
        for (UserMeal userMeal: meals) {
            localDate = userMeal.getDateTime().toLocalDate();
            localDates.add(localDate);
        }
        for (LocalDate date: localDates) {
            boolean IsExceeded = false;
            int localCalories = 0;
            for (UserMeal userMeal: meals) {
                if (userMeal.getDateTime().toLocalDate().isEqual(date)) {
                    localCalories += userMeal.getCalories();
                }
            }
            if (localCalories > caloriesPerDay) {
                for (UserMeal userMeal: meals) {
                    if (userMeal.getDateTime().toLocalDate().isEqual(date) &&
                        userMeal.getDateTime().toLocalTime().isBefore(endTime) &&
                        !userMeal.getDateTime().toLocalTime().isBefore(startTime)) {
                        listWithExcess.add(new UserMealWithExcess(userMeal.getDateTime(),
                        userMeal.getDescription(), userMeal.getCalories(), true));
                    }
                }
            }
        }
        return listWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        Map<LocalDate, Integer> map = meals.stream()
                .collect(Collectors.groupingBy(userMeal -> userMeal.getDateTime().toLocalDate(), summingInt(UserMeal::getCalories)));
        List<UserMealWithExcess> userMealWithExcesses = meals.stream()
                .filter(userMeal -> map.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay)
                .filter(userMeal -> userMeal.getDateTime().toLocalTime().isAfter(startTime))
                .filter(userMeal -> !userMeal.getDateTime().toLocalTime().isAfter(endTime))
                .map(userMeal -> {
                    return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(),
                            userMeal.getCalories(), true);
                })
                .collect(Collectors.toList());

        return userMealWithExcesses;
    }
}
