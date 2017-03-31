package fr.cfarge067.utilitary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * Created by clemzux on 02/03/17.
 */
public class CAppConstant {
    private static CAppConstant ourInstance = new CAppConstant();

    public static CAppConstant getInstance() {
        return ourInstance;
    }

    private CAppConstant() {}

    public static Gson gson = new Gson();

    public static ObjectMapper objectMapper = new ObjectMapper();
    public static WebResource webResource;

    static {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        Client c = Client.create(cc);
//        webResource = c.resource("http://176.157.85.69:9999");
        webResource = c.resource("http://localhost:9999");
    }
}
