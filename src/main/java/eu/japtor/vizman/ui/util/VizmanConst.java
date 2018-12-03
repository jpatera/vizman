package eu.japtor.vizman.ui.util;

import java.util.Locale;

public class VizmanConst {

	public static final Locale APP_LOCALE = Locale.US;
	public static final Locale CZ_LOCALE = new Locale("cs", "CZ");
	public static final String VIZMAN_VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes";

	public static final String PAGE_TITLE_BASE = "VizMan";

//	public static final String TITLE_ROOT = "Home";
//	public static final String PAGE_TITLE_ROOT = PAGE_TITLE_BASE + " - " + TITLE_ROOT;
//	public static final String TAG_ROOT = "home";
//	public static final String ROUTE_ROOT  = "";

	public static final String TITLE_HOME = "Home";
	public static final String PAGE_TITLE_HOME = PAGE_TITLE_BASE + " - " + TITLE_HOME;
	public static final String TAG_HOME = "home";
	public static final String ROUTE_HOME  = "home";

	public static final String TITLE_LOGIN = "Přihlášení";
	public static final String PAGE_TITLE_LOGIN = PAGE_TITLE_BASE + " - " + TITLE_LOGIN;
	public static final String TAG_LOGIN = "login-form";
	public static final String ROUTE_LOGIN = "login";

	public static final String TITLE_LOGOUT = "Odhlášení";
	public static final String ROUTE_LOGOUT = "login/logout";

	public static final String TITLE_PERSON = "Uživatelé";
	public static final String PAGE_TITLE_PERSON = PAGE_TITLE_BASE + " - " + TITLE_PERSON;
	public static final String TAG_PERSON = "person-list-view";
	public static final String ROUTE_PERSON = "person";

	public static final String TITLE_ROLE = "Role";
	public static final String PAGE_TITLE_ROLE = PAGE_TITLE_BASE + " - " + TITLE_ROLE;
	public static final String TAG_ROLE = "role-list-view";
	public static final String ROUTE_ROLE = "role";

	public static final String TITLE_CFG = "Konfigurace";
	public static final String PAGE_TITLE_CFG = PAGE_TITLE_BASE + " - " + TITLE_CFG;
	public static final String TAG_CFG = "cfg-tabs-view";
	public static final String ROUTE_CFG = "cfg";

	public static final String TITLE_DOCH = "Docházka";
	public static final String PAGE_TITLE_DOCH = PAGE_TITLE_BASE + " - " + TITLE_DOCH;
	public static final String TAG_DOCH = "doch-form";
	public static final String ROUTE_DOCH = "doch";

	public static final String TITLE_ZAK = "Zakázky";
	public static final String PAGE_TITLE_ZAK = PAGE_TITLE_BASE + " - " + TITLE_ZAK;
	public static final String TAG_ZAK = "zak-list-view";
	public static final String ROUTE_ZAK = "zak";

	public static final String TITLE_PODZAK = "Podzakázky";
	public static final String PAGE_TITLE_PODZAK = PAGE_TITLE_BASE + " - " + TITLE_PODZAK;
	public static final String TAG_PODZAK = "podzak-list-view";
	public static final String ROUTE_PODZAK = "podzak";

	public static final String TITLE_PRUH = "Proužek";
	public static final String ROUTE_PRUH = "pruh";

	public static final String ROUTE_DEFAULT = ROUTE_PODZAK;

//	public static final Icon ICON_USERS = new Icon (VaadinIcon.USERS);
//	//	public static final Icon ICON_ZAK_LIST = new Icon (VaadinIcon.LIST);
//	public static final Icon ICON_DOCH = new Icon (VaadinIcon.CALENDAR_CLOCK);
//	public static final Icon ICON_LOGOUT = new Icon (VaadinIcon.ARROW_LEFT);

	public static final String TITLE_NOT_FOUND = "Stránka nenalezena";
	public static final String TITLE_ACCESS_DENIED = "Přístup odmítnut";
}
