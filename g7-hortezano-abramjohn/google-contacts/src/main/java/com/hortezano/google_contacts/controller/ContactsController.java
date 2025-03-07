package com.hortezano.google_contacts.controller;

import com.google.api.services.people.v1.model.Person;
import com.hortezano.google_contacts.service.GoogleContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Controller
public class ContactsController {

    @Autowired
    private GoogleContactsService googleContactsService;

    @GetMapping("/contacts")
    public String listContacts(Model model, OAuth2AuthenticationToken authentication) {
        try {
            List<Person> contacts = googleContactsService.listContacts(authentication);
            model.addAttribute("contacts", contacts);
        } catch (IOException e) {
            model.addAttribute("error", "Failed to retrieve contacts: " + e.getMessage());
        }
        return "contacts"; // Refers to contacts.html Thymeleaf template
    }

    @PostMapping("/contacts")
    public String createContact(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                OAuth2AuthenticationToken authentication) {
        try {
            Person newContact = new Person();
            newContact.setNames(Collections.singletonList(
                    new com.google.api.services.people.v1.model.Name().setGivenName(firstName).setFamilyName(lastName)
            ));
            newContact.setEmailAddresses(Collections.singletonList(
                    new com.google.api.services.people.v1.model.EmailAddress().setValue(email)
            ));
            googleContactsService.createContact(authentication, newContact);
        } catch (IOException e) {
            // Optionally log or handle the error
        }
        return "redirect:/contacts";
    }

    @PostMapping("/contacts/update")
    public String updateContact(@RequestParam String resourceName,
                                @RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                OAuth2AuthenticationToken authentication) {
        try {
            Person updatedContact = new Person();
            updatedContact.setNames(Collections.singletonList(
                    new com.google.api.services.people.v1.model.Name().setGivenName(firstName).setFamilyName(lastName)
            ));
            updatedContact.setEmailAddresses(Collections.singletonList(
                    new com.google.api.services.people.v1.model.EmailAddress().setValue(email)
            ));
            googleContactsService.updateContact(authentication, resourceName, updatedContact);
        } catch (IOException e) {
            // Optionally handle error
        }
        return "redirect:/contacts";
    }

    @PostMapping("/contacts/delete")
    public String deleteContact(@RequestParam String resourceName,
                                OAuth2AuthenticationToken authentication) {
        try {
            googleContactsService.deleteContact(authentication, resourceName);
        } catch (IOException e) {
            // Optionally handle error
        }
        return "redirect:/contacts";
    }
}
