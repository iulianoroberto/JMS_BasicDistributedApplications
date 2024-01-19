package it.unisannio.assd.jmsTest;

import java.io.IOException;

import javax.jms.*;
/*
 * Factory specifica di ActiveMQ
 */
import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory;

public class JMSReceiver {
	public static void main(String[] args) {
		   String uri = "tcp://localhost:61616"; 
		   
		   try {
			// this code could be substituted by a lookup operation in a naming service  
		     QueueConnectionFactory connFactory = new ActiveMQQueueConnectionFactory(uri);
		  
		     QueueConnection connection = connFactory.createQueueConnection();
		   
		     /*
		      * Il primo parametro ci consente di specificare se vogliamo una sessione transazionale o meno,
		      * il secondo quale meccanismo di riscontro si vuole utilizzare. Nella seguente implementazione
		      * viene usato l'AUTO_ACKNOWLEDGE, per cui ogni messaggio ricevuto dal broker o JMS provider o 
		      * dal consumatore finale sarà riscontrato in maniera automatica.
		      */
		     QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		     
		     /* the code below could be substituted by a lookup operation in a naming service */
		     /*
		      * Questa riga è relativa alla creazione della coda che potrebbe essere implemnetata in maniera diversa.
		      * Stiamo creando esplicitamente code e topic ma quaesto non è necessario farlo perchè si potrebbero
		      * utilizzare code o topic creati in precedenza attraverso un'operazione di lookup.
		      */
		     Queue myQueue = session.createQueue("hello");
		     
		     /*
		      * Nella seguente riga creiamo il ricevente.
		      */
		     QueueReceiver receiver = session.createReceiver(myQueue);
		     
		     /*
		      * QUESTA E' LA PARTE DIVERSA, RICEZIONE NON BLOCCANTE
		      * Non usiamo un'operazione di ricezione bloccante ma definiamo una callback che viene iniettata nel
		      * ricevente perchèp il ricevente possa ricevere messaggi in modo asincrono.
		      * Classe anonima, � come se scrivessi la classe con la definizione del metodo onMessage.
		      * Non si esegue niente, si inietta la callback nel ricevente e lo si predispone
		      * alla ricezione asincrona.
		      */
		     receiver.setMessageListener(new MessageListener() {
				/*
				 * Metodo di callback messo a disposizione dall'interfaccia MessageListener().
				 * Quì si implemneta direttamente il metodo onMessage, creiamo un istanza della classe
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
		     
		     try {
		    	 System.in.read(); // Operazione fittizia per bloccare l'esecuzione del thread principale. Il thread principale si sospende, in attesa di un comando, per evitare che termini.
		     } catch(IOException e){ } 
		     
		   } catch(JMSException e) { 
			   System.err.println("Error " + e);
		   } 
	   }
}