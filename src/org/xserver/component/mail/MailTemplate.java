package org.xserver.component.mail;

import java.io.File;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.xserver.common.util.StringUtil;
import org.xserver.component.spring.SpringUtil;

/**
 * The <code>MailTemplate</code> aim at reducing the difficulty when using
 * JavaMail.When use <code>MailTemplate</code> most should configure properties
 * in <code>classpath:mailContext.xml</code>.
 * 
 * @author postonzhang
 * 
 */
public class MailTemplate {

	private JavaMailSender mailSender;

	private String fromAddress;

	private String defaultEncoding = "GBK";

	private static final Logger log = LoggerFactory
			.getLogger(MailTemplate.class);

	public MailTemplate(String fromAddress, JavaMailSender mailSender) {
		this.fromAddress = fromAddress;
		this.mailSender = mailSender;
	}

	/**
	 * The root Mail send method
	 * 
	 * @param to
	 *            the recipients
	 * @param subject
	 *            the mail subject
	 * @param text
	 *            the mail context
	 * @param multipart
	 *            is multipart
	 * @param html
	 *            the mail is html
	 * @param attachments
	 *            the mail file attachments
	 */
	private void send(String[] to, String subject, String text,
			boolean multipart, boolean html, File... attachments) {
		MimeMessage message = mailSender.createMimeMessage();

		try {
			if (multipart) {
				MimeMessageHelper mmh = new MimeMessageHelper(message, true,
						defaultEncoding);
				mmh.setFrom(fromAddress);
				mmh.setTo(to);
				mmh.setSubject(subject);
				mmh.setText(text, html);

				if (attachments != null) {
					for (File file : attachments) {
						mmh.addInline(file.getName(), file);
					}
				}

				mailSender.send(message);
			} else {
				SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
				simpleMailMessage.setFrom(fromAddress);
				simpleMailMessage.setTo(to);
				simpleMailMessage.setSubject(subject);
				simpleMailMessage.setText(text);

				mailSender.send(simpleMailMessage);
			}
		} catch (Exception e) {
			log.error("send email to [" + StringUtil.toString(to) + "] error",
					e);
		}
	}

	/**
	 * Mail to specialized person just with plain text.
	 * 
	 * @param to
	 * @param subject
	 * @param text
	 */
	public void send(String[] to, String subject, String text) {
		send(to, subject, text, false, false);
	}

	/**
	 * Mail to single person just with plain text.
	 * 
	 * @param to
	 * @param subject
	 * @param text
	 */
	public void send(String to, String subject, String text) {
		send(new String[] { to }, subject, text);
	}

	public void sendHtml(String to, String subject, String text) {
		sendHtml(new String[] { to }, subject, text);
	}

	public void sendHtml(String[] to, String subject, String text) {
		send(to, subject, text, true, true);
	}

	/**
	 * Mail to specialized person with attachments.
	 * 
	 * @param to
	 * @param subject
	 * @param text
	 * @param attachments
	 */
	public void send(String[] to, String subject, String text,
			File... attachments) {
		send(to, subject, text, true, true, attachments);
	}

	/**
	 * Send E-mail to single person with attachments.
	 * 
	 * @param to
	 * @param subject
	 * @param text
	 * @param attachments
	 */
	public void send(String to, String subject, String text,
			File... attachments) {
		send(new String[] { to }, subject, text, attachments);
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public static MailTemplate getMailTemplate() {
		return (MailTemplate) SpringUtil.getBean("mailTemplate");
	}

}
