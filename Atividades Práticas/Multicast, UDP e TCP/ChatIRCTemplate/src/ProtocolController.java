'''
    ### QUESTÃO 1 - TCP ###
    # Autores: Juan e Alisson
    # Data de criação:      20/07/2021
    # Data de modificação:  30/07/2021
    #Descrição: Nesta atividade foi implementado um serviço de chat, que possibilita o envio de mensagens para um grupo de pessoas, utilizando mensagens Multicast, o envio de
        mensagens privadas, ou seja, mensagens enviadas individualmente para pessoas ativas, através de mensagens UDP, utilizando uma interface visual para interação (GUI). 
        Há vários tipos de mensagens que podem ser enviadas:
            - Tipo 1: A mensagem do tipo 1 é enviada via Multicast para identificar que uma operação de Join será executada, sendo utilizada quando o usuário deseja se juntar a um grupo de 
            conversação. Nesta mensagem estão presentes os seguintes campos: Tipo da mensagem (tipo 1), a origem (o nick do usuário que a enviou) e a mensagem em sí, que neste caso é 
            vazia.
            - Tipo 2: A mensagem do tipo 2 (JOINACK) é enviada via UDP como resposta para a mensagem de Join recebida. O JOINACK torna possível que as listas de usuários ativos
            permaneça atualizada. Nesta mensagem estão presentes os seguintes campos: Tipo da mensagem (tipo 2), a origem (o nick do usuário que a enviou) e a mensagem em sí, que neste caso é 
            vazia.
            -Tipo 3: A mensagem do tipo 3 (Mensagem Comum) é enviada para todos os membros presentes no grupo utilizando um IP e porta específicos. Essa mensagem é enviada para todos 
            quando um mensagem comum é enviada no grupo. Uma mensagem comum seria uma mensagem que não resulta em nenhuma ação específica, ela só é mostrada para todos do grupo.
            Nesta mensagem estão presentes os seguintes campos: Tipo da mensagem (tipo 3), a origem (o nick do usuário que a enviou) e a mensagem em sí, que neste caso é 
            a mensagem enviada pelo usuário.
            - Tipo 4: A mensagem do tipo 4 (Mensagem Individual) é enviada via UDP, utilizada para enviar mensagens para um usuários específico, que neste caso estará escutando na porta 6799.
            Nesta mensagem estão presentes os seguintes campos: Tipo da mensagem (tipo 4), a origem (o nick do usuário que a enviou) e a mensagem em sí, que neste caso é 
            a mensagem enviada para um usuário específico.
            -Tipo 5: A mensagem do tipo 5 (LEAVE) é enviada para todos do grupo identificando que o membro que a mandou, está deixando grupo de conversação, permitindo que aqueles membros ativos
            que receberam essa mensagem, o retire da lista de membros online.Nesta mensagem estão presentes os seguintes campos: Tipo da mensagem (tipo 5), a origem (o nick do usuário que a enviou) 
            e a mensagem em sí, que neste caso é vazia.
            -Tipo 6: A mensagem do tipo 6 (LIST) é enviada quando um membro deseja listar os arquivos presentes em uma pasta específica, caso ele precise verificar quais arquivos estão presentes
            antes de enviar um arquivo específico para algum outro membro.Nesta mensagem estão presentes os seguintes campos: Tipo da mensagem (tipo 6), a origem (o nick do usuário que a enviou) 
            e a mensagem em sí, que neste caso é uma string com todos os nomes de arquivos concatenados (cada nome de arquivo é separado por um '\n').
'''


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
 * @author alisson
 * @author juan

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
        this.udpSocket = new DatagramSocket(uport);

        this.onlineUsers = new HashMap<>();
        this.onlineUsers.put("Todos", group);
    }


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
                //LIST: Comando para listar arquivos de um diretório
                type = 6;
            
            }else {
                // MSG: manda mensagem para todos
                type = 3;              
            }

            message = new Message(type, this.nick, msg);
            sendMessageGroup(message);
            
        } else {
            if (msg.equals("JOINACK")) {
                // JOINAK: resposta ao join
                type = 2;
            } else {
                // MSGIDV: msg enviado 'privada'
                type = 4;
            }
            /* Cria uma nova mensagem */
            /* Envia a mensagem para um usuário específico (targetUser) */
            message = new Message(type, this.nick, msg);
            sendMessage(message, onlineUsers.get(targetUser));

        }

    }

    private void sendMessageGroup(Message msg) throws IOException {
        byte[] m = msg.getBytes();

        /* Cria o datagrama que contém a mensagem,o tamanho da mensagem, o IP do grupo que deseja envia e a porta */
        DatagramPacket messageOut = new DatagramPacket(m, m.length, group, mport);
        /* Envia em multicast */
        this.multicastSocket.send(messageOut);
    }
    
    private void sendMessage(Message msg, InetAddress target) throws IOException {
        byte[] m = msg.getBytes();
        
        /* Envia a mensagem */
        DatagramPacket messageOut = new DatagramPacket(m, m.length, target, uport);
        this.udpSocket.send(messageOut);
    }
    
    public void join() throws IOException {
        /* Envia o endereço de multicasto do grupo para que realize um JOIN */
        this.multicastSocket.joinGroup(group);
        
        /* Cria uma mensagem, enviando o tipo 1 (JOIN) e o nick de quem está enviando a mensagem */
        Byte type = 1;
        Message message = new Message(type, this.nick, "");
        
        /* Envia a mensagem para todos (em multicast) */
        this.sendMessageGroup(message);
    }
    
    public void leave() throws IOException {
        /* Método invocado caso queira sair do chat*/

        /* Crian uma mensagem que tem o tipo 5 (LEAVE) e o nick de quem sairá do chat */
        Byte type = 5;
        Message message = new Message(type, this.nick, "");


        /* Envia uma mensagem com o tipo 5 para ser retirado da lista de online */
        this.sendMessageGroup(message);
        
        /* Envia uma mensagem multicast */
        /* Chama o método leaveGroup enviando o IP de multicast utilizado para o grupo */
        this.multicastSocket.leaveGroup(group);
        close();
    }
    
    public void close() throws IOException {
        /* Fecha os sockets */
        if (udpSocket != null)
        udpSocket.close();
        if (multicastSocket != null)
            this.multicastSocket.close();
        }
        
    public void processPacket(DatagramPacket p) throws IOException {
        // todo: pegar apenaso util
        Message message = new Message(Arrays.copyOf(p.getData(), p.getLength()));
        String data = String.valueOf(p.getData());
        
        if(!nick.equals(message.getSource()) || message.getType() == 6) {
            /* Obtem o apelido de quem enviou a mensagem */
            String senderNick = message.getSource();

            
            if (message.getType() == 1 ) {
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
            } else if (message.getType() == 6) {
                /* Percorre o diretório definido anteriormente, adicionando o nome dos arquivos na variável arq (separando os nomes por \n)*/
                File files = new File(this.diretorio);
                File listaFiles[] = files.listFiles();
                File arquivos = null;
                StringBuilder arq = new StringBuilder();


                for (int i= 0; i< listaFiles.length;  i++) {
                    arquivos = listaFiles[i];
                    
                    arq.append("\n" + arquivos.getName());
                }

                Message msgArquivos = new Message(message.getType(), this.nick, String.valueOf(arq));
                this.ui.update(msgArquivos);
            }
            
            /* Atualiza UI */
            ui.update(message);
        }
        
    }

    public void receiveMulticastPacket() throws IOException {

        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

        this.multicastSocket.receive(packet);
        this.processPacket(packet);
    }

    public void receiveUdpPacket() throws IOException {

        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

        this.udpSocket.receive(packet);

        this.processPacket(packet);
    }
}
