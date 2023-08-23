package rw.gov.sacco.stockmis.v1.utils.types;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Mail {

    private String subject;

    private String fullNames;

    private String toEmail;

    private String template;

    private Object data;

    private Object otherData;

    private Object additionalData;

    private Object additional;

    public Mail(String subject, String fullNames, String toEmail, String template, Object data) {
        this.subject = subject;
        this.fullNames = fullNames;
        this.toEmail = toEmail;
        this.template = template;
        this.data = data;
    }

    public Mail(String subject, String fullNames, String toEmail, String template, Object data, Object otherData) {
        this.subject = subject;
        this.fullNames = fullNames;
        this.toEmail = toEmail;
        this.template = template;
        this.data = data;
        this.otherData = otherData;
    }

    public Mail(String subject, String fullNames, String toEmail, String template, Object data, Object otherData,Object additionalData) {
        this.subject = subject;
        this.fullNames = fullNames;
        this.toEmail = toEmail;
        this.template = template;
        this.data = data;
        this.otherData = otherData;
        this.additionalData = additionalData;
    }


}
