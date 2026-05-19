package game.main;

import game.ui.GameWindow;

public class Main {

    public static void main(String[] args) {
        System.out.println("Iniciando Forrest Defend...");
        
        // Aqui nós delegamos a inicialização para a GameWindow, 
        // que já tem toda a lógica pronta para desenhar a tela!
        GameWindow.main(args);
    }
}