package com.sayed.seu.forntend.ui;


import com.sayed.seu.forntend.MainView;
import com.sayed.seu.forntend.auth.ApplicationUser;
import com.sayed.seu.forntend.exception.ResourceNotFoundException;
import com.sayed.seu.forntend.model.Notifications;
import com.sayed.seu.forntend.model.Student;
import com.sayed.seu.forntend.model.Team;
import com.sayed.seu.forntend.service.NotificationsService;
import com.sayed.seu.forntend.service.TeamManagementService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static com.sayed.seu.forntend.utils.Course.INTERN;
import static com.sayed.seu.forntend.utils.Course.RESEARCH;
import static com.sayed.seu.forntend.utils.Semester.*;
import static com.sayed.seu.forntend.utils.Term.FINAL;
import static com.sayed.seu.forntend.utils.Term.MID;
import static com.sayed.seu.forntend.utils.Utils.getSemesterYear;


@Route(value = "notification", layout = MainView.class)
@PageTitle(value = "Notification")
public class NotificationView extends VerticalLayout {

    private final NotificationsService notificationsService;
    private final TeamManagementService teamManagementService;
    private final Grid<Notifications> grid = new Grid<>(Notifications.class);
    private final ApplicationUser currentUser;
    private final H2 message = new H2("You have no new notification");
    private List<Notifications> notificationsList;


    public NotificationView(NotificationsService notificationsService, TeamManagementService teamManagementService) {
        this.notificationsService = notificationsService;
        this.teamManagementService = teamManagementService;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        currentUser = (ApplicationUser) authentication.getPrincipal();

        setSizeUndefined();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setMargin(true);
        add(message, grid);
        configGrid();
        retrieveAllNotification();

    }

    private void configGrid() {
        grid.setClassName("notification-grid");

        grid.removeColumnByKey("id");
        grid.removeColumnByKey("facultyName");
        grid.removeColumnByKey("team");

        grid.addColumn(notifications -> notifications.getTeam().getCourseName()).setHeader("Course Name");
        grid.addColumn(notifications -> notifications.getTeam().getTerm()).setHeader("Term");
        grid.addColumn(notifications -> notifications.getTeam().getSemester().getName() + "," + notifications.getTeam().getSemester().getYear()).setHeader("Semester");
        grid.addColumn(TemplateRenderer.<Notifications>of("<div> [[item.name1]] <br> [[item.name2]] <br>  [[item.name3]] </div>")
                .withProperty("name1", notifications -> notifications.getTeam().getStudents().get(0).getName())
                .withProperty("name2", notifications -> {
                    if (notifications.getTeam().getStudents().size() >= 2) {
                        return notifications.getTeam().getStudents().get(1).getName();
                    } else {
                        return "";
                    }
                })
                .withProperty("name3", notifications -> {
                    if (notifications.getTeam().getStudents().size() >= 3) {
                        return notifications.getTeam().getStudents().get(2).getName();
                    } else {
                        return "";
                    }
                })
        ).setHeader("Student Name");

        grid.addColumn(TemplateRenderer.<Notifications>of("<div>[[item.id1]] <br> [[item.id2]] <br> [[item.id3]] </div>")
                .withProperty("id1", notifications -> notifications.getTeam().getStudents().get(0).getId())
                .withProperty("id2", notifications -> {
                    if (notifications.getTeam().getStudents().size() >= 2) {
                        return notifications.getTeam().getStudents().get(1).getId();
                    } else {
                        return "";
                    }
                })
                .withProperty("id3", notifications -> {
                    if (notifications.getTeam().getStudents().size() >= 3) {
                        return notifications.getTeam().getStudents().get(2).getId();
                    } else {
                        return "";
                    }
                })
        ).setHeader("Student ID");

        grid.addComponentColumn(this::getEditColumn)
                .setWidth("50px")
                .setFlexGrow(0);
        grid.getColumns().forEach(teamColumn -> teamColumn.setAutoWidth(true));
    }

    private Component getEditColumn(Notifications notifications) {
        Button button = new Button();
        button.setIcon(VaadinIcon.EDIT.create());
        button.getElement().setProperty("Edit", "Edit student info");
        button.addClickListener(event -> {
            createEditFrom(notifications);
        });
        return button;
    }

    private void updateGrid() {
        if (notificationsList == null) return;
        else grid.setItems(notificationsList);
    }

    private void retrieveAllNotification() {
        try {
            List<Notifications> response = notificationsService.retrieve(currentUser.getUsername());
            message.setVisible(false);
            grid.setVisible(true);
            notificationsList = response;
            updateGrid();

        } catch (ResourceNotFoundException e) {
            message.setVisible(true);
            grid.setVisible(false);
        }
    }

