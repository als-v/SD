Como executar:
    Para que seja possível executar essa aplicação é necessário instalar primeiramento o RabbitMQ.
    Abaixo está o link com as instruções de como realizar esse passo:
        - link: https://www.rabbitmq.com/download.html 

1. Iniciar o client.py passando como parâmetro as fila que deseja se inscrever:
    - python client.py smell taste

2. Executar o classificador:
    - python classificador.py

3. Executar o coletor:
    - python coletor.py

Bibliotecas:   
    CSV: Implementa classes para gravar e ler dados tabulares no formato CSV
    Pika: Implementa o protocolo AMQP 0-9-1, tentando permanecer bastante independente da biblioteca de suporte à rede subjacente.

Exemplos de uso: 
    Existem 3 possíveis fila que o cliente podem se inscrever:
        - taste
        - smell
        - fever
    As quais representam 3 dos sintomas causados pela Covid.
    
	Exemplo de uso 1: Se inscrever na fila "taste" e "smell":

    Em um terminal inicie o client, passando como parâmetro as filas que deseja se inscrever:
        - python client.py taste smell

	Em um terminal, execute o classificador:
        - python classificador.py
	
	E por fim, execute o coletor:
        - python coletor.py

	Agora, no console do client irá aparecer os tweets realcionados com os tópicos da fila,
        indicando de qual fila ele foi consumido, os usuário que realizou o tweet e o tweet em sí.
    A seguir está um exemplo de saída recebida pelo client:

        [x] 'fever'
        UserName: Junayd
        Tweet: Local media reports that a one month old baby has been admitted with high fever due to #COVID19.
        Praying for a spe… https://t.co/rkG3M1y4q4
