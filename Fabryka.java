package sample;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Fabryka extends Thread {

    private volatile LinkedList<String> buf;
    private Semaphore wolne;
    private Semaphore zajete;

    private Semaphore chron_k;


    private Circle koloK;
    private Circle koloL;
    private Circle koloM;
    private Circle wyprodukowany;

    private Random los = new Random();
    private Text pojemnoscMagazynu;
    private int minimalnyCzasProdukcji;
    private int iloscWyprodukowanych = 0;
    static boolean wyprodukowanoWszystko = false;
    private static int coProdukuje = 0;

    Fabryka(LinkedList<String> buf, Semaphore wolne, Semaphore zajete, Semaphore chron_k, int minimalnyCzasprodukcji, Text pojemnoscMagazynu,
            Circle koloL, Circle koloK, Circle koloM, Circle wyprodukowany) {
        this.buf = buf;
        this.wolne = wolne;
        this.zajete = zajete;
        this.chron_k = chron_k;
        this.minimalnyCzasProdukcji = minimalnyCzasprodukcji;
        this.pojemnoscMagazynu = pojemnoscMagazynu;
        this.koloK = koloK;
        this.koloL = koloL;
        this.koloM = koloM;
        this.wyprodukowany = wyprodukowany;
    }


    public void run() {
        while (!wyprodukowanoWszystko) {

            if (iloscWyprodukowanych == Controller.maxProduktow)
                wyprodukowanoWszystko = true;

            if (Fabryka.coProdukuje == 0)
                wyprodukowany.setFill(Color.BLACK);
            else if (Fabryka.coProdukuje == 1)
                wyprodukowany.setFill(Color.TOMATO);
            else if (Fabryka.coProdukuje == 2)
                wyprodukowany.setFill(Color.VIOLET);

            producent_start();
            //pobranie bufora


            if (buf.contains("L") && buf.contains("K") && buf.contains("M")) {
                koloL.setVisible(false);
                koloL.setTranslateX(95);
                koloL.setTranslateY(140);

                koloM.setVisible(false);
                koloM.setTranslateX(380);
                koloM.setTranslateY(140);

                koloK.setVisible(false);
                koloK.setTranslateX(240);
                koloK.setTranslateY(140);

                pojemnoscMagazynu.setText("Produkuje LKM");

                buf.clear();
                System.out.println("PRODUKUJE LKM");
                try {
                    sleep(los.nextInt(1000) + minimalnyCzasProdukcji + 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                coProdukuje = 1;
                pojemnoscMagazynu.setText("WYPRODUKOWALEM LKM");
                iloscWyprodukowanych++;
                System.out.println("Ilosc wyprodukowanych produktow: "+iloscWyprodukowanych);
                TranslateTransition temp = new TranslateTransition();
                temp.setDuration(Duration.millis(200));
                temp.setToY(350);
                temp.setNode(wyprodukowany);

                Platform.runLater(temp::play);
                temp.setOnFinished(e ->
                {
                    wyprodukowany.setTranslateY(300);
                    wyprodukowany.setTranslateX(200);
                });
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else if (buf.contains("K") && buf.contains("M")) {

                koloM.setVisible(false);
                koloM.setTranslateX(380);
                koloM.setTranslateY(140);

                koloK.setVisible(false);
                koloK.setTranslateX(240);
                koloK.setTranslateY(140);

                buf.clear();
                pojemnoscMagazynu.setText("Produkuje KM");
                System.out.println("PRODUKUJE KM");
                try {
                    sleep(los.nextInt(1000) + minimalnyCzasProdukcji + 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pojemnoscMagazynu.setText("WYPRODUKOWALEM KM");
                iloscWyprodukowanych++;
                System.out.println("Ilosc wyprodukowanych produktow: "+iloscWyprodukowanych);
                TranslateTransition temp = new TranslateTransition();
                temp.setDuration(Duration.millis(200));
                temp.setToY(350);
                temp.setNode(wyprodukowany);

                Platform.runLater(temp::play);
                temp.setOnFinished(e ->
                {
                    wyprodukowany.setTranslateY(300);
                    wyprodukowany.setTranslateX(200);
                });
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                coProdukuje = 2;
            }

            wolne.release();
            chron_k.release();

            //CO ROBIE Z MATERIALEM
            if (coProdukuje == 1)
                System.out.println("LKM NA RYNEK");
            else if (coProdukuje == 2)
                System.out.println("KMM wysyłam");

            if (wyprodukowanoWszystko)
                System.out.println("Wyprodukowano zadana ilosc produktow!");
        }
    }

    private void producent_start() {
        try {
            zajete.acquire();
            chron_k.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
