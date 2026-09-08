package com.gamezone.persistence;

import com.gamezone.model.Client;
import com.gamezone.model.Seller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading and writing Client and Seller data to plain text
 * files.
 */
public class PersonRepository {
    private static final String CLIENTS_FILE = "data/clients.csv";
    private static final String SELLERS_FILE = "data/sellers.csv";

    /**
     * Saves the full list of clients to disk, overwriting the file.
     *
     * @param clients list of clients to persist
     */
    public void saveClients(List<Client> clients) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CLIENTS_FILE))) {
            for (Client client : clients) {
                writer.println(client.getId() + "|" + client.getName() + "|"
                        + client.getPhone() + "|" + client.getEmail());
            }
        } catch (IOException e) {
            System.out.println("Error saving clients: " + e.getMessage());
        }
    }
        /**
     * Loads all clients from disk.
     *
     * @return list of clients loaded from disk
     */
    public List<Client> loadClients() {
        List<Client> clients = new ArrayList<>();
        File file = new File(CLIENTS_FILE);
        if (!file.exists()) {
            return clients;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                clients.add(new Client(parts[0], parts[1], parts[2], parts[3]));
            }
        } catch (IOException e) {
            System.out.println("Error loading clients: " + e.getMessage());
        }
        return clients;
    }
        /**
     * Saves the full list of sellers to disk, overwriting the file.
     *
     * @param sellers list of sellers to persist
     */
    public void saveSellers(List<Seller> sellers) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SELLERS_FILE))) {
            for (Seller seller : sellers) {
                writer.println(seller.getId() + "|" + seller.getName() + "|"
                        + seller.getPhone() + "|" + seller.getEmployeeCode() + "|"
                        + seller.getShift());
            }
        } catch (IOException e) {
            System.out.println("Error saving sellers: " + e.getMessage());
        }
    }
        /**
     * Loads all sellers from disk.
     *
     * @return list of sellers loaded from disk
     */
    public List<Seller> loadSellers() {
        List<Seller> sellers = new ArrayList<>();
        File file = new File(SELLERS_FILE);
        if (!file.exists()) {
            return sellers;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                sellers.add(new Seller(parts[0], parts[1], parts[2], parts[3], parts[4]));
            }
        } catch (IOException e) {
            System.out.println("Error loading sellers: " + e.getMessage());
        }
        return sellers;
    }
}