package com.asledz.kancelaria_prawnicza.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class RoleTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link Role#Role()}
     *   <li>{@link Role#setId(Long)}
     *   <li>{@link Role#setName(String)}
     *   <li>{@link Role#setUsers(Collection)}
     *   <li>{@link Role#getId()}
     *   <li>{@link Role#getName()}
     *   <li>{@link Role#getUsers()}
     * </ul>
     */
    @Test
    void testConstructor() {
        Role actualRole = new Role();
        actualRole.setId(1L);
        actualRole.setName("Name");
        ArrayList<User> users = new ArrayList<>();
        actualRole.setUsers(users);
        assertEquals(1L, actualRole.getId().longValue());
        assertEquals("Name", actualRole.getName());
        assertSame(users, actualRole.getUsers());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link Role#Role(Long, String, Collection)}
     *   <li>{@link Role#setId(Long)}
     *   <li>{@link Role#setName(String)}
     *   <li>{@link Role#setUsers(Collection)}
     *   <li>{@link Role#getId()}
     *   <li>{@link Role#getName()}
     *   <li>{@link Role#getUsers()}
     * </ul>
     */
    @Test
    void testConstructor2() {
        ArrayList<User> users = new ArrayList<>();
        Role actualRole = new Role(1L, "Name", users);
        actualRole.setId(1L);
        actualRole.setName("Name");
        ArrayList<User> users2 = new ArrayList<>();
        actualRole.setUsers(users2);
        assertEquals(1L, actualRole.getId().longValue());
        assertEquals("Name", actualRole.getName());
        Collection<User> users3 = actualRole.getUsers();
        assertSame(users2, users3);
        assertEquals(users, users3);
    }
}

