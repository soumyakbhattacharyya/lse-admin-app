package com.lse.admin.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.lse.admin.aws.ConnectToLedger;
import com.lse.admin.aws.Constants;
import com.lse.admin.skin.ShellHelper;

import software.amazon.qldb.QldbSession;

@ShellComponent
public class VerifyConnectivity {

  @Autowired
  ShellHelper shellHelper;

  public static final Logger log = LoggerFactory.getLogger(VerifyConnectivity.class);

  @ShellMethod(value = "Verify Connectivity.", key = "connect")
  public String connect() {
    QldbSession session = ConnectToLedger.createQldbSession();
    if (Constants.LEDGER_NAME.equals(session.getLedgerName())) {
      return shellHelper.getSuccessMessage("Connectivity was successful.");
    }
    return shellHelper.getErrorMessage("Connectivity with required ledger was unsuccessful");
  }
}
