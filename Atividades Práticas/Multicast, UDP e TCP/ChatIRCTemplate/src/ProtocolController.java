
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Properties;

/**
 * Gerencia o protocolo e o processamento das mensagens
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

    public ProtocolController(Properties properties) throws IOException {
        mport = (Integer) properties.get("multicastPort");
        uport = (Integer) properties.get("udpPort");
        group = (InetAddress) properties.get("multicastIP");
        nick = (String) properties.get("nickname");
        ui = (UIControl) properties.get("UI");

        multicastSocket = new MulticastSocket(mport);
        udpSocket = new DatagramSocket(uport);
        
        onlineUsers = new HashMap<>();
        onlineUsers.put("Todos", group);  
    }

    public void send(String targetUser, String msg) throws IOException {
        Byte type;
        Message message = null;

        System.out.println("=== SEND ===");
        System.out.println("targetUSer:");
        System.out.println(targetUser);
        System.out.println("msg:");
        System.out.println(msg);

        // verifica se a mensagem é para todos ou se é 'privada'
        if(targetUser.equals("Todos")){
            if(msg.equals("JOIN")){
                // JOIN: junta ao grupo todos
                type = 1;
            } else if(msg.equals("LEAVE")){
                // LEAVE: deixa o grupo
                type = 5;
            } else {
                // MSG: manda mensagem para todos
                type = 3;
            }

            message = new Message(type, this.nick, msg);
            sendMessageGroup(message);

        } else {
            if(msg.equals("JOINAK")){
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
        byte [] m = msg.getBytes();

        System.out.println("=== SENDMESSAGEGROUP ===");
        System.out.println("msg");
        System.out.println(msg);

        /* Envia o tamanho da mensagem */
        DatagramPacket messageOut = new DatagramPacket(m, m.length, group, mport);
        multicastSocket.send(messageOut);

        System.out.println("messageOut");
    }

    private void sendMessage(Message msg, InetAddress target) throws IOException {
        byte [] m = msg.getBytes();

        System.out.println("=== SENDMESSAGE ===");
        System.out.println("msg");
        System.out.println(msg);
        
        /* Envia a mensagem */
        DatagramPacket messageOut = new DatagramPacket(m, m.length, target, uport);
        udpSocket.send(messageOut);
        
        System.out.println("messageOut");
        System.out.println(messageOut);
        
    }
    
    public void join() throws IOException {
        System.out.println("=== JOIN ===");
        multicastSocket.joinGroup(group);

        Byte type = 1;
        Message message = new Message(type, this.nick, "");

        this.sendMessageGroup(message);
    }
    
    public void leave() throws IOException {
        System.out.println("=== LEAVE ===");
        multicastSocket.leaveGroup(group);
        
        Byte type = 5;
        Message message = new Message(type, this.nick, "");
        this.sendMessageGroup(message);
        
        multicastSocket.leaveGroup(group);
        close();
    }
    
    public void close() throws IOException {
        if (udpSocket != null) udpSocket.close();
        if (multicastSocket != null) multicastSocket.close();
    }

    public void processPacket(DatagramPacket p) throws IOException {
        Message message = new Message(p.getData());

        /* Obtem o apelido de quem enviou a mensagem */
        String senderNick = message.getSource();   

        System.out.println("Mensagem: ");
        System.out.println(message);
        System.out.println("DatagramPacket: ");
        System.out.println(p);

        if (message.getType() == 1) {
            if(nick.equals(senderNick) == false) {
                /* Salva o apelido e endereço na lista de usuários ativos */
                onlineUsers.put(senderNick, p.getAddress());
                /* Envia JOINACK */
                send(senderNick, "JOINACK");
            }
        } else if (message.getType() == 2) {
            /* Salva o apelido e endereço na lista de suários ativos */
            onlineUsers.put(senderNick, p.getAddress());
        } else if (message.getType() == 5) {
            /* remove o apelido e endereço da lista de suários ativos */
            onlineUsers.remove(senderNick);
        }

        /* Atualiza UI */
        ui.update(message);
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
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
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
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
        this.udpSocket.receive(packet);
        
        this.processPacket(packet);  
    }
}
