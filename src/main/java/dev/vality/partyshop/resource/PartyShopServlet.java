package dev.vality.partyshop.resource;

import com.rbkmoney.damsel.party_shop.PartyShopServiceSrv;
import dev.vality.woody.thrift.impl.http.THServiceBuilder;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/party-shop/v1")
@RequiredArgsConstructor
public class PartyShopServlet extends GenericServlet {

    private Servlet thriftServlet;

    private final PartyShopServiceSrv.Iface partyShopHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(PartyShopServiceSrv.Iface.class, partyShopHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
