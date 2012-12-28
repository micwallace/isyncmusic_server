/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ISyncMusicServer;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;
import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.*;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author michael
 */
public class httpd {
    // start listening on the specified port
    private RequestListenerThread serverthread;
    public void start(String args) throws Exception {
        if (args == null) {
            System.err.println("Please specify document root directory");
            System.exit(1);
        }
        serverthread = new RequestListenerThread(8080, args);
        serverthread.setDaemon(false);
        serverthread.start();
    }
    
    public void stop(){
        System.out.println("HTTP Request listener thread terminated");
        serverthread.stopServer();
    }

    public void restart() {
    }

    public boolean testServer() {
        try {
            String strUrl = "http://127.0.0.1:8080/iasindex.json";
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            int recode = urlConn.getResponseCode();
            urlConn.disconnect();  
            if (recode==200){
                return true;
            } else {
                System.err.println("Internal HTTP error");
                return false;
            }
        } catch (IOException ex) {
            System.err.println("Error opening connection");
            Logger.getLogger(httpd.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    static class HttpFileHandler implements HttpRequestHandler {

        private final String docRoot;

        public HttpFileHandler(final String docRoot) {
            super();
            this.docRoot = docRoot;
        }

        @Override
        public void handle(
                final HttpRequest request,
                final HttpResponse response,
                final HttpContext context) throws HttpException, IOException {

            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
            if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
                throw new MethodNotSupportedException(method + " method not supported");
            }
            String target = request.getRequestLine().getUri();

            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                byte[] entityContent = EntityUtils.toByteArray(entity);
                System.out.println("Incoming entity content (bytes): " + entityContent.length);
            }

            final File file = new File(this.docRoot, URLDecoder.decode(target, "UTF-8"));
            if (!file.exists()) {

                response.setStatusCode(HttpStatus.SC_NOT_FOUND);
                
                StringEntity entity = new StringEntity(
                        "<html><body><h1>File" + file.getPath()
                        + " not found</h1></body></html>",
                        ContentType.create("text/html", "UTF-8"));
                response.setEntity(entity);
                System.out.println("File " + file.getPath() + " not found");

            } else if (!file.canRead() || file.isDirectory()) {

                response.setStatusCode(HttpStatus.SC_FORBIDDEN);
                StringEntity entity = new StringEntity(
                        "<html><body><h1>Access denied</h1></body></html>",
                        ContentType.create("text/html", "UTF-8"));
                response.setEntity(entity);
                System.out.println("Cannot read file " + file.getPath());

            } else {
                String filename = file.getName();
                int dotposition= filename.lastIndexOf(".");
                String fileext = filename.substring(dotposition + 1, filename.length()); 
                if (fileext.equals("m4a") || fileext.equals("mp4")){ fileext = "mp3"; }
                // depreciated for now, will need later to handle video files
                String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
                response.setStatusCode(HttpStatus.SC_OK);
                FileEntity body = new FileEntity(file, ContentType.create("audio/"+fileext, (Charset) null));
                response.setEntity(body);
                System.out.println("Serving file " + file.getPath() + " MIME Type: "+mimeType);
            }
        }
    }

    static class RequestListenerThread extends Thread {

        private final ServerSocket serversocket;
        private final HttpParams params;
        public final HttpService httpService;
        public boolean interrupt;

        public RequestListenerThread(int port, final String docroot) throws IOException {
            interrupt = false;
            this.serversocket = new ServerSocket(port);
            this.params = new SyncBasicHttpParams();
            this.params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000).setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024).setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false).setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true).setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");

            // Set up the HTTP protocol processor
            HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpResponseInterceptor[]{
                        new ResponseDate(),
                        new ResponseServer(),
                        new ResponseContent(),
                        new ResponseConnControl()
                    });

            // Set up request handlers
            HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
            reqistry.register("*", new HttpFileHandler(docroot));

            // Set up the HTTP service
            this.httpService = new HttpService(
                    httpproc,
                    new DefaultConnectionReuseStrategy(),
                    new DefaultHttpResponseFactory(),
                    reqistry,
                    this.params);
        }

        @Override
        public void run() {
            System.out.println("Listening on port " + this.serversocket.getLocalPort());
            while (!Thread.interrupted() || !interrupt) {
                try {
                    // Set up HTTP connection
                    Socket socket = this.serversocket.accept();
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                    System.out.println("Incoming connection from " + socket.getInetAddress());
                    conn.bind(socket, this.params);

                    // Start worker thread
                    Thread t = new WorkerThread(this.httpService, conn);
                    t.setDaemon(true);
                    t.start();
                } catch (InterruptedIOException ex) {
                    break;
                } catch (IOException e) {
                    System.err.println("I/O error initialising connection thread: "
                            + e.getMessage());
                    break;
                }
            }
        }
        public void stopServer(){
            try {
                this.serversocket.close();
                interrupt = true;
            } catch (IOException ex) {
                Logger.getLogger(httpd.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static class WorkerThread extends Thread {

        private final HttpService httpservice;
        private final HttpServerConnection conn;

        public WorkerThread(
                final HttpService httpservice,
                final HttpServerConnection conn) {
            super();
            this.httpservice = httpservice;
            this.conn = conn;
        }

        @Override
        public void run() {
            System.out.println("New connection thread");
            HttpContext context = new BasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
                System.err.println("Client closed connection");
            } catch (IOException ex) {
                System.err.println("I/O error: " + ex.getMessage());
            } catch (HttpException ex) {
                System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {
                }
            }
        }
    }
}
