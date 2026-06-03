package com.blog.dto.request;

import lombok.Data;

@Data
public class MkdirRequest {
    private String name;
    private Long localId = 0L;
}