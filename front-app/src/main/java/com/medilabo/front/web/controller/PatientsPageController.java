package com.medilabo.front.web.controller;

import com.medilabo.libs.client.patient.PatientApiClient;
import com.medilabo.libs.client.patient.dto.PatientDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientsPageController {

    private final PatientApiClient api;

    @GetMapping
    public String list(Model model) {
        var patients = api.list();
        model.addAttribute("patients", patients);
        return "patients/list";
    }


    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("patient", new PatientDto());
        return "patients/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("patient") PatientDto form, BindingResult br) {
        if (br.hasErrors()) return "patients/form";
        api.create(form);
        return "redirect:/patients";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("patient", api.get(id));
        return "patients/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("patient") PatientDto form, BindingResult br) {
        if (br.hasErrors()) return "patients/form";
        api.update(id, form);
        return "redirect:/patients";
    }


    @PostMapping("/{id}/partial")
    public String partialUpdate(@PathVariable Long id, @ModelAttribute("patient") PatientDto form) {
        // Construit uniquement les champs réellement fournis (non nuls / non vides) pour PATCH
        Map<String, Object> fields = new HashMap<>();
        if (form.getPrenom() != null && !form.getPrenom().isBlank()) fields.put("prenom", form.getPrenom());
        if (form.getNom() != null && !form.getNom().isBlank()) fields.put("nom", form.getNom());
        if (form.getDateDeNaissance() != null) fields.put("dateDeNaissance", form.getDateDeNaissance().toString());
        if (form.getGenre() != null) fields.put("genre", form.getGenre().name());
        if (form.getAdressePostale() != null && !form.getAdressePostale().isBlank()) fields.put("adressePostale", form.getAdressePostale());
        if (form.getNumeroTelephone() != null && !form.getNumeroTelephone().isBlank()) fields.put("numeroTelephone", form.getNumeroTelephone());

        if (!fields.isEmpty()) {
            api.patch(id, fields);
        }
        return "redirect:/patients";
    }
}