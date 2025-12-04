package com.vaaskel.ui.views.admin;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaaskel.api.user.UserDto;
import com.vaaskel.ui.util.DateTimeRenderers;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.Arrays;
import java.util.List;

/**
 * Admin view for managing users.
 */
@PageTitle("User Management")
@Route("admin_user")
@Menu(order = 1, icon = LineAwesomeIconUrl.USER_SOLID)
@RolesAllowed("ADMIN")
public class UserManagementView extends Div {

    private final Grid<UserDto> grid = new Grid<>(UserDto.class, false);

    public UserManagementView() {
        addClassName("user-management-view");

        configureGrid();
        formatView();
        refreshGrid();
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

    private void formatView() {
        add(grid);
        grid.setSizeFull();
        setSizeFull();
    }

    private void refreshGrid() {
        // TODO replace with service call when backend is ready
        List<UserDto> users = getDummyUsers();
        grid.setItems(users);
    }

    // --- Dummy data for development ---

    private List<UserDto> getDummyUsers() {
        return Arrays.asList(
                createUser(4957L, "Amarachi Nkechi"),
                createUser(675L, "Bonelwa Ngqawana"),
                createUser(6816L, "Debashis Bhuiyan"),
                createUser(5144L, "Jacqueline Asong"),
                createUser(9800L, "Kobus van de Vegte"),
                createUser(3599L, "Mattie Blooman"),
                createUser(3989L, "Oea Romana"),
                createUser(1077L, "Stephanus Huggins"),
                createUser(8942L, "Torsten Paulsson")
        );
    }

    private UserDto createUser(Long id, String username) {
        UserDto user = new UserDto();
        user.setId(id);
        user.setUsername(username);
        return user;
    }
}