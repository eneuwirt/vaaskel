package com.vaaskel.ui.views.admin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaaskel.api.user.UserDto;
import com.vaaskel.service.user.UserService;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Route("admin/users/:userId")
@PageTitle("User edit")
@Menu(order = 11, icon = LineAwesomeIconUrl.USER_EDIT_SOLID)
@RolesAllowed("ADMIN")
public class UserEditView extends VerticalLayout implements BeforeEnterObserver {
    private static final String ROUTE_USERS = "admin/users";
    private static final String PARAM_USER_ID = "userId";

    private final UserService userService;
    private final Binder<UserDto> binder = new Binder<>(UserDto.class);

    private UserDto currentUser;
    private boolean createMode;

    private final Button saveButton = new Button();
    private final Button cancelButton = new Button();

    private final Tabs tabs = new Tabs();
    private final VerticalLayout pages = new VerticalLayout();

    private final Tab accountTab = new Tab();
    private final Tab securityTab = new Tab();

    private final UserAccountTab accountPage = new UserAccountTab();
    private final UserSecurityTab securityPage = new UserSecurityTab();

    private final Map<Tab, Component> tabToPage = new LinkedHashMap<>();

    private final TextField infoId = new TextField();
    private final TextField infoUsername = new TextField();
    private final TextField infoCreatedAt = new TextField();
    private final TextField infoChangedAt = new TextField();

    public UserEditView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureHeaderActions();
        configureTabs();

        getUI().ifPresent(ui -> ui.getPage().setTitle(getTranslation("view.userEdit.title")));

        securityPage.setResetPasswordHandler(this::resetPasswordForCurrentUser);

        accountPage.bind(binder);

        HorizontalLayout headerBar = buildHeaderBar();
        Component infoBar = buildInfoBar();
        VerticalLayout contentLayout = buildContentLayout();

        add(headerBar, infoBar, contentLayout);
        setFlexGrow(1, contentLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String rawId = event.getRouteParameters().get(PARAM_USER_ID).orElse("new");

        if ("new".equalsIgnoreCase(rawId)) {
            enterCreateMode();
        } else {
            try {
                Long id = Long.valueOf(rawId);
                enterEditMode(id, event);
            } catch (NumberFormatException ex) {
                Notification.show(getTranslation("view.userEdit.notification.invalidId"), 5000,
                        Notification.Position.MIDDLE);
                event.forwardTo(UserManagementView.class);
            }
        }
    }

    private void configureHeaderActions() {
        saveButton.setText(getTranslation("view.userEdit.button.save"));
        saveButton.setIcon(VaadinIcon.CHECK.create());
        saveButton.addClickListener(_ -> saveUser());

        cancelButton.setText(getTranslation("view.userEdit.button.cancel"));
        cancelButton.setIcon(VaadinIcon.ARROW_BACKWARD.create());
        cancelButton.addClickListener(_ -> navigateBackToList());
    }

    private void configureTabs() {
        accountTab.setLabel(getTranslation("view.userEdit.tab.account"));
        securityTab.setLabel(getTranslation("view.userEdit.tab.security"));

        tabs.add(accountTab, securityTab);

        tabToPage.put(accountTab, accountPage);
        tabToPage.put(securityTab, securityPage);

        tabs.addSelectedChangeListener(_ -> updateVisiblePage());
    }

    private HorizontalLayout buildHeaderBar() {
        HorizontalLayout headerBar = new HorizontalLayout(cancelButton, saveButton);
        headerBar.setWidthFull();
        headerBar.setPadding(false);
        headerBar.setSpacing(true);
        return headerBar;
    }

