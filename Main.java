import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
public class Main extends JFrame implements ActionListener {
//components in 1st page
    private JLabel lbl1, lbl2, lbl3, lbl4;
    private JComboBox<String> destinationComboBox;
    private JButton btnNext;
    private JLabel lblDiscount;
    private JCheckBox chkStudent, chkSeniorCitizen, chkPWD;
    private JButton btnCalculate;
    private JTextField txtName;
    private JTextField txtDate;

    // Database credentials
    private final String JDBC_URL = "jdbc:mysql://localhost:3306/bus_ticketing_system";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    String url = "jdbc:mysql://localhost:3306/bus_ticketing_system";
    String username = "root";
    String password = "";

    public Main() {
        setTitle("Balanga Transit");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(new FlowLayout());

        // Opening Page
        lbl1 = new JLabel("Welcome to Balanga Transit!");
        lbl1.setFont(new Font("Serif", Font.BOLD, 20));
        lbl1.setBounds(70, 2, 300, 30);

        lbl2 = new JLabel("Select your destination:");
        lbl2.setBounds(125, 27, 200, 30);
//destination choices
        String[] destinations = {"Abucay", "Orani", "Samal", "Hermosa", "Dinalupihan", "Bagac", "Pilar", "Morong", "Mariveles", "Orion"};
        destinationComboBox = new JComboBox<>(destinations);
        destinationComboBox.setFocusable(false);
        destinationComboBox.setBounds(120, 55, 150, 30);
//user input
        lbl3 = new JLabel("Enter your name:");
        lbl3.setBounds(140, 85, 200, 30);

        txtName = new JTextField(20);
        txtName.setBounds(120, 110, 150, 30);

        lbl4 = new JLabel("Enter the date (yyyy-mm-dd):");
        lbl4.setBounds(115, 140, 200, 30);

        txtDate = new JTextField(10);
        txtDate.setBounds(120, 165, 150, 30);

        btnNext = new JButton("History");
        btnNext.setBounds(205, 310, 80, 30);

        add(lbl1);
        add(lbl2);
        add(destinationComboBox);
        add(lbl3);
        add(txtName);
        add(lbl4);
        add(txtDate);
        add(btnNext);

        btnNext.addActionListener(this);

        // Discount Page

        lblDiscount = new JLabel("Are you a student/senior citizen/PWD?");
        lblDiscount.setBounds(90, 200, 250, 30);

        chkStudent = new JCheckBox("Student");
        chkStudent.setBounds(140, 225, 80, 30);
        chkStudent.setBackground(new Color(255, 179, 102));
        chkStudent.setFocusable(false);

        chkSeniorCitizen = new JCheckBox("Senior Citizen");
        chkSeniorCitizen.setBounds(140, 250, 120, 30);
        chkSeniorCitizen.setBackground(new Color(255, 179, 102));
        chkSeniorCitizen.setFocusable(false);

        chkPWD = new JCheckBox("PWD");
        chkPWD.setBounds(140, 275, 80, 30);
        chkPWD.setBackground(new Color(255, 179, 102));
        chkPWD.setFocusable(false);

        btnCalculate = new JButton("Calculate");
        btnCalculate.setFocusable(false);
        btnCalculate.setBounds(100, 310, 100, 30);

        add(lblDiscount);
        add(chkStudent);
        add(chkSeniorCitizen);
        add(chkPWD);
        add(btnCalculate);

        btnCalculate.addActionListener(this);
        this.getContentPane().setLayout(null);
        this.setResizable(false);
        this.getContentPane().setBackground(new Color(255, 179, 102));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnNext) {

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM busticketing");

                while(resultSet.next()){
                    System.out.println(resultSet.getString(1) + ", " + resultSet.getString(2) + ", " + resultSet.getString(3) + ", " + resultSet.getDouble(4));
                }

                connection.close();

            } catch(Exception ex){
                System.out.println(ex);
            }
        } else if (e.getSource() == btnCalculate) {
            calculateDiscount();
        }
    }//computation for discount
    private void calculateDiscount() {
        String name = txtName.getText();
        String destination = (String) destinationComboBox.getSelectedItem();
        String date = txtDate.getText();

        double fare = getFare(destination);
        double discount = 0;

        if (chkStudent.isSelected() || chkSeniorCitizen.isSelected() || chkPWD.isSelected()) {
            discount = fare * 0.2;
        }

        double totalAmount = fare - discount;

        String receipt = "Balanga Transit Ticket\n\n" +
                "Name: " + name + "\n" +
                "Destination: " + destination + "\n" +
                "Date: " + date + "\n" +
                "Fare: Php" + fare + "\n" +
                "Discount: Php" + discount + "\n" +
                "Total Amount: Php" + totalAmount;

        JOptionPane.showMessageDialog(this, receipt);

        insertDataIntoDatabase(name, destination, date, totalAmount);
    }

    private double getFare(String destination) {
        double fare = 0;

        switch (destination) {
            case "Abucay":
                fare = 70;
                break;
            case "Orani":
                fare = 90;
                break;
            case "Samal":
                fare = 80;
                break;
            case "Hermosa":
                fare = 100;
                break;
            case "Dinalupihan":
                fare = 120;
                break;
            case "Bagac":
                fare = 85;
                break;
            case "Pilar":
                fare = 65;
                break;
            case "Morong":
                fare = 90;
                break;
            case "Mariveles":
                fare = 110;
                break;
            case "Orion":
                fare = 70;
                break;
        }

        return fare;
    }
//inserting data into the database
    private void insertDataIntoDatabase(String name, String destination, String date, double amount) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            String sql = "INSERT INTO busticketing VALUES(?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, destination);
            pstmt.setString(3, date);
            pstmt.setDouble(4, amount);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "TICKET SUCCESSFULLY PURCHASED");

        connection.close();

        }catch(Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}