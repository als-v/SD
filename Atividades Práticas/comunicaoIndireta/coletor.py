#!/usr/bin/env python
import pika
import sys
import csv
import json

from pika import connection
# Aqui eu vou pegar os tweets e adicionar em uma fila (tweets), para que o
# classificador possa usar para inserar nas outras filas

def main():
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()

    # with open('covid19_tweets.csv', 'r', encoding='utf8') as database:
    #     tweetsDatabase = database.readline()
    #     for linha in tweetsDatabase:
    #         result = tweetsDatabase.split(',')
    #         print(result)

    with open('covid19_tweets.csv', newline='', errors="ignore") as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        for row in spamreader:
            data = ','.join(row)
            print(data)
            print("\n\n\n\n\n\n", data)
            # Aqui eu vou trocar as mensagens recebidas de callback da fila tweet
            channel.exchange_declare(exchange='tweets', exchange_type='direct')
            # Aqui envio o que recebi da fila de tweets
            channel.basic_publish(exchange='', routing_key='tweets', body= data)

    connection.close()

if __name__ == '__main__':
    main()