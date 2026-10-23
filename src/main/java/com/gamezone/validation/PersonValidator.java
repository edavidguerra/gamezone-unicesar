package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Person;

import java.util.List;

/**
 * Validation rules for people (clients and sellers).
 */
public class PersonValidator {

    private PersonValidator() {
    }

    /**
     * Validates the data of a person before it is registered.
     *
     * @param id person identifier, required
     * @param name full name, required
     * @param email email address, must contain an at sign and a dot
     * @param phone phone number, required
     * @throws InvalidDataException if any value is invalid
     */
    public static void validatePersonData(String id, String name, String email, String phone) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("identificador", "no puede estar vacio");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("nombre", "no puede estar vacio");
        }
        if (phone == null || phone.isBlank()) {
            throw new InvalidDataException("telefono", "no puede estar vacio");
        }
        if (email == null || email.isBlank() || !email.contains("@") || !email.contains(".")) {
            throw new InvalidDataException("correo", "debe tener un formato valido");
        }
    }

    /**
     * Validates that a searched person exists.
     *
     * @param person result of the search
     * @param personId identifier that was searched
     * @throws ResourceNotFoundException if the person is null
     */
    public static void validatePersonExists(Person person, String personId) {
        if (person == null) {
            throw new ResourceNotFoundException("persona", personId);
        }
    }

    /**
     * Validates that no registered person already uses the identifier.
     *
     * @param existing people already registered
     * @param newId identifier to register
     * @throws BusinessRuleException if the identifier is already used
     */
    public static void validateUniquePerson(List<? extends Person> existing, String newId) {
        for (Person person : existing) {
            if (person.getId().equals(newId)) {
                throw new BusinessRuleException("Ya existe una persona con el identificador " + newId);
            }
        }
    }
}