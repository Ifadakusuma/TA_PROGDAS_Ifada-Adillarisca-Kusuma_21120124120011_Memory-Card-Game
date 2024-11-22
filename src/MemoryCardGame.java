import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class MemoryCardGame extends JFrame {
    private int lives;
    private int score;
    private int matchedPairs;
    private ArrayList<Card> selectedCards;
    private JLabel livesLabel, scoreLabel;
    private ArrayList<ImageIcon> images;
    private ArrayList<Card> cards;
    private String[] imagePaths = {"img1.jpg", "img2.jpg", "img3.jpg", "img4.jpg", "img5.jpg",
            "img6.jpg", "img7.jpg", "img8.jpg", "img9.jpg", "img10.jpg"};
    private static final int CARD_SIZE = 130;
    private JButton startButton;
    private JPanel cardPanel, homePanel, scorePanel;
    private JTextField playerNameField;
    private boolean gameStarted;
    private boolean isChecking = false;
    private String playerName;

    public MemoryCardGame() {
        setTitle("Memory Card Game");
        setSize(800, 700);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        homePanel = new BackgroundPanel("backgroundd.jpg");
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel gameTitle = new JLabel("Memory Card Game");
        gameTitle.setFont(new Font("Serif", Font.BOLD, 65));
        gameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        homePanel.add(gameTitle);

        homePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        Font buttonFont = new Font("Serif", Font.BOLD, 28);

        startButton = new JButton("Yok Mulaii");
        startButton.setFont(buttonFont);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> startGame());
        homePanel.add(startButton);

        homePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton howToPlayButton = new JButton("Cara Mainn?");
        howToPlayButton.setFont(buttonFont);
        howToPlayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        howToPlayButton.addActionListener(e -> showRules());
        homePanel.add(howToPlayButton);

        //untuk nampung label dan text field input pemain
        JPanel playerNamePanel = new JPanel();
        playerNamePanel.setLayout(new BoxLayout(playerNamePanel, BoxLayout.Y_AXIS));
        playerNamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerNamePanel.setMaximumSize(new Dimension(250, 80));
        playerNamePanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Isi Nama Kamu:");
        nameLabel.setFont(new Font("Serif", Font.BOLD, 20));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerNamePanel.add(nameLabel);

        playerNamePanel.add(Box.createRigidArea(new Dimension(0, 15)));

        playerNameField = new JTextField(10);
        playerNameField.setFont(new Font("Arial", Font.BOLD, 20));
        playerNameField.setMaximumSize(new Dimension(200, 30));
        playerNamePanel.add(playerNameField);

        homePanel.add(Box.createRigidArea(new Dimension(0, 30)));
        homePanel.add(playerNamePanel);

        scorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        livesLabel = new JLabel("Nyawa Kamu: 4");
        livesLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scorePanel.add(livesLabel);
        scorePanel.add(scoreLabel);
        scorePanel.setVisible(false);


        add(homePanel, BorderLayout.CENTER);
        add(scorePanel, BorderLayout.SOUTH);
    }

    //aktif saat memulai ulang permainan dari awal
    private void initializeGame() {
        lives = 4;
        score = 0;
        matchedPairs = 0;
        selectedCards = new ArrayList<>();
        images = new ArrayList<>();
        cards = new ArrayList<>();
        isChecking = false;

        livesLabel.setText("Nyawa Kamu: " + lives);
        scoreLabel.setText("Score: " + score);
    }

    //memastikan semua gambar dimuat dan menyiapkan gambar pasangan kartu
    private void loadImages() {
        images.clear();
        for (String path : imagePaths) {
            try {
                ImageIcon icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(CARD_SIZE, CARD_SIZE, Image.SCALE_SMOOTH));
                images.add(icon);
            } catch (Exception e) {
                System.err.println("Error loading image: " + path);
            }
        }
        if (images.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No images found. Please check the image paths.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        images.addAll(new ArrayList<>(images));
        Collections.shuffle(images);
    }

    //ika permainan dimulai ulang, panel kartu lama akan dihapus dari frame untuk diganti dengan yang baru.
    private void createCards() {
        if (cardPanel != null) {
            remove(cardPanel);
        }

        // Menggunakan GridBagLayout untuk menata kartu dengan rapi
        cardPanel = new BackgroundPanel("backgroundd.jpg");
        cardPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  // Menambahkan jarak antar kartu
        gbc.anchor = GridBagConstraints.CENTER;  // Menjaga kartu tetap terpusat

        int row = 0;
        int col = 0;

        // Menambahkan kartu ke dalam grid
        for (ImageIcon image : images) {
            Card card = new Card(image);
            card.addActionListener(new CardListener(card));
            cards.add(card);

            // Set posisi kartu dalam grid
            gbc.gridx = col;
            gbc.gridy = row;

            // Tambahkan kartu ke panel
            cardPanel.add(card, gbc);

            // Mengatur posisi kartu berikutnya
            col++;
            if (col == 5) {  // Setiap baris ada 5 kartu
                col = 0;
                row++;
            }
        }

        add(cardPanel, BorderLayout.CENTER);
    }

    private void showRules() {
        JOptionPane.showMessageDialog(this, """
            1) Hafalkan posisi kartu dengan cepat saat semua kartu terbuka di awal permainan.
            2) Cocokkan pasangan kartu untuk mendapatkan poin.
            3) Apabila kartu yang dipilih tidak cocok, Anda kehilangan satu nyawa.
            4) Permainan berakhir saat semua pasangan ditemukan atau nyawa habis.
            """, "Jadi Mainnya Gini Nih", JOptionPane.INFORMATION_MESSAGE);
    }

    private void startGame() {
        playerName = playerNameField.getText().trim();
        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "EITSS ISI NAMA KAMU DULU DONG!!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (gameStarted) return;

        initializeGame();
        homePanel.setVisible(false);
        scorePanel.setVisible(true);

        loadImages();
        createCards();

        gameStarted = true;
        startButton.setEnabled(false);

        for (Card card : cards) {
            card.reveal();
        }

        Timer timer = new Timer(3000, e -> {
            for (Card card : cards) {
                card.hide();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void restartGame() {
        initializeGame();
        loadImages();
        createCards();

        for (Card card : cards) {
            card.reveal();
        }

        Timer timer = new Timer(3000, e -> {
            for (Card card : cards) {
                card.hide();
            }
        });
        timer.setRepeats(false);
        timer.start();

        revalidate();
        repaint();
    }

    private void gameOver() {
        int choice = JOptionPane.showOptionDialog(
                this,
                playerName + ", Kamu Kalah Nih! Final Score: " + score + "\nHabis ini kamu mau pilih yang mana?",
                "Kalah Deh",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Main Lagi", "Kembali ke Menu"},
                "Main Lagi"
        );

        switch (choice) {
            case JOptionPane.YES_OPTION:
                restartGame();
                break;
            case JOptionPane.NO_OPTION:
                resetToHome();
                break;
            case JOptionPane.CANCEL_OPTION:
                System.exit(0); // Keluar dari aplikasi
                break;
            default:
                resetToHome();
                break;
        }
    }

    private void youWin() {
        int choice = JOptionPane.showOptionDialog(
                this,
                playerName + ", Selamat yaaa! Kamu Menang! Final Score: " + score + "\nHabis ini kamu mau pilih yang mana?",
                "HOREEE!!!!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Main Lagi", "Kembali ke Menu"},
                "Main Lagi"
        );


        switch (choice) {
            case JOptionPane.YES_OPTION:
                restartGame();
                break;
            case JOptionPane.NO_OPTION:
                resetToHome();
                break;
            case JOptionPane.CANCEL_OPTION:
                System.exit(0); // Keluar dari aplikasi
                break;
            default:
                resetToHome();
                break;
        }
    }

    private void resetToHome() {
        gameStarted = false;
        if (cardPanel != null) {
            remove(cardPanel);
            cardPanel = null;
        }
        homePanel.setVisible(true);
        scorePanel.setVisible(false);
        startButton.setEnabled(true);

        playerNameField.setText("");
        playerName = null;

        if (selectedCards != null) selectedCards.clear();
        if (images != null) images.clear();
        if (cards != null) cards.clear();

        revalidate();
        repaint();
    }

    private class CardListener implements ActionListener {
        private final Card card;

        public CardListener(Card card) {
            this.card = card;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameStarted && !card.isRevealed() && !isChecking) {
                card.reveal();
                selectedCards.add(card);

                if (selectedCards.size() == 2) {
                    isChecking = true;
                    Timer timer = new Timer(500, ev -> {
                        checkCards();
                        selectedCards.clear();
                        isChecking = false;
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        }

        private void checkCards() {
            Card firstCard = selectedCards.get(0);
            Card secondCard = selectedCards.get(1);

            if (firstCard.getImage().equals(secondCard.getImage())) {
                score++;
                scoreLabel.setText("Score: " + score);
                firstCard.setMatched(true);
                secondCard.setMatched(true);
                matchedPairs++;

                if (matchedPairs == images.size() / 2) {
                    youWin();
                }
            } else {
                firstCard.hide();
                secondCard.hide();
                lives--;
                livesLabel.setText("Nyawa Kamu: " + lives);

                if (lives == 0) {
                    gameOver();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemoryCardGame().setVisible(true));
    }

    class BackgroundPanel extends JPanel {
        private final Image background;

        public BackgroundPanel(String imagePath) {
            this.background = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }

}



    class Card extends JButton {
    private final ImageIcon image;
    private boolean revealed = false;
    private boolean matched = false;

    public Card(ImageIcon image) {
        this.image = image;
        setIcon(null);
        setFocusPainted(false);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(120, 120));
    }

    public ImageIcon getImage() {
        return image;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public boolean isMatched() {
        return matched;
    }

    public void reveal() {
        if (!matched) {
            setIcon(image);
            revealed = true;
        }
    }

    public void hide() {
        if (!matched) {
            setIcon(null);
            revealed = false;
        }
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }
}