package Chat;

import Models.Membre;
import Repository.ServiceCommentaire;
import Repository.ServiceMembre;
import lombok.AllArgsConstructor;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable{
    public static List<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    ServiceCommentaire serviceCommentaire = new ServiceCommentaire();
    ServiceMembre serviceMembre = new ServiceMembre() ;

    public ClientHandler(Socket socket){


        try {
            if (clientHandlers.size() < 4) {

                this.socket = socket;

                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.clientUsername = bufferedReader.readLine();


                System.out.println(this.clientUsername + " s'est connecté");
                Membre membre = serviceMembre.findMember(this.clientUsername);
                if (membre == null) {
                    serviceMembre.setUsername(this.clientUsername);
                    serviceMembre.saveMember();
                }


                clientHandlers.add(this);
                broadcastMessage("SERVER : " + clientUsername + " est entré dans le chat");
            }else{
                System.out.println("Le serveur est plein ! Nombre de client Maximal atteint (4)");
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                this.bufferedWriter.write("Le serveur est plein ! Nombre de client Maximal atteint (4)");
               this.bufferedWriter.newLine();
               this.bufferedWriter.flush();
               this.socket = null;
            }
        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {


        if(this.socket!=null) {
            Membre m = serviceMembre.findMember(this.clientUsername);
            String messageFromClient;

            while (socket.isConnected()) {
                try {
                    messageFromClient = bufferedReader.readLine();

                    if (!messageFromClient.contains("/quit")) {
                        serviceCommentaire.setMembre(m);
                        serviceCommentaire.setMessage(messageFromClient);
                        serviceCommentaire.saveComments();
                    }
                    if (messageFromClient.contains("/quit")) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        this.socket = null;

                        break;
                    } else {
                        broadcastMessage(messageFromClient);
                    }

                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }
    }

    public void broadcastMessage(String messageToSend){
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER : " + clientUsername + " est sorti du chat");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try {
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
