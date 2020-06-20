package com.sayed.seu.forntend.ui.coordinator;

import com.sayed.seu.forntend.MainView;
import com.sayed.seu.forntend.exception.ResourceNotFoundException;
import com.sayed.seu.forntend.model.Notifications;
import com.sayed.seu.forntend.model.Semester;
import com.sayed.seu.forntend.model.Team;
import com.sayed.seu.forntend.service.NotificationsService;
import com.sayed.seu.forntend.service.TeamManagementService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.sayed.seu.forntend.utils.Semester.*;
import static com.sayed.seu.forntend.utils.Utils.getSemesterYear;


@Route(value = "view-student", layout = MainView.class)
@PageTitle(value = "View Student")
public class StudentView extends VerticalLayout {

    private final TeamManagementService managementService;
    private final NotificationsService notificationsService;
    private final ComboBox<String> semesterNameComb = new ComboBox<>();
    private final ComboBox<String> semesterYearComb = new ComboBox<>();
    private final Button semesterFilterButton = new Button("Filter");
    private final HorizontalLayout filterLayout = new HorizontalLayout();
    private final Grid<Team> grid = new Grid<>(Team.class);
    private List<Team> teamList;
    private ListDataProvider<Team> dataProvider;

    public StudentView(TeamManagementService managementService, NotificationsService notificationsService) {
        this.managementService = managementService;
        this.notificationsService = notificationsService;
        setMargin(true);
        setSizeUndefined();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        configureGrid();
        configureSemesterFiltering();
        add(filterLayout, grid);
        retrieveAllTeam();
    }

    private void configureSemesterFiltering() {
        semesterNameComb.setPlaceholder(SPRING.getSemester());
        semesterNameComb.setItems(SPRING.getSemester(), FALL.getSemester(), SUMMER.getSemester());
        semesterNameComb.setRequired(true);

        semesterYearComb.setPlaceholder("2019");
        semesterYearComb.setItems(getSemesterYear());
        semesterYearComb.setRequired(true);

        filterLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        filterLayout.add(semesterNameComb, semesterYearComb, semesterFilterButton);

        semesterFilterButton.addClickListener(buttonClickEvent -> {
            if (semesterNameComb.getValue() == null || semesterYearComb.getValue() == null) {
                Notification.show("Please select a semester", 3000, Notification.Position.TOP_CENTER);
            } else {
                Semester semester = new Semester();
                semester.setName(semesterNameComb.getValue());
                semester.setYear(semesterYearComb.getValue());
                retrieveAllTeamBySemester(semester);
            }

        });
    }


