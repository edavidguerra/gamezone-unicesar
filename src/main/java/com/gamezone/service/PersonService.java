package com.gamezone.service;

import com.gamezone.model.Client;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;

import java.util.List;

/**
 * Contains the business rules related to people: registering clients
 * and listing clients and sellers.
 */
public class PersonService {
    private PersonRepository personRepository;
    private List<Client> clients;
    private List<Seller> sellers;

    /**
     * Creates the person service and loads previously stored clients
     * and sellers.
     *
     * @param personRepository repository used to persist people
     */
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
        this.clients = personRepository.loadClients();
        this.sellers = personRepository.loadSellers();
    }
}