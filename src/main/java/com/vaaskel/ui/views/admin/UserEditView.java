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

    private static final String PARAM_USER_ID = "userId";

    private final UserService userService;
    private final Binder<UserDto> binder = new Binder<>(UserDto.class);

    private UserDto currentUser;
    private boolean createMode;

    // Header actions
    private final Button saveButton = new Button();
    private final Button cancelButton = new Button();

    // Tabs + pages
    private final Tabs tabs = new Tabs();
    private final VerticalLayout pages = new VerticalLayout();

    private final Tab accountTab = new Tab();
    private final Tab securityTab = new Tab();
    private final Tab systemTab = new Tab();

    private final UserAccountTab accountPage = new UserAccountTab();
    private final UserSecurityTab securityPage = new UserSecurityTab();
    private final UserSystemTab systemPage = new UserSystemTab();

    private final Map<Tab, Component> tabToPage = new LinkedHashMap<>();

    public UserEditView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureHeaderActions();
        configureTabs();

        securityPage.setResetPasswordHandler(this::resetPasswordForCurrentUser);

        // Wire sub-views to binder (user handling stays in parent)
        accountPage.bind(binder);
        systemPage.bindReadOnly(binder); // read-only fields are filled explicitly on load

        HorizontalLayout headerBar = buildHeaderBar();
        VerticalLayout contentLayout = buildContentLayout();

        add(headerBar, contentLayout);
        setFlexGrow(1, contentLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String rawId = event.getRouteParameters()
                .get(PARAM_USER_ID)
                .orElse("new");

        if ("new".equalsIgnoreCase(rawId)) {
            enterCreateMode();
        } else {
            try {
                Long id = Long.valueOf(rawId);
                enterEditMode(id, event);
            } catch (NumberFormatException ex) {
                Notification.show(getTranslation("view.userEdit.notification.invalidId"),
                        5000, Notification.Position.MIDDLE);
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
        systemTab.setLabel(getTranslation("view.userEdit.tab.system"));

        tabs.add(accountTab, securityTab, systemTab);

        tabToPage.put(accountTab, accountPage);
        tabToPage.put(securityTab, securityPage);
        tabToPage.put(systemTab, systemPage);

        tabs.addSelectedChangeListener(_ -> updateVisiblePage());
    }

    private HorizontalLayout buildHeaderBar() {
        HorizontalLayout headerBar = new HorizontalLayout(cancelButton, saveButton);
        headerBar.setWidthFull();
        headerBar.setPadding(false);
        headerBar.setSpacing(true);
        return headerBar;
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

        systemPage.clearSystemFields();
        securityPage.clearSensitiveFields();
    }

    private void enterEditMode(Long userId, BeforeEnterEvent event) {
        createMode = false;

        Optional<UserDto> loaded = userService.findUserById(userId);
        if (loaded.isEmpty()) {
            Notification.show(getTranslation("view.userEdit.notification.notFound"),
                    5000, Notification.Position.MIDDLE);
            event.forwardTo(UserManagementView.class);
            return;
        }

        currentUser = loaded.get();
        binder.setBean(currentUser);

        systemPage.populateSystemFields(currentUser);
        securityPage.clearSensitiveFields();
    }

    private void saveUser() {
        if (currentUser == null) {
            Notification.show(getTranslation("view.userEdit.notification.noUserLoaded"),
                    5000, Notification.Position.MIDDLE);
            return;
        }

        if (!binder.validate().isOk()) {
            Notification.show(getTranslation("view.userEdit.notification.validationFailed"),
                    5000, Notification.Position.MIDDLE);
            return;
        }

        UserDto toSave = binder.getBean();

        // Password change is intentionally NOT part of UserDto binding.
        // Wire this to your service once you decide the API.
        // Example idea:
        // Optional<String> newPassword = securityPage.getNewPasswordIfProvided();
        // userService.updateCredentials(toSave.getId(), toSave.getUsername(), newPassword);
        if (!securityPage.isPasswordConfirmationOk()) {
            Notification.show(getTranslation("view.userEdit.notification.passwordMismatch"),
                    5000, Notification.Position.MIDDLE);
            return;
        }

        UserDto saved = userService.saveUser(toSave);

        Notification.show(getTranslation("view.userEdit.notification.saved"),
                3000, Notification.Position.BOTTOM_START);

        if (createMode && saved.getId() != null) {
            getUI().ifPresent(ui -> ui.navigate("admin/users/" + saved.getId()));
        } else {
            navigateBackToList();
        }
    }

    private void resetPasswordForCurrentUser(String rawPassword) {
        if (currentUser == null || currentUser.getId() == null) {
            Notification.show(getTranslation("view.userEdit.notification.passwordResetRequiresSavedUser"),
                    5000, Notification.Position.MIDDLE);
            return;
        }

        try {
            userService.resetPassword(currentUser.getId(), rawPassword);
            securityPage.clearSensitiveFields();
            Notification.show(getTranslation("view.userEdit.notification.passwordResetOk"),
                    3000, Notification.Position.BOTTOM_START);
        } catch (RuntimeException ex) {
            // Keep it generic in UI; log details on server side if needed
            Notification.show(getTranslation("view.userEdit.notification.passwordResetFailed"),
                    5000, Notification.Position.MIDDLE);
        }
    }


    private void navigateBackToList() {
        getUI().ifPresent(ui -> ui.navigate("admin/users"));
    }

    // ------------------------------------------------------------
    // Embedded sub-views
    // ------------------------------------------------------------

    private static final class UserAccountTab extends Composite<VerticalLayout> {

        private final TextField usernameField = new TextField();

        UserAccountTab() {
            var root = getContent();
            root.setPadding(false);
            root.setSpacing(true);
            root.setWidthFull();

            usernameField.setLabel(getTranslation("view.userEdit.field.username"));
            usernameField.setWidthFull();

            FormLayout form = new FormLayout();
            form.setWidthFull();
            form.add(usernameField);

            root.add(form);
        }

        void bind(Binder<UserDto> binder) {
            binder.forField(usernameField)
                    .asRequired(getTranslation("view.userEdit.validation.usernameRequired"))
                    .bind(UserDto::getUsername, UserDto::setUsername);
        }
    }

    private static final class UserSecurityTab extends Composite<VerticalLayout> {

        private final TextField loginNameField = new TextField();
        private final EmailField emailField = new EmailField();

        private final PasswordField newPassword = new PasswordField();
        private final PasswordField confirmPassword = new PasswordField();

        private final Button resetPasswordButton = new Button();

        private Consumer<String> resetPasswordHandler;

        UserSecurityTab() {
            var root = getContent();
            root.setPadding(false);
            root.setSpacing(true);
            root.setWidthFull();

            loginNameField.setLabel(getTranslation("view.userEdit.field.loginName"));
            emailField.setLabel(getTranslation("view.userEdit.field.email"));
            newPassword.setLabel(getTranslation("view.userEdit.field.newPassword"));
            confirmPassword.setLabel(getTranslation("view.userEdit.field.confirmPassword"));

            loginNameField.setWidthFull();
            emailField.setWidthFull();
            newPassword.setWidthFull();
            confirmPassword.setWidthFull();

            newPassword.setRevealButtonVisible(false);
            confirmPassword.setRevealButtonVisible(false);

            resetPasswordButton.setText(getTranslation("view.userEdit.button.resetPassword"));
            resetPasswordButton.setIcon(VaadinIcon.REFRESH.create());
            resetPasswordButton.addClickListener(_ -> onResetPasswordClicked());

            FormLayout form = new FormLayout();
            form.setWidthFull();
            form.add(loginNameField, emailField, newPassword, confirmPassword);

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
                Notification.show(getTranslation("view.userEdit.notification.passwordMismatch"),
                        5000, Notification.Position.MIDDLE);
                return;
            }

            String p1 = newPassword.getValue();
            if (p1 == null || p1.isBlank()) {
                return; // nothing to do
            }

            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader(resetPasswordButton.getText());
            dialog.setText(getTranslation("view.userEdit.button.resetPassword") + "?");
            dialog.setCancelable(true);
            dialog.setConfirmText(getTranslation("view.userEdit.button.resetPassword"));

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


    private static final class UserSystemTab extends Composite<VerticalLayout> {

        private final TextField idField = new TextField();
        private final TextField createdAtField = new TextField();
        private final TextField changedAtField = new TextField();

        UserSystemTab() {
            var root = getContent();
            root.setPadding(false);
            root.setSpacing(true);
            root.setWidthFull();

            idField.setLabel(getTranslation("view.userEdit.field.id"));
            idField.setReadOnly(true);
            idField.setWidthFull();

            createdAtField.setLabel(getTranslation("createdAt"));
            createdAtField.setReadOnly(true);
            createdAtField.setWidthFull();

            changedAtField.setLabel(getTranslation("changedAt"));
            changedAtField.setReadOnly(true);
            changedAtField.setWidthFull();

            FormLayout form = new FormLayout();
            form.setWidthFull();
            form.add(idField, createdAtField, changedAtField);

            root.add(form);
        }

        void bindReadOnly(Binder<UserDto> ignoredBinder) {
            // Intentionally empty: system fields are populated manually.
        }

        void populateSystemFields(UserDto user) {
            idField.setValue(user.getId() != null ? String.valueOf(user.getId()) : "");
            createdAtField.setValue(user.getCreatedAt() != null ? user.getCreatedAt().toString() : "");
            changedAtField.setValue(user.getChangedAt() != null ? user.getChangedAt().toString() : "");
        }

        void clearSystemFields() {
            idField.clear();
            createdAtField.clear();
            changedAtField.clear();
        }
    }
}
