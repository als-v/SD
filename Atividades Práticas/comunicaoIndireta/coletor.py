#!/usr/bin/env python
import pika
import sys

from pika import connection
# Aqui eu vou pegar os tweets e adicionar em uma fila (tweets), para que o
# classificador possa usar para inserar nas outras filas

def main():
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()


    # Aqui eu vou trocar as mensagens recebidas de callback da fila tweet
    channel.exchange_declare(exchange='tweets', exchange_type='direct')
    # Aqui envio o que recebi da fila de tweets
    channel.basic_publish(exchange='', routing_key='tweets', body="TWEETS")

    connection.close()

if __name__ == '__main__':
    main()