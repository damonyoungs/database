package org.damon.database.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @Author chengrong.yang
 * @Date 2021/2/1 14:42
 */
public class PageRequestSort extends PageRequest {
    private static final long serialVersionUID = 1701815455593097273L;

    protected PageRequestSort(int page, int size, Sort sort) {
        super(page, size, sort);
    }

    public static PageRequest by(int page, int size) {
        return of(page, size, SortUtil.sort());
    }
}