    private void configureGrid() {
        grid.addClassName("student-grid");
        grid.setSizeUndefined();

        grid.removeColumnByKey("id");
        grid.removeColumnByKey("faculty");
        grid.removeColumnByKey("semester");
        grid.removeColumnByKey("students");
        grid.removeColumnByKey("courseName");
        grid.removeColumnByKey("term");
        grid.removeColumnByKey("topic");

        Grid.Column<Team> courseNameColumn = grid.addColumn("courseName");

        Grid.Column<Team> facultyColumn = grid.addColumn(team -> team.getFaculty().getId().toUpperCase()).setHeader("Faculty");

        Grid.Column<Team> termColumn = grid.addColumn("term");

        Grid.Column<Team> semesterColumn = grid.addColumn(team -> team.getSemester().getName() + "," + team.getSemester().getYear()).setHeader("Semester");

        Grid.Column<Team> studentNameColumn = grid.addColumn(TemplateRenderer.<Team>of("<div> [[item.name1]] <br> [[item.name2]] <br>  [[item.name3]] </div>")
                .withProperty("name1", team -> {
                    return team.getStudents().get(0).getName();
                })
                .withProperty("name2", team -> {
                    if (team.getStudents().size() >= 2) {
                        return team.getStudents().get(1).getName();
                    } else {
                        return "";
                    }
                })
                .withProperty("name3", team -> {
                    if (team.getStudents().size() >= 3) {
                        return team.getStudents().get(2).getName();
                    } else {
                        return "";
                    }
                })
        ).setHeader("Student Name");

        Grid.Column<Team> studentIdColumn = grid.addColumn(TemplateRenderer.<Team>of("<div>[[item.id1]] <br> [[item.id2]] <br> [[item.id3]] </div>")
                .withProperty("id1", team -> {
                    return team.getStudents().get(0).getId();
                })
                .withProperty("id2", team -> {
                    if (team.getStudents().size() >= 2) {
                        return team.getStudents().get(1).getId();
                    } else {
                        return "";
                    }
                })
                .withProperty("id3", team -> {
                    if (team.getStudents().size() >= 3) {
                        return team.getStudents().get(2).getId();
                    } else {
                        return "";
                    }
                })

        ).setHeader("Student ID");

        grid.addColumn("topic").setAutoWidth(true);

        grid.addComponentColumn(this::getNotificationColumn).setWidth("50px").setFlexGrow(0).setAutoWidth(true);

        HeaderRow filterRow = grid.appendHeaderRow();

        TextField courseNameField = new TextField();
        courseNameField.addValueChangeListener(event ->
                dataProvider.addFilter(team ->
                        StringUtils.containsIgnoreCase(team.getCourseName(), courseNameField.getValue())
                )
        );
        courseNameField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(courseNameColumn).setComponent(courseNameField);
        courseNameField.setWidth("70%");
        courseNameField.setPlaceholder("Filter");

        TextField facultyNameField = new TextField();
        facultyNameField.addValueChangeListener(event ->
                dataProvider.addFilter(team ->
                        StringUtils.containsIgnoreCase(team.getFaculty().getId(), facultyNameField.getValue())
                )
        );
        facultyNameField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(facultyColumn).setComponent(facultyNameField);
        facultyNameField.setWidth("70%");
        facultyNameField.setPlaceholder("Filter");

        TextField termNameField = new TextField();
        termNameField.addValueChangeListener(event ->
                dataProvider.addFilter(team ->
                        StringUtils.containsIgnoreCase(team.getTerm(), termNameField.getValue())
                )
        );
        termNameField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(termColumn).setComponent(termNameField);
        termNameField.setWidth("70%");
        termNameField.setPlaceholder("Filter");

        grid.getColumns().forEach(teamColumn -> teamColumn.setAutoWidth(true));

    }


    private Component getNotificationColumn(Team team) {
        Button button = new Button();
        button.setIcon(VaadinIcon.BELL.create());
        button.getElement().setProperty("Notification", "Send Notification to Faculty");
        button.addClickListener(event -> {
            openConfirmDialog(team);
        });
        return button;
    }

    private void openConfirmDialog(Team team) {
        Dialog dialog = new Dialog();
        Label label = new Label("Send Notification?");
        NativeButton confirmButton = new NativeButton("Confirm");
        NativeButton cancelButton = new NativeButton("Cancel");
        dialog.add(label, confirmButton, cancelButton);
        dialog.open();

        confirmButton.addClickListener(event -> {
            dialog.close();
            Notifications notifications = new Notifications();
            notifications.setFacultyName(team.getFaculty().getId());
            notifications.setTeam(team);
            try {
                Notifications response = notificationsService.insert(notifications);
                if (response != null) {
                    Notification.show("Notification Successfully Send", 3000, Notification.Position.TOP_CENTER);
                } else {
                    Notification.show("Something went wrong. Please try again", 3000, Notification.Position.TOP_CENTER);
                }
            } catch (ResourceNotFoundException e) {
                Notification.show("Something went wrong. Please try again", 3000, Notification.Position.TOP_CENTER);
            }
        });

        cancelButton.addClickListener(event -> {
            dialog.close();
        });


    }

    private void updateGrid() {
        if (teamList == null) return;
        dataProvider = new ListDataProvider<>(teamList);
        grid.setItems(teamList);
        grid.setDataProvider(dataProvider);
    }

    private void retrieveAllTeam() {
        try {
            List<Team> response = managementService.retrieve();
            if (response == null) {
                Notification.show("Nothing found", 5000, Notification.Position.TOP_CENTER);
            } else {
                teamList = response;
                updateGrid();
            }
        } catch (Exception e) {
            Notification.show("Nothing found", 5000, Notification.Position.TOP_CENTER);
        }
    }

    private void retrieveAllTeamBySemester(Semester semester) {
        try {
            List<Team> response = managementService.retrieve(semester);
            if (response == null) {
                Notification.show("Nothing found", 5000, Notification.Position.TOP_CENTER);
            } else {
                teamList = response;
                updateGrid();
            }
        } catch (Exception e) {
            Notification.show("Nothing found", 5000, Notification.Position.TOP_CENTER);
        }
    }
}
