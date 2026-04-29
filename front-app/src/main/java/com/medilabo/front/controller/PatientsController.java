package com.medilabo.front.controller;

import com.medilabo.front.dto.NoteDto;
import com.medilabo.front.dto.PatientDto;
import com.medilabo.front.service.FrontViewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Contrôleur Web (Spring MVC) pour la gestion des vues liées aux patients.
 * <p>
 * Routes gérées:
 * - GET /patients           -> liste des patients (vue: patients/list)
 * - GET /patients/new       -> formulaire de création (vue: patients/create)
 * - POST /patients          -> création d'un patient puis redirection vers /patients/{id}
 * - GET /patients/{id}      -> détails d'un patient (vue: patients/details)
 * - POST /patients/{id}/notes -> ajout d'une note, puis redirection vers /patients/{id}
 * <p>
 * Ce contrôleur délègue les opérations métier et d'accès aux données à {@link FrontViewService}
 * et se concentre sur:
 * - la validation basique des paramètres de formulaire,
 * - l'alimentation du modèle pour les vues Thymeleaf,
 * - les redirections après succès.
 */
@Controller
@RequestMapping("/patients")
public class PatientsController {

    /**
     * Genres autorisés pour la création d'un patient.
     * <p>
     * Valeurs acceptées: "M", "F", "OTHER".
     */
    private static final Set<String> ALLOWED_GENDERS = Set.of("M", "F", "OTHER");

    /**
     * Motif de validation de numéro de téléphone tolérant:
     * - commence par un chiffre ou un signe '+'
     * - autorise chiffres, espaces, parenthèses et tirets
     * - longueur minimale globale de 7 caractères (avec séparateurs)
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+\\d][\\d\\s()\\-]{6,}$");

    private final FrontViewService viewService;

    public PatientsController(FrontViewService viewService) {
        this.viewService = viewService;
    }

    /**
     * Affiche la liste des patients.
     *
     * Modèle:
     * - "patients": liste des patients retournée par {@link FrontViewService#listPatients()}
     *
     * Vue: "patients/list"
     *
     * @param model modèle Spring MVC
     * @return le nom de la vue de liste
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("patients", viewService.listPatients());
        return "patients/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        populateCreateForm(model, "", "", "", "", "", "");
        return "patients/create";
    }

    @PostMapping
    public String create(@RequestParam String prenom,
                         @RequestParam String nom,
                         @RequestParam String dateDeNaissance,
                         @RequestParam String genre,
                         @RequestParam(required = false) String adressePostale,
                         @RequestParam(required = false) String numeroTelephone,
                         Model model) {

        String safePrenom = prenom == null ? "" : prenom.trim();
        String safeNom = nom == null ? "" : nom.trim();
        String safeGenre = genre == null ? "" : genre.trim().toUpperCase();
        String safeAdresse = adressePostale == null ? "" : adressePostale.trim();
        String safeTelephone = numeroTelephone == null ? "" : numeroTelephone.trim();

        populateCreateForm(model, safePrenom, safeNom, dateDeNaissance, safeGenre, safeAdresse, safeTelephone);

        if (safePrenom.isBlank() || safeNom.isBlank()) {
            model.addAttribute("errorMessage", "Le prenom et le nom sont obligatoires.");
            return "patients/create";
        }

        if (!ALLOWED_GENDERS.contains(safeGenre)) {
            model.addAttribute("errorMessage", "Le genre doit etre M, F ou OTHER.");
            return "patients/create";
        }

        if (!safeTelephone.isBlank() && !PHONE_PATTERN.matcher(safeTelephone).matches()) {
            model.addAttribute("errorMessage", "Le numero de telephone est invalide.");
            return "patients/create";
        }

        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(dateDeNaissance);
        } catch (DateTimeParseException e) {
            model.addAttribute("errorMessage", "La date de naissance est invalide.");
            return "patients/create";
        }

        PatientDto created;
        try {
            created = viewService.createPatient(
                    new PatientDto(
                            null,
                            safePrenom,
                            safeNom,
                            birthDate,
                            safeGenre,
                            safeAdresse.isBlank() ? null : safeAdresse,
                            safeTelephone.isBlank() ? null : safeTelephone
                    )
            );
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "patients/create";
        }

        return "redirect:/patients/" + created.id();
    }

    /**
     * Affiche la page de détails d'un patient.
     * <p>
     * Modèle:
     * - "patient": informations du patient
     * - "notes": notes associées au patient
     * - "assessment": résultat d'évaluation (ex: risque)
     * <p>
     * Si le patient est introuvable, redirige vers la liste.
     *
     * Vue: "patients/details"
     *
     * @param id    identifiant du patient
     * @param model modèle Spring MVC
     * @return nom de la vue de détails ou redirection vers "/patients"
     */
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

    /**
     * Ajoute une note à un patient, si le contenu n'est pas vide après troncature,
     * puis redirige vers la page de détails du patient.
     *
     * @param id      identifiant du patient
     * @param content contenu de la note (sera tronqué)
     * @return redirection vers "/patients/{id}"
     */
    @PostMapping("/{id}/notes")
    public String addNote(@PathVariable Long id,
                          @RequestParam("content") String content) {
        String safeContent = content == null ? "" : content.trim();

        if (!safeContent.isBlank()) {
            viewService.addNote(new NoteDto(null, id, null, safeContent, null));
        }

        return "redirect:/patients/" + id;
    }

    /**
     * Alimente le modèle avec les champs du formulaire de création pour réaffichage (succès/erreur).
     *
     * Clés du modèle:
     * - "prenom", "nom", "dateDeNaissance", "genre", "adressePostale", "numeroTelephone"
     *
     * @param model            modèle Spring MVC
     * @param prenom           valeur du prénom
     * @param nom              valeur du nom
     * @param dateDeNaissance  valeur de la date de naissance (texte)
     * @param genre            valeur du genre
     * @param adressePostale   valeur de l'adresse postale
     * @param numeroTelephone  valeur du numéro de téléphone
     */
    private void populateCreateForm(Model model,
                                    String prenom,
                                    String nom,
                                    String dateDeNaissance,
                                    String genre,
                                    String adressePostale,
                                    String numeroTelephone) {
        model.addAttribute("prenom", prenom);
        model.addAttribute("nom", nom);
        model.addAttribute("dateDeNaissance", dateDeNaissance);
        model.addAttribute("genre", genre);
        model.addAttribute("adressePostale", adressePostale);
        model.addAttribute("numeroTelephone", numeroTelephone);
    }
}
