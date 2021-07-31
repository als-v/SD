Compilar e executar:
	Para compilar a atividade:
	- Primeiramente é necessário acessar a pasta /src, localizada em: SD\Atividades Práticas\Multicast, UDP e TCP\ChatIRCTemplate\src
	- javac *.java
	Para executar o Chat:
	- java ChatGUI

Bibliotecas
	- DatagramSocket
	- MulticastSocket 
	- DatagramPacket
	- InnetAddress
	- HashMap
	- Properties

Exemplos de uso: 
	Exemplo de uso: Envia uma mensagem para todos membros do grupo:

  	- Após compilar e executar o ChatGUI, a primeira coisa que é possível fazer é  alterar o apelido
	que deseja utilizar, e isso pode ser feito no canto inferior esquerdo, na caixa de texto nomeada 'Apelido'

  	- Em seguida é necessário entrar no grupo, clicando no botão 'Entrar no grupo'.
	
	- Ao entrar no grupo é necessário selecionar 'Todos' para que seja possível enviar uma mensagem para todos
		membros presentes naquele grupo.
	
	- Por fim é só escrever uma mensagem na caixa de texto na parte superior e clicar no botão 'Enviar'.
	
	Um outro exemplo de uso: Uma outra funcionalidade implementada é listar arquivos de uma pasta padrão 
		(../server_files)
	
	- Após ter executado o exemplo anterior, basta enviar a mensagem 'LIST', no chat 'Todos', que listará todos os seus arquivos
		presentes na pasta padrão somente para você, caso ele queira verificar quais arquivos estão presentes, antes de enviar para
		algum outro membro.

  	