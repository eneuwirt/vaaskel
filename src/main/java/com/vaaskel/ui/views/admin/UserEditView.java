package com.vaaskel.ui.views.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaaskel.api.user.UserDto;
import com.vaaskel.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.Map;
import java.util.Optional;

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

    // Header
    private final H2 headerTitle = new H2();
    private final Span headerSubtitle = new Span();
    private final Button saveButton = new Button();
    private final Button cancelButton = new Button();

    // Tabs
    private final Tabs tabs = new Tabs();
    private final Tab accountTab = new Tab();
    private final Tab systemTab = new Tab();

    // Pages
    private final VerticalLayout accountPage = new VerticalLayout();
    private final VerticalLayout systemPage = new VerticalLayout();

    // Account fields
    private final TextField usernameField = new TextField();
    private final TextField nameField = new TextField();

    // System fields (read-only)
    private final TextField idField = new TextField();
    private final TextField createdAtField = new TextField();
    private final TextField changedAtField = new TextField();

    public UserEditView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureHeader();
        configureTabs();
        configureAccountForm();
        configureSystemForm();
        configureBinder();

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
                Notification.show(getTranslation("view.userEdit.notification.invalidId"), 5000, Notification.Position.MIDDLE);
                event.forwardTo(UserManagementView.class);
            }
        }
    }

    private void configureHeader() {
        headerTitle.getStyle().set("margin", "0");
        headerSubtitle.getStyle().set("color", "var(--lumo-secondary-text-color)");
        headerSubtitle.getStyle().set("font-size", "var(--lumo-font-size-s)");

        saveButton.setText(getTranslation("view.userEdit.button.save"));
        saveButton.setIcon(VaadinIcon.CHECK.create());
        saveButton.addClickListener(e -> saveUser());

        cancelButton.setText(getTranslation("view.userEdit.button.cancel"));
        cancelButton.setIcon(VaadinIcon.ARROW_LEFT.create());
        cancelButton.addClickListener(e -> navigateBackToList());
    }

    private void configureTabs() {
        accountTab.setLabel(getTranslation("view.userEdit.tab.account"));
        systemTab.setLabel(getTranslation("view.userEdit.tab.system"));

        tabs.add(accountTab, systemTab);
        tabs.setWidthFull();
        tabs.addSelectedChangeListener(e -> updateVisiblePage());
    }

    private void configureAccountForm() {
        usernameField.setLabel(getTranslation("view.userEdit.field.username"));
        usernameField.setRequiredIndicatorVisible(true);

        nameField.setLabel(getTranslation("view.userEdit.field.name"));

        FormLayout formLayout = new FormLayout();
        formLayout.add(usernameField, nameField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        accountPage.setPadding(false);
        accountPage.setSpacing(false);
        accountPage.add(formLayout);
    }

    private void configureSystemForm() {
        idField.setLabel(getTranslation("view.userEdit.field.id"));
        idField.setReadOnly(true);

        createdAtField.setLabel(getTranslation("view.userEdit.field.createdAt"));
        createdAtField.setReadOnly(true);

        changedAtField.setLabel(getTranslation("view.userEdit.field.changedAt"));
        changedAtField.setReadOnly(true);

        FormLayout formLayout = new FormLayout();
        formLayout.add(idField, createdAtField, changedAtField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 3)
        );

        systemPage.setPadding(false);
        systemPage.setSpacing(false);
        systemPage.add(formLayout);
    }

    private void configureBinder() {
        binder.forField(usernameField)
                .asRequired(getTranslation("view.userEdit.validation.usernameRequired"))
                .bind(UserDto::getUsername, UserDto::setUsername);

        binder.forField(nameField)
                .bind(UserDto::getUsername, UserDto::setUsername);

        // System fields are read-only and will be populated manually
    }

    private HorizontalLayout buildHeaderBar() {
        VerticalLayout titles = new VerticalLayout(headerTitle, headerSubtitle);
        titles.setSpacing(false);
        titles.setPadding(false);

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, saveButton);

        HorizontalLayout headerBar = new HorizontalLayout(titles, buttons);
        headerBar.setWidthFull();
        headerBar.setAlignItems(Alignment.CENTER);
        headerBar.expand(titles);

        return headerBar;
    }

    private VerticalLayout buildContentLayout() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(false);
        content.setSpacing(true);

        content.add(tabs, accountPage, systemPage);
        updateVisiblePage();

        return content;
    }

    private void updateVisiblePage() {
        accountPage.setVisible(tabs.getSelectedTab() == accountTab);
        systemPage.setVisible(tabs.getSelectedTab() == systemTab);
    }

    private void enterCreateMode() {
        createMode = true;
        currentUser = new UserDto();

        binder.setBean(currentUser);
        headerTitle.setText(getTranslation("view.userEdit.header.new"));
        headerSubtitle.setText(getTranslation("view.userEdit.header.new.subtitle"));

        idField.clear();
        createdAtField.clear();
        changedAtField.clear();
    }

    private void enterEditMode(Long userId, BeforeEnterEvent event) {
        createMode = false;

        Optional<UserDto> loaded = userService.findUserById(userId);
        if (loaded.isEmpty()) {
            Notification.show(getTranslation("view.userEdit.notification.notFound"), 5000, Notification.Position.MIDDLE);
            event.forwardTo(UserManagementView.class);
            return;
        }

        currentUser = loaded.get();
        binder.setBean(currentUser);

        headerTitle.setText(getTranslation("view.userEdit.header.edit",
                Map.of("username", safe(currentUser.getUsername()))));

        headerSubtitle.setText(getTranslation("view.userEdit.header.edit.subtitle",
                Map.of("id", currentUser.getId() != null ? currentUser.getId().toString() : "?")));

        // Populate system fields (adapt getters to your UserDto)
        if (currentUser.getId() != null) {
            idField.setValue(String.valueOf(currentUser.getId()));
        } else {
            idField.clear();
        }

        if (currentUser.getCreatedAt() != null) {
            createdAtField.setValue(currentUser.getCreatedAt().toString());
        } else {
            createdAtField.clear();
        }

        if (currentUser.getChangedAt() != null) {
            changedAtField.setValue(currentUser.getChangedAt().toString());
        } else {
            changedAtField.clear();
        }
    }

    private void saveUser() {
        if (currentUser == null) {
            Notification.show(getTranslation("view.userEdit.notification.noUserLoaded"), 5000, Notification.Position.MIDDLE);
            return;
        }

        if (!binder.validate().isOk()) {
            Notification.show(getTranslation("view.userEdit.notification.validationFailed"), 5000, Notification.Position.MIDDLE);
            return;
        }

        UserDto toSave = binder.getBean();
        UserDto saved = userService.saveUser(toSave);

        Notification.show(getTranslation("view.userEdit.notification.saved"), 3000, Notification.Position.BOTTOM_START);

        // After saving a new user, switch to edit mode with the assigned ID
        if (createMode && saved.getId() != null) {
            getUI().ifPresent(ui -> ui.navigate("admin/users/" + saved.getId()));
        } else {
            navigateBackToList();
        }
    }

    private void navigateBackToList() {
        getUI().ifPresent(ui -> ui.navigate("admin/users"));
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
