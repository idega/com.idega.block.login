package com.idega.block.login.presentation;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;

public class LoginEditorWindow extends IWAdminWindow{

  public LoginEditorWindow() {
    super();
    setScrollbar(false);
    setWidth(170 );
    setHeight(270 );
    //keepFocus();
  }

  public void main(IWContext iwc) throws Exception{
    LoginEditor BE = new LoginEditor();
    Table T = new Table(1,1);
    T.setAlignment(1,1,"center");
    T.add(BE,1,1);
    add(T);
    setTitle("Login Editor");
    //addTitle("Login Editor");
  }
}