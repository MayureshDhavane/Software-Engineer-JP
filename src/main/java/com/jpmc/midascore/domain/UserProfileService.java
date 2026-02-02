package com.jpmc.midascore.domain;

import java.util.List;

public class UserProfileService {

    public static class User {
        private String userId;
        private String name;
        private List<Address> addresses; // Note: This list can sometimes be null

        public User(String userId, String name, List<Address> addresses) {
            this.userId = userId;
            this.name = name;
            this.addresses = addresses;
        }

        public String getUserId() { return userId; }
        public String getName() { return name; }
        public List<Address> getAddresses() { return addresses; }
    }

    public static class Address {
        private String street;
        private String city;
        private boolean isPrimary;

        public Address(String street, String city, boolean isPrimary) {
            this.street = street;
            this.city = city;
            this.isPrimary = isPrimary;
        }

        public String getStreet() { return street; }
        public String getCity() { return city; }
        public boolean isPrimary() { return isPrimary; }
    }

    /**
     * Finds and returns the primary address for a given user.
     * Returns null if no primary address is found.
     *
     * THE BUG: This method is not safe against all possible User data states.
     */
    public Address getPrimaryAddress(User user) {
        // Adding the guard
        if (user == null || user.getAddresses() == null) {
            return null;
        }

        for (Address address : user.getAddresses()) {
            if (address.isPrimary()) {
                return address;
            }
        }
        // If no primary address was found after checking all of them
        return null;
    }
}