package fr.cfarge067.message;

import com.google.gson.Gson;
import com.sun.jersey.api.client.WebResource;
import fr.cfarge067.server.CServer;
import fr.cfarge067.utilitary.CAppConstant;
import fr.univtln.projuml.clt.Events.COption;
import fr.univtln.projuml.clt.Users.CUser;

import javax.websocket.Session;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by clemzux on 01/04/17.
 */
public class COptionMessage {

    private static WebResource wr = CAppConstant.webResource;
    private static Gson gson = CAppConstant.gson;

    public static void processOption(String pOption) {

        wr.path("options").type(MediaType.APPLICATION_JSON).post(gson.fromJson(pOption, COption.class));

        for (Session s : CServer.getSessions())
            try {
                s.getBasicRemote().sendText(pOption);
            } catch (IOException e) { e.printStackTrace(); }
    }
}
