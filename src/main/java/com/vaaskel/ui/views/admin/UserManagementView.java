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
@PageTitle("view.userManagement.title")
@Route("admin_user")
@Menu(order = 1, icon = LineAwesomeIconUrl.USER_SOLID, title = "User Management")
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
                .setHeader(getTranslation("view.userManagement.grid.id"))
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(UserDto::getUsername)
                .setHeader(getTranslation("view.userManagement.grid.username"))
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(DateTimeRenderers.localDateTimeRenderer(UserDto::getChangedAt))
                .setHeader(getTranslation("view.userManagement.grid.changedAt"))
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(DateTimeRenderers.localDateTimeRenderer(UserDto::getCreatedAt))
                .setHeader(getTranslation("view.userManagement.grid.createdAt"))
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
                query -> (int) userService.countUsers();

        grid.setDataProvider(new CallbackDataProvider<>(fetchCallback, countCallback));
    }

    private void formatView() {
        add(grid);
        grid.setSizeFull();
        setSizeFull();
    }
}