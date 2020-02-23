package com.lse.admin.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
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

import com.lse.admin.aws.RegisterProductService;
import com.lse.admin.config.InputReader;
import com.lse.admin.model.Product;
import com.lse.admin.model.ProductFactory;
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

      Sheet sheet = workbook.getSheetAt(0);
      if (null != sheet & "Inventory".equalsIgnoreCase(sheet.getSheetName())) {

        List<Product> product = ProductFactory.THIS.from(sheet);
        shellHelper.printSuccess("finished reading products from excel sheet");
        RegisterProductService.execute(product);
        shellHelper.printSuccess("successfully created records");
     }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return inputDirToReadFrom;
  }

}
