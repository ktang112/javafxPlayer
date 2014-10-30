package javafxapplication1;

import java.io.File;
import static java.lang.Math.max;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author kevo
 */
public class SimplePlayer extends Application {

    //format for the text box in slider
    private static final NumberFormat twoDp = new DecimalFormat("0.##");
    //private static final Color foregroundColor = Color.WHITE;    

    private String MEDIA
            //= "http://192.168.0.4/barsandtone.flv";  /home/kevo/Downloads          
            //= "http://192.168.0.4/20051210-w50s.flv";
            = "http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv";
            //= "file:///home/kevo/Desktop/eg.mp4"; 
            //= "file:///c:/Users/Kevo/Downloads/bbb.mp4";

    private boolean atEndOfMedia = false;
    //private final Media media = new Media(MEDIA);

    @Override
    public void start(final Stage primaryStage) {

        primaryStage.setTitle("VideoAnalysis");
        primaryStage.setFullScreen(true);
        VBox mainBox = new VBox();
        StackPane root = new StackPane();

        //buttons
        final Button btnPlay = new Button(">");
        Button btnStepF = new Button(">|");
        Button btnStepB = new Button("|<");
        Button stop = new Button("stop");
        Button slow = new Button("slow");
        Button reset = new Button("reset");

        //panels
        final HBox m1cntrl1 = new HBox(12);
        m1cntrl1.setAlignment(Pos.CENTER);
        final VBox vbox = new VBox(10);

        //p1
        m1cntrl1.getChildren().add(btnStepB);
        m1cntrl1.getChildren().add(btnPlay);
        m1cntrl1.getChildren().add(btnStepF);
        m1cntrl1.getChildren().add(stop);
        m1cntrl1.getChildren().add(reset);

        //p2
        m1cntrl1.getChildren().add(slow);        

        //media player                 
        Media clip = new Media(MEDIA);
        final Label status = new Label();
        final MediaPlayer mp = new MediaPlayer(clip);
        mp.setMute(true);
        final MediaView mediaView = new MediaView();
        mediaView.setMediaPlayer(mp);
        final DoubleSlider dbl = new DoubleSlider();
        //menubar layout 
        //====================================================================
        //fileChooser
        final FileChooser fileChooser = new FileChooser();
        final Menu menu1 = new Menu("File");
        MenuItem openf = new MenuItem("Open file");
        openf.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                //open file chooser, set variable
                configureFileChooser(fileChooser);
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    MEDIA = "file:///" + file.toString();
                    MEDIA = MEDIA.replace('\\', '/');
                    final MediaPlayer mediaPlayer = new MediaPlayer(new Media(MEDIA));
                    mediaView.setMediaPlayer(mediaPlayer);
                    System.out.print("Media changed: " + MEDIA + "\n");

                    mediaPlayer.currentTimeProperty()
                            .addListener(new ChangeListener<Duration>() {
                                @Override
                                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue
                                ) {
                                    final MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
                                    //slider.setValue(newValue.toSeconds());
                                    double diff = round(mediaPlayer.getStopTime().toSeconds() - mediaPlayer.getCurrentTime().toSeconds(),2);
                                    status.setText(mediaPlayer.getStatus().toString() + "\n time elapsed \t"
                                            + round(mediaPlayer.getCurrentTime().toSeconds(),2) + " / "
                                            + round(mediaPlayer.getStopTime().toSeconds(),2)
                                            + "\n time left: " + diff);
                                    if (diff < 0.04 && diff > 0) {
                                        btnPlay.setText("finished");
                                        mediaPlayer.stop();
                                        //mediaPlayer.setOnStopped(null);
                                    }
                                    mediaPlayer.setOnReady(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    int w = mediaPlayer.getMedia().getWidth();
                                                    int h = mediaPlayer.getMedia().getHeight();
                                                    primaryStage.setMinWidth(w);
                                                    primaryStage.setMinHeight(h);
                                                    vbox.setMinSize(w, 100);
                                                    dbl.setMin(0.0);
                                                    dbl.setMax(mediaPlayer.getTotalDuration().toSeconds());
                                                }
                                            }
                                    );

                                }
                            });
                }
            }
        });
        
               
        m1cntrl1.getChildren().add(status);
        Scene scene = new Scene(root, max(800, mainBox.getWidth() + 10), 550, Color.GRAY);
        scene.getStylesheets().addAll(SimplePlayer.class.getResource("double_slider.css").toExternalForm());

        //ipbox
        MenuItem openu = new MenuItem("Open URL");
        openu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                //open file chooser, set variable
                System.out.print("URL function\n");
                //IP ipbox = new IP();                
            }
        });

        MenuItem close = new MenuItem("Close");
        close.setAccelerator(KeyCombination.keyCombination("Alt+F4"));
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                //open file chooser, set variable
                primaryStage.close();
                System.exit(0);
            }
        });

        //final Menu menu2 = new Menu("Options");
        //final Menu menu3 = new Menu("Help");
        menu1.getItems().addAll(openf, openu, close);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu1); //menu2, menu3);
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        final VBox menuBox = new VBox();
        menuBox.getChildren().add(menuBar);
        //====================================================================
        System.out.print(MEDIA + '\n');

        //labels for information        
        vbox.setPadding(new Insets(0, 3, 3, 8));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(menuBar);
        vbox.getChildren().add(mediaView);
        vbox.getChildren().add(m1cntrl1);        

        //double slider
        dbl.setMajorTickUnit(50f);
        //vbox.getChildren().add(dbl);
        vbox.getChildren().add(createSliderBox(dbl, mediaView));
        //vbox.getChildren().add(slider);

        mainBox.getChildren().addAll(menuBox, vbox);

        btnPlay.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
                Status status = mediaPlayer.getStatus();
                System.out.print("playing: " + MEDIA + "\n");
                //boolean stopped;
                //stopped = mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED);

                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    return;
                }
                if (status == Status.PAUSED
                        || status == Status.READY
                        || status == Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                        atEndOfMedia = false;
                    }
                    mediaPlayer.play();
                    btnPlay.setText("||");
                } else {
                    mediaPlayer.pause();
                    btnPlay.setText(">");
                }

            }
        });

        //stop function for mediaplayer2
        stop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
                mediaPlayer.stop();

                btnPlay.setText(">");
            }
        });

        reset.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
                //reset modifications                 
                mediaPlayer.setStartTime(Duration.ZERO);
                mediaPlayer.pause();
                mediaPlayer.setCycleCount(1);
                mediaPlayer.setRate(1);
            }
        });

        btnStepF.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
                        double fps = 50;
                        double nextFrame = mediaPlayer.getCurrentTime().toMillis() + fps;
                        mediaPlayer.seek(Duration.millis(nextFrame));
                    }
                }
        );

        btnStepB.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
                        double fps = 50;
                        double prevFrame = mediaPlayer.getCurrentTime().toMillis() - fps;
                        mediaPlayer.seek(Duration.millis(prevFrame));
                    }
                }
        );

        slow.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
                        double rate = 0.5;
                        mediaPlayer.setRate(rate);
                    }
                }
        );

        root.getChildren().add(mainBox);

        //Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static HBox createSliderBox(DoubleSlider dSlider, final MediaView mediaView) {

        HBox sliderBox = new HBox(100);
        //textfields
        final Button ABLoop = new Button("A-B");
        final TextField setA = new TextField();
        final TextField setB = new TextField();

        setA.setPromptText("A");
        setA.setMaxWidth(50);
        setB.setPromptText("B");
        setB.setMaxWidth(50);
        sliderBox.getChildren().add(dSlider);
        sliderBox.getChildren().add(setA);
        sliderBox.getChildren().add(setB);
        sliderBox.getChildren().add(ABLoop);

        sliderBox.setAlignment(Pos.CENTER);
        dSlider.value1Property().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0,
                    Number arg1, Number arg2) {
                setA.setText(twoDp.format(arg2.doubleValue()));
            }
        });
        dSlider.value2Property().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0,
                    Number arg1, Number arg2) {
                setB.setText(twoDp.format(arg2.doubleValue()));
            }
        });

        ABLoop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
                String startString = setA.getText();
                double start = round(Double.parseDouble(startString) / 100 * mediaPlayer.getMedia().getDuration().toSeconds(),2);
                String stopString = setB.getText();
                double stop = round(Double.parseDouble(stopString) / 100 * mediaPlayer.getMedia().getDuration().toSeconds(),2);

                System.out.print(start + "\n" + stop + "\n");
                mediaPlayer.setStartTime(Duration.seconds(start));
                mediaPlayer.setStopTime(Duration.seconds(stop));
                mediaPlayer.cycleCountProperty();
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.play();
            }
        }
        );
        return sliderBox;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Play media");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
