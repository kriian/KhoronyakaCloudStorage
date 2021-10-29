package messages;

import lombok.Getter;

import utils.CommandType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Класс для посылки списка файлов наа сервере
 */

@Getter
public class ListMessage extends AbstractMessage {

    private final List<String> files;

    public ListMessage(Path dir) throws Exception {
        setType(CommandType.LIST_MESSAGE);
        files = Files.list(dir).map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
    }
}
