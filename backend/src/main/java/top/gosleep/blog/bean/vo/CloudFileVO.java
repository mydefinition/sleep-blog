package top.gosleep.blog.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import top.gosleep.blog.bean.entity.CloudFile;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CloudFileVO {
    private Long id;
    private Long parentId;
    private Long ownerId;
    private String userName;
    private String fileName;
    private Integer isDir;
    private LocalDateTime createdAt;

    public static CloudFileVO from(CloudFile cloudFile) {
        return new CloudFileVO(cloudFile.getId(),
                cloudFile.getParentId(),
                cloudFile.getOwnerId(),
                null,
                cloudFile.getName(),
                cloudFile.getIsDir(),
                cloudFile.getCreatedAt());
    }
}
