package sample;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class Controller {
    private final int rozmiar_bufora = 3;
    @FXML
    Button startButton;
    @FXML
    TextField minimalnyCzasProdukcji;
    @FXML
    TextField minimalnyCzasTransportu;
    @FXML
    TextField iloscProduktow;
    @FXML
    Text IloscL;
    @FXML
    Text IloscK;
    @FXML
    Text IloscM;
    @FXML
    AnchorPane mainPane;
    @FXML
    Text pojemnoscMagazynu;

    private LinkedList<String> buf = new LinkedList<>();
    private Semaphore wolne = new Semaphore(rozmiar_bufora);
    private Semaphore zajete = new Semaphore(0);
    private Semaphore chron_j = new Semaphore(1);
    private Semaphore chron_k = new Semaphore(1);
    private int minCzasProdukcji;
    private int minCzasTransportu;
    static int maxProduktow;

    @FXML
    public void startuj() {
        try {
            minCzasProdukcji = Integer.parseInt(minimalnyCzasProdukcji.getText());
            minCzasTransportu = Integer.parseInt(minimalnyCzasTransportu.getText());
            maxProduktow = Integer.parseInt(iloscProduktow.getText());

            //Przygotowanie

            //Tworzenie fabryki
            //Tworzenie magazynu
            //Tworzenie transportu
        } catch (NumberFormatException e) {
            System.out.println("Nie wprowadzono liczb");
        }
        Circle koloL = new Circle(10);
        mainPane.getChildren().add(koloL);
        Circle koloK = new Circle(10);
        mainPane.getChildren().add(koloK);
        Circle koloM = new Circle(10);
        mainPane.getChildren().add(koloM);
        koloL.toFront();
        koloM.toFront();
        koloK.toFront();

        Magazyn magazynL = new Magazyn(buf, wolne, zajete, chron_j, "L", 2, IloscL, koloL);
        Magazyn magazynK = new Magazyn(buf, wolne, zajete, chron_j, "K", 5, IloscK, koloK);
        Magazyn magazynM = new Magazyn(buf, wolne, zajete, chron_j, "M", 5, IloscM, koloM);

        magazynL.start();
        magazynK.start();
        magazynM.start();

        Circle wyprodukowany = new Circle(20);

        mainPane.getChildren().add(wyprodukowany);
        Fabryka fabryka = new Fabryka(buf, wolne, zajete, chron_k, minCzasProdukcji, pojemnoscMagazynu, koloL, koloK, koloM, wyprodukowany);
        fabryka.start();

        Rectangle dostawczak = new Rectangle(25, 10);
        dostawczak.setTranslateY(30);
        mainPane.getChildren().add(dostawczak);
        dostawczak.setFill(Color.GOLD);
        new Thread(() ->
        {
            Random los = new Random();
            for (int i = 0; i < 100; i++) {
                try {
                    sleep(los.nextInt(1500) + minCzasTransportu + 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                TranslateTransition temp1 = new TranslateTransition();
                temp1.setDuration(Duration.millis(500));
                temp1.setToX(380);
                temp1.setNode(dostawczak);

                TranslateTransition temp2 = new TranslateTransition();
                temp2.setDuration(Duration.millis(500));
                temp2.setToX(0);
                temp2.setNode(dostawczak);

                SequentialTransition a = new SequentialTransition(temp1, temp2);

                a.setOnFinished(e ->
                {
                    magazynK.zwiekszIloscTowaru(3);
                    magazynM.zwiekszIloscTowaru(4);
                    magazynL.zwiekszIloscTowaru(2);
                });
                if(!Fabryka.wyprodukowanoWszystko)
                    Platform.runLater(a::play);
            }
        }).start();
    }
}

