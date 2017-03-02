package fr.cfarge067.server;


import com.google.gson.Gson;
import com.sun.jersey.api.client.WebResource;
import fr.cfarge067.utilitary.CAppConstant;
import fr.univtln.projuml.clt.Users.CGroup;
import fr.univtln.projuml.clt.Users.CUser;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by bruno on 26/03/14.
 */

@ServerEndpoint("/echo")
public class CServer {

    WebResource wr = CAppConstant.webResource;
    Gson gson = new Gson();

    private static List<Session> sessions = new ArrayList<>();
    public final static String SERVER_IP;
    public final static int SERVER_PORT;

    static {
        String ip = null;
        int port = 8025;

//        try {
//            ip = System.getProperty("localhost");
//        } catch (NullPointerException e) {
//        }
//
//        try {
//            port = Integer.parseInt(System.getProperty("fr.univtln.bruno.test.simple.websocket.server.port"));
//        } catch (NullPointerException e) {
//        } catch (NumberFormatException e) {
//        }
        if (ip == null) ip = "localhost";
        SERVER_IP = ip;
        SERVER_PORT = port;
        System.out.println("Server IP:" + SERVER_IP + " Port: " + SERVER_PORT);
    }

    @OnOpen
    public void onOpen(Session session){
        System.out.println(session.getId() + " has opened a connection");
        sessions.add(session);
        try {
            session.getBasicRemote().sendText("Connection Established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void processUser(String pUser) {

        wr.path("users").type(MediaType.APPLICATION_JSON).post(gson.fromJson(pUser, CUser.class));

        for (Session s : sessions)
            try {
                s.getBasicRemote().sendText(pUser);
            } catch (IOException e) { e.printStackTrace(); }
    }

    private void processGroup(CGroup pGroup) {


    }

    private void processMessage(String pMessage, Session pSession) {

        try {
            if (gson.fromJson(pMessage, CUser.class) instanceof CUser)
                processUser(pMessage);
        }catch (Exception e) {}

        try {
            if (gson.fromJson(pMessage, CGroup.class) instanceof CGroup)
                processGroup((CGroup) gson.fromJson(pMessage, CGroup.class));
        }catch (Exception e) {}

        //////////////////////////// test envoi message
        for (Session s : sessions)
            try {
                s.getBasicRemote().sendText(pMessage);
            } catch (IOException e) { e.printStackTrace(); }

    }

    @OnMessage
    public void onMessage(String message, Session session){

        processMessage(message, session);
    }

    @OnClose
    public void onClose(Session session){
        System.out.println("Session " +session.getId()+" has ended");
    }

    public static void main(String[] args) {
        System.out.println("Server starting...");
        org.glassfish.tyrus.server.Server server =
                new org.glassfish.tyrus.server.Server(SERVER_IP, SERVER_PORT, "/", null, CServer.class);

        try {
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please press a key to stop the server.");
            reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}