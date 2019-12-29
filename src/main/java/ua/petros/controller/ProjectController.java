package ua.petros.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.petros.model.Project;
import ua.petros.model.User;
import ua.petros.service.ProjectService;
import ua.petros.service.StatusService;
import ua.petros.service.UserService;
import ua.petros.validator.ProjectValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Taras on 18.12.2019.
 */

@Controller
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ProjectValidator projectValidator;

    private User userPrincipal;
    private List<User> fieldContactUsers;
    private List<User> fuUsers;

    // Show all projects
    @RequestMapping(value = "/projects", method = {RequestMethod.GET})
    public String showMembers(Model model, Authentication authentication) {
        List<Project> listProject = projectValidator.getUserProjectList(authentication);
        model.addAttribute("list", listProject);
        return "projectsList";
    }

    //Show project
    @RequestMapping(value = "/projects/{projectId}", method = {RequestMethod.GET})
    public String showProject(Model model, @PathVariable("projectId") String projectId) {
        model.addAttribute("project", projectService.getById(UUID.fromString(projectId)));
        return "projectShow";
    }

    // Show form to create new project
    @RequestMapping(value = "/projects/new", method = {RequestMethod.GET})
    public String showNewProjectForm(Model model, Authentication authentication) {
        initializeModelAttributes(authentication);
        model.addAttribute("userPrincipal", userPrincipal);
        model.addAttribute("listFieldContactUsers", fieldContactUsers);
        model.addAttribute("listFuUsers", fuUsers);
        model.addAttribute("listStatuses", statusService.getAll());
        return "projectNew";
    }

    // Create project
    @RequestMapping(value = "/projects", method = {RequestMethod.POST})
    public String addProject(Model model, Authentication authentication,
                             @ModelAttribute("name") String name,
                             @ModelAttribute("description") String description,
                             @ModelAttribute("startDate") String startDate,
                             @ModelAttribute("stopDate") String stopDate,
                             @ModelAttribute("statusName") String statusName,
                             @ModelAttribute("fieldContactId") String fieldContactId,
                             @ModelAttribute("fuId") String fuId,
                             @ModelAttribute("feedback") String feedback
    ) {

        // prepare project info
        Project project = new Project();
        UUID uuid = UUID.randomUUID();
        project.setId(uuid);
        project.setName(name);
        project.setDescription(description);
        project.setFeedback(feedback);

        Date dateStart = null;
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                dateStart = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        project.setStartDate(dateStart);

        Date dateStop = null;
        if (stopDate != null && !stopDate.trim().isEmpty()) {
            try {
                dateStop = new SimpleDateFormat("yyyy-MM-dd").parse(stopDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        project.setStopDate(dateStop);

        project.setStatus(statusService.findByName(statusName));
        if (fieldContactId != null && !fieldContactId.trim().isEmpty()) {
            project.setFieldContactUser(userService.getById(UUID.fromString(fieldContactId)));
        }
        if (fuId != null && !fuId.trim().isEmpty()) {
            project.setFuUser(userService.getById(UUID.fromString(fuId)));
        }

        // validate project
        Map<String, String> messages = projectValidator.validate(project);

        // If no errors, create project
        if (messages.isEmpty()) {
            projectService.save(project);
        } else {
            // back to the new project form
            initializeModelAttributes(authentication);
            model.addAttribute("userPrincipal", userPrincipal);
            model.addAttribute("messages", messages);
            model.addAttribute("project", project);
            model.addAttribute("listFieldContactUsers", fieldContactUsers);
            model.addAttribute("listFuUsers", fuUsers);
            model.addAttribute("listStatuses", statusService.getAll());
            return "projectNew";
        }

        //show project
        return "redirect:/projects/" + uuid;
    }

    // delete project
    @RequestMapping(value = "/projects/{projectId}", method = RequestMethod.DELETE)
    public String deleteProject(Model model, @ModelAttribute("projectId") String projectId) {

        if (!projectId.trim().isEmpty()) {
            projectService.delete(projectService.getById(UUID.fromString(projectId)));
        }
        // Return all projects
        return "redirect:/projects";
    }

    // Show form to edit project
    @RequestMapping(value = "/projects/{projectId}/edit", method = {RequestMethod.GET})
    public String showEditProjectForm(Model model, Authentication authentication,
                                      @PathVariable("projectId") String projectId) {
        initializeModelAttributes(authentication);
        model.addAttribute("project", projectService.getById(UUID.fromString(projectId)));
        model.addAttribute("userPrincipal", userPrincipal);
        model.addAttribute("listFieldContactUsers", fieldContactUsers);
        model.addAttribute("listFuUsers", fuUsers);
        model.addAttribute("listStatuses", statusService.getAll());
        return "projectEdit";
    }

    // Edit project
    @RequestMapping(value = "/projects/{projectId}", method = RequestMethod.PUT)
    public String editProject(Model model, Authentication authentication,
                             @ModelAttribute("name") String name,
                             @ModelAttribute("description") String description,
                             @ModelAttribute("startDate") String startDate,
                             @ModelAttribute("stopDate") String stopDate,
                             @ModelAttribute("statusName") String statusName,
                             @ModelAttribute("fieldContactId") String fieldContactId,
                             @ModelAttribute("fuId") String fuId,
                             @ModelAttribute("feedback") String feedback,
                             @ModelAttribute("projectId") String projectId
    ) {

        // prepare project info
        Project project = new Project();
        project.setId(UUID.fromString(projectId));
        project.setName(name);
        project.setDescription(description);
        project.setFeedback(feedback);

        Date dateStart = null;
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                dateStart = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        project.setStartDate(dateStart);

        Date dateStop = null;
        if (stopDate != null && !stopDate.trim().isEmpty()) {
            try {
                dateStop = new SimpleDateFormat("yyyy-MM-dd").parse(stopDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        project.setStopDate(dateStop);

        project.setStatus(statusService.findByName(statusName));
        if (fieldContactId != null && !fieldContactId.trim().isEmpty()) {
            project.setFieldContactUser(userService.getById(UUID.fromString(fieldContactId)));
        }
        if (fuId != null && !fuId.trim().isEmpty()) {
            project.setFuUser(userService.getById(UUID.fromString(fuId)));
        }

        // validate project
        Map<String, String> messages = projectValidator.validate(project);

        // If no errors, save project
        if (messages.isEmpty()) {
            projectService.save(project);
        } else {
            // back to the edit project form
            initializeModelAttributes(authentication);
            model.addAttribute("userPrincipal", userPrincipal);
            model.addAttribute("messages", messages);
            model.addAttribute("project", project);
            model.addAttribute("listFieldContactUsers", fieldContactUsers);
            model.addAttribute("listFuUsers", fuUsers);
            model.addAttribute("listStatuses", statusService.getAll());
            return "projectEdit";
        }

        //show project
        return "redirect:/projects/" + projectId;
    }


    // Generate lists to be passed to model views
    private void initializeModelAttributes(Authentication authentication){
        List<User> listUsers = userService.getAll();
        fieldContactUsers = listUsers.stream()
                .filter(user -> "ROLE_FIELDCONTACT".equals(user.getRoles().iterator().next().getName()))
                .collect(Collectors.toList());
        fuUsers = listUsers.stream()
                .filter(user -> "ROLE_FU".equals(user.getRoles().iterator().next().getName()))
                .collect(Collectors.toList());

        String currentPrincipalName = authentication.getName();
        userPrincipal = userService.findByUsername(currentPrincipalName);
    }
}
