package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;


@Route("login-form-2")
//@Theme(value = Lumo.class, variant = Lumo.DARK)
@HtmlImport("frontend://bower_components/iron-form/iron-form.html")
// TODO: probably delete
public class LoginForm  extends VerticalLayout {

    public LoginForm(){
        init();
    }

    public void init(){
        FormLayout nameLayout = new FormLayout();

        TextField username = new TextField();
        username.setLabel("UserName");
        username.setPlaceholder("username");

        PasswordField password = new PasswordField();
        password.setLabel("password");
        password.setPlaceholder("*****");

        Button loginButton = new Button("login");

        loginButton.addClickListener(event -> {
        });

        nameLayout.add(username, password, loginButton);

        add(nameLayout);
    }
}
