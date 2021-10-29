package messages;

import lombok.Getter;
import utils.CommandType;


/**
 *
 */

@Getter
public class FileRequest extends AbstractMessage {

    private final String name;

    public FileRequest(String name) {
        this.name = name;
        setType(CommandType.FILE_REQUEST);
    }
}
