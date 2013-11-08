package org.xserver.test;

import org.springframework.stereotype.Component;
import org.xserver.component.core.XServerHttpRequest;
import org.xserver.component.core.XServerHttpResponse;
import org.xserver.component.mail.MailTemplate;
import org.xserver.wrap.HttpInterface;

@Component
public class Mail implements HttpInterface {

	public Object send(XServerHttpRequest request, XServerHttpResponse response) {
		MailTemplate mailTemplate = MailTemplate.getMailTemplate();
		mailTemplate.send("poston1@163.com", "test", "testesteets");
		// mailTemplate
		// .send("poston1@163.com",
		// "texs",
		// "sfadfafa",
		// new File(
		// "E:\\XLServerFramework\\ServerUtil\\src\\spring\\com\\xunlei\\springutil\\MailTemplate.java"));
		return "ok";
	}
}
