package com.blackjack.service;

import com.blackjack.domain.Cards;
import com.blackjack.domain.Players;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BlackjackService {

    public List<Players> players = new ArrayList();
    public List<Cards> deck = new ArrayList();
    public DeckService deckService = new DeckService();
    public Scanner scanner = new Scanner(System.in);
    public PlayersService playersService = new PlayersService();

    public void startGame() {
        players = new ArrayList();
        deck = deckService.create();
        shuffle(deck);

        System.out.println("\n\n=== BLACKJACK ===");

        System.out.println("Quantos jogadores irão jogar?");
        Integer nPlayers = scanner.nextInt();
        if (nPlayers <= 0) {
            System.out.println("Adicione ao menos um jogador");
        } else {
            createPlayers(nPlayers);
        }

        System.out.println("\n\n=== INICIANDO JOGO ===");
        sendCardsByPlayer();

        AtomicBoolean hasBlackjack = new AtomicBoolean(false);
        while (!hasBlackjack.get()) {
            for (Players p : players) {
                System.out.println("\n\n=== " + p.getName() + ", sua vez de jogar ===");
                System.out.println("Opções:");
                System.out.println("1=Tirar uma carta");
                System.out.println("2=Consultar pontuação");

                Integer optPlayer = scanner.nextInt();
                if (optPlayer == 1) {
                    p = playersService.pullCard(p, deck);
                    if (p.getPoints() == 21) {
                        hasBlackjack.set(true);
                        System.out.println("\n\n=== " + p.getName() + " venceu a partida com " + p.getPoints() + " pontos!! ===");
                        showCardsByPlayer(p);
                        break;
                    }
                } else if (optPlayer == 2) {
                    System.out.println(p.getPoints() + " pontos ate o momento");
                }
            }
        }
    }

    public void shuffle(List<Cards> deck) {
        deck = deckService.shuffle(deck);
    }

    public void showPoints() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        players.forEach(p -> {
            atomicInteger.addAndGet(p.getPoints());
        });

        System.out.println("\n\n=== PONTOS ACUMULADOS ===");
        System.out.println(atomicInteger.get());
    }

    public void createPlayers(Integer n) {
        if (n <= 0) {
            System.out.println("Adicione ao menos um jogador");
        } else {
            for (int i = 0; i < n; i++) {
                System.out.println("Insira o nome do jogador " + (i + 1) + ":");
                players.add(playersService.create(scanner.next()));
            }

            System.out.println("\n\n=== JOGADORES CADASTRADOS ===");
            players.forEach(p -> {
                System.out.println(p.getName());
            });
        }
    }

    public void sendCardsByPlayer() {
        System.out.println("\n\n=== DISTRIBUINDO CARTAS INICIAIS ===");
        players.forEach(p -> {
            p = playersService.pullCard(p, deck);
        });
    }

    public void showCardsByPlayer(Players player) {
        System.out.println("\n\n=== CARTAS DE " + player.getName() + " ===");

        player.getCards().forEach(c -> {
            System.out.println(c.getNaipe() + " de " + c.getName()+" ("+c.getValue()+" pontos)");
        });
    }

}