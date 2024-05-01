package com.yangyoung.english.util;

import lombok.Getter;

@Getter
public enum SheetType {

    LECTURE(0),
    STUDENT(1),
    SECTION(2),
    ;

    private final int sheetIndex;

    SheetType(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
}
