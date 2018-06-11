package mitsk.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GUI extends JFrame {
    private Integer clients = 0;

    private JTextField clientsTextField = new JTextField(clients.toString());

    private Integer impatient = 0;

    private JTextField impatientTextField = new JTextField(impatient.toString());

    private Integer inQueue = 0;

    private JTextField inQueueTextField = new JTextField(inQueue.toString());

    GUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setTitle("Restaurant Federation");

        {
            clientsTextField.setEditable(false);
            clientsTextField.setHorizontalAlignment(JTextField.CENTER);

            JLabel clientsLabel = new JLabel("Clients");
            clientsLabel.setLabelFor(clientsTextField);
            clientsLabel.setVerticalTextPosition(JLabel.CENTER);
            clientsLabel.setHorizontalTextPosition(JLabel.CENTER);

            JPanel clientsPanel = new JPanel(new GridLayout(2, 1));
            clientsPanel.add(clientsLabel);
            clientsPanel.add(clientsTextField);
            clientsPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
            add(clientsPanel);
        }

        {
            impatientTextField.setEditable(false);
            impatientTextField.setHorizontalAlignment(JTextField.CENTER);

            JLabel impatientLabel = new JLabel("Impatient Clients");
            impatientLabel.setLabelFor(impatientTextField);
            impatientLabel.setVerticalTextPosition(JLabel.CENTER);
            impatientLabel.setHorizontalTextPosition(JLabel.CENTER);

            JPanel impatientPanel = new JPanel(new GridLayout(2, 1));
            impatientPanel.add(impatientLabel);
            impatientPanel.add(impatientTextField);
            impatientPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
            add(impatientPanel);
        }

        {
            inQueueTextField.setEditable(false);
            inQueueTextField.setHorizontalAlignment(JTextField.CENTER);

            JLabel inQueueLabel = new JLabel("Queue");
            inQueueLabel.setLabelFor(inQueueTextField);
            inQueueLabel.setVerticalTextPosition(JLabel.CENTER);
            inQueueLabel.setHorizontalTextPosition(JLabel.CENTER);

            JPanel inQueuePanel = new JPanel(new GridLayout(2, 1));
            inQueuePanel.add(inQueueLabel);
            inQueuePanel.add(inQueueTextField);
            inQueuePanel.setBorder(new EmptyBorder(16, 16, 16, 16));
            add(inQueuePanel);
        }

        setLayout(new GridLayout(1, 3));
    }

    void addClient() {
        ++clients;

        updateAll();
    }

    void addImpatientClient() {
        removeClient();

        removeFromQueue();

        ++impatient;

        updateAll();
    }

    void addToQueue() {
        ++inQueue;

        updateAll();
    }

    void removeClient() {
        --clients;

        updateAll();
    }

    void removeFromQueue() {
        --inQueue;

        updateAll();
    }

    public void run() {
        pack();

        setVisible(true);
    }

    private void updateAll() {
        clientsTextField.setText(clients.toString());

        impatientTextField.setText(impatient.toString());

        inQueueTextField.setText(inQueue.toString());

        validate();

        repaint();

        pack();
    }
}
