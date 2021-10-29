package messages;

import lombok.Getter;
import lombok.Setter;
import utils.CommandType;

import java.io.Serializable;


/**
 * Абстрактный класс для любых типов сообщений, таких как файлы, команды и т.д.
 */

@Getter
@Setter
public class AbstractMessage implements Serializable {
    private CommandType type;
}
