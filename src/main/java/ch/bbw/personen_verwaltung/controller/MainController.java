package ch.bbw.personen_verwaltung.controller;

import ch.bbw.personen_verwaltung.model.ErrorObject;
import ch.bbw.personen_verwaltung.model.Person;
import ch.bbw.personen_verwaltung.repo.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {
    private final PersonRepository personRepo;

    @Autowired
    public MainController(PersonRepository personRepo) {
        this.personRepo = personRepo;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("people", this.personRepo.findAll());
        return "index";
    }

    @GetMapping("/personForm")
    public String personForm(Model model, @RequestParam Optional<Long> personId) {
        if (personId.isPresent()) {
            model.addAttribute("person", this.personRepo.findById(personId.get()).get());
            model.addAttribute("title", "Person Aktualisieren");
        } else {
            model.addAttribute("person", new Person());
            model.addAttribute("title", "Person Hinzufügen");
        }

        return "personForm";
    }

    @PostMapping("/savePerson")
    public String addPerson(Model model, @ModelAttribute Person person) throws ParseException {
        Person dbPerson = null;
        if (person.getId() != null) {
            dbPerson = this.personRepo.findById(person.getId()).orElse(null);
        }

        List<ErrorObject> errors = new ArrayList<>();
        String emailRegex = "^(.+)@(.+)$";

        if (person.getVorname().length() < 2 || person.getVorname().length() > 32) {
            errors.add(new ErrorObject("Ungültiger Vorname", "2 - 30 Zeichen"));
        }
        if (person.getNachname().length() < 2 || person.getNachname().length() > 32) {
            errors.add(new ErrorObject("Ungültiger Nachname", "2 - 30 Zeichen"));
        }
        if (!person.getEmail().matches(emailRegex)) {
            errors.add(new ErrorObject("Ungültige E-Mail", "Bitte eine korrekte E-Mail angeben."));
        }
        if (person.getGeburtsdatum().length() == 0) {
            errors.add(new ErrorObject("Kein Datum ausgewählt", "Bitte versuchen Sie es erneut!"));
        } else if (new SimpleDateFormat("dd-MM-yyyy").parse(person.getGeburtsdatum()).getYear() >= 105) {
            errors.add(new ErrorObject("Datum ist nicht gültig!", "Geburtstdatum soll vor 2005 sein."));
        }
        if (errors.size() != 0) {
            model.addAttribute("errors", errors);
            model.addAttribute("person", person);

            return "personForm";
        } else {
            if (dbPerson == null) {
                this.personRepo.save(person);
            } else {
                dbPerson.setVorname(person.getVorname());
                dbPerson.setNachname(person.getNachname());
                dbPerson.setGeburtsdatum(person.getGeburtsdatum());
                dbPerson.setGeschlecht(person.getGeschlecht());
                dbPerson.setEmail(person.getEmail());

                this.personRepo.save(dbPerson);
            }
        }

        return "redirect:/";
    }

    @GetMapping("/deletePerson")
    public String deletePerson(@RequestParam Long personId) {
        this.personRepo.deleteById(personId);

        return "redirect:/";
    }
}
