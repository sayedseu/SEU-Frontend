package com.sayed.seu.forntend;


import com.sayed.seu.forntend.auth.ApplicationUser;
import com.sayed.seu.forntend.ui.AddStudentView;
import com.sayed.seu.forntend.ui.NotificationView;
import com.sayed.seu.forntend.ui.coordinator.StudentView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sayed.seu.forntend.auth.ApplicationUserRole.CHAIRMAN;
import static com.sayed.seu.forntend.auth.ApplicationUserRole.COORDINATOR;


@Theme(value = Lumo.class, variant = Lumo.LIGHT)
public class MainView extends AppLayout {

    private static String CURRENT_USER_ROLE;
    private final Tabs menu;

    public MainView() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUser user = (ApplicationUser) authentication.getPrincipal();
        String authorities = user.getAuthorities().toArray()[0].toString();
        CURRENT_USER_ROLE = authorities.replace("ROLE_", "");
        menu = createTabMenus();
        addToDrawer(menu);
        createHeader();
    }

    private static <T extends HasComponents> T populateLink(T a, String title) {
        a.add(title);
        return a;
    }

    private void createHeader() {
        Anchor logout = new Anchor("logout", new H6("Log Out"));

        InputStream resourceAsStream = getClass().getResourceAsStream("/seu.png");
        StreamResource streamResource = new StreamResource("seu.png", (InputStreamFactory) () -> resourceAsStream);
        Image logo = new Image(streamResource, "");
        logo.setHeight("50px");

        H4 title = new H4("Southeast University");

        HorizontalLayout titleLayout = new HorizontalLayout(logo, title);
        titleLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), titleLayout, logout);
        header.expand(titleLayout);
        header.setMargin(true);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.addClassName("header");
        header.setWidth("100%");
        addToNavbar(header);
    }

    private Tabs createTabMenus() {
        final Tabs tabs = new Tabs();
        tabs.getStyle().set("max-width", "100%");
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private Tab[] getAvailableTabs() {
        final List<Tab> tabs = new ArrayList<>();
        if (CURRENT_USER_ROLE.equals(CHAIRMAN.name())) {
            tabs.add(createTab("Add Student", AddStudentView.class));
            tabs.add(createTab("Notification", NotificationView.class));
            return tabs.toArray(new Tab[tabs.size()]);
        } else if (CURRENT_USER_ROLE.equals(COORDINATOR.name())) {
            tabs.add(createTab("Add Student", AddStudentView.class));
            tabs.add(createTab("View Student", StudentView.class));
            tabs.add(createTab("Notification", NotificationView.class));
            return tabs.toArray(new Tab[tabs.size()]);
        } else {
            tabs.add(createTab("Add Student", AddStudentView.class));
            tabs.add(createTab("Notification", NotificationView.class));
            return tabs.toArray(new Tab[tabs.size()]);
        }
    }

    private Tab createTab(String title, Class<? extends Component> viewClass) {
        return createTab(populateLink(new RouterLink(null, viewClass), title));
    }

    private Tab createTab(Component component) {
        final Tab tab = new Tab();
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        tab.add(component);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        selectTab();
    }


    private void selectTab() {
        String target = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
        Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
            Component child = tab.getChildren().findFirst().get();
            return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
        }).findFirst();
        tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
    }
}
