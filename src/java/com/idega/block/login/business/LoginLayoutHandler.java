package com.idega.block.login.business;

import java.util.List;
import com.idega.block.login.presentation.Login;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.text.Text;
import com.idega.builder.handler.PropertyHandler;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class LoginLayoutHandler implements PropertyHandler {

  public LoginLayoutHandler() {
  }
  public List getDefaultHandlerTypes() {
    return null;
  }
  public PresentationObject getHandlerObject(String name,String value,IWContext iwc){
    DropdownMenu menu = new DropdownMenu(name);
    menu.addMenuElement("","Select:");
    menu.addMenuElement(Login.LAYOUT_VERTICAL,"Vertical");
    menu.addMenuElement(Login.LAYOUT_HORIZONTAL,"Horizontal");
    menu.addMenuElement(Login.LAYOUT_STACKED,"Stacked");
    menu.addMenuElement(Login.SINGLE_LINE,"Single row");
    menu.setSelectedElement(value);
    return menu;
  }

}