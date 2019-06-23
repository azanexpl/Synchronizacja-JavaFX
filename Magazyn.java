package sample;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Magazyn extends Thread {

    private volatile LinkedList<String> buf;
    private Semaphore wolne;
    private Semaphore zajete;
    private Semaphore chron_j;

    private String skladnik;
    private int stanMagazynu;
    private Text textStanMagazynu;

    private Circle kolo;
    private boolean statusAnimacji = false;

    Magazyn(LinkedList<String> buf, Semaphore wolne, Semaphore zajete, Semaphore chron_j, String skladnik, int stanMagazynu, Text textStanMagazynu,
            Circle kolo) {
        this.buf = buf;
        this.wolne = wolne;
        this.zajete = zajete;
        this.chron_j = chron_j;
        this.skladnik = skladnik;
        this.stanMagazynu = stanMagazynu;
        this.textStanMagazynu = textStanMagazynu;
        this.kolo = kolo;
        kolo.setVisible(true);
        textStanMagazynu.setText(stanMagazynu + "");
        kolo.setTranslateY(140);
        switch (skladnik) {
            case "L":
                kolo.setTranslateX(95);
                kolo.setFill(Color.PALEVIOLETRED);
                break;
            case "K":
                kolo.setTranslateX(240);
                kolo.setFill(Color.LIGHTBLUE);
                break;
            case "M":
                kolo.setTranslateX(380);
                kolo.setFill(Color.GREEN);
                break;
        }
    }


    public void run() {
        while (!Fabryka.wyprodukowanoWszystko) {
            if (!buf.contains(skladnik) && stanMagazynu > 0 && buf.size() < 4) //skladnik nie znajduje sie w produkcji
            {
                //PRODUKUJ
                Platform.runLater(() ->
                        kolo.setVisible(true));


                stanMagazynu--;
                Platform.runLater(
                        () ->
                                textStanMagazynu.setText("" + stanMagazynu)
                );

                SequentialTransition tranportDoFabryki = new SequentialTransition();
                switch (skladnik) {
                    case "L": {
                        TranslateTransition temp1 = new TranslateTransition();
                        temp1.setDuration(Duration.millis(1000));
                        temp1.setToX(95);
                        temp1.setToY(180);
                        temp1.setNode(kolo);

                        tranportDoFabryki.getChildren().add(temp1);

                        TranslateTransition temp2 = new TranslateTransition();
                        temp2.setDuration(Duration.millis(1000));
                        temp2.setToX(150);
                        temp2.setToY(180);
                        temp2.setNode(kolo);

                        tranportDoFabryki.getChildren().add(temp2);

                        TranslateTransition temp3 = new TranslateTransition();
                        temp3.setDuration(Duration.millis(1000));
                        temp3.setToX(150);
                        temp3.setToY(210);
                        temp3.setNode(kolo);

                        tranportDoFabryki.getChildren().add(temp3);
                        break;
                    }
                    case "K": {
                        TranslateTransition temp3 = new TranslateTransition();
                        temp3.setDuration(Duration.millis(1000));
                        temp3.setToX(kolo.getTranslateX());
                        temp3.setToY(210);
                        temp3.setNode(kolo);

                        tranportDoFabryki.getChildren().add(temp3);
                        break;
                    }
                    case "M": {
                        TranslateTransition temp1 = new TranslateTransition();
                        temp1.setDuration(Duration.millis(500));
                        temp1.setToX(380);
                        temp1.setToY(180);
                        temp1.setNode(kolo);

                        tranportDoFabryki.getChildren().add(temp1);

                        TranslateTransition temp2 = new TranslateTransition();
                        temp2.setDuration(Duration.millis(500));
                        temp2.setToX(325);
                        temp2.setToY(180);
                        temp2.setNode(kolo);

                        tranportDoFabryki.getChildren().add(temp2);

                        TranslateTransition temp3 = new TranslateTransition();
                        temp3.setDuration(Duration.millis(500));
                        temp3.setToX(325);
                        temp3.setToY(210);
                        temp3.setNode(kolo);

                        tranportDoFabryki.getChildren().add(temp3);
                        break;
                    }
                }
                statusAnimacji = true;
                Platform.runLater(
                        tranportDoFabryki::play);

                tranportDoFabryki.setOnFinished(e ->
                        statusAnimacji = false
                );
                while (statusAnimacji) {
                    try {
                        sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                proceduraProducenta();
            }
        }
    }

    private void proceduraProducenta() {
        try {
            wolne.acquire();
            chron_j.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        buf.add(skladnik);
        zajete.release();
        chron_j.release();
    }

    void zwiekszIloscTowaru(int i) {
        stanMagazynu += i;
        Platform.runLater(
                () ->
                        textStanMagazynu.setText("" + stanMagazynu)
        );

    }
}
