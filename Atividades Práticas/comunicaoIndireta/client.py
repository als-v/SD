#!/usr/bin/env python
import pika
import sys

# Serão criadas três filas : smell, taste e fever. Representando 3 dos sintomas do COVID, dependendo 
def main():

    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()

    channel.exchange_declare(exchange='direct_logs', exchange_type='direct')

    result = channel.queue_declare(queue='', exclusive=True)
    queue_name = result.method.queue

    filas = ["taste", "smell", "fever"]
    topicos = sys.argv[1:]
    if not topicos or topicos not in filas:
        sys.stderr.write("Necessario se cadastrar em um dos topicos a seguir: [smell] [taste] [fever] \n")
        sys.exit(1)

    for topico in topicos:
        channel.queue_bind(
            exchange='direct_logs', queue=queue_name, routing_key=topico)

    print(' [*] Waiting for logs. To exit press CTRL+C')


    def callback(ch, method, properties, body):
        # print(" [x] %r:%r" % (method.routing_key, body))
        # data = json.loads(body)
        data = body.decode()
        print("[X] %r" % (method.routing_key))
        # print(data.split('--'))
        print(data)
        print("------------------------------------------------------")
        print("\n\n\n")



    channel.basic_consume(
        queue=queue_name, on_message_callback=callback, auto_ack=True)

    channel.start_consuming()

if __name__ == '__main__':
    main()