    private Component buildInfoBar() {
        infoId.setLabel(getTranslation("view.userEdit.field.id"));
        infoUsername.setLabel(getTranslation("view.userEdit.field.username"));
        infoCreatedAt.setLabel(getTranslation("createdAt"));
        infoChangedAt.setLabel(getTranslation("changedAt"));

        infoId.setReadOnly(true);
        infoUsername.setReadOnly(true);
        infoCreatedAt.setReadOnly(true);
        infoChangedAt.setReadOnly(true);

        infoId.setWidthFull();
        infoUsername.setWidthFull();
        infoCreatedAt.setWidthFull();
        infoChangedAt.setWidthFull();

        HorizontalLayout bar = new HorizontalLayout(infoId, infoUsername, infoCreatedAt, infoChangedAt);
        bar.setWidthFull();
        bar.setPadding(false);
        bar.setSpacing(true);
        return bar;
    }

    private VerticalLayout buildContentLayout() {
        pages.setSizeFull();
        pages.setPadding(false);
        pages.setSpacing(true);

        pages.add(tabs);
        tabToPage.values().forEach(pages::add);

        updateVisiblePage();
        return pages;
    }

    private void updateVisiblePage() {
        Tab selected = tabs.getSelectedTab();
        tabToPage.forEach((tab, page) -> page.setVisible(tab == selected));
    }

    private void enterCreateMode() {
        createMode = true;
        currentUser = new UserDto();

        binder.setBean(currentUser);

        clearInfoBar();
        securityPage.clearSensitiveFields();
    }

    private void enterEditMode(Long userId, BeforeEnterEvent event) {
        createMode = false;

        Optional<UserDto> loaded = userService.findUserById(userId);
        if (loaded.isEmpty()) {
            Notification.show(getTranslation("view.userEdit.notification.notFound"), 5000,
                    Notification.Position.MIDDLE);
            event.forwardTo(UserManagementView.class);
            return;
        }

        currentUser = loaded.get();
        binder.setBean(currentUser);

        populateInfoBar(currentUser);
        securityPage.clearSensitiveFields();
    }

    private void saveUser() {
        if (currentUser == null) {
            Notification.show(getTranslation("view.userEdit.notification.noUserLoaded"), 5000,
                    Notification.Position.MIDDLE);
            return;
        }

        if (!binder.validate().isOk()) {
            Notification.show(getTranslation("view.userEdit.notification.validationFailed"), 5000,
                    Notification.Position.MIDDLE);
            return;
        }

        if (!securityPage.isPasswordConfirmationOk()) {
            Notification.show(getTranslation("view.userEdit.notification.passwordMismatch"), 5000,
                    Notification.Position.MIDDLE);
            return;
        }

        UserDto toSave = binder.getBean();
        UserDto saved = userService.saveUser(toSave);

        Notification.show(getTranslation("view.userEdit.notification.saved"), 3000,
                Notification.Position.BOTTOM_START);

        populateInfoBar(saved);

        if (createMode && saved.getId() != null) {
            getUI().ifPresent(ui -> ui.navigate(ROUTE_USERS + "/" + saved.getId()));
        } else {
            navigateBackToList();
        }
    }

    private void resetPasswordForCurrentUser(String rawPassword) {
        if (currentUser == null || currentUser.getId() == null) {
            Notification.show(getTranslation("view.userEdit.notification.passwordResetRequiresSavedUser"), 5000,
                    Notification.Position.MIDDLE);
            return;
        }

        try {
            userService.resetPassword(currentUser.getId(), rawPassword);
            securityPage.clearSensitiveFields();
            Notification.show(getTranslation("view.userEdit.notification.passwordResetOk"), 3000,
                    Notification.Position.BOTTOM_START);
        } catch (RuntimeException ex) {
            Notification.show(getTranslation("view.userEdit.notification.passwordResetFailed"), 5000,
                    Notification.Position.MIDDLE);
        }
    }

    private void navigateBackToList() {
        getUI().ifPresent(ui -> ui.navigate(ROUTE_USERS));
    }

    private void clearInfoBar() {
        infoId.clear();
        infoUsername.clear();
        infoCreatedAt.clear();
        infoChangedAt.clear();
    }

