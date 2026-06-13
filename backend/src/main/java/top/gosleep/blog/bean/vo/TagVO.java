package top.gosleep.blog.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import top.gosleep.blog.bean.entity.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class TagVO {
    private Long id;
    private String name;

    public static TagVO from(Tag tag) {
        return new TagVO(tag.getId(), tag.getName());
    }

    public static List<TagVO> from(List<Tag> tags) {
        return tags.stream().map(TagVO::from).collect(Collectors.toList());
    }
}
