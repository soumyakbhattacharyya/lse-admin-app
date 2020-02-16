package com.lse.admin.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import com.lse.admin.config.InputReader;
import com.lse.admin.skin.ShellHelper;

@ShellComponent
public class RegisterProduct {

  @Autowired
  ShellHelper shellHelper;

  @Autowired
  InputReader inputReader;

  public static final Logger log = LoggerFactory.getLogger(RegisterProduct.class);

  @ShellMethod(value = "Registers a new product.", key = "register-product")
  public String register() {

    String inputDirToReadFrom = null;
    String fileToReadFrom = null;

    // 1. read user's directory where input files exist--------------------------------------------
    do {
      String inputDirName = inputReader.prompt("Input directory");
      if (StringUtils.hasText(inputDirName)) {
        shellHelper.printSuccess("name for input directory received");
        inputDirToReadFrom = inputDirName;
      } else {
        shellHelper.printWarning("Input directory unknown? Please enter valid input directory!");
      }
    } while (inputDirToReadFrom == null);

    // 2. read name of input file --------------------------------------------
    do {
      String fileName = inputReader.prompt("File name");
      if (StringUtils.hasText(fileName)) {
        shellHelper.printSuccess("file name received");
        fileToReadFrom = fileName;
      } else {
        shellHelper.printWarning("File name unknown? Please enter valid file name!");
      }
    } while (fileToReadFrom == null);

    try (FileInputStream file = new FileInputStream(new File(fileToReadFrom)); Workbook workbook = new XSSFWorkbook(file)) {

      // @SuppressWarnings("resource")
      // Workbook workbook = new XSSFWorkbook(file);
      Sheet sheet = workbook.getSheetAt(0);

      for (Row row : sheet) {

        int currentRow = row.getRowNum();
        log.info("current row " + String.valueOf(currentRow));

        if (currentRow > 0) {

          // product attributes
          String id = UUID.randomUUID().toString();
          double code = 0;
          String shortDescription = "";
          String longDescription = "";
          double price = 0;
          Date createdOn = null;
          String createdBy = "";
          String status = "";

          for (Cell cell : row) {

            int columnIndex = cell.getColumnIndex();
            int rowIndex = cell.getRowIndex();
            String cellType = cell.getCellType().toString();

            // TODO - refactor into switch case and leverage enums to map columns to attribute

            // ignore the header

            log.info("processing data from position rowIndex = {0} columnIndex = {1} for a cell type of {2}", rowIndex, columnIndex, cellType);

            if (columnIndex == 0) {
              code = cell.getNumericCellValue();
            } else if (columnIndex == 1) {
              shortDescription = cell.getRichStringCellValue().getString();
            } else if (columnIndex == 2) {
              longDescription = cell.getRichStringCellValue().getString();
            } else if (columnIndex == 3) {
              price = cell.getNumericCellValue();
            } else if (columnIndex == 4) {
              createdOn = cell.getDateCellValue();
            } else if (columnIndex == 5) {
              createdBy = cell.getRichStringCellValue().getString();
            } else {
              status = cell.getRichStringCellValue().getString();
            }
          }

          // Product product = new Product(id, String.valueOf(code), shortDescription, longDescription, price, QldbHelper.convertToLocalDate("2020-02-02"), createdBy,
          // ProductStatus.NEW);
          // RegisterProduct.execute(null);
        } else {
          log.info("ignore processing header info");
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return inputDirToReadFrom;
  }

}
