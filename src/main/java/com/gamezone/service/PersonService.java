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
        /**
     * Registers a new client and persists the updated list.
     *
     * @param id unique identifier of the client
     * @param name full name
     * @param phone contact phone number
     * @param email contact email address
     * @return the registered client
     */
    public Client registerClient(String id, String name, String phone, String email) {
        Client client = new Client(id, name, phone, email);
        clients.add(client);
        personRepository.saveClients(clients);
        return client;
    }
        /**
     * @return the complete list of registered clients
     */
    public List<Client> listClients() {
        return clients;
    }
}