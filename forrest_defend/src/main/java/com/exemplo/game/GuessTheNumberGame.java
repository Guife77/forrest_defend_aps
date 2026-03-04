package main.java.com.exemplo.game;

import java.util.Random;
import java.util.Scanner;

public class GuessTheNumberGame {

    private final Random random = new Random();

    public void run() {
        System.out.println("=== Guess The Number (Console) ===");
        System.out.println("Regras: eu penso num número, você tenta adivinhar. Digite 'q' pra sair.\n");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                int max = askMax(scanner);
                int secret = random.nextInt(max) + 1;
                int attempts = 0;

                System.out.printf("Pensei em um número entre 1 e %d. Manda ver!\n", max);

                while (true) {
                    System.out.print("> ");
                    String input = scanner.nextLine().trim();

                    if (input.equalsIgnoreCase("q")) {
                        System.out.println("Falou! Saindo do jogo.");
                        return;
                    }

                    Integer guess = parseInt(input);
                    if (guess == null) {
                        System.out.println("Isso não é número, fera. Tenta de novo (ou 'q' pra sair).");
                        continue;
                    }

                    attempts++;

                    if (guess < 1 || guess > max) {
                        System.out.printf("Vai com calma. É entre 1 e %d.\n", max);
                        continue;
                    }

                    if (guess < secret) {
                        System.out.println("Muito baixo.");
                    } else if (guess > secret) {
                        System.out.println("Muito alto.");
                    } else {
                        System.out.printf("Acertou! Número: %d | Tentativas: %d\n", secret, attempts);
                        break;
                    }
                }

                System.out.print("\nJogar de novo? (s/n): ");
                String again = scanner.nextLine().trim();
                if (!again.equalsIgnoreCase("s") && !again.equalsIgnoreCase("sim")) {
                    System.out.println("Beleza. Até mais!");
                    return;
                }
                System.out.println();
            }
        }
    }

    private int askMax(Scanner scanner) {
        while (true) {
            System.out.print("Escolha a dificuldade (máximo do número, ex: 10 / 50 / 100) ou 'q' pra sair: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) {
                System.out.println("Falou! Saindo do jogo.");
                System.exit(0);
            }

            Integer max = parseInt(input);
            if (max == null || max < 2 || max > 1_000_000) {
                System.out.println("Manda um número válido (>=2 e <=1.000.000).");
                continue;
            }
            return max;
        }
    }

    private Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}