    private void createEditFrom(Notifications notifications) {
        Team team = notifications.getTeam();
        Dialog dialog = new Dialog();
        VerticalLayout verticalLayout = new VerticalLayout();
        HorizontalLayout buttonLayout = new HorizontalLayout();
        FormLayout studentInfoLayout = new FormLayout();
        FormLayout selectorLayout = new FormLayout();


        verticalLayout.setSizeFull();
        verticalLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidth("100%");
        selectorLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("5em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3)
        );


        ComboBox<String> semesterNameSelector = new ComboBox<>();
        ComboBox<String> semesterYearSelector = new ComboBox<>();
        ComboBox<String> courseNameSelector = new ComboBox<>();
        ComboBox<String> termSelector = new ComboBox<>();
        ComboBox<Integer> numberOfGMSelector = new ComboBox<>();
        TextField topicName = new TextField();
        TextField studentOneName = new TextField();
        TextField studentOneID = new TextField();
        TextField studentTwoName = new TextField();
        TextField studentTwoID = new TextField();
        TextField studentThreeName = new TextField();
        TextField studentThreeID = new TextField();

        semesterNameSelector.setLabel("Semester");
        semesterNameSelector.setRequired(true);
        semesterNameSelector.setItems(SPRING.getSemester(), FALL.getSemester(), SUMMER.getSemester());
        semesterNameSelector.setValue(team.getSemester().getName());

        semesterYearSelector.setLabel("Year");
        semesterYearSelector.setRequired(true);
        semesterYearSelector.setItems(getSemesterYear());
        semesterYearSelector.setValue(team.getSemester().getYear());

        courseNameSelector.setLabel("Intern/Research");
        courseNameSelector.setRequired(true);
        courseNameSelector.setItems(RESEARCH.getCourse(), INTERN.getCourse());
        courseNameSelector.setValue(team.getCourseName());

        numberOfGMSelector.setLabel("Number Of Group Member");
        numberOfGMSelector.setRequired(true);
        numberOfGMSelector.setItems(1, 2, 3);
        numberOfGMSelector.setValue(team.getStudents().size());

        termSelector.setLabel("Mid/Final");
        termSelector.setRequired(true);
        termSelector.setItems(MID.getTerm(), FINAL.getTerm());
        termSelector.setValue(team.getTerm());

        topicName.setLabel("Topic");
        topicName.setRequired(true);
        if (team.getCourseName().equals(INTERN.getCourse())) {
            topicName.setVisible(false);
        } else {
            topicName.setVisible(true);
            topicName.setValue("" + team.getTopic());
        }


        studentOneName.setLabel("Student-1 Name");
        studentOneName.setRequired(true);
        studentTwoName.setLabel("Student-2 Name");
        studentTwoName.setRequired(true);
        studentThreeName.setLabel("Student-3 Name");
        studentThreeName.setRequired(true);
        studentOneID.setLabel("Student-1 ID");
        studentOneID.setRequired(true);
        studentTwoID.setLabel("Student-2 ID");
        studentTwoID.setRequired(true);
        studentThreeID.setLabel("Student-3 ID");
        studentThreeID.setRequired(true);


        if (team.getStudents().size() == 1) {
            studentOneName.setVisible(true);
            studentOneID.setVisible(true);
            studentTwoName.setVisible(false);
            studentTwoID.setVisible(false);
            studentThreeName.setVisible(false);
            studentThreeID.setVisible(false);
            studentOneName.setValue(team.getStudents().get(0).getName());
            studentOneID.setValue(team.getStudents().get(0).getId());
        } else if (team.getStudents().size() == 2) {
            studentOneName.setVisible(true);
            studentOneID.setVisible(true);
            studentTwoName.setVisible(true);
            studentTwoID.setVisible(true);
            studentThreeName.setVisible(false);
            studentThreeID.setVisible(false);
            studentOneName.setValue(team.getStudents().get(0).getName());
            studentOneID.setValue(team.getStudents().get(0).getId());
            studentTwoName.setValue(team.getStudents().get(1).getName());
            studentTwoID.setValue(team.getStudents().get(1).getId());
        } else {
            studentOneName.setVisible(true);
            studentOneID.setVisible(true);
            studentTwoName.setVisible(true);
            studentTwoID.setVisible(true);
            studentThreeName.setVisible(true);
            studentThreeID.setVisible(true);
            studentOneName.setValue(team.getStudents().get(0).getName());
            studentOneID.setValue(team.getStudents().get(0).getId());
            studentTwoName.setValue(team.getStudents().get(1).getName());
            studentTwoID.setValue(team.getStudents().get(1).getId());
            studentThreeName.setValue(team.getStudents().get(2).getName());
            studentThreeID.setValue(team.getStudents().get(2).getId());
        }

        courseNameSelector.addValueChangeListener(event -> {
            topicName.setVisible(!event.getValue().equals(INTERN.getCourse()));
        });


        numberOfGMSelector.addValueChangeListener(event -> {
            switch (event.getValue()) {
                case 2:
                    studentOneName.setVisible(true);
                    studentOneID.setVisible(true);
                    studentTwoName.setVisible(true);
                    studentTwoID.setVisible(true);
                    studentThreeName.setVisible(false);
                    studentThreeID.setVisible(false);
                    break;
                case 3:
                    studentOneName.setVisible(true);
                    studentOneID.setVisible(true);
                    studentTwoName.setVisible(true);
                    studentTwoID.setVisible(true);
                    studentThreeName.setVisible(true);
                    studentThreeID.setVisible(true);
                    break;
                case 1:
                default:
                    studentOneName.setVisible(true);
                    studentOneID.setVisible(true);
                    studentTwoName.setVisible(false);
                    studentTwoID.setVisible(false);
                    studentThreeName.setVisible(false);
                    studentThreeID.setVisible(false);
                    break;
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        Button saveButton = new Button("Save");
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_PRIMARY);

        selectorLayout.add(semesterNameSelector, semesterYearSelector, courseNameSelector, termSelector, numberOfGMSelector, topicName);
        studentInfoLayout.add(studentOneName, studentOneID, studentTwoName, studentTwoID, studentThreeName, studentThreeID);
        buttonLayout.add(cancelButton, saveButton);
        verticalLayout.add(selectorLayout, studentInfoLayout, buttonLayout);
        dialog.add(verticalLayout);
        dialog.open();

        cancelButton.addClickListener(event -> dialog.close());

        saveButton.addClickListener(event -> {
            boolean flag = true;
            String semesterName = semesterNameSelector.getValue();
            String semesterYear = semesterYearSelector.getValue();
            String courseName = courseNameSelector.getValue();
            String term = termSelector.getValue();
            int groupMember = numberOfGMSelector.getValue();
            if (courseName.equals(RESEARCH.getCourse()) && topicName.getValue().trim().isEmpty()) {
                flag = false;
            }
            if (groupMember == 1) {
                if (studentOneName.getValue().isEmpty() || studentOneID.getValue().isEmpty()) {
                    flag = false;
                }
            } else if (groupMember == 2) {
                if (studentOneName.getValue().isEmpty() || studentOneID.getValue().isEmpty() || studentTwoName.getValue().isEmpty() || studentTwoID.getValue().isEmpty()
                ) {
                    flag = false;
                }
            } else if (groupMember == 3) {
                if (studentOneName.getValue().isEmpty() || studentOneID.getValue().isEmpty() || studentTwoName.getValue().isEmpty() || studentTwoID.getValue().isEmpty() || studentThreeName.getValue().isEmpty() || studentThreeID.getValue().isEmpty()) {
                    flag = false;
                }
            }
            if (!flag) {
                Notification.show("One or more field remains empty", 3000, Notification.Position.TOP_CENTER);
            } else {

                team.getSemester().setName(semesterName);
                team.getSemester().setName(semesterYear);

                List<Student> studentList = new ArrayList<>();

                if (groupMember == 1) {
                    studentList.add(new Student(studentOneID.getValue(), studentOneName.getValue()));
                } else if (groupMember == 2) {
                    studentList.add(new Student(studentOneID.getValue(), studentOneName.getValue()));
                    studentList.add(new Student(studentTwoID.getValue(), studentTwoName.getValue()));
                } else if (groupMember == 3) {
                    studentList.add(new Student(studentOneID.getValue(), studentOneName.getValue()));
                    studentList.add(new Student(studentTwoID.getValue(), studentTwoName.getValue()));
                    studentList.add(new Student(studentThreeID.getValue(), studentThreeName.getValue()));
                }
                team.setCourseName(courseName);
                team.setStudents(studentList);
                team.setTerm(term);
                team.setTopic(topicName.getValue());

                upload(team, notifications.getId());
                dialog.close();
            }
        });
    }

    private void upload(Team team, long id) {
        try {
            Team response = teamManagementService.update(team);
            System.out.println(response);
            if (response == null) {
                showNotification();
            } else {
                delete(id);
            }
        } catch (ResourceNotFoundException e) {
            showNotification();
        }
    }

    private void delete(long id) {
        try {
            boolean response = notificationsService.delete(id);
            System.out.println(response);
            if (response) {
                retrieveAllNotification();
            } else {
                showNotification();
            }
        } catch (ResourceNotFoundException e) {
            showNotification();
        }
    }

    private void showNotification() {
        Notification.show("Something went wrong. Please try again latter", 3000, Notification.Position.TOP_CENTER);
    }
}
