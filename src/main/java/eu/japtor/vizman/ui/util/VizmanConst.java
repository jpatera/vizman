package eu.japtor.vizman.ui.util;

import java.util.Locale;

public class VizmanConst {

	public static final Locale APP_LOCALE = Locale.US;
	public static final Locale CZ_LOCALE = new Locale("cs", "CZ");

	public static final String PERM_USR_VIEW_BASIC_READ = "USR_VIEW_BASIC_READ";

	public static final String PAGE_ROOT_TITLE  = "Root";
	public static final String PAGE_USR_TITLE = "Usr list";
	public static final String PAGE_USR_TAG = "usr-list-view";
	public static final String PAGE_ZAK_TITLE = "Zak list";
	public static final String PAGE_ZAK_TAG = "zak-list-view";

	public static final String ROUTE_ROOT  = "";
	public static final String ROUTE_USR = "usr";
	public static final String ROUTE_DOCH  = "doch";
	public static final String ROUTE_PRUH  = "pruh";
	public static final String ROUTE_ZAK = "zak";
	public static final String ROUTE_DEFAULT = ROUTE_ZAK;

//	public static final Icon ICON_USERS = new Icon (VaadinIcon.USERS);
//	//	public static final Icon ICON_ZAK_LIST = new Icon (VaadinIcon.LIST);
//	public static final Icon ICON_DOCH = new Icon (VaadinIcon.CALENDAR_CLOCK);
//	public static final Icon ICON_LOGOUT = new Icon (VaadinIcon.ARROW_LEFT);

	public static final String TITLE_HOME = "HOME";
	public static final String TITLE_ZAK = "Zakázky";
	public static final String TITLE_USR = "Uživatelé";
	public static final String TITLE_LOGOUT = "Odhlášení";
	public static final String TITLE_NOT_FOUND = "Stránka nenalezena";
	public static final String TITLE_ACCESS_DENIED = "Přístup odmítnut";

	public static final String VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes";
}
