package io.gitub.agpaluch.controller;

import io.gitub.agpaluch.model.ProjectStep;
import io.gitub.agpaluch.model.projection.ProjectWriteModel;
import io.gitub.agpaluch.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/projects")
class ProjectController {
    private final ProjectService service;

    ProjectController(final ProjectService service) {
        this.service = service;
    }

    @GetMapping
    String showProjects(Model model) {
        model.addAttribute("project", new ProjectWriteModel());
        return "projects";
    }

    @PostMapping
    String addProject(@ModelAttribute("project") ProjectWriteModel current, Model model) {
        service.save(current);
        model.addAttribute("project", new ProjectWriteModel());
        model.addAttribute("message", "Dodano projekt");
        return "projects";


    }

    @PostMapping(params = "addStep")
    String addProjectStep(@ModelAttribute("project") ProjectWriteModel current) {
        current.getSteps().add(new ProjectStep());
        return "projects";
    }


}
