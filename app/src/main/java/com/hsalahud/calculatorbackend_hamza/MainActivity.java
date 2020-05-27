package com.hsalahud.calculatorbackend_hamza;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Variables that effect the flow of how a user would use the calculator

    //isNewNumber indicates a number on either side of an operation
    public boolean isNewNumber = true;
    //    public String calculatedAmount = "";
    //We store the number in the display that comes to the left of the operation
    public String storeDisplayPriorToOperation = "";
    //Keep track of last operation
    public String lastOperation = "";
    //Determing if the last button we clicked is an operation
    public boolean lastClickIsOperation = false;
    //Determining if we clicked on the decimal button
    public boolean isDecimalInDisplay = false;
    //Keeping track of whether we typed a number to the right of an operation
    public boolean typedSecondNumber = false;


    /**
     * This method shows a number on a button and displays it once clicked. It handles numbers on the left and right
     * hand side of the operation slightly differently
     * @param view
     */
    public void clickNum (View view) {
        TextView display = (TextView) findViewById(R.id.display);
        Button button = (Button) view;

        if (lastClickIsOperation==false){
            if (isNewNumber==true) {
                display.setText(button.getText());
                isNewNumber = false;
            }else {
                display.setText(display.getText().toString() + button.getText().toString());
            }
        }else{
            lastClickIsOperation = false;
            typedSecondNumber = true;
            display.setText(button.getText());
        }

    }

    /**
     * Method that keeps track of displaying a decimal when the button is clicked and makes sure we cannot enter multiple decimals
     * @param view
     */
    public void clickDecimal (View view) {
        TextView display = (TextView) findViewById(R.id.display);

        if (isDecimalInDisplay==false){
            display.setText((display.getText().toString() + "."));
            isDecimalInDisplay = true;
        }

    }

    /**
     * Method that resets all the values we are keeping track of. Used for the AC button
     * @param view
     */
    public void clickReset (View view) {
        TextView display = (TextView) findViewById(R.id.display);
        display.setText("0");
//        calculatedAmount = "";
        lastOperation = "";
        isNewNumber = true;
        lastClickIsOperation = false;
        isDecimalInDisplay = false;
        storeDisplayPriorToOperation = "";
    }

    /**
     * Method that keeps track of what operation was clicked on. It resets some values so we know that we are at the number
     * to the right of the operation
     * @param view
     */
    public void clickOperation (View view) {
        TextView display = (TextView) findViewById(R.id.display);
        Button button = (Button) view;
        String id = getResources().getResourceEntryName(button.getId());

        lastClickIsOperation = true;
        isDecimalInDisplay = false;
        storeDisplayPriorToOperation = display.getText().toString();

        switch (id){
            case "plus":
                lastOperation = "plus";
                break;
            case "minus":
                lastOperation = "minus";
                break;
            case "multiply":
                lastOperation = "multiply";
                break;
            case "divide":
                lastOperation = "divide";
                break;
        }
    }

    /**
     * Method for clicking the equals button which calculates the equation and displays an integer or double value appropriately
     * @param view
     */
    public void clickEquals (View view) {
        TextView display = (TextView) findViewById(R.id.display);
        double firstValue = Double.parseDouble(storeDisplayPriorToOperation);
        double answer;

        switch (lastOperation){
            case "plus":

                answer = firstValue + Double.parseDouble(display.getText().toString());
                if (storeDisplayPriorToOperation.contains(".") || isDecimalInDisplay == true){
                    display.setText(Double.toString(answer));
                }else {
                    int answerAsInt = (int) answer;
                    display.setText(Integer.toString(answerAsInt));
                }

                break;
            case "minus":
                answer = firstValue - Double.parseDouble(display.getText().toString());
                if (storeDisplayPriorToOperation.contains(".") || isDecimalInDisplay == true){
                    display.setText(Double.toString(answer));
                }else {
                    int answerAsInt = (int) answer;
                    display.setText(Integer.toString(answerAsInt));
                }
                break;
            case "multiply":
                answer = firstValue * Double.parseDouble(display.getText().toString());
                if (storeDisplayPriorToOperation.contains(".") || isDecimalInDisplay == true){
                    display.setText(Double.toString(answer));
                }else {
                    int answerAsInt = (int) answer;
                    display.setText(Integer.toString(answerAsInt));
                }
//                lastOperation = "multiply";
                break;
            case "divide":
                answer = firstValue / Double.parseDouble(display.getText().toString());

                if (firstValue % Double.parseDouble(display.getText().toString())==0){
                    int answerAsInt = (int)(double) answer;
                    display.setText(Integer.toString(answerAsInt));
                }else{
                    display.setText(Double.toString(answer));
                }

                break;
        }
    }

    /**
     * This method applies the appropriate parameters to the URL and makes an API call to the backend which computes the requested calculation
     * @return a String with our answer
     */
    public String getAnswerViaAPI() {
        TextView display = (TextView) findViewById(R.id.display);


        try {
            //API endpoint
            String addr = "http://10.0.2.2:8080/calculator/calculate/" + storeDisplayPriorToOperation + "/" + lastOperation + "/" + display.getText() + "/" + isDecimalInDisplay;
            //Create a URL object to read the address
            URL address = new URL(addr);

            BufferedReader in = new BufferedReader(new InputStreamReader(address.openStream()));
            String line= in.readLine();

            in.close();

            return line;

            //For future reference, we can also use ASYNC Task: https://www.youtube.com/watch?v=EThkglxLxSM
            //Solving network issues: https://www.youtube.com/watch?v=cexOVddIMkc

            ////ALTERNATE METHOD BELOW/////////
//            HttpURLConnection con = (HttpURLConnection) address.openConnection();
//            con.setRequestMethod("GET");
//            con.connect();
//            BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String value = bf.readLine();
//            return value;
            ////////////////////////////

            //catching errors
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * method which displays the answer on the calculator. Needed for our Completable future
     * @param answer
     */
    public void displayAnswerFromAPI (String answer) {
        TextView display = (TextView) findViewById(R.id.display);
        display.setText(answer);
    }


    /**
     * Click method which makes an asynchronous call to the API and then displays it on the calculator. Triggered with the onClick event from the calculator's
     * equal sign
     * @param view
     */
    public void clickEqualsAsync (View view) {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        future.supplyAsync(this::getAnswerViaAPI).thenAccept(this::displayAnswerFromAPI).join();
    }


}
