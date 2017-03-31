package fr.cfarge067.server;


import com.google.gson.Gson;
import com.sun.jersey.api.client.WebResource;
import fr.cfarge067.message.*;
import fr.cfarge067.utilitary.CAppConstant;
import fr.univtln.projuml.clt.Events.CMeeting;
import fr.univtln.projuml.clt.Events.COption;
import fr.univtln.projuml.clt.Events.CSurvey;
import fr.univtln.projuml.clt.Places.CBuilding;
import fr.univtln.projuml.clt.Places.CRoom;
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

    // var permetant de decoder et encoder les messages en json
    Gson gson = CAppConstant.gson;

    // liste de touts les "appareils" connectés au serveur websocket
    private static List<Session> sessions = new ArrayList<>();

    public final static String SERVER_IP;
    public final static int SERVER_PORT;

    // bloc permettant de definir les données utiles a la mise en marche du serveur
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

    // methode appelée lorsqu'un utilisateur se connecte au serveur
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

    // methode appelée quand on recoit un message, elle se charge de trouver
    // a quelle instance des classes métiers correcpond le message
    private void processMessage(String pMessage, Session pSession) {

//         on traite les utilisateurs
        try {
            if (gson.fromJson(pMessage, CUser.class) instanceof CUser)
                // on appelle le traitement des utilisateurs
                CUserMessage.processUser(pMessage);
        }catch (Exception e) {}

//        on traite les groupes
        try {
            if (gson.fromJson(pMessage, CGroup.class) instanceof CGroup)
                // on appelle le traitement des groupes
                CGoupMessage.processGroup(pMessage);
        }catch (Exception e) {}

//        on traite les salles de classes
        try {
            if (gson.fromJson(pMessage, CRoom.class) instanceof CRoom)
                // on appelle le traitement des salles
                CRoomMessage.processRoom(pMessage);
        }catch (Exception e) {}

//        on traite les batiments (buildings)
        try {
            if (gson.fromJson(pMessage, CBuilding.class) instanceof CBuilding)
                // on appelle le traitement des batiments
                CBuildingMessage.processBuilding(pMessage);
        }catch (Exception e) {}

        //        on traite les reunions (meetings)
        try {
            if (gson.fromJson(pMessage, CMeeting.class) instanceof CMeeting)
                // on appelle le traitement des reunions
                CMeetingMessage.processMeeting(pMessage);
        }catch (Exception e) {}

        //        on traite les sondages (surveys)
        try {
            if (gson.fromJson(pMessage, CSurvey.class) instanceof CSurvey)
                // on appelle le traitement des sondages
                CSurveyMessage.processSurvey(pMessage);
        }catch (Exception e) {}

        //        on traite les options (questions des sondages)
        try {
            if (gson.fromJson(pMessage, COption.class) instanceof COption)
                // on appelle le traitement des questions des sondages
                COptionMessage.processOption(pMessage);
        }catch (Exception e) {}
    }

    // methode appelée quand le serveur recoit un message
    @OnMessage
    public void onMessage(String message, Session session){

        // on traite les messages recus
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

    public static List<Session> getSessions() {
        return sessions;
    }
}