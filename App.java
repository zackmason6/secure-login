/*
 * This program simulates the secure login process of a secure application.
 * The user will be greeted with terms of use before beginning. They will then
 * have to enter a username or password. There are three levels of security
 * and 3 different valid password/username combinations. Upon 3 failed login
 * attempts the program will exit. Additionally, after providing successful
 * username and password, the user will have to dual authenticate using a 
 * completely randomized 10-digit alphanumeric code. If this process takes the
 * user too long the program will exit for security. When the user finally gets
 * past the dual authentication, they will be greeted with their username and 
 * their security level.
 *
 */
package com.mycompany.sdev425hw2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import static java.net.InetAddress.getLocalHost;
import java.net.UnknownHostException;
import java.security.Timestamp;
import java.util.Date;
import java.util.Random;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import java.util.Timer;
import java.util.TimerTask;
import javafx.geometry.Insets;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Zachary Mason. Adopted from Oracle's Login Tutorial Application
 * https://docs.oracle.com/javafx/2/get_started/form.htm
 * and the source code given to us for this project and created by user Jim.
 * 
 * 
 * 
 */

// The following is the user class. This is used to create different users with
// different security clearances.
class User {
    String username;
    String password;
    int clearanceLevel;
    String emailAddress;
    
    // constructor
    public User(String myusername, String mypassword){
        username = myusername;
        password = mypassword;
    }
    
    // Method to set clearance based on username.
    public int setClearance(int newClearance){
        clearanceLevel = newClearance;
        return clearanceLevel;
    }
    
}

// public class
public class App extends Application {
    
    // sets up the counter variable. This is used to keep track of failed login
    // attempts.
    int counter = 0;
    
    // The following method starts the application with the first login window.
    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.setTitle("SDEV425 Login");
        // Grid Pane divides your window into grids
        GridPane grid = new GridPane();
        // Align to Center
        // Note Position is geometric object for alignment
        grid.setAlignment(Pos.CENTER);
        // Set gap between the components
        // Larger numbers mean bigger spaces
        grid.setHgap(10);
        grid.setVgap(10);

        // Create some text to place in the scene
        Text scenetitle = new Text("Welcome. Login to continue.");
        // Add text to grid 0,0 span 2 columns, 1 row
        grid.add(scenetitle, 0, 0, 2, 1);

        // Create Label
        Label userName = new Label("User Name:");
        // Add label to grid 0,1
        grid.add(userName, 0, 1);

        // Create Textfield
        TextField userTextField = new TextField();
        // Add textfield to grid 1,1
        grid.add(userTextField, 1, 1);

        // Create Label
        Label pw = new Label("Password:");
        // Add label to grid 0,2
        grid.add(pw, 0, 2);

        // Create Passwordfield
        PasswordField pwBox = new PasswordField();
        // Add Password field to grid 1,2
        grid.add(pwBox, 1, 2);

