package com.example.seniormanager.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class SendMail {
    public static void sendTicket(String title,  String seniorCode, String description) throws MessagingException {
        String fromEmail = "";
        String pass = "squcyynfxeibscdw";
        String toEmail = "BIT240104@st.cmcu.edu.vn"; // ae tự điền sp mail nhận

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com"); // ip gg
        prop.put("mail.smtp.port", "587"); // port gg
        prop.put("mail.smtp.auth", "true"); // authen
        prop.put("mail.smtp.starttls.enable", "true"); // mã hóa TLS
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2"); // version của TLS

        Session session = Session.getInstance(prop, new Authenticator() { // tạo phiên
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, pass);
            }
        });

        Message message = new MimeMessage(session); // tạo thư mới
        message.setFrom(new InternetAddress(fromEmail)); // from
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail)
        ); // to
        message.setSubject("[Ticket] " + title); // tiêu đề thư

        message.setText(
                "TIÊU ĐỀ:\n" + title +
                        "\n\nMÃ NHÂN VIÊN: " + seniorCode +
                        "\n\nMÔ TẢ:\n" + description
        ); // nội dung thư

        Transport.send(message); // gửi
    }
}
