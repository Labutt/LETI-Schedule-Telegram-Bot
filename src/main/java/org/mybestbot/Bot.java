package org.mybestbot;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Bot extends TelegramLongPollingBot {
    HashMap<Long, String> users = new HashMap<Long, String>();
    HashMap<Long, Boolean> usersWelcome = new HashMap<Long, Boolean>();

    @Override
    public String getBotUsername() {
        return "BestETUScheduleBot";
    }

    @Override
    public String getBotToken() {
        return setBotToken();
    }
    public String setBotToken() {
        try {
            Path path = Paths.get("D:\\botSource\\token.txt");
            return Files.readString(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();
            handleMessage(msg.getText(), id);

            System.out.println(user.getFirstName() + " wrote " + msg.getText()); //лог
        }
    }


    public  void handleMessage(String message, long id){ //обработка сообщений пользователя
        if(!usersWelcome.containsKey(id)){ //приветствие пользователя в первый раз
            sendText(id,"Добро пожаловать! Данный бот поможет вам узнать расписание вашего любимого университета! Для начала, введите номер вашей группы.");
            usersWelcome.put(id, true);
        }
        else if(message.matches("\\d{4}") && users.get(id)== null){ //обработка сообщения с номером группы
            if(APIquery.getInfoOnExistingGroups().contains(message)) {
                sendText(id, "Вы успешно ввели номер группы: " + message);
                users.putIfAbsent(id, message);
                sendMainMenu(id);
            }
            else{
                sendText(id, "Кажется, такой группы не существует. Пожалуйста, введите существующую группу");
            }
        }
        else if (users.get(id)== null && usersWelcome.containsKey(id)){
            sendText(id, "Немного вас не понял. Для начала введите номер своей группы");
        }

        else if(users.get(id) != null){
            switch (message){ //обработка сообщений в основном цикле работы программы
                case "Узнать расписание на эту неделю":
                    APIquery.setGroup(users.get(id));
                    Locale russianLocale = new Locale("ru");
                    for(int i = 0; i < 6; i++){
                        DayOfWeek day = DayOfWeek.of(i+1);
                        String dayInRussian = day.getDisplayName(TextStyle.FULL, russianLocale);
                        String weekday = String.valueOf(i);
                        sendText(id, dayInRussian.toUpperCase());
                        if (MessageCreator.checkWeek().equals("1")) {
                            sendText(id, MessageCreator.sendDayScheduleWeek1(weekday));
                        }
                        else if (MessageCreator.checkWeek().equals("2")){
                            sendText(id, MessageCreator.sendDayScheduleWeek1(weekday));
                        }
                    }
                    break;
                case "Узнать ближайшую пару":
                    APIquery.setGroup(users.get(id));
                    sendText(id, MessageCreator.sendClosestLesson());
                    break;
                case "Узнать расписание на конкретный день":
                    APIquery.setGroup(users.get(id));
                    sendDaySelectionMenu(id);
                    break;
                case "Узнать расписание на следующий день":
                    APIquery.setGroup(users.get(id));
                    sendText(id, MessageCreator.sendNextDaySchedule());
                    break;
                case "Изменить номер группы":
                    sendText(id, "Введите другой номер группы");
                    users.put(id, null);
                    break;
                case "Понедельник":
                    APIquery.setGroup(users.get(id));
                    sendText(id, MessageCreator.sendDaySchedule("0"));
                    break;
                case "Вторник":
                    APIquery.setGroup(users.get(id));
                    sendText(id, MessageCreator.sendDaySchedule("1"));
                    break;
                case "Среда":
                    APIquery.setGroup(users.get(id));
                    sendText(id, MessageCreator.sendDaySchedule("2"));
                    break;
                case "Четверг":
                    APIquery.setGroup(users.get(id));
                    sendText(id, MessageCreator.sendDaySchedule("3"));
                    break;
                case "Пятница":
                    APIquery.setGroup(users.get(id));
                    sendText(id, MessageCreator.sendDaySchedule("4"));
                    break;
                case "Суббота":
                    APIquery.setGroup(users.get(id));
                    sendText(id, MessageCreator.sendDaySchedule("5"));
                    break;
                case "Назад в главное меню":
                    sendMainMenu(id);
                    break;
                default:
                    sendText(id, "К сожалению, я не понимаю о чем вы. Выберете одну из предложенных опций.");
                    break;

            }
        }
        else {
            sendText(id, "К сожалению, я не понимаю о чем вы."); //если непредвиденное сообщение
        }
    }

    public void sendText(Long who, String what){ //отправка сообщений пользователю
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace(); //лог, если ошибка при отправке пользователю сообщений
            System.out.println("Ошибка при отправке сообщения: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void sendMainMenu(Long userId) { //главное меню

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Узнать расписание на эту неделю");
        row1.add("Узнать ближайшую пару");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Узнать расписание на конкретный день");
        row2.add("Узнать расписание на следующий день");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Изменить номер группы");

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(userId));
        message.setText("Выберите опцию:");
        message.setReplyMarkup(keyboardMarkup); // Присоединяем клавиатуру

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDaySelectionMenu(Long userId) { //меню с днями недели
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Понедельник");
        row1.add("Вторник");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Среда");
        row2.add("Четверг");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Пятница");
        row3.add("Суббота");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Назад в главное меню");

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(userId));
        message.setText("Выберите день недели:");
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
// Comment for file change
