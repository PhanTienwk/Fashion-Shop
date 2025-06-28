package tad.TemplatePattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class AbstractEmailHandler {
	public final String execute(String email, HttpServletRequest request, RedirectAttributes reAttributes) {
        if (!validateCaptcha(request)) {
            reAttributes.addFlashAttribute("alert", 4);
            return "redirect:/guest.htm";
        }

        if (!prepare(email, request)) {
            reAttributes.addFlashAttribute("alert", 3);
            return "redirect:/guest.htm";
        }

        try {
            sendEmail(email);
            reAttributes.addFlashAttribute("alert", 2);
        } catch (Exception e) {
            e.printStackTrace();
            reAttributes.addFlashAttribute("alert", 3);
        }

        return "redirect:/guest.htm";
    }

    protected boolean validateCaptcha(HttpServletRequest request) {
        String sessionCaptcha = (String) request.getSession().getAttribute("captcha");
        String userCaptcha = request.getParameter("captcha");
        return sessionCaptcha != null && userCaptcha != null &&
               sessionCaptcha.equalsIgnoreCase(userCaptcha);
    }

    protected abstract boolean prepare(String email, HttpServletRequest request);

    protected abstract void sendEmail(String email) throws Exception;
}
