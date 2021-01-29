package com.example.app_anima;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender extends javax.mail.Authenticator{
    private static final String MAILHOST = "smtp.naver.com";
    private String user, password, authcode;
    private Session session;

    public MailSender(String user, String password) {
        this.user = user;
        this.password = password;
        authcode = createEmailCode();

        Properties props = new Properties();
        props.put("mail.smtp.host", MAILHOST);
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.auth", "true");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String title, String authcode, String recipients) throws Exception {
        final MimeMessage message = new MimeMessage(session);
        message.setSender(new InternetAddress(user));
        message.setSubject(title);
        message.setText("인증코드 ["+authcode+"]를 입력하세요.");
        if (recipients.indexOf(',') > 0) message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
        Transport.send(message);
    }

    public String getAuthcode() {
        return authcode;
    }

    public static String createEmailCode() {
        //이메일 인증코드 생성
        String[] str = {"a", "b", "c", "d", "e", "f", "g", "h",
                "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
                "s", "t", "u", "v", "w", "x", "y", "z",
                "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String newCode = new String();

        for (int x = 0; x < 8; x++) {
            int random = (int) (Math.random() * str.length);
            newCode += str[random];
        }
        return newCode;
    }
}