    private void populateInfoBar(UserDto user) {
        infoId.setValue(user.getId() != null ? String.valueOf(user.getId()) : "");
        infoUsername.setValue(user.getUsername() != null ? user.getUsername() : "");
        infoCreatedAt.setValue(user.getCreatedAt() != null ? user.getCreatedAt().toString() : "");
        infoChangedAt.setValue(user.getChangedAt() != null ? user.getChangedAt().toString() : "");
    }

    // ------------------------------------------------------------
    // Embedded sub-views
    // ------------------------------------------------------------

    private static final class UserAccountTab extends Composite<VerticalLayout> {
        private final EmailField emailField = new EmailField();
        private final TextField usernameField = new TextField();

        UserAccountTab() {
            var root = getContent();
            root.setPadding(false);
            root.setSpacing(true);
            root.setWidthFull();

            usernameField.setLabel(getTranslation("view.userEdit.field.username"));
            usernameField.setWidthFull();

            emailField.setLabel(getTranslation("view.userEdit.field.email"));
            emailField.setWidthFull();

            FormLayout form = new FormLayout();
            form.setWidthFull();
            form.add(usernameField, emailField);

            root.add(form);
        }

        void bind(Binder<UserDto> binder) {
            binder.forField(usernameField)
                    .asRequired(getTranslation("view.userEdit.validation.usernameRequired"))
                    .bind(UserDto::getUsername, UserDto::setUsername);

            // Bind email only if UserDto supports it:
            // binder.forField(emailField)
            //         .bind(UserDto::getEmail, UserDto::setEmail);
        }
    }

    private static final class UserSecurityTab extends Composite<VerticalLayout> {
        private final PasswordField newPassword = new PasswordField();
        private final PasswordField confirmPassword = new PasswordField();

        private final Button resetPasswordButton = new Button();

        private Consumer<String> resetPasswordHandler;

        UserSecurityTab() {
            var root = getContent();
            root.setPadding(false);
            root.setSpacing(true);
            root.setWidthFull();

            newPassword.setLabel(getTranslation("view.userEdit.field.newPassword"));
            confirmPassword.setLabel(getTranslation("view.userEdit.field.confirmPassword"));

            newPassword.setWidthFull();
            confirmPassword.setWidthFull();

            newPassword.setRevealButtonVisible(false);
            confirmPassword.setRevealButtonVisible(false);

            resetPasswordButton.setText(getTranslation("view.userEdit.button.resetPassword"));
            resetPasswordButton.setIcon(VaadinIcon.REFRESH.create());
            resetPasswordButton.addClickListener(_ -> onResetPasswordClicked());

            FormLayout form = new FormLayout();
            form.setWidthFull();
            form.add(newPassword, confirmPassword);

            HorizontalLayout actions = new HorizontalLayout(resetPasswordButton);
            actions.setPadding(false);
            actions.setSpacing(true);

            root.add(form, actions);
        }

        void setResetPasswordHandler(Consumer<String> handler) {
            this.resetPasswordHandler = handler;
        }

        private void onResetPasswordClicked() {
            if (!isPasswordConfirmationOk()) {
                Notification.show(getTranslation("view.userEdit.notification.passwordMismatch"), 5000,
                        Notification.Position.MIDDLE);
                return;
            }

            String p1 = newPassword.getValue();
            if (p1 == null || p1.isBlank()) {
                return;
            }

            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setText(getTranslation("view.userEdit.dialog.resetPassword.text"));
            dialog.setConfirmText(getTranslation("view.userEdit.dialog.resetPassword.confirm"));
            dialog.setHeader(getTranslation("view.userEdit.dialog.resetPassword.header"));
            dialog.setCancelable(true);

            dialog.addConfirmListener(_ -> {
                if (resetPasswordHandler != null) {
                    resetPasswordHandler.accept(p1);
                }
            });

            dialog.open();
        }

        boolean isPasswordConfirmationOk() {
            String p1 = newPassword.getValue();
            String p2 = confirmPassword.getValue();
            if (p1 == null || p1.isBlank()) {
                return true;
            }
            return p1.equals(p2);
        }

        void clearSensitiveFields() {
            newPassword.clear();
            confirmPassword.clear();
        }
    }
}