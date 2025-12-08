package com.vaaskel.ui.views.login;

import com.vaaskel.security.AuthenticatedUser;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver, HasDynamicTitle {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        setAction(RouteUtil.getRoutePath(
                VaadinService.getCurrent().getContext(),
                getClass()
        ));

        LoginI18n i18n = LoginI18n.createDefault();

        // Header
        LoginI18n.Header header = new LoginI18n.Header();
        header.setTitle(getTranslation("view.login.header.title"));
        header.setDescription(getTranslation("view.login.header.description"));
        i18n.setHeader(header);

        // Form
        LoginI18n.Form form = i18n.getForm();
        form.setTitle(getTranslation("view.login.form.title"));
        form.setUsername(getTranslation("view.login.form.username"));
        form.setPassword(getTranslation("view.login.form.password"));
        form.setSubmit(getTranslation("view.login.form.submit"));
        form.setForgotPassword(getTranslation("view.login.form.forgotPassword"));

        // Error messages
        LoginI18n.ErrorMessage errorMessage = i18n.getErrorMessage();
        errorMessage.setTitle(getTranslation("view.login.error.title"));
        errorMessage.setMessage(getTranslation("view.login.error.message"));

        i18n.setAdditionalInformation(null);

        setI18n(i18n);

        setForgotPasswordButtonVisible(false);
        setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            setOpened(false);
            event.forwardTo("");
        }

        setError(event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error"));
    }

    @Override
    public String getPageTitle() {
        String key = "view.login.title";
        String translated = getTranslation(key);
        String missingMarker = "!" + key + "!";

        if (missingMarker.equals(translated)) {
            return "Login";
        }
        return translated;
    }
}
