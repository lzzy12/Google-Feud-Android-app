// Copyright by Shivam Jha, 2018
package tk.comschool.shivam.googlefeud;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    List<Button> buttonArrayList = new ArrayList<>();
    List<TextView> answerTextViewList = new ArrayList<>();
    int score = 0, leftChances = 3;
    private String jsonData = "";

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

    public void setJsonData() {
        InputStream inputStream = this.getResources().openRawResource(R.raw.questions);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferInput = new BufferedReader(inputStreamReader);
        try{
            String read;
            while ((read = bufferInput.readLine()) != null){
                jsonData += read;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
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
                            // Answer is correct
                            MediaPlayer correctMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.correct);
                            correctMediaPlayer.start();
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
                        MediaPlayer correctMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
                        correctMediaPlayer.start();
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
        setJsonData();
    }
}



