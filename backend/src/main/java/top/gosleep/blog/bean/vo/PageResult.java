package top.gosleep.blog.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    private List<T> items;
    private long total;
    private long page;
    private long size;

    public long getTotalPages() {
        return (total + size - 1) / size;
    }

    public static <T> PageResult<T> empty(long page, long size) {
        return new PageResult<>(Collections.emptyList(), 0, page, size);
    }
}