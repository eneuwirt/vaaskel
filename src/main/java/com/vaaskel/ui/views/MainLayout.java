package com.vaaskel.ui.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaaskel.domain.security.entity.User;
import com.vaaskel.security.AuthenticatedUser;
import com.vaaskel.service.settings.UserSettingsService;
import com.vaaskel.ui.theme.ThemeApplier;
import jakarta.annotation.security.PermitAll;

import java.util.List;
import java.util.Optional;

@Layout
@PermitAll
public class MainLayout extends AppLayout implements AfterNavigationObserver {
    private final UserSettingsService userSettingsService;

    private H1 viewTitle;

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;

    public MainLayout(UserSettingsService userSettingsService, AuthenticatedUser authenticatedUser,
            AccessAnnotationChecker accessChecker) {
        this.userSettingsService = userSettingsService;
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel(getTranslation("main.menu.toggle"));

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span(getTranslation("main.app.name"));
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            if (entry.icon() != null) {
                nav.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
            } else {
                nav.addItem(new SideNavItem(entry.title(), entry.path()));
            }
        });

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getUsername());
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar, new Span(user.getUsername()), new Icon("lumo", "dropdown"));
            div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);
            userName.add(div);

            // Theme submenu
            MenuItem themeRoot = userName.getSubMenu().addItem(getTranslation("main.user.theme"));

            themeRoot.getSubMenu().addItem(getTranslation("main.user.theme.system"),
                    e -> setTheme(user, com.vaaskel.domain.settings.ThemePreference.SYSTEM));
            themeRoot.getSubMenu().addItem(getTranslation("main.user.theme.light"),
                    e -> setTheme(user, com.vaaskel.domain.settings.ThemePreference.LIGHT));
            themeRoot.getSubMenu().addItem(getTranslation("main.user.theme.dark"),
                    e -> setTheme(user, com.vaaskel.domain.settings.ThemePreference.DARK));

            userName.getSubMenu().add(new Hr());
            userName.getSubMenu().addItem(getTranslation("main.user.signout"), e -> authenticatedUser.logout());

            layout.add(userMenu);
        } else {
            // Not logged in: follow system preference with fallback to dark
            Anchor loginLink = new Anchor("login", getTranslation("main.user.signin"));
            layout.add(loginLink);
        }

        return layout;
    }

    private void setTheme(User user, com.vaaskel.domain.settings.ThemePreference pref) {
        userSettingsService.updateTheme(user, pref);
        getUI().ifPresent(ui -> ThemeApplier.apply(ui, pref));
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        // Resolve localized page title (I18N)
        String title = getCurrentPageTitle();

        // Set title in header
        viewTitle.setText(title);

        // Set browser tab title
        getUI().ifPresent(ui -> ui.getPage().setTitle(title));
    }

    private String getCurrentPageTitle() {
        String keyOrTitle = MenuConfiguration.getPageHeader(getContent()).orElse("");

        if (keyOrTitle == null || keyOrTitle.isEmpty()) {
            return "";
        }

        // Try to translate the @PageTitle value as an I18N key
        String translated = getTranslation(keyOrTitle);

        // I18NProvider returns "!key!" if missing
        String missingMarker = "!" + keyOrTitle + "!";

        // Fallback to literal title if translation not found
        if (missingMarker.equals(translated)) {
            return keyOrTitle;
        }

        return translated;
    }
}
