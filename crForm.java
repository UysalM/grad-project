import com.aspose.email.MailAddress;
import com.aspose.email.MailMessage;
import com.aspose.email.SmtpClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class crForm extends JDialog {
    private JTextField textFieldName;
    private JTextField textFieldEmail;
    private JTextField textFieldDate;
    private JButton OKButton;
    private JButton clearButton;
    private JTable RegisteredCustomers;
    private JPanel RegForm;
    private JButton showRecordsButton;
    private JButton IDFilterButton;
    private JTextField textFieldID;
    private JTextField textFieldDFilter;
    private JButton dateFilterButton;
    private JButton resetTableButton;
    private JButton getEMailAddressesButton;

    public crForm(JFrame parent) {
        super(parent);
        setTitle("Register new customer");
        setContentPane(RegForm);
        setMinimumSize(new Dimension(800,500));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
                textFieldName.setText("");
                textFieldEmail.setText("");
                textFieldDate.setText("");
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldName.setText("");
                textFieldEmail.setText("");
                textFieldDate.setText("");
                textFieldID.setText("");
                textFieldDFilter.setText("");
            }
        });
        setVisible(true);
        showRecordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisteredCustomers.setModel(new DefaultTableModel());
                showRecords();
            }
        });
        IDFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisteredCustomers.setModel(new DefaultTableModel());
                idFilter();
                textFieldID.setText("");
            }
        });
        resetTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisteredCustomers.setModel(new DefaultTableModel());
            }
        });
        dateFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisteredCustomers.setModel(new DefaultTableModel());
                dateFilter();
            }
        });
        getEMailAddressesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getEmailAdd();
            }
        });
    }

    private void getEmailAdd() {
        String host = "jdbc:mysql://localhost:3306/uysalgp";
        String user = "root";
        String password = "uysalGAUdb";
        String dtFilter = textFieldDFilter.getText();
        Pattern pattern = Pattern.compile("^((?:19|20)[0-9][0-9])-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$");
        Matcher matcher = pattern.matcher(dtFilter);
        boolean matchFound = matcher.find();
        ArrayList<String> emailAdd = new ArrayList<>();


        if (matchFound) {
            try {
                Connection con = DriverManager.getConnection(host, user, password);
                Statement st = con.createStatement();
                String query = "SELECT * FROM customers WHERE Date = '" + dtFilter + "'";
                ResultSet rs = st.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData();
                DefaultTableModel model = (DefaultTableModel) RegisteredCustomers.getModel();

                int columnNo = rsmd.getColumnCount();
                String[] columnName = new String[columnNo];
                for (int i = 0; i < columnNo; i++) {
                    columnName[i] = rsmd.getColumnName(i + 1);
                    model.setColumnIdentifiers(columnName);
                }
                model.setColumnIdentifiers(columnName);
                String email;

                while (rs.next()) {
                    email = rs.getString(2);
                    emailAdd.add(email);
                }
                st.close();
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            MailMessage message = new MailMessage();

            message.setSubject("Covid-19 Tracing & Tracking");
            message.setBody("You have visited our facility on" + " " + dtFilter + " " + "and you may have came in contact with a Covid-19 positive person. Please go to the nearest test center. If you start showing symptoms, get a pcr test and isolate yourself");
            message.setFrom(new MailAddress("uysalgau@outlook.com", "Uysal GAU", false));

            SmtpClient client = new SmtpClient();
            client.setHost("smtp.office365.com");
            client.setUsername("uysalgau@outlook.com");
            client.setPassword("uysal@GAU2022");
            client.setPort(587);

            String[] eAddress = new String[emailAdd.size()];
            for (int x = 0; x < emailAdd.size(); x++) {
                eAddress[x] = String.valueOf(emailAdd.get(x));
            }
            for (int z = 0; z < eAddress.length; z++) {
                message.getTo().addItem(new MailAddress(eAddress[z], "Dear Customer", false));
                try {
                    client.send(message);
                    JOptionPane.showMessageDialog(null, "Email sent successfully");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to send email", "Try Again", ERROR_MESSAGE);
                }
            }
        }
         else {
            JOptionPane.showMessageDialog(null,"Please enter a valid date format", "Invalid date format", ERROR_MESSAGE);
        }
    }

    private void dateFilter() {
        String host = "jdbc:mysql://localhost:3306/uysalgp";
        String user = "root";
        String password = "uysalGAUdb";
        String dtFilter = textFieldDFilter.getText();
        Pattern pattern = Pattern.compile("^((?:19|20)[0-9][0-9])-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$");
        Matcher matcher = pattern.matcher(dtFilter);
        boolean matchFound = matcher.find();

        if (matchFound) {
            try {
                Connection con = DriverManager.getConnection(host, user, password);
                Statement st = con.createStatement();
                String query = "SELECT * FROM customers WHERE Date = '" + dtFilter + "'";
                ResultSet rs = st.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData();
                DefaultTableModel model = (DefaultTableModel) RegisteredCustomers.getModel();

                int columnNo = rsmd.getColumnCount();
                String[] columnName = new String[columnNo];
                for (int i = 0; i < columnNo; i++) {
                    columnName[i] = rsmd.getColumnName(i + 1);
                    model.setColumnIdentifiers(columnName);
                }
                model.setColumnIdentifiers(columnName);
                String nameSurname, email, date, customerID;

                while (rs.next()) {
                    nameSurname = rs.getString(1);
                    email = rs.getString(2);
                    date = rs.getString(3);
                    customerID = rs.getString(4);
                    String[] row = {nameSurname, email, date, customerID};
                    model.addRow(row);
                }
                st.close();
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            JOptionPane.showMessageDialog(null,"Please enter a valid date format", "Invalid date format", ERROR_MESSAGE);
        }


    }

    private void idFilter() {
        String host = "jdbc:mysql://localhost:3306/uysalgp";
        String user = "root";
        String password = "uysalGAUdb";
        String ID = textFieldID.getText();

        try {
            Integer.parseInt(ID);
            try {
                Connection con = DriverManager.getConnection(host, user, password);
                Statement st = con.createStatement();
                String query = "SELECT * FROM customers WHERE customerID = '" + ID + "'";
                ResultSet rs = st.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData();
                DefaultTableModel model = (DefaultTableModel) RegisteredCustomers.getModel();

                int columnNo = rsmd.getColumnCount();
                String[] columnName = new String[columnNo];
                for (int i = 0; i < columnNo; i++) {
                    columnName[i] = rsmd.getColumnName(i + 1);
                    model.setColumnIdentifiers(columnName);
                }
                model.setColumnIdentifiers(columnName);
                String nameSurname, email, date, customerID;

                while (rs.next()) {
                    nameSurname = rs.getString(1);
                    email = rs.getString(2);
                    date = rs.getString(3);
                    customerID = rs.getString(4);
                    String[] row = {nameSurname, email, date, customerID};
                    model.addRow(row);
                }
                st.close();
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,"Enter an integer ID","Invalid ID",ERROR_MESSAGE);
        }


    }

    private void showRecords() {
        String host = "jdbc:mysql://localhost:3306/uysalgp";
        String user = "root";
        String password = "uysalGAUdb";

        try {
            Connection con = DriverManager.getConnection(host, user, password);
            Statement st = con.createStatement();
            String query = "SELECT * FROM customers";
            ResultSet rs = st.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            DefaultTableModel model = (DefaultTableModel) RegisteredCustomers.getModel();

            int columnNo = rsmd.getColumnCount();
            String[] columnName = new String[columnNo];
            for (int i = 0; i < columnNo; i++) {
                columnName[i] = rsmd.getColumnName(i + 1);
                model.setColumnIdentifiers(columnName);
            }
            model.setColumnIdentifiers(columnName);
            String nameSurname, email, date, customerID;

            while (rs.next()) {
                nameSurname = rs.getString(1);
                email = rs.getString(2);
                date = rs.getString(3);
                customerID = rs.getString(4);
                String[] row = {nameSurname, email, date, customerID};
                model.addRow(row);
            }
            st.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerUser() {
        String name = textFieldName.getText();
        String email = textFieldEmail.getText();
        String date = textFieldDate.getText();



        if (name.isEmpty() || email.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete all fields", "Error", ERROR_MESSAGE);
            return;
        }




        customer = insertNewRow(name, email, date);
        if (customer != null) {
            JOptionPane.showMessageDialog(this,"Successful registration");
        }
        else {
            JOptionPane.showMessageDialog(this, "Failed to register customer", "Try again", ERROR_MESSAGE);
        }
    }
    public CustomerInfo customer;
    private CustomerInfo insertNewRow(String name, String email, String date) {
        CustomerInfo customer = null;
        String host = "jdbc:mysql://localhost:3306/uysalgp";
        String user = "root";
        String password = "uysalGAUdb";

        try {
            Connection con = DriverManager.getConnection(host, user, password);
            Statement st = con.createStatement();
            String query = "INSERT INTO customers (NameSurname, Email, Date) VALUES(?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, date);

            int addedRows = ps.executeUpdate();
            if (addedRows > 0) {
                customer = new CustomerInfo();
                customer.name = name;
                customer.email = email;
                customer.dateRegistered = date;
            }
            st.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return customer;
    }

    public static void main(String[] args) {
        crForm form = new crForm(null);
    }

}
