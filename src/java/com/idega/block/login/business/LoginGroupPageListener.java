package com.idega.block.login.business;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.builder.business.BuilderService;
import com.idega.event.IWPageEventListener;
import com.idega.idegaweb.IWException;
import com.idega.presentation.IWContext;
import com.idega.user.data.Group;

/**
 * @author aron
 *
 */
public class LoginGroupPageListener implements IWPageEventListener {
	
	private static String prmGroupToPageMap = "login_group_to_page_map";
	
	public boolean actionPerformed(IWContext iwc) throws IWException {
		/** todo: */
		if ( iwc.isLoggedOn() && LoginBusinessBean.isLogOnAction(iwc) ){
			System.err.println("trying to get page for usergroup");
			String page = checkUserGroups(iwc);
			if(page!=null){
				System.err.println("setting group page");
				try {
					((BuilderService)IBOLookup.getServiceInstance(iwc ,BuilderService.class)).setPriorityPageId(iwc, page);
				} catch (IBOLookupException e) {
					throw new IWException("[LoginGroupPageListener] BuilderService could not be found");
				} catch (RemoteException e) {
					throw new IWException("[loginGroupPageListener] BuilderService could not be invoked");
				}
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
