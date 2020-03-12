package com.lse.admin.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lse.admin.aws.RegisterProductService;

public final class ProductFactory {

  public static final ProductFactory THIS = new ProductFactory();

  private ProductFactory() {
  }

  /**
   * takes an excel sheet as input and returns a collection of products
   */
  public List<Product> from(Sheet sheet) {

    final List<Product> products = new ArrayList<Product>();

    Iterator<Row> rowIterator = sheet.iterator();
    while (rowIterator.hasNext()) {

      Row currentRow = rowIterator.next();
      Iterator<Cell> cellIterator = currentRow.cellIterator();

      if (currentRow.getRowNum() > 0) {

        // product attributes
        String id = UUID.randomUUID().toString();
        int code = 0;
        String type = "";
        String description = "";
        double buyUnitPrice = 0;
        double suggestedUnitPrice = 0;

        System.out.println("row number " + currentRow.getRowNum());
        while (cellIterator.hasNext()) {
          Cell cell = cellIterator.next();

          int columnIndex = cell.getColumnIndex();
          if (columnIndex == 0) {
            code = (int) cell.getNumericCellValue();
            continue;
          } else if (columnIndex == 1) {
            type = cell.getRichStringCellValue().getString();
            continue;
          } else if (columnIndex == 2) {
            description = cell.getRichStringCellValue().getString();
            continue;
          } else if (columnIndex == 3) {
            buyUnitPrice = cell.getNumericCellValue();
            continue;
          } else if (columnIndex == 4) {
            suggestedUnitPrice = cell.getNumericCellValue();
            continue;
          }

        }

        if (code != 0) {
          Product product = new Product(id, code, type, description, buyUnitPrice, suggestedUnitPrice);
          products.add(product);
        }

      }

      // RegisterProductService.execute(products);
    }

    return products;

  }

  public static void main(String[] args) {
    String fileToReadFrom = "C:\\Soumyak\\workspace\\lse-admin-cli\\product-detail.xlsx";
    try (FileInputStream file = new FileInputStream(new File(fileToReadFrom)); Workbook workbook = new XSSFWorkbook(file)) {

      Sheet sheet = workbook.getSheetAt(0);
      if (null != sheet & "Inventory".equalsIgnoreCase(sheet.getSheetName())) {

        List<Product> product = ProductFactory.THIS.from(sheet);
        // shellHelper.printSuccess("finished reading products from excel sheet");
        // RegisterProductService.execute(product);
        // shellHelper.printSuccess("successfully created records");
        System.out.println("just to stop" + product.size());
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
