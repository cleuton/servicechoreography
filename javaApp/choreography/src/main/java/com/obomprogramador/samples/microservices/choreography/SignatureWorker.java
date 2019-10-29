package com.obomprogramador.samples.microservices.choreography;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.apache.commons.codec.DecoderException;
import org.json.JSONException;
import org.json.JSONObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class SignatureWorker {

	private static final String TASK_QUEUE_NAME = "verifySignature";
	private static final String RESPONSE_QUEUE_NAME = "signature_response";
	private static Logger logger = Logger.getLogger(SignatureWorker.class.getName());
	
	public static void main(String[] args) throws IOException, TimeoutException {
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    final Connection connection = factory.newConnection();
	    final Channel channel = connection.createChannel();

	    channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
	    logger.info("Waiting for messages. To exit press CTRL+C");
	    
	    channel.basicQos(1);
	    
	    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	        String message = new String(delivery.getBody(), "UTF-8");
	        JSONObject my_obj = new JSONObject(message);

	        logger.info("Received '" + message + "'");
	        try {
	            doWork(my_obj);
	        } catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SignatureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
	            System.out.println("Done");
	            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
	        }
	    };
	    channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> { });	    
	}
	
	private static void doWork(JSONObject msg) throws InvalidKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, SignatureException, JSONException, IOException, DecoderException {
		String keystorePath = System.getenv("KEYSTORE_PATH") != null ? System.getenv("KEYSTORE_PATH") : "*";
		String alias = System.getenv("KEY_ALIAS") != null ? System.getenv("KEY_ALIAS") : "meucertificado"; 
		String keystorePassword = System.getenv("KEY_PSW") != null ? System.getenv("KEY_PSW") : "teste001";
		
		boolean result=VerifySignature.verify(msg.getString("hexSignature"), msg.getString("text"), keystorePath, 
				alias, keystorePassword);
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try (Connection connection = factory.newConnection();
		    Channel channel = connection.createChannel()) {
			channel.queueDeclare(RESPONSE_QUEUE_NAME, false, false, false, null);
			int resultString = result ? 1 : 0;
			String message = "{\"response\" : " + resultString + "}";
			channel.basicPublish("", RESPONSE_QUEUE_NAME, null, message.getBytes());
			logger.info("Sent '" + message + "'");
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		
	}

}
