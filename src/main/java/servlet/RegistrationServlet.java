package servlet;

import exception.DBException;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(PageGenerator.getInstance().getPage("registrationPage.html", new HashMap<>()));

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String name = req.getParameter("name");
        String password = req.getParameter("password");
        String money = req.getParameter("money");
        Long aLong = Long.valueOf(money);
        Map<String, Object> fields = new HashMap<>();

        try {
            if (new BankClientService().addClient(new BankClient(name, password, aLong))) {
                fields.put("message", "Add client successful");
                resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", fields));
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                fields.put("message", "Client not add");
                resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", fields));
            }
        } catch (DBException e) {
            e.getMessage();
        }

    }
}
