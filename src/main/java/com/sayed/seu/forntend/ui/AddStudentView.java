package com.sayed.seu.forntend.ui;

import com.sayed.seu.forntend.MainView;
import com.sayed.seu.forntend.auth.ApplicationUser;
import com.sayed.seu.forntend.model.*;
import com.sayed.seu.forntend.service.ApplicationUserDetailsService;
import com.sayed.seu.forntend.service.TeamManagementService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
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


@Route(value = "", layout = MainView.class)
@PageTitle(value = "Add Student")
public class AddStudentView extends VerticalLayout {

    private final ApplicationUserDetailsService userDetailsService;
    private final TeamManagementService managementService;
    private final FormLayout selectorFormLayout = new FormLayout();
    private final FormLayout studentInfoFormLayout = new FormLayout();
    private final FormLayout topicNameFormLayout = new FormLayout();
    private final ComboBox<String> semesterNameSelector = new ComboBox<>();
    private final ComboBox<String> semesterYearSelector = new ComboBox<>();
    private final ComboBox<String> courseNameSelector = new ComboBox<>();
    private final ComboBox<String> termSelector = new ComboBox<>();
    private final ComboBox<Integer> numberOfGMSelector = new ComboBox<>();
    private final TextField topicNameTF = new TextField();
    private final TextField studentOneName = new TextField();
    private final TextField studentOneID = new TextField();
    private final TextField studentTwoName = new TextField();
    private final TextField studentTwoID = new TextField();
    private final TextField studentThreeName = new TextField();
    private final TextField studentThreeID = new TextField();
    private final Button uploadBT = new Button("UPLOAD", new Icon(VaadinIcon.UPLOAD));
    private final ApplicationUser currentUser;
    private String semesterName = "", semesterYear = "";
    private String courseName = "";
    private String term = "";
    private int groupMember = 0;

    public AddStudentView(ApplicationUserDetailsService userDetailsService, TeamManagementService managementService) {
        this.userDetailsService = userDetailsService;
        this.managementService = managementService;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        currentUser = (ApplicationUser) authentication.getPrincipal();
        setSizeUndefined();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSpacing(true);
        add(selectorFormLayout, topicNameFormLayout, studentInfoFormLayout, uploadBT);
        createSelectorFrom();
        createStudentInfoForm();
        createButton();
        initListener();
    }

