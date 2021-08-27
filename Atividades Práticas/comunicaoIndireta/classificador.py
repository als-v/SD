### Comunicação Indireta ###
# Autores: Juan e Alisson
# Data de criação: 19/08/21
# Data de modificação: 19/08/21
# O classificador classificará os tweets baseados nas palavras: fever, smell, taste. Enviando pora uma das filas dos tópicos anteriores.
import pika
import sys

from pika import connection
from pika.spec import Channel


def main():
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()

    # Crio a lista tweet
    channel.queue_declare(queue='tweets')


    def callback(ch, method, properties, body):
            key = ''
            data = body.decode()

            # Verifico quais palavras pertencem ao tweet
            if 'fever'in data:
                key = 'fever'
                
            elif 'smell' in data:
                key = 'smell'
            elif 'taste' in data:
                key = 'taste'
            
            # Fila que será utilizada para troca de mensagens entre o classificador e cliente
            channel.exchange_declare(exchange='direct_logs', exchange_type='direct')
            # Envio para a fila de tópico correspondente ao tweet
            channel.basic_publish(exchange='direct_logs', routing_key=str(key), body=body)
    channel.basic_consume(
    queue='tweets', on_message_callback=callback, auto_ack=True)

    channel.start_consuming()

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        print("Encerrado")