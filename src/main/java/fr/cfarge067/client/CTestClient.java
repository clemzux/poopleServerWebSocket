package fr.cfarge067.client;

import com.google.gson.Gson;
import fr.univtln.projuml.clt.Users.CUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by clemzux on 02/03/17.
 */
public class CTestClient {

    public static void main(String[] args) {
        try {
            // open websocket
            final CClient clientEndPoint = new CClient(new URI("ws://localhost:8025/echo"));

            // add listener
            clientEndPoint.addMessageHandler(new CClient.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Send empty line to stop the client.");
            String line;
            do {
                line = reader.readLine();
                if (!"".equals(line))
                    clientEndPoint.sendMessage(line);
            } while (!"".equals(line));
/////////////////////////////////////////
            CUser user = new CUser();
//            user.setId(6);
            user.setFirstName("papa");
            user.setLastName("papi");
//            user.setMail("lolmdrptdrxptdr");
            user.setPassword(123456);

            Gson gson = new Gson();
            String JSonToSend = gson.toJson(user);

            clientEndPoint.sendMessage(JSonToSend);

//            Thread.sleep(1000);
////////////////////////////////////////
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        } catch (IOException e) {}
    }
}
