package app;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Stage;

public class Ventana extends Application {

	private Boolean ejecuta = true;
	private static final String[] MEDIA_URL = {
			"http://localhost/~neto/kids.mp3",
			"http://localhost/~neto/yonose.mp3",
			"http://localhost/~neto/television.mp3",
			"http://localhost/~neto/sunisup.mp3",
			"http://localhost/~neto/gangnam.mp3",
			"http://localhost/~neto/blind.mp3",
			"http://localhost/~neto/another.mp3",
			"http://localhost/~neto/thebay.mp3" };
	private MediaPlayer[] mediaPlayer;

	private Datos datos;
	private String nombrePuertoSerie;

	private void init(Stage primaryStage) {
		mediaPlayer = new MediaPlayer[8];
		for (int i = 0; i < 8; i++) {
			mediaPlayer[i] = new MediaPlayer(new Media(MEDIA_URL[i]));
			mediaPlayer[i].setVolume(0);
			mediaPlayer[i].play();
		}
		// play();
		nombrePuertoSerie = "/dev/tty.usbmodem1411";

		datos = new Datos();
		datos.abrirPuertoSerial(nombrePuertoSerie);

		TocarCanción tocarCanción = new TocarCanción();
		Thread tocarch = new Thread(tocarCanción, "Hilo para tocar canción.");
		tocarch.start();

		Group root = new Group();
		primaryStage.setScene(new Scene(root));

		root.getChildren().add(new Button("neto"));
	}

	public void play() {
		for (MediaPlayer media : mediaPlayer) {
			Status status = media.getStatus();
			if (status == Status.UNKNOWN || status == Status.HALTED) {
				// System.out.println("Player is in a bad or unknown state, can't play.");
				return;
			}
			if (status == Status.PAUSED || status == Status.STOPPED
					|| status == Status.READY) {
				media.play();
			}
		}
	}

	@Override
	public void stop() {
		ejecuta = false;
		try {
			datos.setEjecutando(false);
			datos.cerrarPuerto();
			for (MediaPlayer media : mediaPlayer) {
				media.stop();
			}
		} catch (Exception ex) {
			System.out.print(ex.getMessage());
		}
	}

	static class MediaControl extends BorderPane {
		private final boolean repeat = true;
		private HBox mediaBar;

		public MediaControl(final MediaPlayer mp) {
			mediaBar = new HBox();
			mediaBar.setPadding(new Insets(5, 10, 5, 10));
			mediaBar.setAlignment(Pos.CENTER_LEFT);
			BorderPane.setAlignment(mediaBar, Pos.CENTER);

			final Button playButton = new Button();
			playButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
				}
			});

			mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);

			mediaBar.getChildren().add(playButton);
			// Add spacer
			Label spacer = new Label("   ");
			mediaBar.getChildren().add(spacer);

			setCenter(mediaBar);
		}
	}

	class TocarCanción implements Runnable {

		@Override
		public void run() {
			while (ejecuta) {
				Boolean nota[] = datos.getNotas();
				for (int i = 0; i < 8; i++) {
					if (nota[i]) {
						// System.out.println(MEDIA_URL[i] + " "+i);
						mediaPlayer[i].setVolume(100);
					} else
						mediaPlayer[i].setVolume(0);
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		init(primaryStage);
		primaryStage.show();
		play();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
