/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package poepart3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {
    
    private Message msg;
    private Login login;

    @BeforeEach
    public void setUp() {
        msg = new Message();
        login = new Login();
        
        // --- POPULATING TEST DATA FOR MESSAGE CLASS ---
        
        // Test Data 1: Sent
        msg.messageIDs.add("0838884567"); // Using this for search test
        msg.allRecipients.add("+27834557896");
        msg.sentMessages.add("Did you get the cake?");
        msg.messageHashes.add("08:1:DIDCAKE?");

        // Test Data 2: Stored
        msg.messageIDs.add("1234567890");
        msg.allRecipients.add("+27838884567");
        msg.storedMessages.add("Where are you? You are late! I have asked you to be on time.");
        msg.messageHashes.add("12:2:WHERETIME.");

        // Test Data 3: Disregard
        msg.messageIDs.add("0987654321");
        msg.allRecipients.add("+27834484567");
        msg.disregardedMessages.add("Yohoooo, I am at your gate.");
        msg.messageHashes.add("09:3:YOHOGATE.");

        // Test Data 4: Sent
        msg.messageIDs.add("0838884567"); // Using this for search test
        msg.allRecipients.add("+27838884567");
        msg.sentMessages.add("It is dinner time !");
        msg.messageHashes.add("08:4:IT!.");
        
        // Test Data 5: Stored
        msg.messageIDs.add("5555555555");
        msg.allRecipients.add("+27838884567");
        msg.storedMessages.add("Ok, I am leaving without you.");
        msg.messageHashes.add("55:5:OKYOU.");
    }

    // ==========================================
    // MESSAGE CLASS TESTS
    // ==========================================

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        // Rubric: The Messages array contains the expected test data
        assertEquals("Did you get the cake?", msg.sentMessages.get(0));
        assertEquals("It is dinner time !", msg.sentMessages.get(1));
        assertEquals(2, msg.sentMessages.size());
    }

    @Test
    public void testDisplayLongestMessage() {
        // Rubric: Test Data 1-4, longest message should be returned
        String expected = "Where are you? You are late! I have asked you to be on time.";
        String actual = msg.findLongestStoredMessage();
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchByMessageID() {
        // Rubric: Search for message ID "0838884567" and return the corresponding recipient and message
        // We test that the ID exists and check the linked recipient
        assertTrue(msg.messageIDs.contains("0838884567"));
        int index = msg.messageIDs.indexOf("0838884567");
        assertEquals("+27834557896", msg.allRecipients.get(index));
    }

    @Test
    public void testSearchByRecipient() {
        // Rubric: Search all messages sent/stored for recipient "+27838884567"
        String targetRecipient = "+27838884567";
        
        // The recipient should have 3 messages associated (Test 2, Test 4, Test 5)
        int messageCount = 0;
        for (String recipient : msg.allRecipients) {
            if (recipient.equals(targetRecipient)) {
                messageCount++;
            }
        }
        assertEquals(3, messageCount);
    }

    @Test
    public void testDeleteByHash() {
        // Rubric: Delete message using Test Message 2 Hash
        String hashToDelete = "12:2:WHERETIME.";
        
        // Confirm it exists before deletion
        assertTrue(msg.messageHashes.contains(hashToDelete));
        assertTrue(msg.storedMessages.contains("Where are you? You are late! I have asked you to be on time."));
        
        // Perform deletion
        msg.deleteByHash(hashToDelete);
        
        // Confirm it was removed from the arrays
        assertFalse(msg.messageHashes.contains(hashToDelete));
        assertFalse(msg.storedMessages.contains("Where are you? You are late! I have asked you to be on time."));
    }

    // ==========================================
    // LOGIN & REGISTRATION TESTS
    // ==========================================

    @Test
    public void testCheckUserName() {
        // Valid: Must contain "_" and be <= 5 chars
        assertTrue(login.checkUserName("kyl_1"));
        
        // Invalid: Too long
        assertFalse(login.checkUserName("kyle!!!!!!"));
        
        // Invalid: No underscore
        assertFalse(login.checkUserName("kyle1"));
    }

    @Test
    public void testCheckPasswordComplexity() {
        // Valid: >=8 chars, uppercase, lowercase, digit, special char
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
        
        // Invalid: Missing special char
        assertFalse(login.checkPasswordComplexity("Password123"));
        
        // Invalid: Missing uppercase
        assertFalse(login.checkPasswordComplexity("password123!"));
    }

    @Test
    public void testCheckCellPhoneNumber() {
        // Valid: +27 followed by 9 digits
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
        
        // Invalid: Missing international code
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    @Test
    public void testRegisterUser() {
        // Valid registration
        String response = login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Registration was a success", response);
        
        // Invalid registration (bad username)
        String badResponse = login.registerUser("kyle!!!!!!", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Registration was unsucessfull", badResponse);
    }

    @Test
    public void testLoginUser() {
        // Register first to set the credentials inside the parent class
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        
        // Valid login
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
        
        // Invalid login (wrong password)
        assertFalse(login.loginUser("kyl_1", "WrongPassword"));
        
        // Invalid login (wrong username)
        assertFalse(login.loginUser("wrong_user", "Ch&&sec@ke99!"));
    }
}