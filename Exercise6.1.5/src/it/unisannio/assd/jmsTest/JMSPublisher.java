package it.unisannio.assd.jmsTest;

import javax.jms.*;	
import org.apache.activemq.artemis.jms.client.ActiveMQTopicConnectionFactory;	// Factory specifica di ActiveMQ

public class JMSPublisher {

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
			TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			
			/*
			 * Questa operazione potrebbe essere sostituita da un'operazione di lookup con cui si recupera
			 * un topic creato in precedenza
			 */
			Topic myTopic = session.createTopic("test_topic");

			// Creo il publisher
			TopicPublisher publisher = session.createPublisher(myTopic);
			// Creo il messaggio
			Message myMsg = session.createTextMessage("Hello JMS World: publisher subscriber architecture.");
			// Effettuo la pubblicazione del messaggio sul topic
			publisher.publish(myMsg);
			// Termino la sessione
			session.close();
			// Chiudo la connessione
			connection.close();
			
		} catch(JMSException e){
			System.err.println("Error " + e);
		} 

	}

}
