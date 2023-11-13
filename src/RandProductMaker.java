import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductMaker extends JFrame {
    private JLabel nameLabel, descriptionLabel, idLabel, costLabel, recordCountLabel;
    private JTextField nameTextField, descriptionTextField, idTextField, costTextField, recordCountTextField;
    private JButton addButton, quitButton;
    private RandomAccessFile productFile;

    public RandProductMaker() throws IOException {
        super("Product Data Entry");

        // Create GUI components
        nameLabel = new JLabel("Product Name:");
        descriptionLabel = new JLabel("Product Description:");
        idLabel = new JLabel("Product ID:");
        costLabel = new JLabel("Product Cost:");
        recordCountLabel = new JLabel("Record Count:");
        nameTextField = new JTextField(20);
        descriptionTextField = new JTextField(20);
        idTextField = new JTextField(20);
        costTextField = new JTextField(20);
        recordCountTextField = new JTextField(5);
        addButton = new JButton("Add Product");
        quitButton = new JButton("Quit");

        // Add components to layout
        setLayout(new GridLayout(7, 2));
        add(nameLabel);
        add(nameTextField);
        add(descriptionLabel);
        add(descriptionTextField);
        add(idLabel);
        add(idTextField);
        add(costLabel);
        add(costTextField);
        add(recordCountLabel);
        add(recordCountTextField);
        add(addButton);
        add(quitButton);
        recordCountTextField.setText("0");

        // Add action listener to Add Product button
        addButton.addActionListener(e -> addProduct());

        // Add action listener to Quit button
        quitButton.addActionListener(e -> {
            closeFile();
            System.exit(0);
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setVisible(true);

        // Suppress secure coding warning
        System.setProperty("java.awt.headless", "true");
    }

    private void addProduct() {
        if (nameTextField.getText().isEmpty() || descriptionTextField.getText().isEmpty() ||
                idTextField.getText().isEmpty() || costTextField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all product data fields.");
            return;
        }

        String name = nameTextField.getText();
        String description = descriptionTextField.getText();
        String id = idTextField.getText();
        double cost = Double.parseDouble(costTextField.getText());
        Product product = new Product(id, name, description, cost);
        String formattedRecord = product.getFormattedRandomAccessRecord();

        try {
            if (productFile == null) {
                productFile = new RandomAccessFile("productData.dat", "rw");
            }
            productFile.seek(productFile.length());
            productFile.writeBytes(formattedRecord);
            int recordCount = Integer.parseInt(recordCountTextField.getText());
            recordCount++;
            recordCountTextField.setText(String.valueOf(recordCount));
            nameTextField.setText("");
            descriptionTextField.setText("");
            idTextField.setText("");
            costTextField.setText("");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while writing to the file.");
        }
    }

    private void closeFile() {
        try {
            if (productFile != null) {
                productFile.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new RandProductMaker();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static class Product {
        private String id;
        private String name;
        private String description;
        private double cost;

        public Product(String id, String name, String description, double cost) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.cost = cost;
        }

        public String getFormattedRandomAccessRecord() {
            return String.format("%-20s%-20s%-20s%-20s", id, name, description, cost);
        }
    }
}
