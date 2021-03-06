package bio.tech.catalog.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component("myLogoutSuccessHandler")
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException {

        final HttpSession session = request.getSession();
        if(session != null) {
            session.removeAttribute("user");
        }

        String param = request.getParameter("message");
        if (param != null) {
            response.sendRedirect("/logout.html?message=" + param);
        } else {
            response.sendRedirect("/logout.html?logSucc=true");
        }
    }
}
