package it.unisannio.assd.jmsTest;

import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.artemis.jms.client.ActiveMQTopicConnectionFactory;

public class JMSSubscriber {

	public static void main(String[] args) {
		String uri = "tcp://localhost:61616";	// URI del JMS provider (indirizzo di trasporto)

		try {
			/*
			 * PRIMO PROBLEMA (1)
			 * Creare una connessione tra il client (sender o receiver) e il server (event service, JMS provider).
			 * A tal fine si sfrutta una factory specifica di ActiveMQ che consente di creare la connessione.
			 * Nel creare la factory si specifica un URI, che punta al server (indirizzo di trasporto locale, punta al JMS provider).
			 */
			TopicConnectionFactory connFactory = new ActiveMQTopicConnectionFactory(uri);	
			TopicConnection connection = connFactory.createTopicConnection();	// creo la connessione al topic
			connection.start();
			TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			
			/*
			 * Questa operazione potrebbe essere sostituita da un'operazione di lookup con cui si recupera
			 * un topic creato in precedenza.
			 * Se viene effettuata dopo il publisher è solo un aggancio al topic già creato (il topic può essere visto come un singleton).
			 */
			Topic myTopic = session.createTopic("test_topic");

			// Creo il subscriber specificando il topic
			TopicSubscriber subscriber = session.createSubscriber(myTopic);
			// Recupero il messaggio tramite la primitiva bloccante receive
			TextMessage msg = (TextMessage)subscriber.receive();
			System.out.println(msg.getText());
			// Termino la sessione
			session.close();
			// Chiudo la connessione
			connection.close();
			
		} catch(Exception e) {
			System.err.println("Error " + e);
		}

	}

}
