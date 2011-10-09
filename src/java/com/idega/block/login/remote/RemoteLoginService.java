/**
 * 
 */
package com.idega.block.login.remote;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * Interface to merge com.idega.block.caldav and com.idega.block.login for 
 * bedework authentication.
 * You can report about problems to: <a href="mailto:martynas@idega.com">Martynas Stakė</a>
 * You can expect to find some test cases notice in the end of the file.
 *
 * @version 1.0.0 2011.08.24
 * @author martynas
 */
public interface RemoteLoginService {
    
    /**
     * <p>Logins to remote server.</p>
     * @param submitButton
     * @return
     */
    public UIComponent getUIComponentForLogin(FacesContext context);
    
    /**
     * <p>Logout from remote server.</p>
     * @return
     */
    public UIComponent getUIComponentForLogout();
}
