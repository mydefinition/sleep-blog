package com.blog.converter;

import com.blog.dto.TagDto;
import com.blog.entity.Tag;

public class TagConverter {
    public static TagDto toDto(Tag tag) {
        TagDto dto = new TagDto();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }

    public static Tag toEntity(TagDto dto) {
        Tag tag = new Tag();
        tag.setId(dto.getId());
        tag.setName(dto.getName());
        return tag;
    }
}
