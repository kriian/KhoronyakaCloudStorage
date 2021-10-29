package utils;


/**
 * Класс является общедоступным хранилищем типов команд.
 * По дефолту является Serializable
 */
public enum CommandType {

    FILE_MESSAGE, // файл как посылка
    FILE_REQUEST, // запрос на получение файла
    LIST_MESSAGE // посылка со списком файлов

}
