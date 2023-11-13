import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductSearch extends JFrame {
    private JLabel searchLabel, resultLabel;
    private JTextField searchTextField;
    private JTextArea resultTextArea;
    private JButton searchButton, quitButton;
    private RandomAccessFile productFile;

    public RandProductSearch() throws IOException {
        super("Product Search");

        // Create GUI components
        searchLabel = new JLabel("Enter partial product name:");
        resultLabel = new JLabel("Search Results:");
        searchTextField = new JTextField(20);
        resultTextArea = new JTextArea(10, 40);
        searchButton = new JButton("Search Products");
        quitButton = new JButton("Quit");

        // Add components to layout
        setLayout(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchLabel);
        searchPanel.add(searchTextField);
        searchPanel.add(searchButton);
        searchPanel.add(quitButton);
        add(searchPanel, BorderLayout.NORTH);
        add(resultLabel, BorderLayout.CENTER);
        add(new JScrollPane(resultTextArea), BorderLayout.SOUTH);

        // Add action listener to Search Products button
        searchButton.addActionListener(e -> searchProducts());

        // Add action listener to Quit button
        quitButton.addActionListener(e -> {
            closeFile();
            System.exit(0);
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setVisible(true);
    }

    private Product readNextRecord() throws IOException {
        byte[] idBytes = new byte[6];
        byte[] nameBytes = new byte[35];
        byte[] descriptionBytes = new byte[75];
        byte[] costBytes = new byte[10];

        int bytesRead = productFile.read(idBytes);
        if (bytesRead == -1) {
            return null; // End of file
        }

        productFile.read(nameBytes);
        productFile.read(descriptionBytes);
        productFile.read(costBytes);

        String id = new String(idBytes).trim();
        String name = new String(nameBytes).trim();
        String description = new String(descriptionBytes).trim();
        double cost = Double.parseDouble(new String(costBytes).trim());

        System.out.println("Read Record: " + id + ", " + name + ", " + description + ", " + cost);

        return new Product(id, name, description, cost);
    }

    private void searchProducts() {
        String partialName = searchTextField.getText();
        try {
            if (productFile == null) {
                productFile = new RandomAccessFile("productData.dat", "r");
            }

            resultTextArea.setText("");

            // Set the file pointer to the beginning of the file
            productFile.seek(0);

            // Read the file until the end
            while (true) {
                Product product = readNextRecord();

                if (product == null) {
                    break; // End of file
                }

                // Check if the product name contains the partial name
                if (product.getName().contains(partialName)) {
                    resultTextArea.append(product.toCSVDataRecord() + "\n");
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while reading from the file.");
        } finally {
            closeFile();
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
                new RandProductSearch();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
