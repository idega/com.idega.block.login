/*
 * $Id: LoginLayoutHandler.java,v 1.7 2004/06/28 11:18:35 thomas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.block.login.business;

import java.util.List;

import com.idega.block.login.presentation.Login;
import com.idega.core.builder.data.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.DropdownMenu;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class LoginLayoutHandler implements ICPropertyHandler {
  /**
   *
   */
  public LoginLayoutHandler() {
  }

  /**
   *
   */
  public List getDefaultHandlerTypes() {
    return(null);
  }

  /**
   *
   */
  public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
    DropdownMenu menu = new DropdownMenu(name);
    menu.addMenuElement("","Select:");
    menu.addMenuElement(Login.LAYOUT_VERTICAL,"Vertical");
    menu.addMenuElement(Login.LAYOUT_HORIZONTAL,"Horizontal");
    menu.addMenuElement(Login.LAYOUT_STACKED,"Stacked");
    menu.addMenuElement(Login.SINGLE_LINE,"Single row");
    menu.addMenuElement(Login.LAYOUT_FORWARD_LINK,"Forward link");
    menu.setSelectedElement(value);
    return(menu);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
