package com.mbopartners.autoconfiguration;

import org.mule.api.MuleContext;
import org.mule.transport.servlet.MuleReceiverServlet;

import javax.servlet.ServletException;

public class SpringBootMuleReceiverServlet extends MuleReceiverServlet {

    public SpringBootMuleReceiverServlet(MuleContext muleContext) {
        super.muleContext = muleContext;
    }

    @Override
    protected MuleContext setupMuleContext() throws ServletException {
        return muleContext;
    }

}
