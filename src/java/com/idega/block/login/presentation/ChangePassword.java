package com.idega.block.login.presentation;

import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.builder.data.ICPage;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.PasswordInput;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;

public class ChangePassword  extends Block {

	private final static int ACTION_VIEW_FORM = 1;
	private final static int ACTION_FORM_SUBMIT = 2;

	private final static String PARAMETER_FORM_SUBMIT = "cp_sbmt";

	private final static String PARAMETER_CURRENT_PASSWORD = "cap_c_pw";
	private final static String PARAMETER_NEW_PASSWORD = "cap_n_pw";
	private final static String PARAMETER_NEW_PASSWORD_REPEATED = "cap_n_pw_r";

	private int MIN_PASSWORD_LENGTH = 8;
	
	private final static String KEY_PREFIX = "changePassowrd.";
	private final static String KEY_CURRENT_PASSWORD = KEY_PREFIX + "current_password";
	private final static String KEY_NEW_PASSWORD = KEY_PREFIX + "new_password";
	private final static String KEY_NEW_PASSWORD_REPEATED = KEY_PREFIX + "new_password_repeated";
	private final static String KEY_UPDATE = KEY_PREFIX + "update";

	private final static String KEY_PASSWORD_EMPTY = KEY_PREFIX + "password_empty";
	private final static String KEY_PASSWORD_REPEATED_EMPTY = KEY_PREFIX + "password_repeated_empty";
	private final static String KEY_PASSWORDS_NOT_SAME = KEY_PREFIX + "passwords_not_same";
	private final static String KEY_PASSWORD_INVALID = KEY_PREFIX + "invalid_password";	
	private final static String KEY_PASSWORD_TOO_SHORT = KEY_PREFIX + "password_too_short";	
	private final static String KEY_PASSWORD_SAVED = KEY_PREFIX + "password_saved";	

	private final static String DEFAULT_CURRENT_PASSWORD = "Current password";	
	private final static String DEFAULT_NEW_PASSWORD = "New password";	
	private final static String DEFAULT_NEW_PASSWORD_REPEATED = "Repeat new password";	
	private final static String DEFAULT_UPDATE = "Update";	

	private final static String DEFAULT_PASSWORD_EMPTY = "Password cannot be empty.";		
	private final static String DEFAULT_PASSWORD_REPEATED_EMPTY = "Repeated password cannot be empty.";		
	private final static String DEFAULT_PASSWORDS_NOT_SAME = "New passwords not the same.";		
	private final static String DEFAULT_PASSWORD_INVALID = "Invalid password.";		
	private final static String DEFAULT_PASSWORD_TOO_SHORT = "Password too short. Must be at least {0} letters/digits.";		
	private final static String DEFAULT_PASSWORD_SAVED = "Your password has been saved.";	

	private User user = null;
	private IWResourceBundle iwrb;
		
	private int parseAction (final IWContext iwc) {
		if (iwc.isParameterSet(PARAMETER_FORM_SUBMIT)) {
			return ACTION_FORM_SUBMIT;
		}
		else {
			return ACTION_VIEW_FORM;
		}
	}
	
	public void main(IWContext iwc) {
		if (!iwc.isLoggedOn()) {
			return;
		}
		this.iwrb = getResourceBundle(iwc);
		this.user = iwc.getCurrentUser();

		int action = parseAction(iwc);
		switch (action) {
			case ACTION_VIEW_FORM:
				drawForm(iwc);
				break;
			case ACTION_FORM_SUBMIT:
				updatePassword(iwc);
				break;
		}
	}

	private void drawForm(IWContext iwc) {
		Form form = new Form();
		form.addParameter(PARAMETER_FORM_SUBMIT, Boolean.TRUE.toString());
		form.setID("changePasswordForm");
		form.setStyleClass("changePasswordForm");
		
		Layer header = new Layer(Layer.DIV);
		header.setStyleClass("header");
		form.add(header);
		
		Heading1 heading = new Heading1(this.iwrb.getLocalizedString("change_password", "Change password"));
		header.add(heading);
		
		Layer contents = new Layer(Layer.DIV);
		contents.setStyleClass("formContents");
		form.add(contents);
		
		Layer section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		contents.add(section);
		
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(this.iwrb.getLocalizedString("change_password_helper_text", "Please fill in your current password and enter the new desired one.")));
		section.add(paragraph);
		
		PasswordInput currentPassword = new PasswordInput(PARAMETER_CURRENT_PASSWORD);	
		currentPassword.keepStatusOnAction(true);

		PasswordInput newPassword = new PasswordInput(PARAMETER_NEW_PASSWORD);
		newPassword.keepStatusOnAction(true);

