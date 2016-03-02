/* Programa que realiza una comunicion entre el cliente y el servidor
 * Clase que maneja el Servidor  Clase que maneja el servidor  
 * Establece un servidor que recibe una conexión de un cliente, envía 
 * una cadena al cliente y cierra la conexión.
 */
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Servidor extends JFrame 
{
   private JTextArea areaPantalla; // muestra información al usuario
   private ObjectOutputStream salida; // flujo de salida hacia el cliente
   private ObjectInputStream entrada; // flujo de entrada del cliente
   private ServerSocket servidor; // socket servidor
   private Socket conexion; // conexión al cliente
   private int contador = 1; // contador del número de conexiones

   // establece la GUI
   public Servidor()
   {
      super( "Servidor" );

      //Se dibuaja el area para mostrar los archivos
      //add( campoIntroducirRuta, BorderLayout.NORTH );

      areaPantalla = new JTextArea(); // crea objeto areaPantalla
      add( new JScrollPane( areaPantalla ), BorderLayout.CENTER );
      areaPantalla.setEditable(false);

      setSize( 300, 150 ); // establece el tamaño de la ventana
      setVisible( true ); // muestra la ventana
   } // fin del constructor de Servidor

   // establece y ejecuta el servidor
   public void ejecutarServidor()
   {
      try // establece el servidor para que reciba conexiones; procesa las conexiones
      {
         servidor = new ServerSocket( 12345, 100 ); // crea objeto ServerSocket

         while ( true ) 
         {
            try 
            {
               esperarConexion(); // espera una conexión
               obtenerFlujos(); // obtiene los flujos de entrada y salida
               procesarConexion(); // procesa la conexión
            } // fin de try
            catch ( EOFException excepcionEOF ) 
            {
               mostrarMensaje( "\nServidor termino la conexion" );
            } // fin de catch
            finally 
            {
               cerrarConexion(); //  cierra la conexión
               contador++;
            } // fin de finally
         } // fin de while
      } // fin de try
      catch ( IOException exepcionES ) 
      {
         exepcionES.printStackTrace();
      } // fin de catch
   } // fin del método ejecutarServidor

   // espera a que llegue una conexión, después muestra información sobre ésta
   private void esperarConexion() throws IOException
   {
      mostrarMensaje( "Esperando una conexion\n" );
      conexion = servidor.accept(); // permite al servidor aceptar la conexión
      mostrarMensaje( "Conexion " + contador + " recibida de: " +
         conexion.getInetAddress().getHostName() );
   } // fin del método esperarConexion

   // obtiene flujos para enviar y recibir datos
   private void obtenerFlujos() throws IOException
   {
      // establece el flujo de salida para los objetos
      salida = new ObjectOutputStream( conexion.getOutputStream() );
      salida.flush(); // vacía el búfer de salida para enviar información del encabezado

      // establece el flujo de entrada para los objetos
      entrada = new ObjectInputStream( conexion.getInputStream() );

      mostrarMensaje( "\nSe obtuvieron los flujos de E/S\n" );
   } // fin del método obtenerFlujos

   // procesa la conexión con el cliente
   private void procesarConexion() throws IOException
   {
      String mensaje = "Conexion exitosa";
      enviarDatos( mensaje ); // envía mensaje de conexión exitosa

      do // procesa los mensajes enviados desde el cliente
      { 
         try // lee el mensaje y lo muestra en pantalla
         {
            mensaje = ( String ) entrada.readObject(); // lee el nuevo mensaje
            mostrarMensaje( "\n" + mensaje ); // muestra el mensaje
         } // fin de try
         catch ( ClassNotFoundException excepcionClaseNoEncontrada ) 
         {
            mostrarMensaje( "\nSe recibio un tipo de objeto desconocido" );
         } // fin de catch

      } while ( !mensaje.equals( "CLIENTE>>> TERMINAR" ) );
   } // fin del método procesarConexion

   // clierra flujos y socket
   private void cerrarConexion() 
   {
      mostrarMensaje( "\nTerminando conexion\n" );

      try 
      {
         salida.close(); // cierra flujo de salida
         entrada.close(); // cierra flujo de entrada
         conexion.close(); // cierra el socket
      } // fin de try
      catch ( IOException exepcionES ) 
      {
         exepcionES.printStackTrace();
      } // fin de catch
   } // fin del método cerrarConexion

   // envía el mensaje al cliente
   private void enviarDatos( String mensaje )
   {
      try // envía objeto al cliente
      {
         salida.writeObject( "SERVIDOR>>> " + mensaje );
         salida.flush(); // envía toda la salida al cliente
         mostrarMensaje( "\nSERVIDOR>>> " + mensaje );
      } // fin de try
      catch ( IOException exepcionES ) 
      {
         areaPantalla.append( "\nError al escribir objeto" );
      } // fin de catch
   } // fin del método enviarDatos

   // manipula areaPantalla en el subproceso despachador de eventos
   private void mostrarMensaje( final String mensajeAMostrar )
   {
      SwingUtilities.invokeLater(
         new Runnable() 
         {
            public void run() // updates areaPantalla
            {
               areaPantalla.append( mensajeAMostrar ); // adjunta el mensaje
            } // fin del método run
         } // fin de la clase interna anónima
      ); // fin de la llamada a SwingUtilities.invokeLater
   } // fin del método mostrarMensaje
} 
