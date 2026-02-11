package com.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CryptoGraphPanel extends JPanel {
    private List<Double> prices;

    public CryptoGraphPanel(List<Double> prices) {
        this.prices = prices;
        setPreferredSize(new Dimension(220, 100));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (prices == null || prices.size() < 2) return;

        int w = getWidth();
        int h = getHeight();
        double max = prices.stream().max(Double::compare).orElse(1.0);
        double min = prices.stream().min(Double::compare).orElse(0.0);

        // Малюємо лінії
        for (int i = 0; i < prices.size() - 1; i++) {
            int x1 = i * w / (prices.size() - 1);
            int y1 = h - (int) ((prices.get(i) - min) / (max - min) * h);
            int x2 = (i + 1) * w / (prices.size() - 1);
            int y2 = h - (int) ((prices.get(i + 1) - min) / (max - min) * h);

            g.setColor(prices.get(i+1) >= prices.get(i) ? new Color(0,150,0) : Color.RED);
            ((Graphics2D)g).setStroke(new BasicStroke(2));
            g.drawLine(x1, y1, x2, y2);
        }

        // Додатково: горизонтальна сітка
        g.setColor(new Color(200,200,200));
        for (int i=0; i<=4; i++) {
            int y = i*h/4;
            g.drawLine(0,y,w,y);
        }
    }

    public void setPrices(List<Double> prices) {
        this.prices = prices;
        repaint();
    }
}
