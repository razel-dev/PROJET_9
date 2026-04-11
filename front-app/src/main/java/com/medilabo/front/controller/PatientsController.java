package com.medilabo.front.controller;

import com.medilabo.front.dto.NoteDto;
import com.medilabo.front.service.FrontViewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patients")
public class PatientsController {

    private final FrontViewService viewService;

    public PatientsController(FrontViewService viewService) {
        this.viewService = viewService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("patients", viewService.listPatients());
        return "patients/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("patient", viewService.getPatient(id));
        model.addAttribute("notes", viewService.getNotesForPatient(id.longValue()));
        model.addAttribute("newNote", new NoteDto(null, id, null, null, null));
        return "patients/details";
    }

    @PostMapping("/{id}/notes")
    public String addNote(@PathVariable Long id,
                          @RequestParam String content) {
        viewService.addNote(new NoteDto(null, id, null, content, null));
        return "redirect:/patients/" + id;
    }
}