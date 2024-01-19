package it.unisannio.assd.jmsTest;

/*
 * Applicazione distribuita con architettura produttore consumatore.
 * Modello di comunicazione punto punto.
 * Variante con coda e ricezione bloccante.
 * 
 * NOTA CHE L'ORDINE DI AVVIO NON HA IMPORTANZA:
 * Se si avvia prima il ricevente, avendo usato un'operazione di ricezione bloccante il ricevente si sospender‡.
 * Se si avvia prima il mittente, il messaggio inviato sar‡ memorizzato fino a consegna dal gestore di code.
 */

import javax.jms.*;
import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory; // Factory specifica di ActiveMQ

/*
 * PRIMO PROBLEMA (1)
 * Creare una connessione tra il client (sender o receiver) e il server (gestore di code, JMS provider).
 * A tal fine si sfrutta una factory specifica di ActiveMQ che consente di creare la connessione.
 * Nel creare la factory si specifica un URI, che punta al server (indirizzo di trasporto locale, punta al JMS provider).
 * 
 */

public class JMSReceiver {
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
			  * Session.AUTO_ACKNOWLEDGE --> Consente di specificare quale meccanismo di riscontro si vuole usare, in questo caso ogni messaggio ricevuto dal JMS provider o dal consumatore finale sar‡ riscontrato in maniera automatica.
			  * AUTO_ACKNOWLEDGE, per cui ogni messaggio ricevuto dal broker o JMS provider o dal consumatore finale sar√† riscontrato in maniera automatica.
			  */
		     
		     QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		     
		     /*
		      * Questa riga √® relativa alla creazione della coda che potrebbe essere implemnetata in maniera diversa.
		      * Stiamo creando esplicitamente code e topic ma quaesto non √® necessario farlo perch√® si potrebbero
		      * utilizzare code o topic creati in precedenza attraverso un'operazione di lookup.
		      */
		     
		     /*
		      * Viene creata la coda in maniera esplicita.
		      * Questo non Ë necessario perchÈ possono gi‡ esistere delle code/topic create in precedenza e usarli tramite una lookup (sono risorse come lo sono gli oggetti remoti in Java RMI)
		      */
		     
		     // the code below could be substituted by a lookup operation in a naming service 
		     Queue myQueue = session.createQueue("hello");
		     
		     /*
		      * Nella seguente riga creiamo il ricevente.
		      */
		     QueueReceiver receiver = session.createReceiver(myQueue);
		     connection.start();
		     
		     /*
		      * Viene invocata l'operazione di ricezione.
		      * E' un'operazione bloccante.
		      */
		     TextMessage msg = (TextMessage)receiver.receive();
		     System.out.println(msg.getText());
		     
		     /*
		      * Chiusura sessione e connessione
		      */
		     session.close();
		     connection.close();
		   } catch(JMSException e) { 
			   System.err.println("Error " + e);
		   } 
	   }
}