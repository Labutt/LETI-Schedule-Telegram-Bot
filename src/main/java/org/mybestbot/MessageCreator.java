package org.mybestbot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MessageCreator {
    // Дублирование!
    public static String sendDayScheduleWeek1(String weekday){ //расписание на день нечетной недели
        List<Lesson> lessonList = APIquery.getInfo(weekday);
        StringBuilder sb = new StringBuilder(); // переименовать
        for (Lesson lesson : lessonList) {
            if (lesson.getWeek().equals("1")){
                sb.append(printLesson(lesson));
            }
        }
        return sb.toString();
    }
    public static String sendDayScheduleWeek2(String weekday){ //расписание на день четной недели
        List<Lesson> lessonList = APIquery.getInfo(weekday);
        StringBuilder sb = new StringBuilder();
        for (Lesson lesson : lessonList) {
            if (lesson.getWeek().equals("2")){
                sb.append(printLesson(lesson));
            }
        }
        return sb.toString();
    }

    public static String sendDaySchedule(String weekday){ //расписание на день (опция Узнать расписание на конкретный день)
        StringBuilder sb = new StringBuilder();
        // разделители из дефисов под размер экрана подобраны?
        sb.append("НЕЧЕТНАЯ НЕДЕЛЯ").append("\n").append("-------------------------------------------------\n");
        sb.append(sendDayScheduleWeek1(weekday));
        sb.append("ЧЕТНАЯ НЕДЕЛЯ").append("\n").append("-------------------------------------------------\n");
        sb.append(sendDayScheduleWeek2(weekday));
        return sb.toString();
    }

    public static String sendClosestLesson(){ //отправляем ближайшее занятие
        LocalTime currentTime = LocalTime.now(); // лучше указать константность
        int timeInSeconds = currentTime.toSecondOfDay();
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        Integer weekdayNum = dayOfWeek.getValue() - 1; // Получаем номер дня недели
        String weekday = weekdayNum.toString();

        List<Lesson> lessonList = APIquery.getInfo(weekday);
        String currentWeek = checkWeek();
        int closestLessonTimeDifference = Integer.MAX_VALUE; // Изначально устанавливаем максимально возможное значение
        Lesson nearestLesson = null;

        for (Lesson lesson : lessonList) {
            if (lesson.getWeek().equals(currentWeek)) {
                int timeDifference = lesson.getStartTimeSeconds() - timeInSeconds; // либо константа, либо вынести за цикл
                if (timeDifference > 0 && timeDifference < closestLessonTimeDifference) {
                    closestLessonTimeDifference = timeDifference;
                    nearestLesson = lesson;
                }
            }
        }

        if (nearestLesson == null) {
            if (weekday.equals("5") || weekday.equals("6")) { // Можно было сразу это проверить и ранний выход сделать
                return "У вас на этой неделе кончились пары! Повезло!";
            } else {
                return "Сегодня пар больше не будет. Пора домой!";
            }
        }
        else { // Лишний else
            return printLesson(nearestLesson);
        }
    }

    public static String sendNextDaySchedule(){ //расписание на следующий день
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        Integer weekdayNum = dayOfWeek.getValue() - 1; // Получаем номер дня недели
        String weekday = weekdayNum.toString();
        String currentWeek = checkWeek();
        if(currentWeek.equals("1")){ // Лапша из условий
            if(weekday.equals("6")){
                return sendDayScheduleWeek2("0");
            }
            else {
                weekdayNum += 1;
                weekday = weekdayNum.toString();
                return sendDayScheduleWeek1(weekday);
            }
        }
        else {
            if(weekday.equals("6")){
                return sendDayScheduleWeek1("0");
            }
            else {
                weekdayNum += 1;
                weekday = weekdayNum.toString();
                return sendDayScheduleWeek2(weekday);
            }
        }
    }

    private static String printLesson(Lesson lesson) { //просто делаем строку с занятием
        StringBuilder sb = new StringBuilder();
        sb.append("Название: ").append(lesson.getName()).append("\n")
                .append("Учитель: ").append(lesson.getTeacher()).append("\n")
//                        .append("Второй учитель: ").append(lesson.getSecondTeacher()).append("\n") // Закомментированный код
                .append("Тип предмета: ").append(lesson.getSubjectType()).append("\n")
//                        .append("Неделя: ").append(lesson.getWeek()).append("\n")
                .append("Время начала: ").append(lesson.getStartTime()).append("\n")
                .append("Время окончания: ").append(lesson.getEndTime()).append("\n")
                .append("Аудитория: ").append(lesson.getRoom()).append("\n")
                .append("Форма: ").append(lesson.getForm()).append("\n")
                .append("-------------------------------------------------\n");
        return sb.toString();
    }

    public static String checkWeek(){ //проверяем, четная или нечетная неделя // Лучше вернуть enum
        LocalDate semesterStart = LocalDate.of(LocalDate.now().getYear(), 9, 1); // Магические числа
        LocalDate currentDate = LocalDate.now();
        int weekOfSemester = (int) ((currentDate.toEpochDay() - semesterStart.toEpochDay()) / 7) + 1;
        if (weekOfSemester % 2 == 0) {
            return "2";
        } else {
            return "1";
        }
    }
}