    private void createButton() {
        uploadBT.addClickShortcut(Key.ENTER);
        uploadBT.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_PRIMARY);
    }

    private void createSelectorFrom() {
        selectorFormLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("5em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3)
        );

        semesterNameSelector.setLabel("Semester");
        semesterNameSelector.setRequired(true);
        semesterNameSelector.setErrorMessage("Please fill out this field");
        semesterNameSelector.setPlaceholder(SPRING.getSemester());
        semesterNameSelector.setItems(SPRING.getSemester(), FALL.getSemester(), SUMMER.getSemester());

        semesterYearSelector.setLabel("Year");
        semesterYearSelector.setRequired(true);
        semesterYearSelector.setErrorMessage("Please fill out this field");
        semesterYearSelector.setPlaceholder("2019");
        semesterYearSelector.setItems(getSemesterYear());

        courseNameSelector.setLabel("Intern/Research");
        courseNameSelector.setRequired(true);
        courseNameSelector.setErrorMessage("Please fill out this field");
        courseNameSelector.setPlaceholder(RESEARCH.getCourse());
        courseNameSelector.setItems(RESEARCH.getCourse(), INTERN.getCourse());

        numberOfGMSelector.setLabel("Number Of Group Member");
        numberOfGMSelector.setRequired(true);
        numberOfGMSelector.setErrorMessage("Please fill out this field");
        numberOfGMSelector.setPlaceholder("1");
        numberOfGMSelector.setItems(1, 2, 3);

        termSelector.setLabel("Mid/Final");
        termSelector.setRequired(true);
        termSelector.setErrorMessage("Please fill out this field");
        termSelector.setPlaceholder("Mid");
        termSelector.setItems(MID.getTerm(), FINAL.getTerm());

        topicNameTF.setLabel("Topic Name");
        topicNameTF.setRequired(true);
        topicNameTF.setErrorMessage("Please fill out this field");

        selectorFormLayout.add(semesterNameSelector, semesterYearSelector, courseNameSelector, termSelector, numberOfGMSelector, topicNameTF);
    }


    private void createStudentInfoForm() {
        studentOneName.setLabel("Student-1 Name");
        studentOneName.setRequired(true);
        studentOneName.setErrorMessage("Please fill out this field");
        studentTwoName.setLabel("Student-2 Name");
        studentTwoName.setRequired(true);
        studentTwoName.setErrorMessage("Please fill out this field");
        studentThreeName.setLabel("Student-3 Name");
        studentThreeName.setRequired(true);
        studentThreeName.setErrorMessage("Please fill out this field");
        studentOneID.setLabel("Student-1 ID");
        studentOneID.setRequired(true);
        studentOneID.setErrorMessage("Please fill out this field");
        studentTwoID.setLabel("Student-2 ID");
        studentTwoID.setRequired(true);
        studentTwoID.setErrorMessage("Please fill out this field");
        studentThreeID.setLabel("Student-3 ID");
        studentThreeID.setRequired(true);
        studentThreeID.setErrorMessage("Please fill out this field");

        studentOneName.setVisible(true);
        studentOneID.setVisible(true);
        studentTwoName.setVisible(false);
        studentTwoID.setVisible(false);
        studentThreeName.setVisible(false);
        studentThreeID.setVisible(false);

        studentInfoFormLayout.add(studentOneName, studentOneID, studentTwoName, studentTwoID, studentThreeName, studentThreeID);
    }

    private void initListener() {
        semesterNameSelector.addValueChangeListener(event -> semesterName = event.getValue());

        semesterYearSelector.addValueChangeListener(event -> semesterYear = event.getValue());

        courseNameSelector.addValueChangeListener(event -> {
            topicNameTF.setVisible(!event.getValue().equals(INTERN.getCourse()));
            courseName = event.getValue();
        });

        termSelector.addValueChangeListener(event -> term = event.getValue());

        numberOfGMSelector.addValueChangeListener(event -> {
            groupMember = event.getValue();
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

        uploadBT.addClickListener(event -> validateAndUpload());
    }

    private void validateAndUpload() {
        boolean flag = true;
        if (semesterName.isEmpty() || semesterYear.isEmpty() || courseName.isEmpty() || term.isEmpty() || (groupMember <= 0)) {
            flag = false;
        }
        if (courseName.equals(RESEARCH.getCourse()) && topicNameTF.getValue().trim().isEmpty()) {
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
        upload(flag);
    }

    private void upload(boolean flag) {
        if (!flag) {
            Notification.show("One or more field remains empty", 3000, Notification.Position.TOP_CENTER);
        } else {
            Team response = null;
            try {
                response = managementService.insert(getTeam());
            } catch (Exception e) {
                Notification.show(e.getMessage(), 5000, Notification.Position.TOP_CENTER);
            }
            if (response == null) {
                Notification.show("Something went wrong.Please try again latter", 5000, Notification.Position.TOP_CENTER);
            } else {
                Notification.show("Successfully Uploaded", 5000, Notification.Position.TOP_CENTER);
                clearField();
            }
        }
    }

    private Team getTeam() {
        Team team = new Team();
        Faculty faculty = new Faculty();
        Semester semester = new Semester();
        List<Student> studentList = new ArrayList<>();
        ApplicationUserDetails userDetails = userDetailsService.retrieve(currentUser.getUsername()).get();

        faculty.setId(userDetails.getUsername());
        faculty.setEmail(userDetails.getEmail());
        faculty.setName(userDetails.getFullName());

        semester.setName(semesterName);
        semester.setYear(semesterYear);

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
        team.setFaculty(faculty);
        team.setSemester(semester);
        team.setStudents(studentList);
        team.setTerm(term);
        team.setTopic(topicNameTF.getValue());

        return team;
    }

    private void clearField() {
        topicNameTF.clear();
        studentOneName.clear();
        studentOneID.clear();
        studentTwoName.clear();
        studentTwoID.clear();
        studentThreeName.clear();
        studentThreeID.clear();

    }
}
