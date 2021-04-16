/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.utility;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.familysearch.homelands.admin.parser.helper.ExcelUtility;

/**
 * @author wjohnson000
 *
 */
public class TestExcelGeneration {

    public static void main(String...args) throws Exception {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(listify("Name", "Age", "Phone#", "Position", "Salary", "A Date"));
        rows.add(listify("Tom", 25, "801-555-1111", "MANAGER", 100000.0, LocalDate.now()));
        rows.add(listify("Betty", 44, "801-555-2222", "ARCHITECT", 95000.0, LocalDate.now()));
        rows.add(listify("Harry", 17, "801-555-3333", "INTERN", 17500.0, LocalDate.now()));
        rows.add(listify("Marge", 22, "801-555-4444", "PROGRAMMER", 125000.0, LocalDate.now()));
        rows.add(listify("Alexander", 56, "801-555-5555", "PROGRAMMER", 125000.0, LocalDate.now()));
        rows.add(listify("Frieda", 81, "801-555-6666", "STAFF", 45000.0, LocalDate.now()));
        rows.add(listify("Aloycius", 77, "801-555-7777", "STAFF", 45000.0, LocalDate.now()));

        byte[] rawRaw = ExcelUtility.generateExcelData(rows, "Staff", true);
        Files.write(Paths.get("C:/temp/first-spreadsheet.xlsx"), rawRaw, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static List<Object> listify(Object... raw) {
        return Arrays.stream(raw).collect(Collectors.toList());
    }
}
