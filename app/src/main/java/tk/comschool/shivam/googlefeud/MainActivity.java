// Copyright by Shivam Jha, 2018
package tk.comschool.shivam.googlefeud;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    List<Button> buttonArrayList = new ArrayList<>();
    List<TextView> answerTextViewList = new ArrayList<>();
    int score = 0, leftChances = 3;
    private String jsonData = "";
    public class DownloadJSONData extends AsyncTask<String, Void, String>{
        // TODO: Make the class static (Just Needs some variable changes)

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            for(Button button : buttonArrayList){
                button.setClickable(false);
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection  httpURLConnection = null;
            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                  jsonData += (char) data;
                  data = reader.read();
                }
                return jsonData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            for (Button button : buttonArrayList){
                button.setClickable(true);
            }
            ProgressBar bar = findViewById(R.id.downloadProgressBar);
            bar.setVisibility(View.INVISIBLE);
        }
    }

    public void questionCategoryTapped(View view){
        leftChances = 3; //reset lives
        EditText answerEditText = findViewById(R.id.answerEditText);
        answerEditText.setEnabled(true);
        answerEditText.setText("");
        Button questionCategory = (Button) view;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String str = jsonObject.getString(questionCategory.getText().toString().toLowerCase());
            JSONArray arr = new JSONArray(str);
            Random random = new Random();
            str = (String) arr.get(random.nextInt(arr.length()));
            TextView questionTextView = findViewById(R.id.questionTextView);
            questionTextView.setText(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateScoreBoard(){
        TextView scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText("Score: " + Integer.toString(score));
    }

    public void resetButtonClicked(View view){
        score = 0;
        leftChances = 3;
        updateScoreBoard();
        TextView questionTextView = findViewById(R.id.questionTextView);
        questionTextView.setText("");
    }
    public void submitButtonClicked(View view) {
        if (leftChances > 0) {
            EditText answerEditText = findViewById(R.id.answerEditText);
            if (answerEditText.getText().toString().isEmpty())
                Toast.makeText(getApplicationContext(), "Answer cannot be empty", Toast.LENGTH_LONG).show();
            else {
                boolean answerMatched = false;
                TextView questionTextView = findViewById(R.id.questionTextView);
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    String str = jsonObject.getString("allanswers");
                    JSONObject allanswerJSON = new JSONObject(str);
                    str = allanswerJSON.getString(questionTextView.getText().toString());
                    JSONArray answerArray = new JSONArray(str);
                    int scoreAdder = 10000;
                    for (int i = 0; i < answerArray.length(); i++) {
                        if (answerArray.getString(i).equals(answerEditText.getText().toString().toLowerCase()))
                        {
                            answerMatched = true;
                            answerTextViewList.get(i).setText(answerArray.getString(i));
                            break;
                        }
                        scoreAdder -= 1000;
                    }
                    if (answerMatched) {
                        score += scoreAdder;
                        updateScoreBoard();
                        Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
                    } else {
                        leftChances--;
                        if (leftChances == 0){
                            Toast.makeText(getApplicationContext(), "You used all your chances for this question, Tap a question category to get next question!", Toast.LENGTH_LONG).show();
                            answerEditText.setEnabled(false);
                            for (int i = 0; i < answerArray.length(); i++){
                                answerTextViewList.get(i).setText(answerArray.getString(i));
                            }
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Wrong! You have only " + leftChances + " chances left", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            answerEditText.setText("");
        } else {
            Toast.makeText(getApplicationContext(), "You used all your chances for this question, Tap a question category to get next question!", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonArrayList.add((Button) findViewById(R.id.cultureButton));
        buttonArrayList.add((Button) findViewById(R.id.peopleButton));
        buttonArrayList.add((Button) findViewById(R.id.namesButton));
        buttonArrayList.add((Button) findViewById(R.id.questionButton));

        answerTextViewList.add((TextView) findViewById(R.id.answerTextView1));
        answerTextViewList.add((TextView) findViewById(R.id.answerTextView2));
        answerTextViewList.add((TextView) findViewById(R.id.answerTextView3));
        answerTextViewList.add((TextView) findViewById(R.id.answerTextView4));
        answerTextViewList.add((TextView) findViewById(R.id.answerTextView5));
        answerTextViewList.add((TextView) findViewById(R.id.answerTextView6));
        answerTextViewList.add((TextView) findViewById(R.id.answerTextView7));
        answerTextViewList.add((TextView) findViewById(R.id.answerTextView8));
        answerTextViewList.add((TextView) findViewById(R.id.answerTextView9));
        answerTextViewList.add((TextView) findViewById(R.id.answerTextView10));

        String jsonDataURL = "https://gist.github.com/lzzy12/55934307cac676bdec4fd6dced1a80c9/raw/1fc1e750550b78c459d03fa799698410b790450b/googlefeudQuestions.json";
        DownloadJSONData downloadTask = new DownloadJSONData();
        try {
            downloadTask.execute(jsonDataURL);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}



