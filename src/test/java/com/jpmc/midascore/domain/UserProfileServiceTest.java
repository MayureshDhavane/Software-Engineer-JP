package com.jpmc.midascore.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserProfileServiceTest {

    private final UserProfileService service = new UserProfileService();

    @Test
    public void testGetPrimaryAddress_HappyPath() {
        // A user with a valid list of addresses
        List<UserProfileService.Address> addresses = Arrays.asList(
                new UserProfileService.Address("123 Main St", "Anytown", false),
                new UserProfileService.Address("456 Primary Ave", "Metropolis", true),
                new UserProfileService.Address("789 Secondary Rd", "Gotham", false)
        );
        UserProfileService.User user = new UserProfileService.User("U001", "Sai", addresses);

        UserProfileService.Address primary = service.getPrimaryAddress(user);

        assertNotNull(primary);
        assertEquals("456 Primary Ave", primary.getStreet());
    }

    @Test
    public void testGetPrimaryAddress_WithEmptyAddressList() {
        // A user who has an address list, but it's empty
        UserProfileService.User user = new UserProfileService.User("U002", "Gopi", new ArrayList<>());

        // This should not crash, it should just return null
        assertNull(service.getPrimaryAddress(user));
    }

    @Test
    public void testGetPrimaryAddress_WithNullAddressList() {
        // A user whose address list is NULL (e.g., new user, data not loaded yet)
        UserProfileService.User user = new UserProfileService.User("U003", "Vijay", null);

        // This should not crash; method should handle null and return null
        assertNull(service.getPrimaryAddress(user),
                "Method should handle a null address list gracefully and return null.");
    }
}