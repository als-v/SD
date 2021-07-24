
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Properties;

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
    private final InetAddress ipAddr;

    public ProtocolController(Properties properties) throws IOException {
        mport = (Integer) properties.get("multicastPort");
        uport = (Integer) properties.get("udpPort");
        group = (InetAddress) properties.get("multicastIP");
        nick = (String) properties.get("nickname");
        ui = (UIControl) properties.get("UI");

        multicastSocket = new MulticastSocket(mport);
        multicastSocket.setReuseAddress(true);

        udpSocket = new DatagramSocket(uport);

        onlineUsers = new HashMap<>();
        onlineUsers.put("Todos", group);

        DatagramSocket socket = new DatagramSocket();
        socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
        ipAddr = socket.getLocalAddress();
    }

    public void send(String targetUser, String msg) throws IOException {
        Byte type;
        Message message = null;

        // verifica se a mensagem é para todos ou se é 'privada'
        if (targetUser.equals("Todos")) {
            if (msg.equals("JOIN")) {
                System.out.println("JOIN");
                // JOIN: junta ao grupo todos
                type = 1;
            } else if (msg.equals("LEAVE")) {
                // LEAVE: deixa o grupo
                type = 5;
            } else {
                // MSG: manda mensagem para todos
                type = 3;
            }
            
            message = new Message(type, this.nick, msg);
            sendMessageGroup(message);
            
        } else {
            if (msg.equals("JOINACK")) {
                System.out.println("Resposta ao joinak");
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

        /* Envia o tamanho da mensagem */
        DatagramPacket messageOut = new DatagramPacket(m, m.length, group, mport);
        this.multicastSocket.send(messageOut);
    }
    
    private void sendMessage(Message msg, InetAddress target) throws IOException {
        byte[] m = msg.getBytes();
        
        /* Envia a mensagem */
        DatagramPacket messageOut = new DatagramPacket(m, m.length, target, uport);
        udpSocket.send(messageOut);
    }
    
    public void join() throws IOException {
        this.multicastSocket.joinGroup(group);
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
        Message message = new Message(p.getData());
        System.out.println("process()");
        System.out.println(message);
        
        /* Obtem o apelido de quem enviou a mensagem */
        String senderNick = message.getSource();
        System.out.println("msg: ");
        System.out.println(message);
        
        if (message.getType() == 1) {
            if(nick.equals(senderNick) == false) {
                /* Salva o apelido e endereço na lista de usuários ativos */
                this.onlineUsers.put(senderNick, p.getAddress());
                /* Envia JOINACK */
                send(senderNick, "JOINACK");
            }
        } else if (message.getType() == 2) {
            /* Salva o apelido e endereço na lista de suários ativos */
            this.onlineUsers.put(senderNick, p.getAddress());
        } else if (message.getType() == 5) {
            /* remove o apelido e endereço da lista de suários ativos */
            this.onlineUsers.remove(senderNick);
        }

        System.out.println("ONLINE: ");
        System.out.println(this.onlineUsers);
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
