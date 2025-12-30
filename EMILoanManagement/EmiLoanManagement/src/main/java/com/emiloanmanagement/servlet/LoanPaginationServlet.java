  package com.emiloanmanagement.servlet;

import com.emiloanmanagement.dao.LoanDao;
import com.emiloanmanagement.model.Loan;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/loans")
public class LoanPaginationServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID= 3L;
    private final ObjectMapper mapper = new ObjectMapper();
    private final LoanDao loanDao = new LoanDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int page = 1;
        int size = 5;

        if (req.getParameter("page") != null)
            page = Integer.parseInt(req.getParameter("page"));

        if (req.getParameter("size") != null)
            size = Integer.parseInt(req.getParameter("size"));

        if (page <= 0 || size <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "page and size must be positive");
            return;
        }

        List<Loan> loans = loanDao.getLoansPaginated(page, size);
        int totalRecords = loanDao.getTotalLoanCount();
        int totalPages = (int) Math.ceil((double) totalRecords / size);

        Map<String, Object> response = new HashMap<>();
        response.put("page", page);
        response.put("size", size);
        response.put("totalRecords", totalRecords);
        response.put("totalPages", totalPages);
        response.put("data", loans);

        resp.setContentType("application/json");
        resp.getWriter().write(mapper.writeValueAsString(response));
    }
}
