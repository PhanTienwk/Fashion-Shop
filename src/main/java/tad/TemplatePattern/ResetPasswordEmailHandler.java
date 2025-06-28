package tad.TemplatePattern;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import tad.DAO.IAccountDAO;
import tad.bean.MyMailer;
import tad.entity.Account;

@Component
public class ResetPasswordEmailHandler extends AbstractEmailHandler{
	@Autowired
    IAccountDAO accountDAO;

    @Autowired
    MyMailer mailer;
    
	@Override
	protected boolean prepare(String email, HttpServletRequest request) {
		Account acc = accountDAO.findAccountByEmail(email);
        if (acc == null) return false;

        String token = UUID.randomUUID().toString();
        request.getSession().setAttribute("reset-token:" + email, token);
        request.setAttribute("resetLink", "http://localhost:9999/Web_Nong_San/guest/userrequest.htm?email=" + email + "&token=" + token);
        return true;
	}

	@Override
	protected void sendEmail(String email) throws Exception {
		String resetLink = (String) RequestContextHolder.currentRequestAttributes()
                .getAttribute("resetLink", RequestAttributes.SCOPE_REQUEST);

		String subject = "Reset password";
		String body = "Click this link to change your password: " + resetLink;

		mailer.send("TadGarden", email, subject, body);
		
	}

}
