package com.example;

import org.example.CommandServer;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
class CommandServerTest {

    @Test
    void testUserDirectoryCreation(){
        CommandServer commandServer = new CommandServer();
        String userId = "testUser";
        String userDirectory = commandServer.getUserDirectory(userId);
        assertNotNull(userDirectory);
        assertTrue(userDirectory.contains("user_"+userId));
        File dir = new File(userDirectory);
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());
    }
    @Test
    void testMultipleUsers(){
        CommandServer server = new CommandServer();
        String userDirectory1 = server.getUserDirectory("user1");
        String userDirectory2 = server.getUserDirectory("user2");
        assertNotEquals(userDirectory1, userDirectory2);
        assertTrue(new File(userDirectory1).exists());
        assertTrue(new File(userDirectory2).exists());
    }
    
    @Test
    void testCreateNewUserId(){
        CommandServer server = new CommandServer();
        String user1 = server.createNewUserId();
        String user2 = server.createNewUserId();
        String user3 = server.createNewUserId();
        assertNotEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(user2, user3);
        assertEquals(8, user1.length());
        assertEquals(8, user2.length());
        assertEquals(8, user3.length());
    }
    @Test
    void testUpdateUserDirectory(){
        CommandServer server = new CommandServer();
        String userId = "testUser";
        String newDirectory = "/tmp/newDirectory";
        server.updateUserDirectory(userId, newDirectory);
        String userDirectory = server.getUserDirectory(userId);
        assertEquals(newDirectory, userDirectory);
    }


}
