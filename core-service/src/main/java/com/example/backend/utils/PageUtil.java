package com.example.backend.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class PageUtil {
    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_LIMIT = 10;

    private PageUtil() {
    }

    public static Pageable pageable(int index, int limit) {
        int safeIndex = Math.max(index, 0);
        int safeLimit = limit <= 0 ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT);
        return PageRequest.of(safeIndex, safeLimit);
    }
}
