package ru.reshaka.core.email;

import java.text.MessageFormat;

/**
 *
 * @author rogvold
 */
public class MyMail {

    private static final String LETTER_TEMPLATE =
            "<html> <head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /> </head> <body><p>Здравствуйте, {0}! </p>\n"
            + "{1}\n\n"
            + "<p>--<br/>\nСпасибо, что выбрали наш сервис. Оставайтесь с нами! <a href=\"http://reshaka.ru\">Reshaka.Ru</a></p>\n<br/>\n<br/>\n"
            + "<p>--<br/>\nВы можете отключиться от рассылки или изменить список предметов в <h href=\"http://reshaka.ru/profile.xhtml\" >настройке профиля</a></p>\n<br/>\n<br/>\n"
            + "<p>--<br/>\nПожалуйста не отвечайте на это сообщение. По всем вопросам обращаться на admin@reshaka.ru</p></body></html>";
    
    private String text;
    private String recipient;
    private String userName;
    private String htmlText;
    private String subject;

    public MyMail(String subject, String text, String recipient, String userName) {
        this.subject = subject;
        this.text = text;
        this.recipient = recipient;
        this.userName = userName;
        this.htmlText = MessageFormat.format(LETTER_TEMPLATE, userName, text);
    }

    public String getRecipient() {
        return recipient;
    }

    public String getText() {
        return text;
    }

    public String getUserName() {
        return userName;
    }

    public String getHtmlText() {
        return htmlText;
    }

    public String getSubject() {
        return subject;
    }
}
