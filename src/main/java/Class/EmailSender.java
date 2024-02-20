/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Class;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    private static final String USERNAME = "it21708480@my.sliit.lk"; // Your email address
    private static final String PASSWORD = "Shafwan@123"; // Your email password

    public static void sendEmail(String recipientEmail, String orderDetails) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com"); // Use the SMTP server appropriate for your email provider
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your Order is Ready!");
            message.setText("Dear Customer,\n\nYour order details: " + orderDetails + "\n\nYour vehicle is ready for pickup!");

            Transport.send(message);

            System.out.println("Email sent to: " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    
    public static void sendAllocationEmail(String employeeEmail, String orderDetails) {
   Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com"); // Use the SMTP server appropriate for your email provider
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(employeeEmail));
            message.setSubject("Hello you have a new Order!");
            message.setText("Dear Employee,\n\nYour new order details: " + orderDetails + "\n\n!");

            Transport.send(message);

            System.out.println("Email sent to: " + employeeEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
}

}

