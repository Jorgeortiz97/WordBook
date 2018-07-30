package application.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Player {

	private static MediaPlayer mp;


	private static class TextToSpeech {


		public final static String PATH = "tts.mp3";

		public static boolean getSpeech(String word) {


			String encodedUrl = null;
			try {
				encodedUrl = URLEncoder.encode(word, "UTF-8");
			} catch (UnsupportedEncodingException ignored) {
				return false;
			}

			String url = "http://translate.google.com.vn/translate_tts?ie=UTF-8&q=" + encodedUrl + "&tl=en&client=tw-ob";


			URL obj;

			FileOutputStream fos = null;

			try {
				obj = new URL(url);

				HttpURLConnection con;

				con = (HttpURLConnection) obj.openConnection();

				con.setRequestMethod("GET");

				con.setRequestProperty("User-Agent", "Mozilla/5.0");

				InputStream is = con.getInputStream();

				fos = new FileOutputStream(PATH);

				byte[] byteChunk = new byte[4096];
				int n;

				while ((n = is.read(byteChunk)) > 0) {
					fos.write(byteChunk, 0, n);
				}
			} catch (IOException e) {
				return false;
			} finally {
				if (fos != null)
					try {
						fos.close();
					} catch (IOException e) {}
			}

			return true;
		}
	}


	public static boolean play(String word) {
		if (TextToSpeech.getSpeech(word)) {
			Media hit = new Media(new File(TextToSpeech.PATH).toURI().toString());
			mp = new MediaPlayer(hit);
			mp.play();
			return true;
		}
		return false;
	}

}
