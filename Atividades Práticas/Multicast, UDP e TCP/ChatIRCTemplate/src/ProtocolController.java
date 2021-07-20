
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
            sendMessageGroup(message, this.nick);

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

    private void sendMessageGroup(Message msg, String nick) throws IOException {
        byte [] m = msg.getBytes();

        /* Envia o tamanho da mensagem */
        String tamMsg = Integer.toString(m.length);
        DatagramPacket messageOut = new DatagramPacket(tamMsg.getBytes(), tamMsg.getBytes().length, group, mport);
        udpSocket.send(messageOut);

        /* Envia a mensagem */
        messageOut = new DatagramPacket(m, m.length, group, mport);
        udpSocket.send(messageOut);

    }

    private void sendMessage(Message msg, InetAddress target) throws IOException {
        byte [] m = msg.getBytes();

        /* Envia o tamanho da mensagem */
        String tamMsg = Integer.toString(m.length);
        DatagramPacket messageOut = new DatagramPacket(tamMsg.getBytes(), tamMsg.getBytes().length, target, uport);
        udpSocket.send(messageOut);

        /* Envia a mensagem */
        messageOut = new DatagramPacket(m, m.length, target, uport);
        udpSocket.send(messageOut);
    }

    public void join() throws IOException {
        multicastSocket.joinGroup(group);
    }

    public void leave() throws IOException {
        multicastSocket.leaveGroup(group);
    }
    
    public void close() throws IOException {
        if (udpSocket != null) udpSocket.close();
        if (multicastSocket != null) multicastSocket.close();
    }

    public void processPacket(DatagramPacket p) throws IOException {
        Message message = new Message(p.getData());

        /* Obtem o apelido de quem enviou a mensagem */
        String senderNick = message.getSource();   

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
        /* Recebe a segunda mensagem */
        byte[] buffer = new byte[1];
        DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
        multicastSocket.receive(messageIn);

        byte[] msgBytes = messageIn.getData();
        String msgString = new String(msgBytes);
        int tamanho = Integer.valueOf(msgString.trim());

        /* Recebe a terceira mensagem */
        buffer = new byte[tamanho];
        messageIn = new DatagramPacket(buffer, buffer.length);
        multicastSocket.receive(messageIn);
    }

    public void receiveUdpPacket() throws IOException {
        /* Recebe a segunda mensagem */
        byte[] buffer = new byte[1];
        DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
        multicastSocket.receive(messageIn);

        byte[] msgBytes = messageIn.getData();
        String msgString = new String(msgBytes);
        int tamanho = Integer.valueOf(msgString.trim());

        /* Recebe a terceira mensagem */
        buffer = new byte[tamanho];
        messageIn = new DatagramPacket(buffer, buffer.length);
        multicastSocket.receive(messageIn);
    }
}
