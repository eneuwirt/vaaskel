package com.vaaskel.ui.views.admin;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaaskel.api.user.UserDto;
import com.vaaskel.service.UserService;
import com.vaaskel.ui.util.DateTimeRenderers;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

/**
 * Admin view for managing users.
 */
@PageTitle("User Management")
@Route("admin_user")
@Menu(order = 1, icon = LineAwesomeIconUrl.USER_SOLID)
@RolesAllowed("ADMIN")
public class UserManagementView extends Div {

    private final Grid<UserDto> grid = new Grid<>(UserDto.class, false);
    private final UserService userService;

    public UserManagementView(UserService userService) {
        this.userService = userService;

        addClassName("user-management-view");

        configureGrid();
        configureDataProvider();
        formatView();
    }

    private void configureGrid() {
        grid.setSelectionMode(SelectionMode.SINGLE);

        grid.addColumn(UserDto::getId)
                .setHeader("ID")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(UserDto::getUsername)
                .setHeader("Username")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(DateTimeRenderers.localDateTimeRenderer(UserDto::getChangedAt))
                .setHeader("Changed at")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(DateTimeRenderers.localDateTimeRenderer(UserDto::getCreatedAt))
                .setHeader("Created at")
                .setAutoWidth(true)
                .setSortable(true);

        grid.getColumns().forEach(column -> column.setAutoWidth(true));
    }

    private void configureDataProvider() {
        CallbackDataProvider.FetchCallback<UserDto, Void> fetchCallback =
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();
                    return userService.findUsers(offset, limit).stream();
                };

        CallbackDataProvider.CountCallback<UserDto, Void> countCallback =
                query -> (int) userService.countUsers(); // safe if you don't have millions yet

        grid.setDataProvider(new CallbackDataProvider<>(fetchCallback, countCallback));
    }

    private void formatView() {
        add(grid);
        grid.setSizeFull();
        setSizeFull();
    }
}