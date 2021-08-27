#!/usr/bin/env python
import pika
import sys
import csv

from pika import connection
# No coletor os tweets serão obtidos do arquivo covid19_tweets.csv e adicionados em uma fila (tweets), para que o
# classificador possa utilizar para inserir na fila específica de um tópico

def main():
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()

    arq = open('covid19_tweets.csv', errors="ignore")
    tweetData = csv.DictReader(arq)

    for data in tweetData:
        user = "UserName: "+data["user_name"]
        twt = "Tweet: " + data["text"]
        userTwt = user+"\n"+twt+"\n"
        print(userTwt, "\n\n\n\n")
         # Aqui eu vou trocar as mensagens recebidas de callback da fila tweet
        channel.exchange_declare(exchange='tweets', exchange_type='direct')
        # Aqui envio o que recebi da fila de tweets
        channel.basic_publish(exchange='', routing_key='tweets', body= userTwt)



    connection.close()

if __name__ == '__main__':
    main()