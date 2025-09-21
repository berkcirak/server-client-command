package com.example;

import org.example.CommandServer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

public class ClientHandlerTest {

    @Test
    void testCdCommand(){
        CommandServer server = new CommandServer();
        String userId = "testUser";
        String userDirectory = server.getUserDirectory(userId);
        File testDirectory = new File(userDirectory, "testDirectory");
        testDirectory.mkdirs();
        assertTrue(testDirectory.exists());
        assertTrue(testDirectory.isDirectory());
    }

    @Test
    void testPwdCommand(){
        CommandServer server = new CommandServer();
        String userId = "testUser";
        String userDirectory = server.getUserDirectory(userId);
        assertEquals(userDirectory, userDirectory);
    }
    @Test
    void testUserIsolation(){
        CommandServer commandServer = new CommandServer();
        String userDirectory1 = commandServer.getUserDirectory("user1");
        String userDirectory2 = commandServer.getUserDirectory("user2");
        assertNotEquals(userDirectory1, userDirectory2);
        assertTrue(userDirectory1.contains("user1"));
        assertTrue(userDirectory2.contains("user2"));
    }



}
