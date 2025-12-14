package com.vaaskel.ui.views.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaaskel.api.user.UserDto;
import com.vaaskel.service.user.UserService;
import com.vaaskel.ui.util.DateTimeRenderers;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@Route(value = "admin/users")
@PageTitle("User Management")
@Menu(order = 10, icon = LineAwesomeIconUrl.USER_SOLID)
@RolesAllowed("ADMIN")
public class UserManagementView extends Div {
    private final UserService userService;

    // Grid and UI components
    private final Grid<UserDto> grid = new Grid<>(UserDto.class, false);
    private final Button newUserButton = new Button();
    private final TextField usernameFilter = new TextField();

    // Data provider with filter support
    private ConfigurableFilterDataProvider<UserDto, Void, String> dataProvider;

    public UserManagementView(UserService userService) {
        this.userService = userService;

        setSizeFull();

        configureGrid();
        configureDataProvider();

        HorizontalLayout toolbar = buildToolbar();

        grid.setSizeFull();
        add(toolbar, grid);
    }

    private HorizontalLayout buildToolbar() {
        // "New user" button (I18N)
        newUserButton.setText(getTranslation("view.userManagement.newUser"));
        newUserButton.setIcon(VaadinIcon.PLUS.create());
        newUserButton.addClickListener(_ -> navigateToNewUser());

        // Username filter (I18N)
        usernameFilter.setPlaceholder(getTranslation("view.userManagement.username.search"));
        usernameFilter.setClearButtonVisible(true);
        usernameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        usernameFilter.addValueChangeListener(_ -> applyFilter());

        // Toolbar layout
        HorizontalLayout toolbar = new HorizontalLayout(newUserButton, usernameFilter);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.expand(usernameFilter);

        return toolbar;
    }

    private void applyFilter() {
        if (dataProvider == null) {
            return;
        }
        String filterValue = usernameFilter.getValue();
        dataProvider.setFilter(
                (filterValue == null || filterValue.isBlank())
                        ? null
                        : filterValue.trim()
        );
    }


    private void configureGrid() {
        grid.setSelectionMode(SelectionMode.SINGLE);

        // Grid columns (I18N headers)
        grid.addColumn(UserDto::getId)
                .setHeader(getTranslation("view.userManagement.grid.id"))
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(UserDto::getUsername)
                .setHeader(getTranslation("view.userManagement.grid.username"))
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(DateTimeRenderers.localDateTimeRenderer(UserDto::getChangedAt))
                .setHeader(getTranslation("changedAt"))
                .setAutoWidth(true)
                .setSortable(true);

        // Double-click → open user edit view
        grid.addItemDoubleClickListener(event -> {
            UserDto item = event.getItem();
            if (item != null && item.getId() != null) {
                navigateToEditUser(item.getId());
            }
        });
    }

    private void configureDataProvider() {
        CallbackDataProvider.FetchCallback<UserDto, String> fetchCallback = query -> {
            int offset = query.getOffset();
            int limit = query.getLimit();
            String filter = query.getFilter().orElse(null);

            if (filter == null || filter.isBlank()) {
                return userService.findUsers(offset, limit).stream();
            } else {
                return userService.findUsersByUsername(filter.trim(), offset, limit).stream();
            }
        };

        CallbackDataProvider.CountCallback<UserDto, String> countCallback = query -> {
            String filter = query.getFilter().orElse(null);

            if (filter == null || filter.isBlank()) {
                return (int) userService.countUsers();
            } else {
                return (int) userService.countUsersByUsername(filter.trim());
            }
        };

        // Create the underlying callback data provider
        var callbackDataProvider =
                DataProvider.fromFilteringCallbacks(fetchCallback, countCallback);

        // Wrap it into a configurable filter data provider
        dataProvider = callbackDataProvider.withConfigurableFilter();

        // Use the configurable data provider for the grid
        grid.setDataProvider(dataProvider);
    }

    private void navigateToNewUser() {
        // Navigate to the new user creation view (edit view in "create" mode)
        getUI().ifPresent(ui -> ui.navigate("admin/users/new"));
    }

    private void navigateToEditUser(Long userId) {
        // Navigate to existing user edit view
        getUI().ifPresent(ui -> ui.navigate("admin/users/" + userId));
    }
}
