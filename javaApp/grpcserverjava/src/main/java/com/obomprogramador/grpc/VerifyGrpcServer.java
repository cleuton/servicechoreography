package com.obomprogramador.grpc;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.RandomStringUtils;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

/**
 * This is a gRPC server which registers itself with Zookeeper.
 * Environment variables: 
 * - KEYSTORE_PATH: Path to the jks file. If "*" then search in resources (default behaviour)
 * - KEY_ALIAS: Name of the key to search in jks. Defaults to "meucertificado"
 * - KEY_PSW: Key password in jks file. Defaults to "teste001"
 * - ZOOKEEPER_ADDRESS: Address of Zookeeper server. Defaults to "localhost"
 * - ZOOKEEPER_PORT: TCP port of Zookeeper server. Defaults to 2181
 * 
 * Command line arguments: 
 * - <address> <port> <serviceName>
 * - <address>: This server address. Defaults to "localhost"
 * - <port>: This server TCP port. Defaults to 8080
 * - <serviceName>: This server service name. Defaults to "service_" plus random string
 * @author Cleuton Sampaio
 *
 */
public class VerifyGrpcServer {

	private static final Logger logger = Logger.getLogger(VerifyGrpcServer.class.getName());

	private int port;
	private Server server;
	private ApacheCuratorWrapper wrapper;
	
	
	@SuppressWarnings("unused")
	private static class VerifyGrpcService extends SignVerifyGrpc.SignVerifyImplBase {

		@Override
		public void verifySignature(SignVerifyRequest request, StreamObserver<SignVerifyResponse> responseObserver) {
			String keystorePath = System.getenv("KEYSTORE_PATH") != null ? System.getenv("KEYSTORE_PATH") : "*";
			String alias = System.getenv("KEY_ALIAS") != null ? System.getenv("KEY_ALIAS") : "meucertificado"; 
			String keystorePassword = System.getenv("KEY_PSW") != null ? System.getenv("KEY_PSW") : "teste001";
			SignVerifyResponse response = null;
			try {
				boolean resultado = VerifySignature.verify(request.getHexSignature(), request.getText(), keystorePath, alias, keystorePassword);
				response = SignVerifyResponse.newBuilder()
						.setVerificationResult(resultado).build();
				logger.log(Level.INFO, "Sinature verified: " + resultado);
			}
			catch (Exception ex) {
				logger.log(Level.SEVERE, "Exception verifying signature: " + ex.getMessage());
				response = SignVerifyResponse.newBuilder()
						.setVerificationResult(false).build();
			}
			finally {
		        responseObserver.onNext(response);
		        responseObserver.onCompleted();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		/*
		 * Parameters: <address> <port> <serviceName>
		 */
		String address = args.length>0 ? args[0] : "localhost";
		int port = args.length>1 ? Integer.parseInt(args[1]) : 8080;
		String serviceName = args.length>2 ? args[2] : "verifySignature";
		VerifyGrpcServer server = new VerifyGrpcServer(address,port,serviceName);
	    server.start();
	    server.blockUntilShutdown();

	}

	private void blockUntilShutdown() throws InterruptedException {
	    if (server != null) {
	        server.awaitTermination();
	    }
	}

	private void start() throws IOException {
	    this.server.start();
	    logger.info("Server started, listening on " + port);
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	      @Override
	      public void run() {
	        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
	        System.err.println("*** shutting down gRPC server since JVM is shutting down");
	        VerifyGrpcServer.this.stop();
	        System.err.println("*** server shut down");
	      }
	    });
	}
	
	protected void stop() {
	    if (server != null) {
	        server.shutdown();
	    }
		
	}


	public VerifyGrpcServer(String address, int port, String serviceName) throws Exception {
		super();
		String zkAddress = System.getenv("ZOOKEEPER_ADDRESS") != null ? System.getenv("ZOOKEEPER_ADDRESS") : "localhost";
		
		int    zkPort	 = System.getenv("ZOOKEEPER_PORT") != null ? Integer.parseInt(System.getenv("ZOOKEEPER_PORT")) : 2181;
		wrapper = new ApacheCuratorWrapper(zkAddress + ":" + zkPort);
		
		
		this.port = port;
		this.server = ServerBuilder.forPort(port).addService(new VerifyGrpcService()).build();
		
		wrapper.registerInstance(address, port, serviceName);
		
	}

}
