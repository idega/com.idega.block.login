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
import com.idega.jmodule.object.ModuleInfo;



public class LoginEditorWindow extends IWAdminWindow{

  public LoginEditorWindow() {
    super();
  }

  public void main(ModuleInfo modinfo) throws Exception{
    LoginEditor BE = new LoginEditor();
    add(BE);
    setTitle("Login Editor");
  }
}