        // Create Login Button
        Button btn = new Button("Login");
        // Add button to grid 1,4
        grid.add(btn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        
        // Set the Action when button is clicked
        btn.setOnAction(new EventHandler<ActionEvent>() {

            // The following defines the action event that is set to the login
            // button.
            @Override
            public void handle(ActionEvent e) {
                
                // Authenticate the user
                String username = userTextField.getText(); // get username from the text field.
                boolean isValid = authenticate(username, pwBox.getText());
                // If valid clear the grid and Welcome the user
                if (isValid) {
                    try {
                        writelog(userTextField.getText(), "Successful Login");
                    } catch (UnknownHostException ex) {
                        ex.printStackTrace();
                    }
                    primaryStage.close(); //close primaryStage - make it not visible.
                     String secretCode;
                    secretCode = generateCode(); // call the generateCode method to generate a code for dual factor authentication
                    
                    Label thirdLabel = new Label(" Please enter the code that you received\n"
                            + " via email to validate your identity. You will\n"
                            + " have one minute to respond or this will close");
                    TextField userInput = new TextField(); // get user input from the text field
                     StackPane tertiaryLayout = new StackPane(); // set up a new pane
            tertiaryLayout.getChildren().add(thirdLabel); // add the label to the pane
            GridPane myGrid = new GridPane(); // set up a new grid pane
            
            //Setting the padding  
      myGrid.setPadding(new Insets(10, 10, 10, 10)); 
      
      //Setting the vertical and horizontal gaps between the columns 
      myGrid.setVgap(5); 
      myGrid.setHgap(5);       
      
      //Setting the Grid alignment 
      myGrid.setAlignment(Pos.CENTER); 
            
            Scene thirdScene = new Scene(myGrid, 450, 200);
            Button myButton = new Button("Submit");
            myGrid.add(userInput, 0,2);
            myGrid.add(thirdLabel, 0, 0);
            myGrid.add(myButton, 2,2);

                    
                    // New window (Stage)
            Stage emailWindow = new Stage();
            emailWindow.setTitle("Email Authentication");
            emailWindow.setScene(thirdScene);
 
            // Specifies the modality for new window.
            emailWindow.initModality(Modality.WINDOW_MODAL);
 
            // Specifies the owner Window (parent) for new window
            emailWindow.initOwner(primaryStage);
 
            // Set position of second window, related to primary window.
            emailWindow.setX(primaryStage.getX() + 200);
            emailWindow.setY(primaryStage.getY() + 100);
 
            emailWindow.show(); // make emailWindow visible
            
            
            
            //sendEmail("zackmason6@gmail.com", secretCode);
            
            TimerTask task = new TimerTask() // Start a new task that is linked to a timer
{
    public void run()// The following block of code defines what will happen if the timer runs out.
    {
        if( !userInput.getText().equals(secretCode) )
        {
            System.out.println( "you input nothing. exit..." );
            try {
                writelog(username, "Invalid email authentication");
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
            System.exit (0);
        
            
            //System.exit( 0 );
        }
    }    
};

Timer timer = new Timer(); // set the timer and conditions that will cancel it.

timer.schedule( task, 20*1000 );
String usertext = userInput.getText();
if (usertext == secretCode){
timer.cancel();
System.out.println("timer canceled");
}
            
            
            
            
            
            // the following sets an event handler and adds it to a button.
            myButton.setOnAction(new EventHandler<ActionEvent>() {

            // The following block defines an action event.
            @Override
            public void handle(ActionEvent e) {
                
                // Authenticate the user
                boolean isValid = dualauthenticate(userInput.getText(), secretCode);
                // If valid clear the grid and Welcome the user
                if (isValid) {
                    
                    // set up the next stage of the application
                    emailWindow.close(); // close the old window
                    System.out.println("Success!!!!");
                    Stage primaryStage = new Stage(); // set up a new stage
                    String username = userTextField.getText(); // get username from the text field
                    String password = pwBox.getText(); //get password from text field
                    try {
                        writelog(username, "email verification successful"); //write to the log
                    } catch (UnknownHostException ex) {
                        ex.printStackTrace();
                    }
                    secondWindow(primaryStage, username, password); // call the secondWindow method to set up the next window.
                    
                    
                } else {
                    try {
                        //do some other stuff
                        writelog(username, "email verification failed."); // write to the log
                    } catch (UnknownHostException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            });
                 
          
            
            
            
                   // If Invalid Ask user to try again
                } else {   
                    
                    try {
                        writelog(userTextField.getText(), "Unsuccessful Login Attempt"); // write to the log
                    } catch (UnknownHostException ex) {
                        ex.printStackTrace();
                    }
                    final Text actiontarget = new Text();
                    grid.add(actiontarget, 1, 6); // add text to the grid
                    actiontarget.setFill(Color.FIREBRICK); //set fill color of text to red
                    actiontarget.setText("Please try again.");
                    counter = counter +1;
                    if (counter >= 3){ // If too many login events ended in failure, exit the program
                        System.out.println("Too many failed attempts");
                        try {
                            writelog(username, "Too many failed login attempts");
                        } catch (UnknownHostException ex) {
                            ex.printStackTrace();
                        }
                        System.exit(0);
                    }
                }

            }
        });
        // Set the size of Scene
        Scene scene = new Scene(grid, 500, 400);
        primaryStage.setScene(scene); // set the scene 
        primaryStage.show(); // make the scene visible
        
        // set label text
        Label secondLabel = new Label("By exiting this window and continuing with \n"
                + " this application you acknowledge and affirm that you are \n"
                + "affiliated with UMGC or an appropriate government institution \n"
                + "and are authorized to access this application. In addition, by \n"
                + "continuing with this application you agree to only utilize it in \n"
                + "the way that it is intended - to teach students secure programming \n"
                + "in Java.");
 
            StackPane secondaryLayout = new StackPane(); // set up a new stack pane
            secondaryLayout.getChildren().add(secondLabel); // add label
 
            Scene secondScene = new Scene(secondaryLayout, 500, 300); // set size of the scene and use secondaryLayout
 
            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Terms of Use"); // set stage title
            newWindow.setScene(secondScene); // set scene
 
            // Specifies the modality for new window.
            newWindow.initModality(Modality.WINDOW_MODAL); // set window modality so it needs to be interacted with
 
            // Specifies the owner Window (parent) for new window
            newWindow.initOwner(primaryStage);
 
            // Set position of second window, related to primary window.
            newWindow.setX(primaryStage.getX() + 200);
            newWindow.setY(primaryStage.getY() + 100);
 
            newWindow.show();
            
    }
            

    /**
     * @param args the command line arguments
     */
            
    public static void main(String[] args) {
        
        launch(args);
        
    }


    /**
     * @param user the username entered
     * @param pword the password entered
     * @return isValid true for authenticated
     */
    
    // This method sets up a new window. It also sets up the clearance level for each user.
    public void secondWindow(Stage primaryStage, String username, String password){
        User myUser = new User(username, password);
        if (myUser.username.equals("sdevadmin")){
            myUser.setClearance(3);
        }else if (myUser.username.equals("sdevuser")){
            myUser.setClearance(2);
        }else if(myUser.username.equals("sdevguest")){
            myUser.setClearance(1);
        }
        primaryStage.setTitle("Welcome");
        Text usernamebox = new Text("Welcome " + username);
        Text accesslevel = new Text("Your access level is: " + myUser.clearanceLevel);
        GridPane layout = new GridPane();
        layout.add(usernamebox, 1,0);
        layout.add(accesslevel, 1,2);
        Scene welcomeScene = new Scene(layout, 300,250);
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
}
    // this method is not used at this time but would be implemented with more time to work on this project.
    // It is meant to create a window that tells the user that the session has timed out.
    public void thirdWindow(Stage primaryStage){
        primaryStage.setTitle("Timeout");
        Text timeout = new Text("No input received within the time limit. Goodbye.");
        StackPane layout = new StackPane();
        layout.getChildren().add(timeout);
        Scene timeoutScene = new Scene(layout, 300,250);
        primaryStage.setScene(timeoutScene);
        primaryStage.show();
    }
    
    // Authenticate method. This checks the user entered string and password
    // against a short whitelist.
    public boolean authenticate(String user, String pword) {
        boolean isValid = false;
        if (user.equalsIgnoreCase("sdevadmin")
                && pword.equals("425!pass")) {
                isValid = true;
        }
        else if (user.equalsIgnoreCase("sdevuser")
                && pword.equals("passw0rd")) {
                isValid = true;       
                    }

        return isValid;
    }
    
     // This method takes the user input for the dual authentication stage
    // and compares it to the code generated by this program. 
     public boolean dualauthenticate(String userInput, String secretCode) {
        boolean isValid = false;
        if (userInput.equals(secretCode)) {
            isValid = true;
        }

        return isValid;
    }
     
    
     // this method generates a secret code for dual authentication.
     public String generateCode() {
         int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 10;
    Random random = new Random();

    String generatedString = random.ints(leftLimit, rightLimit + 1)
      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
      .limit(targetStringLength)
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();

    System.out.println(generatedString);
    
    return generatedString;

     }
     
     
     // This method was originally supposed to send an email for dual authentication
     // but is not in use right now.
     public void sendEmail(String sendAddress, String secretCode) {
         
String to = "zackmason6@gmail.com";//change accordingly  
      String from = sendAddress;  
      String host = "localhost";//or IP address  
  
     //Get the session object  
      Properties properties = System.getProperties();  
      properties.setProperty("mail.smtp.host", host);  
      Session session = Session.getDefaultInstance(properties);  
  
     //compose the message  
      try{  
         MimeMessage message = new MimeMessage(session);  
         message.setFrom(new InternetAddress(from));  
         message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));  
         message.setSubject("Ping");  
         message.setText("Please enter the following code into your Java application: \n \n" 
         + secretCode);  
  
         // Send message  
         Transport.send(message);  
         System.out.println("message sent successfully....");  
  
      }catch (MessagingException mex) {mex.printStackTrace();}  
   }
     
     // This method writes to the log file. It does includes the username,
     // what operation was attempted, and then it gets the host address and IP 
     // name and adds everything to the log file with a timestamp.
     public void writelog(String username, String operation) throws UnknownHostException{
        
            InetAddress address = InetAddress.getLocalHost(); 
            String hostIP = address.getHostAddress() ;
            String hostName = address.getHostName();
            
                        try(FileWriter fw = new FileWriter("test.txt", true);
    BufferedWriter bw = new BufferedWriter(fw);
    PrintWriter out = new PrintWriter(bw))
{
    Date now = new java.util.Date();
    out.println(username + "; " + operation + "; " + "hostIP: " + hostIP + "; " + "hostName:" + hostName + "; " + now + "\n");
} catch (IOException e) {

}
     
    }
    }
            