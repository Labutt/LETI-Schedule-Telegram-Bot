package org.mybestbot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class APIquery { // Класс синглтон, не работает когда пользователей > 1
    private static String url;
    private static String groupNumberForQuery;

    public static void setGroup(String groupNumber){ //устанавливает url api и группу обрабатываемого пользователя
        url = "https://digital.etu.ru/api/mobile/schedule?groupNumber=" + groupNumber;
        groupNumberForQuery = groupNumber;
    }

    public static List<String> getInfoOnExistingGroups() { //получаем список существующих групп
        List<String> groupList = new ArrayList<>();
        String urlForGroups = "https://digital.etu.ru/api/mobile/groups?year=current";
        HttpURLConnection con = null;
        try {
            URL obj = new URL(urlForGroups);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { // Проверяем, что ответ успешный // ранний выход
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    // детали, в отдельную функцию
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    // Парсим JSON
                    JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();
                    groupList = parseGroupList(jsonArray); // Передаем массив для парсинга
                }
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Обработка ошибки // лишний коммент
        } finally {
            if (con != null) {
                con.disconnect(); // Закрываем соединение
            }
        }
        System.out.println(groupList);
        return groupList;
    }

    private static List<String> parseGroupList(JsonArray jsonArray) { //парсим список существующих групп
        List<String> groupList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) { 
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            JsonArray departments = jsonObject.getAsJsonArray("departments");
            for (int j = 0; j < departments.size(); j++) { // каждый вложенный цикл в отдельную функцию
                JsonObject department = departments.get(j).getAsJsonObject();
                JsonArray groups = department.getAsJsonArray("groups");
                for (int k = 0; k < groups.size(); k++) {
                    JsonObject group = groups.get(k).getAsJsonObject();
                    String number = group.get("number").getAsString();
                    groupList.add(number);
                }
            }
        }

        return groupList; // Возвращаем список номеров групп
    }


    public static List<Lesson> getInfo(String weekday) { //получаем информацию о занятиях в день недели
        List<Lesson> lessons = new ArrayList<>();
        HttpURLConnection con = null;

        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { // ранний выход
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                    lessons = parseLessons(jsonObject, weekday);
                }
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return lessons;
    }

    private static List<Lesson> parseLessons(JsonObject jsonObject, String weekday) { //парсим занятия (weekday от 0 до 6)
        List<Lesson> lessons = new ArrayList<>();
        jsonObject.getAsJsonObject(groupNumberForQuery)
                .getAsJsonObject("days")
                .getAsJsonObject(weekday)
                .getAsJsonArray("lessons")
                .forEach(lessonElement -> {
                    Gson gson = new Gson();
                    Lesson lesson = gson.fromJson(lessonElement, Lesson.class);
                    lessons.add(lesson);
                });
        return lessons;
    }
}
