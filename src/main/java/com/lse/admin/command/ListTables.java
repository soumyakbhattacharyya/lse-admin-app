package com.lse.admin.command;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.lse.admin.aws.ConnectToLedger;

@ShellComponent
public class ListTables {

  @ShellMethod(value = "List tables.", key = "list-tab")
  public String list() {
    return ConnectToLedger.listTables();
  }
}
