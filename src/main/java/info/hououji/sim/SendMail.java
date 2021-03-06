package info.hououji.sim;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;

public class SendMail {

	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
 
	public static void main(String args[]) throws AddressException, MessagingException, IOException {
		generateAndSendEmail();
		System.out.println("\n\n ===> Your Java Program has just sent an Email successfully. Check your email..");
	}
 
	public static void generateAndSendEmail() throws AddressException, MessagingException, IOException {
 
		// Make content;
		StringBuffer content = new StringBuffer() ;
		ByteArrayOutputStream out = new ByteArrayOutputStream() ;
		IOUtils.copy(new FileInputStream("./output/choose-by-value.txt"), out) ;
		content.append(new String(out.toByteArray())) ;
		out = new ByteArrayOutputStream() ;
		IOUtils.copy(new FileInputStream("./output/choose-by-bp.txt"), out) ;
		content.append("<br><br>");
		content.append(new String(out.toByteArray())) ;
		
		// Step1
		System.out.println("\n 1st ===> setup Mail Server Properties..");
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		System.out.println("Mail Server Properties have been setup successfully..");
 
		// Step2
		System.out.println("\n\n 2nd ===> get Mail Session..");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("hououji@gmail.com"));
		generateMailMessage.setSubject("Stock Info Summary - " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
//		generateMailMessage.setContent(content.toString(), "text/html");
		MimeMultipart multipart = new MimeMultipart();
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setText(content.toString(), "utf-8", "html");
		multipart.addBodyPart(mimeBodyPart);
		generateMailMessage.setContent(multipart);
		generateMailMessage.setSentDate(new Date());
		generateMailMessage.saveChanges(); 
		
		System.out.println("Mail Session has been created successfully..");
 
		// Step3
		System.out.println("\n\n 3rd ===> Get Session and Send mail");
		Transport transport = getMailSession.getTransport("smtp");
 
		// https://support.google.com/mail/answer/185833?hl=en-GB
		// Enter your correct gmail UserID and Password
		// if you have 2FA enabled then provide App Specific Password
		transport.connect("smtp.gmail.com", "hououji2000@gmail.com", "rzvhrikthlrtfwgl");
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
	}
}
