package it.unisannio.assd.jmsTest;

import javax.jms.*;
/*
 * Factory specifica di ActiveMQ
 */
import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory;

public class JMSSender {
   public static void main(String[] args) {
	   String url = "tcp://localhost:61616";   
	   try {
		 // this code could be substituted by a lookup operation in a naming service  
		 QueueConnectionFactory connFactory = new ActiveMQQueueConnectionFactory(url);
	     
		 QueueConnection connection = connFactory.createQueueConnection();
	     QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
	     
	     /* the code below could be substituted by a lookup operation in a naming service */
	     /*
	      * Viene creata la stessa coda
	      */
	     Queue myQueue = session.createQueue("hello");
	     
	     /*
	      * Nella seguente riga creiamo il mittente
	      */
	     QueueSender sender = session.createSender(myQueue);
	     
	     /*
	      * Creiamo il messaggio a partire dalla sessione e lo spediamo.
	      */
	     Message myMsg = session.createTextMessage("Hello JMS World");
	     /*
	      * Non invochiamo start perchè non c'è delivery
	      */
	     sender.send(myMsg);
	     
	     session.close();
	     connection.close();
	   } catch(JMSException e) { 
		   System.err.println("Error " + e);
	   }
   }
	
}