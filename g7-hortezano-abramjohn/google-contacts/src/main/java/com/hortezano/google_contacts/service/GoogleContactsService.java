package com.hortezano.google_contacts.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleContactsService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public GoogleContactsService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    private PeopleService getPeopleService(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        GoogleCredential credential = new GoogleCredential().setAccessToken(
                client.getAccessToken().getTokenValue()
        );

        return new PeopleService.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("google_contacts")
                .build();
    }

    public List<Person> listContacts(OAuth2AuthenticationToken authentication) throws IOException {
        PeopleService peopleService = getPeopleService(authentication);

        ListConnectionsResponse response = peopleService.people().connections()
                .list("people/me")
                .setPersonFields("names,emailAddresses,phoneNumbers")
                .execute();

        List<Person> connections = response.getConnections();
        return connections != null ? connections : Collections.emptyList();
    }

    public Person createContact(OAuth2AuthenticationToken authentication, Person newContact) throws IOException {
        PeopleService peopleService = getPeopleService(authentication);
        return peopleService.people().createContact(newContact).execute();
    }

    public Person updateContact(OAuth2AuthenticationToken authentication, String resourceName, Person updatedContact) throws IOException {
        PeopleService peopleService = getPeopleService(authentication);
        return peopleService.people().updateContact(resourceName, updatedContact)
                .setUpdatePersonFields("names,emailAddresses,phoneNumbers")
                .execute();
    }

    public void deleteContact(OAuth2AuthenticationToken authentication, String resourceName) throws IOException {
        PeopleService peopleService = getPeopleService(authentication);
        peopleService.people().deleteContact(resourceName).execute();
    }
}
