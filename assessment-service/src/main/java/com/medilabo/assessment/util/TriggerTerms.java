package com.medilabo.assessment.util;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

/**
 * Utilitaires liés aux « termes déclencheurs ».
 * <p>
 * Fournit :
 * <ul>
 *   <li>Une liste de termes à détecter</li>
 *   <li>Une normalisation de texte (suppression des accents, mise en minuscules)</li>
 *   <li>Un comptage d'occurrences exactes et non chevauchantes</li>
 * </ul>
 * Notes:
 * <ul>
 *   <li>Ne pas journaliser le texte patient en clair pour éviter toute fuite de données.</li>
 *   <li>La normalisation rend la recherche insensible à la casse et aux accents.</li>
 * </ul>
 */
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TriggerTerms {
    /**
     * Constructeur privé : classe utilitaire non instanciable.
     */
    private TriggerTerms() {}

   
    /**
     * Liste immuable des termes à rechercher après normalisation.
     * Les valeurs sont déjà en minuscules et sans diacritiques.
     */
    public static final List<String> TERMS = List.of(
        "hemoglobine a1c", "microalbumine", "taille", "poids",
        "fumeur", "fumeuse", "anormal", "cholesterol",
        "vertiges", "rechute", "reaction", "anticorps"
    );

    /**
     * Version dédupliquée des termes, préservant l'ordre d'insertion.
     */
    private static final Set<String> UNIQUE_TERMS = new LinkedHashSet<>(TERMS);

   
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");

    public static String normalize(String s) {
        if (s == null) {
            if (log.isTraceEnabled()) {
                log.trace("normalize: entrée null -> retourne chaîne vide");
            }
            return "";
        }
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        n = DIACRITICS.matcher(n).replaceAll(""); // retire les accents
        String result = n.toLowerCase(Locale.ROOT);
        if (log.isTraceEnabled()) {
            log.trace("normalize: longueur avant={} après={}", s.length(), result.length());
        }
        return result;
    }

    /**
     * Compte le nombre de termes DISTINCTS présents dans le texte donné 
     * <p>
     * Le comptage s'effectue sur le texte normalisé. Une fois qu'un terme est trouvé au moins
     * une fois, il ne contribue qu'une seule unité au total, même s'il apparaît plusieurs fois.
     * @param text texte à analyser (peut être {@code null})
     * @return nombre de termes distincts présents
     */
    public static int countTriggers(String text) {
        String norm = normalize(text);
        int count = 0;
        final int totalTerms = UNIQUE_TERMS.size();
        for (String term : UNIQUE_TERMS) {
            boolean present = norm.contains(term);
            if (log.isTraceEnabled()) {
                log.trace("countTriggers: terme='{}' present={}", term, present);
            }
            if (present) {
                count++;
                if (count == totalTerms) {
                    if (log.isDebugEnabled()) {
                        log.debug("countTriggers: {} terme(s) distinct(s) présent(s) au total (fin anticipée)", count);
                    }
                    return count; // early exit: tous les termes ont été trouvés
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("countTriggers: {} terme(s) distinct(s) présent(s) au total", count);
        }
        return count;
    }
}