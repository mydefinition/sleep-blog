package top.gosleep.blog.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.gosleep.blog.bean.entity.Tag;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagVO {
    private Long id;
    private String name;

    public TagVO(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
    }

    public static TagVO fromEntity(Tag tag) {
        return new TagVO(tag);
    }
}