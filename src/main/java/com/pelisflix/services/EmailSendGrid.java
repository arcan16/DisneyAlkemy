package com.pelisflix.services;
import com.pelisflix.models.UserEntity;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailSendGrid {
    @Value("${sendgrid.api-key}")
    private String api_key;

    @Value("${sendgrid.email.from}")
    private String emailFrom;

    public void mail(UserEntity user) throws IOException {
        Email from = new Email(emailFrom);
        String subject = "Pelisflix";
        Email to = new Email(user.getEmail());
        Content content = new Content("text/plain", "Bienvenido a Pelisflix, ahora podras consultar" +
                "la informacion de tus peliculas o series favoritas");
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(api_key);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }
}
