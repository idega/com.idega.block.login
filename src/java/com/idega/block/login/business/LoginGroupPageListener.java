package com.idega.block.login.business;

import com.idega.business.*;
import com.idega.presentation.IWContext;
import com.idega.idegaweb.IWException;
import com.idega.user.data.Group;
import com.idega.builder.business.BuilderLogic;
import com.idega.core.accesscontrol.business.*;
import com.idega.event.*;

import java.util.*;

/**
 * @author aron
 *
 */
public class LoginGroupPageListener implements IWPageEventListener {
	
	private static String prmGroupToPageMap = "login_group_to_page_map";
	
	public boolean actionPerformed(IWContext iwc)throws IWException{
		/** todo: */
		if ( iwc.isLoggedOn() && LoginBusinessBean.isLogOnAction(iwc) ){
			System.err.println("trying to get page for usergroup");
			String page = checkUserGroups(iwc);
			if(page!=null){
				System.err.println("setting group page");
				BuilderLogic.getInstance().setCurrentPriorityPageID(iwc,page);
			}
		}
		return false;
	}
	
	private String checkUserGroups(IWContext iwc){
		List userGroups = LoginBusinessBean.getPermissionGroups(iwc);
		Map G2P = getLoginGroupPageMap(iwc);
		if(userGroups!=null ){
			if(G2P !=null){
				Iterator iter = userGroups.iterator();
				Group g;
				String gid;
				while(iter.hasNext()){
					g = (Group) iter.next();
					gid = g.getPrimaryKey().toString();
					if(G2P.containsKey(gid))
						return (String) G2P.get(gid);

				}
			}
			else{
			System.err.println("no pagesInMemory ");
			}
		}
		else{
			System.err.println("no userGroups ");
		}
		return null;
	}
	
	public static void setLoginGroupPageMap(IWContext iwc,Map GroupToPageMap ){
		iwc.setApplicationAttribute(prmGroupToPageMap,GroupToPageMap);
	}
	
	public static Map getLoginGroupPageMap(IWContext iwc){
		Object map = iwc.getApplicationAttribute(prmGroupToPageMap);
		if(map!=null)
			return (Map) map;
		else
			map = new HashMap();
		return null;
	}
	
	public static void addGroupPageMapping(IWContext iwc,String groupId,String pageId){
		Map m = getLoginGroupPageMap(iwc);
		if(m.containsKey(groupId)){
			m.put(groupId,pageId);
		}
		setLoginGroupPageMap(iwc,m);
	}
}
