package org.example;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class JavaMailUtil {
    public static void sendMail(String recepient, String EmailSubject, String EmailMessage) throws Exception {

        //mail.smtp.auth
        //mail.smtp.starttls.enable
        //mail.smtp.host - smtp.gmail.com
        //mail.smtp.port -587
        Properties prop = new Properties();//key and value

        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");

        //email address and password of my account
        String myAccEmail = "timetickershop@gmail.com";
        String myPsw = "yqjjbtzchrrnljgj";

        //login with my email address
        Session sess = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccEmail, myPsw);
            }
        });

        Message message = prepareMessage(sess, myAccEmail, recepient, EmailSubject, EmailMessage);

        //send email
        Transport.send(message);
    }

    //prepare message that you want to send
    private static Message prepareMessage(Session sess, String myAccEmail, String recepient, String EmailSubject, String EmailMessage) {
        try {
            Message message = new MimeMessage(sess);
            //provide from address
            message.setFrom(new InternetAddress(myAccEmail));
            //provide recepient address
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
            //set the subject
            message.setSubject(EmailSubject);
            //meesage body
            message.setText(EmailMessage);

            return message;
        } catch (Exception ex) {
            Logger.getLogger(JavaMailUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
