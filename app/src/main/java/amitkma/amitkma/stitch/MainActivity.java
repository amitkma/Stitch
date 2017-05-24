package amitkma.amitkma.stitch;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import amitkma.stitch.annotations.CallOnAnyThread;
import amitkma.stitch.annotations.CallOnNewThread;
import amitkma.stitch.annotations.CallOnUiThread;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.textview);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        // Stitching this class to generated class
        MainActivityStitch mainActivityStitch = MainActivityStitch.stitch(this);

        mainActivityStitch.showText("\n" + mainActivityStitch.networkCall("https://www.google.com"));

        imageView.setImageBitmap(mainActivityStitch.loadBitmap());
    }

    @CallOnUiThread
    public void showText(String param) {
        Log.d(TAG, "Thread : " + Thread.currentThread().getName());
        mTextView.append("\n" + param + " ------------- set by " + Thread.currentThread().getName() + " thread");
    }

    @CallOnNewThread
    public String networkCall(String urlString) {
        Log.d(TAG, "Thread network call: " + Thread.currentThread().getName());

        String resultString = null;
        URL url;
        try {
            url = new URL(urlString);
            resultString = downloadUrl(url);
            if (resultString == null) {
                throw new IOException("No response received");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "networkCall Result: " + resultString);
        return resultString;
    }

    /**
     * This method will execute on a thread from threadpool. If all the threads are busy then
     * task will be queued until a thread is available to run.
     */
    @CallOnAnyThread
    public Bitmap loadBitmap() {
        Log.d(TAG, "Thread load bitmap: " + Thread.currentThread().getName());
        return getBitmapFromURL(
                "https://image.freepik.com/free-vector/android-boot-logo_634639.jpg");
    }

    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it will throw an IOException.
     */
    private String downloadUrl(URL url) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream, 250);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");

        char[] buffer = new char[maxLength];

        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }
        if (numChars != -1) {
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }
        return result;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
