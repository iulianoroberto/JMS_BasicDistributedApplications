package it.unisannio.assd.jmsTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
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
			connection.setClientID("client1"); // AGGIUNTA (setto ID al client)
			
			TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			
			/*
			 * Questa operazione potrebbe essere sostituita da un'operazione di lookup con cui si recupera
			 * un topic creato in precedenza.
			 * Se viene effettuata dopo il publisher è solo un aggancio al topic già creato (il topic può essere visto come un singleton).
			 */
			Topic myTopic = session.createTopic("test_topic");

			// Creo il subscriber specificando il topic
			//TopicSubscriber subscriber = session.createSubscriber(myTopic); si faceva in precdenza
			// sub1 è il nome del subscriber
			TopicSubscriber subscriber = session.createDurableSubscriber(myTopic, "sub1");
			
			/*
		      * RICEZIONE NON BLOCCANTE
		      * Non usiamo un'operazione di ricezione bloccante ma definiamo una callback che viene iniettata nel
		      * ricevente perchÃ¨p il ricevente possa ricevere messaggi in modo asincrono.
		      * Classe anonima, è come se scrivessi la classe con la definizione del metodo onMessage.
		      * Non si esegue niente, si inietta la callback nel ricevente e lo si predispone
		      * alla ricezione asincrona.
		      */
		     subscriber.setMessageListener(new MessageListener() {
				/*
				 * Metodo di callback messo a disposizione dall'interfaccia MessageListener().
				 * QuÃ¬ si implemneta direttamente il metodo onMessage, creiamo un istanza della classe
				 * anonima appena creata (MessageListener) e questa viene passata al ricevente attraverso il
				 * metodo setMessageListener.
				 */
		    	 @Override
				public void onMessage(Message msg) {
					try {
						System.out.println(((TextMessage)msg).getText());
						session.close();
						connection.close();
					} catch (JMSException e) { }
				}; 
		     });
		     connection.start();	// Si abilita il delivery
		     
		     // I processi restano sospesi perché è forzata questa operazione di lettura, non per la receive.
		     System.in.read(); // Operazione di lettura forzata per evitare che il thread principale termini.
			
		} catch(Exception e) {
			System.err.println("Error " + e);
		}

	}

}
