#!/usr/bin/env python
import pika
import sys
import json

from pika import connection

def main():
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()

# Crio a lista tweet
    channel.queue_declare(queue='tweets')


    def callback(ch, method, properties, body):
            # print(" [x] %r:%r" % (method.routing_key, body))
            key = ''
            data = body.decode()
            if 'fever'in data:
                key = 'fever'
                
            elif 'smell' in data:
                key = 'smell'
            elif 'taste' in data:
                key = 'taste'
            
            # data = body.split(',')
            # print("[x] %r:%r" % (body))
            # Agora eu verifico o body retornado e insiro na fila correta


            # Parametro chave
            # routing_key = ''
    # Aqui eu vou trocar as mensagens recebidas de callback da fila tweet
            channel.exchange_declare(exchange='direct_logs', exchange_type='direct')
    # Aqui envio o que recebi da fila de tweets
            channel.basic_publish(exchange='direct_logs', routing_key=str(key), body=body)

    channel.basic_consume(
    queue='tweets', on_message_callback=callback, auto_ack=True)

    channel.start_consuming()

if __name__ == '__main__':
    main()