package com.medilabo.libs.commons.paging;

import java.util.List;

public final class Pages {
    private Pages() {}

    public static <T> PageResponse<T> of(List<T> content, long totalElements, int page, int size) {
        int totalPages = size <= 0 ? 0 : (int) Math.ceil(totalElements / (double) size);
        return new PageResponse<>(content, totalElements, totalPages, page, size);
    }
}