package com.lean2708.common_library.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private int page;
    private int size;
    private long totalPages;
    private long totalItems;
    private List<T> items;
}
