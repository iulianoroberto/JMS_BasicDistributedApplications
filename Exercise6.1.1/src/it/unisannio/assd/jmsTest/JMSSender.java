package it.unisannio.assd.jmsTest;

/*
 * Applicazione distribuita con architettura produttore consumatore.
 * Modello di comunicazione punto punto.
 * Variante con coda e ricezione bloccante.
 * 
 * NOTA CHE L'ORDINE DI AVVIO NON HA IMPORTANZA:
 * Se si avvia prima il ricevente, avendo usato un'operazione di ricezione bloccante il ricevente si sospenderà.
 * Se si avvia prima il mittente, il messaggio inviato sarà memorizzato fino a consegna dal gestore di code.
 */

import javax.jms.*;
/*
 * Factory specifica di ActiveMQ
 */
import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory;

/*
 * PRIMO PROBLEMA (1)
 * Creare una connessione tra il client (sender o receiver) e il server (gestore di code, JMS provider).
 * A tal fine si sfrutta una factory specifica di ActiveMQ che consente di creare la connessione.
 * Nel creare la factory si specifica un URI, che punta al server (indirizzo di trasporto locale, punta al JMS provider).
 * 
 */

public class JMSSender {
   public static void main(String[] args) {
	   String uri = "tcp://localhost:61616";	// URI del gestore di code
	   
	   try {
		 // (1) this code could be substituted by a lookup operation in a naming service  
		 QueueConnectionFactory connFactory = new ActiveMQQueueConnectionFactory(uri);
		 QueueConnection connection = connFactory.createQueueConnection();
		 
		 /*
		  * (2)
		  * Dalla connessione si crea una sessione, i due parametri sono:
		  * false --> Consente di specificare se si vuole una sessione transazionale.
		  * Session.AUTO_ACKNOWLEDGE --> Consente di specificare quale meccanismo di riscontro si vuole usare, in questo caso ogni messaggio ricevuto dal JMS provider o dal consumatore finale sarà riscontrato in maniera automatica.
		  */
	     QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
	     
	     /*
	      * Viene creata la coda in maniera esplicita.
	      * Questo non è necessario perché possono già esistere delle code/topic create in precedenza e usarli tramite una lookup (sono risorse come lo sono gli oggetti remoti in Java RMI)
	      */
	     // The code below could be substituted by a lookup operation in a naming service
	     Queue myQueue = session.createQueue("hello");
	     
	     /*
	      * Nella seguente riga creiamo il mittente.
	      */
	     QueueSender sender = session.createSender(myQueue);
	     
	     /*
	      * Creiamo il messaggio a partire dalla sessione e lo spediamo.
	      */
	     Message myMsg = session.createTextMessage("Hello JMS World");
	     /*
	      * Non invochiamo start perchÃ¨ non c'Ã¨ delivery.
	      */
	     sender.send(myMsg);
	     
	     session.close();
	     connection.close();
	   } catch(JMSException e) { 
		   System.err.println("Error " + e);
	   }
   }
	
}