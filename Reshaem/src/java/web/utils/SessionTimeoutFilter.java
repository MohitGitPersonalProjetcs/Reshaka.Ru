package web.utils;

import entity.User;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import javax.faces.context.FacesContext;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * When the session destroyed, MySessionListener will
 * do necessary logout operations.
 * Later, at the first request of client,
 * this filter will be fired and redirect
 * the user to the appropriate timeout page
 * if the session is not valid.
 *
 * Thanks to hturksoy
 *
 */
public class SessionTimeoutFilter implements Filter {

    // This should be your default Home or Login page
    private String timeoutPage = "faces/expired.xhtml";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        // check if session is expired
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            // is session expire control required for this request?
            if (isSessionControlRequiredForThisResource(httpServletRequest)) {
                // is session invalid?
                if (isSessionInvalid(httpServletRequest)) {
                    redirect(httpServletRequest, httpServletResponse);
                    return;
                }
            }
            // Process cookies if not signed in
            if (request instanceof HttpServletRequest) {
                HttpSession session = httpServletRequest.getSession();
                if (session != null && session.isNew()) {
                    redirect(httpServletRequest, httpServletResponse);
                }
                if (SessionListener.getSessionAttribute(session, "user") == null) {
                    tryLoginViaCookies(httpServletRequest, session);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    /*
     * session shouldn't be checked for some pages.
     * For example: for timeout page..
     * Since we're redirecting to timeout page from this filter,
     * if we don't disable session control for it,
     * filter will again redirect to it
     * and this will be result with an infinite loop...
     */
    private boolean isSessionControlRequiredForThisResource(HttpServletRequest httpServletRequest) {
        boolean controlRequired = !"expired".equals(httpServletRequest.getParameter("status"));
        controlRequired &= !"post-expired".equals(httpServletRequest.getParameter("status"));
        controlRequired &= !httpServletRequest.getRequestURI().toString().contains(timeoutPage);
        return controlRequired;
    }

    private void redirect(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        System.err.println("Redirect!");
        String timeoutUrl = httpServletRequest.getContextPath()
                + "/" + getTimeoutPage();
        String url = URLEncoder.encode(httpServletRequest.getRequestURL().toString());
        System.out.println("Session is invalid! redirecting to timeoutpage : " + timeoutUrl + "?redirect=" + url);
        //tryLoginViaCookies(httpServletRequest, null);
        httpServletResponse.sendRedirect(timeoutUrl + "?redirect=" + url);
    }

    private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
        boolean sessionInValid = (httpServletRequest.getRequestedSessionId() != null)
                && !httpServletRequest.isRequestedSessionIdValid();

        HttpSession session = httpServletRequest.getSession();
        if (!sessionInValid && session != null) {
            sessionInValid = !SessionListener.isSessionValid(session);
        }

        return sessionInValid;

    }

    private void tryLoginViaCookies(HttpServletRequest sRequest, HttpSession session) {
        System.err.println("try login via cookies");
        if (SessionListener.getSessionAttribute(session, "user") == null) {
            Cookie[] cookies = sRequest.getCookies();
            // try to log in using cookies
            User u = SessionUtils.loginByCookies(cookies);
            if (u == null) {
                System.out.println("Login via cookies => not signed in");
                return;
            }
            if (session != null && session.isNew()) {
                // new session... not logged in!
            } else if (!SessionListener.isSessionValid(session)) {
                session = sRequest.getSession(true);
            }
            SessionListener.setSessionAttribute(session, "user", u);
            System.out.println("Login via cookies: user=" + SessionListener.getSessionAttribute(session, "user"));
        }
    }

    @Override
    public void destroy() {
    }

    public String getTimeoutPage() {
        return timeoutPage;

    }

    public void setTimeoutPage(String timeoutPage) {
        this.timeoutPage = timeoutPage;

    }
}
