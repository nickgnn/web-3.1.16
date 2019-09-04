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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MoneyTransactionServlet extends HttpServlet {

    BankClientService bankClientService = new BankClientService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(PageGenerator.getInstance().getPage("moneyTransactionPage.html", new HashMap<>()));

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String senderName = req.getParameter("senderName");
        Long money = Long.valueOf(req.getParameter("count"));
        String nameTo = req.getParameter("nameTo");
        Map<String, Object> fields = new HashMap<>();

        try {
            BankClient sender = bankClientService.getClientByName(senderName);

            if (bankClientService.sendMoneyToClient(sender, nameTo, money)) {
                fields.put("message", "The transaction was successful");
                resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", fields));
            } else {
                fields.put("message", "transaction rejected");
                resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", fields));
            }
        } catch (DBException | SQLException e) {
            e.getMessage();
        }

        resp.setStatus(HttpServletResponse.SC_OK);    }
}
