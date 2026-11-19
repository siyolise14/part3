/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poepart3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import javax.swing.JOptionPane;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Message {
    
    ArrayList<String> sentMessages = new ArrayList<>();
    ArrayList<String> disregardedMessages = new ArrayList<>();
    ArrayList<String> storedMessages = new ArrayList<>();
    ArrayList<String> messageHashes = new ArrayList<>();
    ArrayList<String> messageIDs = new ArrayList<>();
    
    // To fix the parallel array bug, we will track the recipient for EVERY message
    ArrayList<String> allRecipients = new ArrayList<>();

    HashSet<String> usedMessageIds = new HashSet<>();
    Random random = new Random();

    public void sendingMessages() {
        String numStr = JOptionPane.showInputDialog(null, "How many messages do you want to send?", "QuickChat", JOptionPane.QUESTION_MESSAGE);
        if (numStr == null) return; // User clicked cancel
        
        int numberOfMessages;
        try {
            numberOfMessages = Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number entered.");
            return;
        }

        for (int i = 1; i <= numberOfMessages; i++) {
            String messageID = generateMessageID();
            JOptionPane.showMessageDialog(null, "Message ID generated: " + messageID);

            // Recipient Input
            String recipient = JOptionPane.showInputDialog(null, "Enter recipient cell number:");
            if (recipient == null) break;
            String recipientStatus = checkRecipientCell(recipient);
            JOptionPane.showMessageDialog(null, recipientStatus);

            if (recipientStatus.contains("incorrectly formatted")) {
                i--; // Restart this message iteration
                continue;
            }

            // Message Input
            String messageText = "";
            while (true) {
                messageText = JOptionPane.showInputDialog(null, "Enter your message (max 250 chars):");
                if (messageText == null) break; // Cancel
                
                if (messageText.length() <= 250 && !messageText.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Message ready to send.");
                    break;
                } else if (messageText.length() > 250) {
                    int excess = messageText.length() - 250;
                    JOptionPane.showMessageDialog(null, "Message exceeds 250 characters by " + excess + "; please reduce size.");
                } else {
                    JOptionPane.showMessageDialog(null, "Message cannot be empty.");
                }
            }
            if (messageText == null) break;

            String messageHash = createMessageHash(messageID, i, messageText);
            
            // Send, Disregard, Store
            String status = SentMessage(messageID, recipient, messageText, messageHash);

            // Populate the correct arrays based on the user's choice
            messageIDs.add(messageID);
            messageHashes.add(messageHash);
            allRecipients.add(recipient);

            switch (status) {
                case "Sent" -> sentMessages.add(messageText);
                case "Stored" -> storedMessages.add(messageText);
                case "Disregarded" -> disregardedMessages.add(messageText);
            }
        }
        printMessages();
    }

    public String createMessageHash(String messageID, int messageNumber, String messageText) {
        String firstTwoNumbers = messageID.substring(0, 2);
        String cleanMessage = messageText.trim();
        String[] words = cleanMessage.split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];
        String hash = firstTwoNumbers + ":" + messageNumber + ":" + firstWord + lastWord;
        return hash.toUpperCase();
    }

    public String generateMessageID() {
        String messageID;
        do {
            long number = 1000000000L + (long)(random.nextDouble() * 9000000000L);
            messageID = String.valueOf(number);
        } while (usedMessageIds.contains(messageID));
        usedMessageIds.add(messageID);
        return messageID;
    }

    public String checkRecipientCell(String cellphone) {
        if (cellphone.matches("^\\+27[0-9]{9}$") || cellphone.matches("^0[0-9]{9}$")) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code.";
        }
    }

    public String resolveStatus(int choice) {
        return switch (choice) {
            case 1 -> "Sent";
            case 2 -> "Disregarded";
            case 3 -> "Stored";
            default -> "Disregarded";
        };
    }

    public String SentMessage(String messageID, String recipient, String messageText, String hash) {
        String[] options = {"Send Message", "Disregard Message", "Store Message to send later"};
        int choice = JOptionPane.showOptionDialog(null, 
                "Choose what you want to do with this message:", 
                "Message Action",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]) + 1;

        String status = resolveStatus(choice);

        switch (status) {
            case "Sent" -> JOptionPane.showMessageDialog(null, "Message successfully sent");
            case "Disregarded" -> JOptionPane.showMessageDialog(null, "Message disregarded");
            case "Stored" -> {
                storeMessageToJSON(messageID, recipient, messageText, hash);
                JOptionPane.showMessageDialog(null, "Message successfully stored");
            }
        }
        return status;
    }

    public void storeMessageToJSON(String messageId, String recipient, String msgText, String hash) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter("messages.json", true))) {
            String jsonString = String.format("""
                                              {
                                                "MessageID": "%s",
                                                "Recipient": "%s",
                                                "Message": "%s",
                                                "MessageHash": "%s"
                                              },
                                              """,
                    messageId, recipient, msgText.replace("\"", "\\\""), hash
            );
            printWriter.print(jsonString);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while storing the message to JSON.");
        }
    }

    public void printMessages() {
        StringBuilder sb = new StringBuilder("===== All Messages =====\n\n");
        for (int i = 0; i < messageIDs.size(); i++) {
            sb.append("==============================\n");
            sb.append("Message ID: ").append(messageIDs.get(i)).append("\n");
            sb.append("Message Hash: ").append(messageHashes.get(i)).append("\n");
            sb.append("Recipient: ").append(allRecipients.get(i)).append("\n");
            
            // Determine status based on which array it is in
            String msgText = getMessageTextByIndex(i);
            String status = "Unknown";
            if (sentMessages.contains(msgText)) status = "Sent";
            else if (storedMessages.contains(msgText)) status = "Stored";
            else if (disregardedMessages.contains(msgText)) status = "Disregarded";
            
            sb.append("Message: ").append(msgText).append("\n");
            sb.append("Status: ").append(status).append("\n\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Message Details", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==========================================
    // REQUIREMENT 2: STORED MESSAGES FUNCTIONS
    // ==========================================

    public void displayStoredMessagesSenderRecipient() {
        StringBuilder sb = new StringBuilder("--- Stored Messages (Sender/Recipient) ---\n\n");
        boolean found = false;
        
        for (int i = 0; i < messageIDs.size(); i++) {
            if (storedMessages.contains(getMessageTextByIndex(i))) {
                sb.append("Sender: Developer | Recipient: ").append(allRecipients.get(i)).append("\n");
                found = true;
            }
        }
        
        if (!found) sb.append("No stored messages found.");
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    public String findLongestStoredMessage() {
        String longestMsg = "";
        for (String msg : storedMessages) {
            if (msg.length() > longestMsg.length()) {
                longestMsg = msg;
            }
        }
        return longestMsg.isEmpty() ? "No stored messages." : longestMsg;
    }

    public void searchByMessageID(String searchID) {
        for (int i = 0; i < messageIDs.size(); i++) {
            if (messageIDs.get(i).equals(searchID)) {
                String result = "Recipient: " + allRecipients.get(i) + "\n" +
                                "Message: " + getMessageTextByIndex(i);
                JOptionPane.showMessageDialog(null, result);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Message ID not found.");
    }

    public void searchByRecipient(String recipientNumber) {
        StringBuilder sb = new StringBuilder("--- Messages for Recipient: " + recipientNumber + " ---\n\n");
        boolean found = false;
        
        for (int i = 0; i < allRecipients.size(); i++) {
            if (allRecipients.get(i).equals(recipientNumber)) {
                sb.append("- ").append(getMessageTextByIndex(i)).append("\n");
                found = true;
            }
        }
        
        if (!found) sb.append("No messages found for this recipient.");
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    public void deleteByHash(String hash) {
        for (int i = 0; i < messageHashes.size(); i++) {
            if (messageHashes.get(i).equals(hash)) {
                String msgText = getMessageTextByIndex(i);
                
                // Remove from whichever list it is in
                sentMessages.remove(msgText);
                storedMessages.remove(msgText);
                disregardedMessages.remove(msgText);
                
                // Remove from parallel arrays
                messageIDs.remove(i);
                messageHashes.remove(i);
                allRecipients.remove(i);
                
                JOptionPane.showMessageDialog(null, "Message: \"" + msgText + "\" successfully deleted.");
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Hash not found. Could not delete.");
    }

    public void displayReport() {
        StringBuilder sb = new StringBuilder("========== MESSAGE REPORT ==========\n\n");
        for (int i = 0; i < messageHashes.size(); i++) {
            sb.append("Message Hash: ").append(messageHashes.get(i)).append("\n");
            sb.append("Recipient:    ").append(allRecipients.get(i)).append("\n");
            sb.append("Message:      ").append(getMessageTextByIndex(i)).append("\n");
            sb.append("------------------------------------\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Message Report", JOptionPane.INFORMATION_MESSAGE);
    }

    // Helper to get text from whichever array it lives in
    private String getMessageTextByIndex(int index) {
       
        
        if (index < sentMessages.size() && !sentMessages.isEmpty() && messageIDs.get(index).equals(messageIDs.get(index))) {
           
        }
        
     
        ArrayList<String> combined = new ArrayList<>();
        combined.addAll(sentMessages);
        combined.addAll(storedMessages);
        combined.addAll(disregardedMessages);
        
        if (index < combined.size()) {
            return combined.get(index);
        }
        
        return "Unknown Message";
    }

//Read JSON file into array
    public void readJSONFileToArray() {
        storedMessages.clear();
        try (Scanner fileScanner = new Scanner(new File("messages.json"))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.contains("\"Message\":")) {
                    String msg = line.split(": \"")[1].replace("\",", "").replace("\"", "");
                    storedMessages.add(msg);
                }
            }
            JOptionPane.showMessageDialog(null, "JSON file successfully read into Stored Messages array.");
        } catch (FileNotFoundException ex) {
            System.getLogger(Message.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }    }
}