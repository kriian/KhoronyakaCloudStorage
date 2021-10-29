package messages;

import lombok.Builder;
import lombok.Getter;
import utils.CommandType;


/**
 * Класс объектов сообщений для файлов
 */

@Builder
@Getter
public class FileMessage extends AbstractMessage {
    private static final int BUTCH_SIZE = 8192;

    private final String name;
    private final long size;
    private final byte[] bytes;
    private final boolean isFirstBatch; // это первая посылка
    private final int endByteNum;
    private final boolean isFinishBatch; // это последняя посылка

    public FileMessage(String name,
                       long size,
                       byte[] bytes,
                       boolean isFirstButch,
                       int endByteNum,
                       boolean isFinishBatch) {
        this.name = name;
        this.size = size;
        this.bytes = bytes;
        this.isFirstBatch = isFirstButch;
        this.endByteNum = endByteNum;
        this.isFinishBatch = isFinishBatch;
        setType(CommandType.FILE_MESSAGE);
    }
}
