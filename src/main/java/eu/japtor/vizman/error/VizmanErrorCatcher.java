package eu.japtor.vizman.error;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.ParentLayout;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.exceptions.AccessDeniedException;

import javax.servlet.http.HttpServletResponse;

@Tag(Tag.DIV)
@ParentLayout(MainView.class)
public class VizmanErrorCatcher extends Component
        implements HasErrorParameter<AccessDeniedException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
                                 ErrorParameter<AccessDeniedException> parameter) {
        getElement().setText(
                "Vizman zaregistroval error.");
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
}
