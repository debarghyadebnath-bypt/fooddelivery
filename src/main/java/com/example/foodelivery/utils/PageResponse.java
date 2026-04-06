package com.example.foodelivery.utils;

import com.example.foodelivery.dto.PageableDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageResponse<T> {
    private List<T> content;
    private PageableDto pageable;
    private long totalElements;
    private int totalPages;
    private int size;
    private int numberOfElements;
}