package com.yangyoung.english.util;

import com.yangyoung.english.configuration.OneIndexedPageable;
import com.yangyoung.english.util.SheetType;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UtilService {

    public static List<List<Object>> readExcel(MultipartFile file, SheetType sheetType) throws IOException {
        List<List<Object>> data = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#");

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(sheetType.getSheetIndex());

        Iterator<Row> rowIterator = sheet.iterator();
        // Skip the first row
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();

            List<Object> rowData = new ArrayList<>();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getCellType()) {
                    case STRING:
                        rowData.add(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            rowData.add(cell.getDateCellValue());
                        } else {
                            rowData.add(df.format(cell.getNumericCellValue()));
                        }
                        break;
                    case BOOLEAN:
                        rowData.add(cell.getBooleanCellValue());
                        break;
                    default:
                        rowData.add("");
                }
            }
            data.add(rowData);
        }

        workbook.close();

        return data;
    }

    public static OneIndexedPageable setOneIndexedPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new OneIndexedPageable(pageable);
    }
}
