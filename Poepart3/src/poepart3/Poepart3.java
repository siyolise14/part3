/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package poepart3;

import javax.swing.JOptionPane;

public class Poepart3 {

    public static void main(String[] args) {

        Login register = new Login();
        Message msg = new Message();

        // =========================
        // REGISTRATION
        // =========================

        JOptionPane.showMessageDialog(null,
                "Welcome to QuickChat!\nPlease register to continue.");

        String name = JOptionPane.showInputDialog(
                null, "Please enter your username:");

        if (name == null) return;

        String password = JOptionPane.showInputDialog(
                null, "Please enter your password:");

        if (password == null) return;

        String cellphone = JOptionPane.showInputDialog(
                null, "Please enter your cellphone number:");

        if (cellphone == null) return;

        String registrationMessage =
                register.registerUser(name, password, cellphone);

        JOptionPane.showMessageDialog(null, registrationMessage);

        if (!register.registerStatus(name, password, cellphone)) {
            JOptionPane.showMessageDialog(null,
                    "Registration failed. Please restart the program.");
            return;
        }

        // =========================
        // LOGIN
        // =========================

        JOptionPane.showMessageDialog(null,
                "Registration successful!\nPlease login.");

        String loginUsername = JOptionPane.showInputDialog(
                null, "Enter your username:");

        if (loginUsername == null) return;

        String loginPassword = JOptionPane.showInputDialog(
                null, "Enter your password:");

        if (loginPassword == null) return;

        boolean status = register.loginUser(
                loginUsername, loginPassword);

        JOptionPane.showMessageDialog(null,
                register.returnLoginStatus(status));

        if (!status) {
            JOptionPane.showMessageDialog(null,
                    "Incorrect login details. Goodbye!");
            return;
        }

        JOptionPane.showMessageDialog(null,
                "Welcome to QuickChat!");

        // =========================
        // MAIN MENU
        // =========================

        int option = 0;

        do {

            String[] options = {
                "Send Messages",
                "Show Recently Sent Messages",
                "Quit",
                "Stored Messages"
            };

            option = JOptionPane.showOptionDialog(
                    null,
                    "Please select an option:",
                    "QuickChat Main Menu",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (option) {

                case 0 -> msg.sendingMessages();

                case 1 -> JOptionPane.showMessageDialog(null,
                            "Coming Soon.");

                case 2 -> JOptionPane.showMessageDialog(null,
                            "Goodbye! Thank you for using QuickChat.");

                case 3 -> {
                    // Load stored messages
                    msg.readJSONFileToArray();

                    int storedOpt = 0;

                    do {

                        String[] storedOptions = {
                            "Display Sender and Recipient",
                            "Display Longest Message",
                            "Search by Message ID",
                            "Search by Recipient",
                            "Delete by Message Hash",
                            "Display Full Report",
                            "Back to Main Menu"
                        };

                        storedOpt = JOptionPane.showOptionDialog(
                                null,
                                "Select an option:",
                                "Stored Messages Menu",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                storedOptions,
                                storedOptions[0]);

                        switch (storedOpt) {

                            case 0 -> msg.displayStoredMessagesSenderRecipient();

                            case 1 -> JOptionPane.showMessageDialog(null,
                                    "Longest Message:\n"
                                            + msg.findLongestStoredMessage());
                            
                            case 2 -> {
                                String searchID =
                                        JOptionPane.showInputDialog(
                                                null,
                                                "Enter Message ID:");

                                if (searchID != null) {
                                    msg.searchByMessageID(searchID);
                                }
                            }
                            
                            case 3 -> {
                                String recipient =
                                        JOptionPane.showInputDialog(
                                                null,
                                                "Enter Recipient Number:");

                                if (recipient != null) {
                                    msg.searchByRecipient(recipient);
                                }
                            }
                            
                            case 4 -> {
                                String hash =
                                        JOptionPane.showInputDialog(
                                                null,
                                                "Enter Message Hash:");

                                if (hash != null) {
                                    msg.deleteByHash(hash);
                                }
                            }
                            
                            case 5 -> msg.displayReport();

                            case 6 -> JOptionPane.showMessageDialog(null,
                                    "Returning to main menu...");
                            
                            default -> {
                            }
                        }

                    } while (storedOpt != 6 && storedOpt != -1);
                }

                default -> option = 2;
            }

        } while (option != 2 && option != -1);

    }
}
