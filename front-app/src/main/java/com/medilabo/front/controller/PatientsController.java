package com.medilabo.front.controller;

import com.medilabo.front.dto.NoteDto;
import com.medilabo.front.dto.PatientDto;
import com.medilabo.front.service.FrontViewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.LocalDate;

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

    @GetMapping("/new")
    public String createForm() {
        return "patients/create";
    }

    @PostMapping
    public String create(@RequestParam String prenom,
                         @RequestParam String nom,
                         @RequestParam String dateDeNaissance,
                         @RequestParam String genre,
                         @RequestParam(required = false) String adressePostale,
                         @RequestParam(required = false) String numeroTelephone) {

        PatientDto created = viewService.createPatient(
                new PatientDto(
                        null,
                        prenom.trim(),
                        nom.trim(),
                        LocalDate.parse(dateDeNaissance),
                        genre,
                        adressePostale == null || adressePostale.isBlank() ? null : adressePostale.trim(),
                        numeroTelephone == null || numeroTelephone.isBlank() ? null : numeroTelephone.trim()
                )
        );

        return "redirect:/patients/" + created.id();
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        var patient = viewService.getPatient(id);

        if (patient == null) {
            return "redirect:/patients";
        }

        model.addAttribute("patient", patient);
        model.addAttribute("notes", viewService.getNotesForPatient(id));
        model.addAttribute("assessment", viewService.getAssessment(id));
        return "patients/details";
    }

    @PostMapping("/{id}/notes")
    public String addNote(@PathVariable Long id,
                          @RequestParam("content") String content,
                          @AuthenticationPrincipal OidcUser oidcUser) {
        String safeContent = content == null ? "" : content.trim();
        String author = (oidcUser != null && oidcUser.getPreferredUsername() != null)
                ? oidcUser.getPreferredUsername()
                : "Utilisateur inconnu";

        if (!safeContent.isBlank()) {
            viewService.addNote(new NoteDto(null, id, author, safeContent, null));
        }

        return "redirect:/patients/" + id;
    }
}
