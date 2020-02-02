package eu.japtor.vizman.ui.views;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collections;

@Tag("vizman-login-view")
@Route(value = LoginView.ROUTE)
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE = "login";

    private LoginOverlay login = new LoginOverlay();

    public LoginView() {
        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);
        login.setI18n(createLoginI18nCz());
        login.setOpened(true);
        getElement().appendChild(login.getElement());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Inform the user about an authentication error
        // (yes, the API for resolving query parameters is annoying...)
        if(!event.getLocation().getQueryParameters().getParameters().getOrDefault("error", Collections.emptyList()).isEmpty()) {
            login.setError(true);
        }
    }


    private LoginI18n createLoginI18nCz(){
        LoginI18n i18n = LoginI18n.createDefault();

        // Define all visible Strings to the values you want.
        // This code is copied from above-linked example codes for Login.
        // In a truly international application you would use i.e. `getTranslation(USERNAME)`
        // instead of hardcoded string values. Make use of your I18nProvider.
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("VizMan");
        i18n.getHeader().setDescription("");
        i18n.getForm().setUsername("Jméno"); // this is the one you asked for.
        i18n.getForm().setTitle("Přihlášení do aplikace");
        i18n.getForm().setSubmit("Přihlásit");
        i18n.getForm().setPassword("Heslo");
        i18n.getForm().setForgotPassword("");
        i18n.getErrorMessage().setTitle("Neplatná kombinace jméno/heslo");
        i18n.getErrorMessage().setMessage("Zkuste se přihlásit znova.");
//        i18n.setAdditionalInformation(...)
        return i18n;
    }
}
