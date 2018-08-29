package eu.japtor.vizman.ui.util;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Locale;

public class VizmanConst {

	public static final Locale APP_LOCALE = Locale.US;
	public static final Locale CZ_LOCALE = new Locale("cs", "CZ");

	public static final String PAGE_ROOT_TITLE  = "Root";
	public static final String PAGE_USERS_TITLE = "Usr list";
	public static final String PAGE_USERS_TAG   = "user-list-view";
	public static final String PAGE_ZAKS_TITLE  = "Zak list";
	public static final String PAGE_ZAKS_TAG    = "zak-list-view";

	public static final String ROUTE_ROOT  = "";
	public static final String ROUTE_USERS = "users";
	public static final String ROUTE_DOCH  = "doch";
	public static final String ROUTE_PRUH  = "pruh";
	public static final String ROUTE_ZAKS  = "zaks";
	public static final String ROUTE_DEFAULT = ROUTE_ZAKS;

//	public static final Icon ICON_USERS = new Icon (VaadinIcon.USERS);
//	//	public static final Icon ICON_ZAK_LIST = new Icon (VaadinIcon.LIST);
//	public static final Icon ICON_DOCH = new Icon (VaadinIcon.CALENDAR_CLOCK);
//	public static final Icon ICON_LOGOUT = new Icon (VaadinIcon.ARROW_LEFT);

	public static final String TITLE_HOME = "HOME";
	public static final String TITLE_ZAKS = "Zakázky";
	public static final String TITLE_USERS = "Uživatelé";
	public static final String TITLE_LOGOUT = "Odhlášení";
	public static final String TITLE_NOT_FOUND = "Stránka nenalezena";
	public static final String TITLE_ACCESS_DENIED = "Přístup odmítnut";

	public static final String VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes";
}
