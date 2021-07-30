
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigestSpi;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.io.*;

/**
 * Gerencia o protocolo e o processamento das mensagens
 * 
 * @author rodrigo
 */
public class ProtocolController {

    private final MulticastSocket multicastSocket;
    private final DatagramSocket udpSocket;
    private final InetAddress group;
    private final Integer mport, uport;
    private final String nick;
    private final HashMap<String, InetAddress> onlineUsers;
    private final UIControl ui;
    private String diretorio = "..\\server_files";

    public ProtocolController(Properties properties) throws IOException {
        mport = (Integer) properties.get("multicastPort");
        uport = (Integer) properties.get("udpPort");
        group = (InetAddress) properties.get("multicastIP");
        nick = (String) properties.get("nickname");
        ui = (UIControl) properties.get("UI");

        this.multicastSocket = new MulticastSocket(mport);

        // System.out.println("multicastSocket");
        // System.out.println(this.multicastSocket.getInetAddress());
        // System.out.println(this.multicastSocket.getLocalPort());
        // System.out.println(this.multicastSocket.getChannel());
        // System.out.println(this.multicastSocket.getLocalAddress());
        // System.out.println(this.multicastSocket.getLocalSocketAddress());
        // System.out.println(this.multicastSocket.getRemoteSocketAddress());
        // System.out.println(this.multicastSocket.getNetworkInterface());

        this.udpSocket = new DatagramSocket(uport);

        this.onlineUsers = new HashMap<>();
        this.onlineUsers.put("Todos", group);
    }

    // public void visualizaArquivos() throws IOException{
    //     File files = new File(this.diretorio);
    //     File listaFiles[] = files.listFiles();
    //     File arquivos = null;
    //     StringBuilder arq = new StringBuilder();

    //     int i = 0;
    //     for (int j = listaFiles.length; i < j; i++) {
    //         arquivos = listaFiles[i];
    //         arq.append(arquivos.getName() + "/");
    //         System.out.println(arquivos.getName());
    //     }
    //     System.out.println("Lista com arquivos");
    //     System.out.println(arq);
    // }

    public void send(String targetUser, String msg) throws IOException {
        Byte type;
        Message message = null;

        // verifica se a mensagem é para todos ou se é 'privada'
        if (targetUser.equals("Todos")) {
            if (msg.equals("JOIN")) {
                // JOIN: junta ao grupo todos
                type = 1;
            } else if (msg.equals("LEAVE")) {
                // LEAVE: deixa o grupo
                type = 5;
            } else if (msg.equals("LIST")) {
                type = 6;
                System.out.println("6");
                //Descomentar aqui
                // File files = new File(this.diretorio);
                // File listaFiles[] = files.listFiles();
                // File arquivos = null;
                // StringBuilder arq = new StringBuilder();

                // System.out.println("LIST");
                // System.out.println(this.diretorio);
                // for (int i= 0; i< listaFiles.length;  i++) {
                //     arquivos = listaFiles[i];
                //     arq.append(arquivos.getName() + "/");
                //     System.out.println(arquivos.getName());
                // }
                // System.out.println("Lista com arquivos");
                // System.out.println(arq);
                // String msgArquivos = arq.toString();
                //Acaba aqui

                // ChatGUI mensagem = new ChatGUI();
                // mensagem.writeLocalMessage(this.nick, msgArquivos);

                // Message messageListaArquivos = new Message(type, nick, msgArquivos);
                // sendMessageGroup(messageListaArquivos);
                // msg = msgArquivos;
                // msg = "oi";

                
            }else {
                // MSG: manda mensagem para todos
                type = 3;
                System.out.println("Cheguei Mensagem Normal");
                System.out.println(msg);                
            }

            // System.out.println("Antes de enviar para o grupo");
            // System.out.println(msg);
            message = new Message(type, this.nick, msg);
            // System.out.println(message);
            sendMessageGroup(message);
            
        } else {
            if (msg.equals("JOINACK")) {
                // JOINAK: resposta ao join
                type = 2;
            } else {
                // MSGIDV: msg enviado 'privada'
                type = 4;
            }
            message = new Message(type, this.nick, msg);
            sendMessage(message, onlineUsers.get(targetUser));

        }

    }

    private void sendMessageGroup(Message msg) throws IOException {
        byte[] m = msg.getBytes();
        System.out.println("Mensagem");
        System.out.println(msg);

        /* Envia o tamanho da mensagem */
        DatagramPacket messageOut = new DatagramPacket(m, m.length, group, mport);
        // System.out.println(msg);
        this.multicastSocket.send(messageOut);
    }
    
    private void sendMessage(Message msg, InetAddress target) throws IOException {
        byte[] m = msg.getBytes();
        
        /* Envia a mensagem */
        DatagramPacket messageOut = new DatagramPacket(m, m.length, target, uport);
        this.udpSocket.send(messageOut);
    }
    
