package ai.hemton;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    private TextView status;private android.speech.tts.TextToSpeech textToSpeech;

    private static final int REQUEST_RECORD_AUDIO = 100;
    private static final int REQUEST_SPEECH = 101;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
android.view.View orb = findViewById(R.id.orb);
android.view.animation.Animation pulse =
        android.view.animation.AnimationUtils.loadAnimation(
                this, R.anim.orb_pulse);
orb.startAnimation(pulse);
textToSpeech = new android.speech.tts.TextToSpeech(
        this,
        status -> {
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(java.util.Locale.US);
            }
        }
);
        status = findViewById(R.id.status);
        Button listenButton = findViewById(R.id.listenButton);

        listenButton.setOnClickListener(v -> startListening());
    }

    private void startListening() {

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO
            );

            return;
        }

        openSpeechRecognizer();
    }

    private void openSpeechRecognizer() {

        Intent intent =
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Hey! I'm listening..."
        );

        try {

            status.setText("Listening...");

            startActivityForResult(
                    intent,
                    REQUEST_SPEECH
            );

        } catch (Exception e) {

            status.setText(
                    "Speech recognition unavailable"
            );
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == REQUEST_SPEECH
                && resultCode == RESULT_OK
                && data != null) {

            ArrayList<String> results =
                    data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS
                    );

            if (results != null && !results.isEmpty()) {

                String spokenText = results.get(0);

                status.setText("Thinking...");

                askHemton(spokenText);

            } else {

                status.setText(
                        "I didn't hear that."
                );
            }
        }
    }

    private void askHemton(String message) {

        executor.execute(() -> {

            try {

                URL url = new URL(
                        "https://hemton-ai.vercel.app/api/chat"
                );

                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

                connection.setDoOutput(true);

                JSONObject body = new JSONObject();

                JSONArray messages = new JSONArray();

                JSONObject userMessage =
                        new JSONObject();

                userMessage.put(
                        "role",
                        "user"
                );

                userMessage.put(
                        "content",
                        message
                );

                messages.put(userMessage);

                body.put(
                        "messages",
                        messages
                );

                body.put(
                        "memory",
                        new JSONArray()
                );

                OutputStream output =
                        connection.getOutputStream();

                output.write(
                        body.toString()
                                .getBytes(StandardCharsets.UTF_8)
                );

                output.close();

                int responseCode =
                        connection.getResponseCode();

                InputStream inputStream;

                if (responseCode >= 200
                        && responseCode < 300) {

                    inputStream =
                            connection.getInputStream();

                } else {

                    inputStream =
                            connection.getErrorStream();
                }

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        inputStream
                                )
                        );

                StringBuilder response =
                        new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {

                    response.append(line);
                }

                reader.close();

                JSONObject result =
                        new JSONObject(
                                response.toString()
                        );

                String reply =
                        result.optString(
                                "reply",
                                "I didn't get a response."
                        );

runOnUiThread(() -> {
    status.setText(reply);
    textToSpeech.speak(
            reply,
            android.speech.tts.TextToSpeech.QUEUE_FLUSH,
            null,
            "hemton_reply"
    );
});
                connection.disconnect();

            } catch (Exception e) {

                runOnUiThread(() ->
                        status.setText(
                                "HEMTON connection error"
                        )
                );
            }
        });
    }

    @Override
    protected void onDestroy() {
if (textToSpeech != null) {
    textToSpeech.stop();
    textToSpeech.shutdown();
}
        executor.shutdown();

        super.onDestroy();
    }
}
