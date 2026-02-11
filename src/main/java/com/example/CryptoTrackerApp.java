package com.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CryptoTrackerApp extends JFrame {

    private final CryptoDataFetcher fetcher;
    private final JPanel container;
    private List<CryptoDataFetcher.Crypto> currentCryptos;

    public CryptoTrackerApp() {
        super("Crypto Tracker");
        fetcher = new CryptoDataFetcher();

        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(245,245,245));
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane);
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        startUpdating();
    }

    private void startUpdating() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::updateData, 0, 15, TimeUnit.SECONDS);
    }

    private void updateData() {
        try {
            List<CryptoDataFetcher.Crypto> top5 = fetcher.fetchTop5();

            // Оновлюємо історію для графіку
            if (currentCryptos != null) {
                for (int i = 0; i < top5.size(); i++) {
                    CryptoDataFetcher.Crypto old = currentCryptos.get(i);
                    CryptoDataFetcher.Crypto fresh = top5.get(i);
                    fresh.history = old.history;
                    fresh.history.add(fresh.price);
                    if (fresh.history.size() > 10) fresh.history.remove(0);
                }
            }

            currentCryptos = top5;

            SwingUtilities.invokeLater(() -> {
                container.removeAll();
                for (CryptoDataFetcher.Crypto c : top5) {
                    JPanel panel = new JPanel();
                    panel.setLayout(new BorderLayout());
                    panel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                            BorderFactory.createEmptyBorder(10,10,10,10)
                    ));
                    panel.setBackground(Color.WHITE);
                    panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

                    JLabel label = new JLabel(c.name + " (" + c.symbol.toUpperCase() + ")");
                    label.setFont(new Font("Arial", Font.BOLD, 18));

                    JLabel priceLabel = new JLabel("$" + String.format("%.2f", c.price));
                    priceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                    if (c.history.size() >= 2 && c.price >= c.history.get(c.history.size()-2)) {
                        priceLabel.setForeground(new Color(0,150,0)); // зелена ціна
                    } else {
                        priceLabel.setForeground(Color.RED);
                    }

                    JPanel topPanel = new JPanel(new BorderLayout());
                    topPanel.setBackground(Color.WHITE);
                    topPanel.add(label, BorderLayout.WEST);
                    topPanel.add(priceLabel, BorderLayout.EAST);

                    CryptoGraphPanel graph = new CryptoGraphPanel(c.history);

                    panel.add(topPanel, BorderLayout.NORTH);
                    panel.add(graph, BorderLayout.CENTER);

                    container.add(panel);
                    container.add(Box.createRigidArea(new Dimension(0,10)));
                }
                container.revalidate();
                container.repaint();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CryptoTrackerApp::new);
    }
}