    public void join() throws IOException {
        this.multicastSocket.joinGroup(group);
        
        // System.out.println("joinGroup");
        // System.out.println(group);
        // System.out.println(this.multicastSocket.getInetAddress());
        // System.out.println(this.multicastSocket.getLocalPort());
        // System.out.println(this.multicastSocket.getChannel());
        // System.out.println(this.multicastSocket.getLocalAddress());
        // System.out.println(this.multicastSocket.getLocalSocketAddress());
        // System.out.println(this.multicastSocket.getRemoteSocketAddress());
        // System.out.println(this.multicastSocket.getNetworkInterface());

        Byte type = 1;
        Message message = new Message(type, this.nick, "");
        
        this.sendMessageGroup(message);
    }
    
    public void leave() throws IOException {
        Byte type = 5;
        Message message = new Message(type, this.nick, "");
        this.sendMessageGroup(message);
        
        this.multicastSocket.leaveGroup(group);
        close();
    }
    
    public void close() throws IOException {
        if (udpSocket != null)
        udpSocket.close();
        if (multicastSocket != null)
            this.multicastSocket.close();
        }
        
    public void processPacket(DatagramPacket p) throws IOException {
        // todo: pegar apenaso util
        Message message = new Message(Arrays.copyOf(p.getData(), p.getLength()));
        String data = String.valueOf(p.getData());
        System.out.println("LEN");
        System.out.println(p.getLength());
        System.out.println(message);
        System.out.println(data);


        // Message message = new Message(p.getData());
        
        if(!nick.equals(message.getSource()) || message.getType() == 6) {
            /* Obtem o apelido de quem enviou a mensagem */
            String senderNick = message.getSource();
            System.out.println("Processamento pacotes: ");
            
            if (message.getType() == 1 ) {
                if(nick.equals(senderNick) == false) {
                    /* Salva o apelido e endereço na lista de usuários ativos */
                    this.onlineUsers.put(senderNick, p.getAddress());
                    System.out.println("JOINACK ENVIADO");
                    /* Envia JOINACK */
                    send(senderNick, "JOINACK");
                }
            } else if (message.getType() == 2) {
                /* Salva o apelido e endereço na lista de suários ativos */
                this.onlineUsers.put(senderNick, p.getAddress());
            } else if (message.getType() == 5) {
                /* remove o apelido e endereço da lista de suários ativos */
                this.onlineUsers.remove(senderNick);
            } else if (message.getType() == 6) {
                // if (data.equals("$in")) {
                File files = new File(this.diretorio);
                File listaFiles[] = files.listFiles();
                File arquivos = null;
                StringBuilder arq = new StringBuilder();

                System.out.println("LIST");
                System.out.println(this.diretorio);
                // arq.append("\n");
                for (int i= 0; i< listaFiles.length;  i++) {
                    arquivos = listaFiles[i];
                    
                    arq.append("\n" + arquivos.getName());
                    System.out.println(arquivos.getName());
                }
                System.out.println("Lista com arquivos");
                System.out.println(arq);
                Message msgArquivos = new Message(message.getType(), this.nick, String.valueOf(arq));
                System.out.println(msgArquivos);
                this.ui.update(msgArquivos);

            }
            
            /* Atualiza UI */
            ui.update(message);
        }
        
    }

    public void receiveMulticastPacket() throws IOException {
        // /* Recebe a segunda mensagem */
        // byte[] buffer = new byte[1];
        // DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
        // multicastSocket.receive(messageIn);

        // byte[] msgBytes = messageIn.getData();
        // String msgString = new String(msgBytes);
        // int tamanho = Integer.valueOf(msgString.trim());

        // /* Recebe a terceira mensagem */
        // buffer = new byte[tamanho];
        // messageIn = new DatagramPacket(buffer, buffer.length);
        // multicastSocket.receive(messageIn);
        System.out.println("receive....");
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
        System.out.println("packet multiocast");
        System.out.println(packet.getData());
        this.multicastSocket.receive(packet);
        this.processPacket(packet);
    }

    public void receiveUdpPacket() throws IOException {
        // /* Recebe a segunda mensagem */
        // byte[] buffer = new byte[1];
        // DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
        // multicastSocket.receive(messageIn);

        // byte[] msgBytes = messageIn.getData();
        // String msgString = new String(msgBytes);
        // int tamanho = Integer.valueOf(msgString.trim());

        // /* Recebe a terceira mensagem */
        // buffer = new byte[tamanho];
        // messageIn = new DatagramPacket(buffer, buffer.length);
        // multicastSocket.receive(messageIn);
        System.out.println("receive udp....");
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
        System.out.println("receive udp packet");
        System.out.println(packet.getData());
        this.udpSocket.receive(packet);

        this.processPacket(packet);
    }
}
