package com.mathbank.common.util;

import lombok.Getter;

@Getter
public class PageInfo {
    private final int currentPage;
    private final int pageSize;
    private final int totalCount;
    private final int totalPages;
    private final int offset;

    public PageInfo(int currentPage, int pageSize, int totalCount) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
        this.offset = (currentPage - 1) * pageSize;
    }

    public boolean hasPrev() {
        return currentPage > 1;
    }

    public boolean hasNext() {
        return currentPage < totalPages;
    }
}
