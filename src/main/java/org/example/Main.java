package org.example;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Main {


    //<editor-fold desc="variables estàtiques">
    static int direccio = 6;                        // 6->dreta  4 -> esquerra  8->amunt   2-> avall


    static String blauC = "\033[3;34m";             // per colorejar de blau
    static String roigN = "\033[1;31m";             // per colorejar de roig
    static String cafeN = "\033[1;33m";             // per colorejar de café
    static String purpuraN = "\033[1;35m";          // per colorejar de café
    static String colorF = purpuraN;                // color Fantasma
    static String verdN = "\033[1;32m";             // per colorejar de verd
    static String nc = "\033[0m";                   // per retornar al color normal
    static boolean pausa = false;                   // per pausar el joc

    static String sistemaOperatiu = System.getProperty("os.name");
    //</editor-fold>


    public static void main(String[] args) throws InterruptedException {

        //<editor-fold desc="Variables Locals">
        int maxF = 20, maxC = 24;
        int[][] taulell = new int[maxF][maxC];
        char pacman = '<';              // serà així: <-    ->   ^|   v|
        char fantasma = '&';            // fantasma
        int contaPunts = 0;             // per contar els punts que hi han a la matriu i els que ens hem menjat
        int pos_I_pacman = maxF / 2 - 5;// posició fila pacman
        int pos_J_pacman = maxC / 2;    // posició col  pacman
        //  int posIF1 = maxF / 2, posIF2 = maxF / 2, posIF3 = maxF / 2;       // posició fila fantasma 1, 2, 3
        // int posJF1 = maxC / 2, posJF2 = maxC / 2, posJF3 = maxC / 2;        // posició col  fantasma 1, 2, 3
        int qMoviments = 0;             // Quantitat de Moviments
        int velocitat = 500;            // velocitat de moviments. S'incrementarà poc a poc
        int maxFantasmes = 4;
        int[][] fantasmes = new int[maxFantasmes][3];   // fantasmes, posició i direcció
        // i -> número de fantasmes
        // j -> (0) - posició X (i)   (1) - posició Y (j)   (2) - direcció (2, 4, 6, 8)
        boolean jocAcabat = false;
        int tempsOuGran = 0;            // temps que té el pacman per menjar-se un fantasma després d'agafar un ou Gran
        //</editor-fold>


        //<editor-fold desc="codi Event Botó">
        Frame f = new Frame("Taulell de control");
        f.setLayout(new FlowLayout());
        f.setSize(500, 100);        // TAMAY DEL FRAME
        Label l = new Label();
        l.setText("Posa ací el ratolí i prem botons dreta o esquera del teclat");
        f.add(l);
        f.setVisible(true);


        // POSEM EL CODI DEL LISTENER (cada cop que premem algun botó)
        KeyListener listener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent event) {
//                if (espai[maxF - 1][llocNau] == -1) {
//
//                }
                if (event.getKeyCode() == KeyEvent.VK_RIGHT) {      // al prèmer tecla dreta
                    direccio = 6;
                }
                if (event.getKeyCode() == KeyEvent.VK_LEFT) {       // al prèmer la tecla esquerra
                    direccio = 4;
                }
                if (event.getKeyCode() == KeyEvent.VK_UP) {       // al prèmer la tecla AMUNT
                    direccio = 8;
                }
                if (event.getKeyCode() == KeyEvent.VK_DOWN) {       // al prèmer la tecla AVALL
                    direccio = 2;
                }
                if (event.getKeyCode() == KeyEvent.VK_P) {      // al prèmer tecla dreta
                    pausa = true;
                }
                if (event.getKeyCode() == KeyEvent.VK_R) {      // al prèmer tecla dreta
                    pausa = false;
                }
            }

            @Override
            public void keyReleased(KeyEvent event) {
                //       printEventInfo("Key Released", event);
            }

            @Override
            public void keyTyped(KeyEvent event) {
                //      printEventInfo("Key Typed", event);
            }

            private void printEventInfo(String str, KeyEvent e) {
//                System.out.println(str);
//                int code = e.getKeyCode();
//                System.out.println("   Code: " + KeyEvent.getKeyText(code));
//                System.out.println("   Char: " + e.getKeyChar());
//                int mods = e.getModifiersEx();
//                System.out.println("    Mods: "
//                        + KeyEvent.getModifiersExText(mods));
//                System.out.println("    Location: "
//                        + keyboardLocation(e.getKeyLocation()));
//                System.out.println("    Action? " + e.isActionKey());
            }

            private String keyboardLocation(int keybrd) throws InterruptedException {
                switch (keybrd) {
                    case KeyEvent.KEY_LOCATION_RIGHT:

                        // return "Right";

                    case KeyEvent.KEY_LOCATION_LEFT:
                        // canviaPos(false);
                        //   return "Left";

                    case KeyEvent.KEY_LOCATION_NUMPAD:
                        return "NumPad";
                    case KeyEvent.KEY_LOCATION_STANDARD:
                        return "Standard";
                    case KeyEvent.KEY_LOCATION_UNKNOWN:
                    default:
                        return "Unknown";
                }
            }
        };

        // AFEGIM EL listener AL JPANELL CREAT DALT
        f.addKeyListener(listener);


        if (sistemaOperatiu.contains("indows")) {
            //  blauC = roigN = cafeN = purpuraN = colorF = nc = "";          // tristament windows no té colors a la terminal
            System.out.println("DETECTAT SISTEMA OPERATIU WINDOWS");
            System.out.println(" NO FUNCIONARÀ AMB cmd NI POWER SHELL");
            System.out.println("INSTAL·LA LA CONSOLA git bash I EXECUTA DES D'ALLÀ");
            Thread.sleep(7000);

        }


        //</editor-fold>


        /** CODIFICACIÓ DEL TAULELL
         *
         *
         * inicialitzem taulell amb parets
         * -1 -> paret  -> #
         *  0 -> punts  -> ·
         *  1 -> espai en blanc (no hi han punts)
         * 2 -> Ou gran-> Arroba (@)
         * 3 -> pacman ->   <   o   -
         * fantasmes[numFantasma][PosicioX,PosicioY,direccio] -> fantasma -> &
         */


        //<editor-fold desc="CREACIÓ TAULELL">
        for (int i = 0; i < maxF; i++) {
            for (int j = 0; j < maxC; j++) {


                // POSEM ELS OUS GRANS (@) en les cantonades!!!
                if (
                        i == 2 && j == 2 ||
                                i == 2 && j == maxC - 3 ||
                                i == maxF - 3 && j == 2 ||
                                i == maxF - 3 && j == maxC - 3
                )

                    taulell[i][j] = 2;

                else if (

                        (i >= maxF / 2 - 2 && i <= maxF / 2 + 1) && (j >= maxC / 2 - 2 && j <= maxC - 11)   // posem les parets del mig
                                ||
                                (i >= maxF / 2 - 2 && i <= maxF / 2 + 1 && (j <= 3 || j >= maxC - 4))       // parets laterals del mig esquerra i dreta
                                ||
                                (i == 0 || j == 0 || i == maxF - 1 || j == maxC - 1)                        // parets externes (quadrat marc)
                                ||
                                ((i == 4 || i == maxF - 5) && j > 1 && j < maxC - 2)                        // paret de la fila 4 i maxF-5
                                ||
                                (((j == 6 || j == 8 || j == 15 || j == 17) && i > 5 && i < maxF - 6))       // parets de les columnes 6,8,15 i 17
                                ||
                                ((i == 2 || i == maxF - 3) && j > 3 && j < maxC - 4)                        // parets de la fila 2 i maxF-3

                )

                    taulell[i][j] = -1;                                         // posem paret

                else if (i == pos_I_pacman && j == pos_J_pacman)                // lloc inicial pacman
                    taulell[i][j] = pacman;
                else
                    taulell[i][j] = 0;                                          // la resta posem ous petits (0's)
            }
        }
        //</editor-fold>


        // COMENCEM EL JOC!!
        do {
            // control de la pausa
            if (pausa) {                // si premem la p farem pausa el joc
                Thread.sleep(100);      // hem de frenar el joc encara que sigui 100 ms perquè no acceleri massa
            } else {                    // Si no hi ha pausa continuem el joc

                //<editor-fold desc="Contem els punts">
                contaPunts = 0;
                for (int i = 0; i < maxF; i++) {
                    for (int j = 0; j < maxC; j++) {
                        if (taulell[i][j] == 0 || taulell[i][j] == 2)
                            contaPunts++;
                    }
                }
                //</editor-fold>


                //<editor-fold desc="Moviments del pacman pel taulell">
                //          //////// MOVIMENTS DEL PACMAN //////////

                switch (direccio) {
                    case 6:
                        if (taulell[pos_I_pacman][pos_J_pacman + 1] != -1) {
                            taulell[pos_I_pacman][pos_J_pacman] = 1;
                            pos_J_pacman++;
                        }
                        break;
                    case 4:
                        if (taulell[pos_I_pacman][pos_J_pacman - 1] != -1) {
                            taulell[pos_I_pacman][pos_J_pacman] = 1;
                            pos_J_pacman--;
                        }
                        break;
                    case 8:
                        if (taulell[pos_I_pacman - 1][pos_J_pacman] != -1) {
                            taulell[pos_I_pacman][pos_J_pacman] = 1;
                            pos_I_pacman--;
                        }
                        break;
                    case 2:
                        if (taulell[pos_I_pacman + 1][pos_J_pacman] != -1) {
                            taulell[pos_I_pacman][pos_J_pacman] = 1;
                            pos_I_pacman++;
                        }
                        break;
                }
                if (taulell[pos_I_pacman][pos_J_pacman] == 2)           // si ens menjem un ou gran
                    tempsOuGran = 20;                                  // tenim 20 mossos per menjar-nos els fantasmes

                taulell[pos_I_pacman][pos_J_pacman] = 3;                // posem el pacman en el lloc corresponent al taulell
                //</editor-fold>


                //<editor-fold desc="Lloc inicial dels fantasmes i moviment a través del tauell">
                //          // POSEM ELS FANTASMES ( 3 fantasmes) a partir del moviment 10
                int[] dirs = {2, 4, 6, 8};
                int filaF, colF, dirF;
                Random rnd = new Random();

                if (qMoviments == 10) {         // posem els fantasmes en el moviment 10

                    // inicialitzem els 4 fantasmes. fila, columna i direcció
                    for (int i = 0; i < fantasmes.length; i++) {
                        fantasmes[i][0] = maxF / 2 - 5;             // posició I (fila)
                        fantasmes[i][1] = maxC / 2;                 // posició J (col )
                        fantasmes[i][2] = dirs[rnd.nextInt(dirs.length)]; // direcció (2, 4, 6, 8)  // sols a l'inici
                    }

                    // A partir del moviment 11 van sols ... i giren de manera aleatòria
                } else if (qMoviments > 10) {
                    for (int i = 0; i < fantasmes.length; i++) {

                        filaF = fantasmes[i][0];      // fila
                        colF = fantasmes[i][1];       // col
                        dirF = fantasmes[i][2];       // dir

                        if (dirF == 6) {
                            if (taulell[filaF][colF + 1] != -1) {      // dreta
                                colF++;
                                fantasmes[i][0] = filaF;
                                fantasmes[i][1] = colF;
                            } else {
                                fantasmes[i][2] = dirs[rnd.nextInt(dirs.length)];
                            }
                        } else if (dirF == 8) {
                            if (taulell[filaF - 1][colF] != -1) {      // amunt
                                filaF--;
                                fantasmes[i][0] = filaF;
                                fantasmes[i][1] = colF;
                            } else {
                                fantasmes[i][2] = dirs[rnd.nextInt(dirs.length)];
                            }
                        } else if (dirF == 4) {
                            if (taulell[filaF][colF - 1] != -1) {      // esq
                                colF--;
                                fantasmes[i][0] = filaF;
                                fantasmes[i][1] = colF;
                            } else {
                                fantasmes[i][2] = dirs[rnd.nextInt(dirs.length)];
                            }
                        } else if (dirF == 2) {
                            if (taulell[filaF + 1][colF] != -1) {      // baix
                                filaF++;
                                fantasmes[i][0] = filaF;
                                fantasmes[i][1] = colF;
                            } else {
                                fantasmes[i][2] = dirs[rnd.nextInt(dirs.length)];
                            }
                        }
                    }
                }
                //</editor-fold>


                //<editor-fold desc="Imprimim el taulell">
                // imprimim el taulell
                System.out.println(cafeN + "\n\n\n\n\n\t\t\t\t\t\tPAC" + blauC + "MAN!!" + nc);
                System.out.println(blauC + "\t\t\t\t\t\t---" + cafeN + "-----\n" + nc);
                for (int i = 0; i < maxF; i++) {
                    System.out.print("\t\t");
                    for (int j = 0; j < maxC; j++) {
                        if (tempsOuGran > 0)               // color dels fantasmes
                            colorF = cafeN;
                        else
                            colorF = purpuraN;
                        if (
                                i == fantasmes[0][0] && j == fantasmes[0][1] ||
                                        i == fantasmes[1][0] && j == fantasmes[1][1] ||
                                        i == fantasmes[2][0] && j == fantasmes[2][1] ||
                                        i == fantasmes[3][0] && j == fantasmes[3][1]

                        )
                            System.out.print(colorF + " " + fantasma + " " + nc);   // fantasmes //
                        else if (taulell[i][j] == 0)
                            System.out.print(cafeN + " . " + nc);               // ou petit
                        else if (taulell[i][j] == 1)
                            System.out.print("   ");                            // espai en blanc
                        else if (taulell[i][j] == 3)
                            System.out.print(roigN + " " + pacman + " " + nc);  // pacman
                        else if (taulell[i][j] == 2)
                            System.out.print(blauC + " @ " + nc);               // OU GRAN
                        else if (taulell[i][j] == -1) {
                            if (i == 0 || i == maxF - 1 || j == 0 || j == maxC - 1)
                                System.out.print(verdN + " # " + nc);                            // paret
                            else
                                System.out.print(" # ");                            // paret
                        }


                        ///////// DETECCIO FANTASMES ////////
                        // QUE PILLEN AL PACMAN I EL MATEN //
                        if (taulell[i][j] == 3 &&           // 3 -> pacman

                                (
                                        i == fantasmes[0][0] && j == fantasmes[0][1] ||
                                                i == fantasmes[1][0] && j == fantasmes[1][1] ||
                                                i == fantasmes[2][0] && j == fantasmes[2][1] ||
                                                i == fantasmes[3][0] && j == fantasmes[3][1]
                                )
                        )
                            if (tempsOuGran <= 0)     // Si el temps de l'Ou Gran encara està vigent, ens podem menjar un fantasma
                                jocAcabat = true;     // sinó acabem el joc


                    }
                    System.out.println();
                }


                System.out.println("\n\n");
                System.out.println(roigN + "\t\t\tqueden: " + contaPunts + " punts" + nc);
                System.out.println(blauC + "\t\t\tP) Pausar\tR) Reanudar" + nc);
                System.out.println("\n\n\n\n");
                //</editor-fold>


                //<editor-fold desc="Increment de la velocitat del joc">
                if (qMoviments % 25 == 0 && velocitat > 100)      // 100 màxim de retràs. Sinò es buggueja
                    velocitat = velocitat - 30;

                Thread.sleep(velocitat);          // velocitat del programa
                //</editor-fold>


                //<editor-fold desc="Aspecte Pacman (obri i tanca la boca)">

                qMoviments++;               // quantitat de moviments.
                // canviem l'aspecte de pacman si va a la dreta o a l'esquerra
                if (direccio == 6)
                    if (qMoviments % 2 == 0)
                        pacman = '<';
                    else
                        pacman = '-';

                else if (direccio == 4)
                    if (qMoviments % 2 == 0)
                        pacman = '>';
                    else
                        pacman = '-';

                else if (direccio == 2)
                    if (qMoviments % 2 == 0)
                        pacman = '^';
                    else
                        pacman = '|';

                else if (direccio == 8)
                    if (qMoviments % 2 == 0)
                        pacman = 'v';
                    else
                        pacman = '|';

                if (tempsOuGran > 0)
                    tempsOuGran--;              // descomptem el temps de l'Ou Gran
                //</editor-fold>

            }   // final pausa

        }
        while (contaPunts > 0 && !jocAcabat);

        //<editor-fold desc="Informació Final del Joc">
        System.out.println(cafeN + "\n\n\n\t\t\tFINAL JOC!!!!" + nc);
        if (contaPunts == 0) {
            System.out.println(blauC + "\t\t\tHo has fet en " + qMoviments + " mossos!!!\n\n\n" + nc);
        } else {
            Thread.sleep(30);
            System.err.println(roigN + "\t\t\tT'HA MENJAT UN FANTASMA!!" + nc);
        }
        //</editor-fold>

    }
}