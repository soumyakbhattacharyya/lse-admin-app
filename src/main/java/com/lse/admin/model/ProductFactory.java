package com.lse.admin.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

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

      while (cellIterator.hasNext()) {
        Cell cell = cellIterator.next();

        if (currentRow.getRowNum() > 0) { // To filter column headings
          if (cell.getColumnIndex() == 0) {// To match column
                                           // index

            // product attributes
            String id = UUID.randomUUID().toString();
            int code = 0;
            String type = "";
            String description = "";
            double buyUnitPrice = 0;
            double suggestedUnitPrice = 0;

            int columnIndex = cell.getColumnIndex();
            if (columnIndex == 0) {
              code = (int) cell.getNumericCellValue();
            } else if (columnIndex == 1) {
              type = cell.getRichStringCellValue().getString();
            } else if (columnIndex == 2) {
              description = cell.getRichStringCellValue().getString();
            } else if (columnIndex == 3) {
              buyUnitPrice = cell.getNumericCellValue();
            } else if (columnIndex == 4) {
              suggestedUnitPrice = cell.getNumericCellValue();
            }

            Product product = new Product(id, code, type, description, buyUnitPrice, suggestedUnitPrice);
            products.add(product);
            // RegisterProductService.execute(product);
          }

        }
      }

    }

    return products;

  }

}
