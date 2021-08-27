### Comunicação Indireta ###
# Autores: Juan e Alisson
# Data de criação: 19/08/21
# Data de modificação: 19/08/21
# No coletor os tweets serão obtidos do arquivo covid19_tweets.csv e adicionados em uma fila (tweets), para que o
# classificador possa utilizar para inserir na fila específica de um tópico
import pika
import sys
import csv
from pika import connection

def main():
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()

    arq = open('./dataset/covid19_tweets.csv', errors="ignore")
    tweetData = csv.DictReader(arq)

    for data in tweetData:
        user = "UserName: " + data["user_name"]
        twt = "Tweet: " + data["text"]
        dadoUserTweet = user + "\n" + twt + "\n"
         # Aqui eu vou trocar as mensagens recebidas de callback da fila tweet
        channel.exchange_declare(exchange='tweets', exchange_type='direct')
        # Aqui envio o que recebi da fila de tweets
        channel.basic_publish(exchange='', routing_key='tweets', body= dadoUserTweet)



    connection.close()

if __name__ == '__main__':
    main()