		PasswordInput newPasswordRepeat = new PasswordInput(PARAMETER_NEW_PASSWORD_REPEATED);
		newPasswordRepeat.keepStatusOnAction(true);

		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		Label label = new Label(this.iwrb.getLocalizedString(KEY_CURRENT_PASSWORD, DEFAULT_CURRENT_PASSWORD), currentPassword);
		formItem.add(label);
		formItem.add(currentPassword);
		section.add(formItem);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString(KEY_NEW_PASSWORD, DEFAULT_NEW_PASSWORD), newPassword);
		formItem.add(label);
		formItem.add(newPassword);
		section.add(formItem);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString(KEY_NEW_PASSWORD_REPEATED, DEFAULT_NEW_PASSWORD_REPEATED), newPasswordRepeat);
		formItem.add(label);
		formItem.add(newPasswordRepeat);
		section.add(formItem);
		
		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		section.add(clearLayer);

		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		contents.add(buttonLayer);
		
		Layer span = new Layer(Layer.SPAN);
		span.add(new Text(this.iwrb.getLocalizedString(KEY_UPDATE, DEFAULT_UPDATE)));
		Link send = new Link(span);
		send.setStyleClass("sendLink");
		send.setToFormSubmit(form);
		buttonLayer.add(send);
		
		add(form);
	}
	
	private void updatePassword(IWContext iwc) {
		LoginTable loginTable = LoginDBHandler.getUserLogin(((Integer) this.user.getPrimaryKey()).intValue());
		String login = loginTable.getUserLogin();
		String currentPassword = iwc.getParameter(PARAMETER_CURRENT_PASSWORD);
		String newPassword1 = iwc.getParameter(PARAMETER_NEW_PASSWORD);
		String newPassword2 = iwc.getParameter(PARAMETER_NEW_PASSWORD_REPEATED);		

		boolean hasErrors = false;
		Collection errors = new ArrayList();
		
		if (!LoginDBHandler.verifyPassword(login, currentPassword)) {
			hasErrors = true;
			errors.add(this.iwrb.getLocalizedString(KEY_PASSWORD_INVALID, DEFAULT_PASSWORD_INVALID));
		}

		// Validate new password
		if (!newPassword1.equals("") || !newPassword2.equals("")) {
			if (newPassword1.equals("")) {
				hasErrors = true;
				errors.add(this.iwrb.getLocalizedString(KEY_PASSWORD_EMPTY, DEFAULT_PASSWORD_EMPTY));
			}
			if (newPassword2.equals("")) {
				hasErrors = true;
				errors.add(this.iwrb.getLocalizedString(KEY_PASSWORD_REPEATED_EMPTY, DEFAULT_PASSWORD_REPEATED_EMPTY));
			}
			if (!newPassword1.equals(newPassword2)) {
				hasErrors = true;
				errors.add(this.iwrb.getLocalizedString(KEY_PASSWORDS_NOT_SAME, DEFAULT_PASSWORDS_NOT_SAME));
			}
			if (newPassword1.length() < this.MIN_PASSWORD_LENGTH) {
				Object[] arguments = { String.valueOf(this.MIN_PASSWORD_LENGTH) };
				hasErrors = true;
				errors.add(MessageFormat.format(this.iwrb.getLocalizedString(KEY_PASSWORD_TOO_SHORT, DEFAULT_PASSWORD_TOO_SHORT), arguments));
			}
		}

		if (!hasErrors) {
			try {
				LoginDBHandler.updateLogin(((Integer)this.user.getPrimaryKey()).intValue(), login, newPassword1);
				getUserBusiness(iwc).callAllUserGroupPluginAfterUserCreateOrUpdateMethod(this.user);
			}
			catch (Exception e) {
				hasErrors = true;
				errors.add(this.iwrb.getLocalizedString(KEY_PREFIX + "password_update_failed", "Password update failed"));
			}
		}
			// Ok to update password
		if (!hasErrors) {
			Form form = new Form();
			form.setID("changePasswordForm");
			form.setStyleClass("changePasswordForm");

			Layer header = new Layer(Layer.DIV);
			header.setStyleClass("header");
			form.add(header);
			
			Heading1 heading = new Heading1(this.iwrb.getLocalizedString("change_password", "Change password"));
			header.add(heading);
			
			Layer layer = new Layer(Layer.DIV);
			layer.setStyleClass("receipt");
			
			Layer image = new Layer(Layer.DIV);
			image.setStyleClass("receiptImage");
			layer.add(image);
			
			heading = new Heading1(this.iwrb.getLocalizedString(KEY_PASSWORD_SAVED, DEFAULT_PASSWORD_SAVED));
			layer.add(heading);
			
			Paragraph paragraph = new Paragraph();
			paragraph.add(new Text(this.iwrb.getLocalizedString(KEY_PASSWORD_SAVED + "_text", DEFAULT_PASSWORD_SAVED + " info")));
			layer.add(paragraph);
			
			ICPage userHomePage = null;
			try {
				UserBusiness ub = (UserBusiness) IBOLookup.getServiceInstance(iwc, UserBusiness.class);
				userHomePage = ub.getHomePageForUser(this.user);
			}
			catch (FinderException fe) {
				//No page found...
			}
			catch (RemoteException re) {
				throw new IBORuntimeException(re);
			}
			
			if (userHomePage != null) {
				Layer span = new Layer(Layer.SPAN);
				span.add(new Text(this.iwrb.getLocalizedString("my_page", "My page")));
				Link link = new Link(span);
				link.setStyleClass("homeLink");
				link.setPage(userHomePage);
				paragraph.add(new Break(2));
				paragraph.add(link);
			}
			
			form.add(layer);
			add(form);
		}
		else {
			showErrors(iwc, errors);
			drawForm(iwc);
		}
	}
	
	protected UserBusiness getUserBusiness(IWApplicationContext iwac) throws RemoteException {
		return (UserBusiness) IBOLookup.getServiceInstance(iwac, UserBusiness.class);
	}

	public void setMinimumPasswordLength(int length) {
		this.MIN_PASSWORD_LENGTH = length;
	}

	protected void showErrors(IWContext iwc, Collection errors) {
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("errorLayer");
		
		Layer image = new Layer(Layer.DIV);
		image.setStyleClass("errorImage");
		layer.add(image);
		
		Heading1 heading = new Heading1(getResourceBundle(iwc).getLocalizedString("application_errors_occured", "There was a problem with the following items"));
		layer.add(heading);
		
		Lists list = new Lists();
		layer.add(list);
		
		Iterator iter = errors.iterator();
		while (iter.hasNext()) {
			String element = (String) iter.next();
			ListItem item = new ListItem();
			item.add(new Text(element));
			
			list.add(item);
		}
		
		add(layer);
	}
}
