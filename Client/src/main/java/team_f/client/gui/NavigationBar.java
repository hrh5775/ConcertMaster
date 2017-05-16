package team_f.client.gui;

import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import team_f.client.configuration.Configuration;
import team_f.client.controls.sidebar.MenuSection;
import team_f.client.controls.sidebar.MenuSectionItem;
import team_f.client.controls.sidebar.Sidebar;
import team_f.client.pages.BasePage;
import team_f.client.pages.PageAction;
import team_f.client.singletons.*;
import javax.lang.model.type.NullType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class NavigationBar {
    private BorderPane _pane;
    private Configuration _configuration;
    private BasePage _currentPage;
    private List<BasePage> _initializedPageList = new LinkedList<>();
    private PageAction<Void, NullType> _loadPage;

    public NavigationBar(BorderPane pane, Configuration configuration) {
        _pane = pane;
        _configuration = configuration;
    }

    public void setOnLoad(PageAction<Void, NullType> action) {
        _loadPage = action;
    }

    public Sidebar getNavigationBar() {
        Sidebar sidebar = new Sidebar();
        //MenuSection menuSection;
        ArrayList<MenuSection> menuSectionArrayList=new ArrayList<>();
        MenuSectionItem menuSectionItem;
        ToggleGroup toggleGroup = new ToggleGroup();
        sidebar.setStyle("-fx-background-color:   #e0e0d1");

        MenuSection menuSectionHome = new MenuSection("Home", "/homeM.png", null);
        menuSectionHome.setAnimated(false);
        menuSectionHome.setCollapsible(false);
        menuSectionHome.setOnMouseClicked(event -> {
            loadPage(HomeScreenSingleton.getInstance());
        });
        sidebar.add(menuSectionHome);
        menuSectionArrayList.add(menuSectionHome);

        MenuSection menuSectionServiceSchedule = new MenuSection("Service Schedule", "/calendarM.png", toggleGroup);

        menuSectionItem = new MenuSectionItem("Show Schedules");
        menuSectionItem.setOnAction(event -> loadPage(CalendarSingleton.getInstance(_configuration)));

        menuSectionServiceSchedule.add(menuSectionItem);
        menuSectionItem = new MenuSectionItem("Publish/Unpublish Schedule");
        //  menuSectionItem.setOnMouseClicked(event -> new LegendTable());
        menuSectionItem.setOnMouseClicked(event -> loadPage(MonthPublisherSingleton.getInstance(_configuration)));
        menuSectionServiceSchedule.add(menuSectionItem);
        sidebar.add(menuSectionServiceSchedule);
        menuSectionArrayList.add(menuSectionServiceSchedule);

        MenuSection menuSectionServices = new MenuSection("Services", "/dutyM.png", toggleGroup);
        menuSectionItem = new MenuSectionItem("Service Schedules");
        menuSectionServices.add(menuSectionItem);
        menuSectionItem = new MenuSectionItem("Service Management");
        menuSectionServices.add(menuSectionItem);
        sidebar.add(menuSectionServices);
        menuSectionArrayList.add(menuSectionServices);

        MenuSection menuSectionMusician = new MenuSection("Musicians", "/orchestraiconM.png", toggleGroup);
        menuSectionItem = new MenuSectionItem("Musician List");
        menuSectionItem.setOnMouseClicked(event -> {
            loadPage(MusiciansListSingleton.getInstance(_configuration));
        });
        menuSectionMusician.add(menuSectionItem);
        sidebar.add(menuSectionMusician);
        menuSectionArrayList.add(menuSectionMusician);

        MenuSection menuSectionCompositions = new MenuSection("Compositions", "/musicfolderM.png", toggleGroup);
        menuSectionItem = new MenuSectionItem("Musical Work Management");
        menuSectionItem.setOnMouseClicked(event -> loadPage(MusicalWorkSingleton.getInstance(_configuration)));
        menuSectionCompositions.add(menuSectionItem);
        menuSectionItem = new MenuSectionItem("Instrumentation Management");
        menuSectionItem.setOnMouseClicked(event -> loadPage(InstrumentationSingleton.getInstance(_configuration)));
        menuSectionCompositions.add(menuSectionItem);
        sidebar.add(menuSectionCompositions);
        menuSectionArrayList.add(menuSectionCompositions);

        MenuSection menuSectionInventory = new MenuSection("Inventory", "/inventaryM.png", toggleGroup);
        menuSectionItem = new MenuSectionItem("Show Inventory");
        menuSectionInventory.add(menuSectionItem);
        menuSectionItem = new MenuSectionItem("Add Item");
        menuSectionInventory.add(menuSectionItem);
        sidebar.add(menuSectionInventory);
        menuSectionArrayList.add(menuSectionInventory);

        MenuSection menuSectionUserScreen = new MenuSection("User Profile", "/userM.png", toggleGroup);
        menuSectionItem = new MenuSectionItem("Section Management");
        menuSectionUserScreen.add(menuSectionItem);
        menuSectionItem = new MenuSectionItem("Musician Management");
        menuSectionUserScreen.add(menuSectionItem);
        sidebar.add(menuSectionUserScreen);
        menuSectionArrayList.add(menuSectionUserScreen);

        MenuSection menuSectionAdministration = new MenuSection("Administration", "/settingsM.png", toggleGroup);
        menuSectionItem = new MenuSectionItem("Section Management");
        menuSectionAdministration.add(menuSectionItem);
        menuSectionItem = new MenuSectionItem("Musician Management");
        menuSectionAdministration.add(menuSectionItem);
        menuSectionItem.setOnMouseClicked(event -> {
            loadPage(MusiciansTableSingleton.getInstance(_configuration));
        });
        sidebar.add(menuSectionAdministration);
        menuSectionArrayList.add(menuSectionAdministration);

        menuSectionHome.setOnMouseClicked(event -> {
            for (MenuSection menuSection : menuSectionArrayList) {
                menuSection.setExpanded(false);
            }

            loadPage(HomeScreenSingleton.getInstance());
        });

        // load the default site
        loadPage(HomeScreenSingleton.getInstance());

        return sidebar;
    }

    public BasePage getCurrentPage() {
        return _currentPage;
    }

    private void setCurrentPage(BasePage page) {
        _currentPage = page;

        setPane(_currentPage);
    }

    private List<BasePage> getInitializedPageList() {
        return _initializedPageList;
    }

    private void addInitializedPageListItem(BasePage page) {
        if(!_initializedPageList.contains(page)) {
            page.initialize();
            _initializedPageList.add(page);
        }
    }

    private void setPane(Node page) {
        _pane.setCenter(page);
    }

    private boolean loadPage(BasePage page) {
        if(page != null) {
            if(exitPage(page)) {
                addInitializedPageListItem(page);
                setCurrentPage(page);

                if(_loadPage != null) {
                    _loadPage.doAction(null);
                }

                page.load();

                return true;
            }

            return false;
        }

        return false;
    }

    private boolean exitPage(BasePage page) {
        if(page != null) {
            page.exit();
            setCurrentPage(null);
            return true;
        }

        return false;
    }
}
