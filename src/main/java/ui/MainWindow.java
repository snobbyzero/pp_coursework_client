package ui;

import entity.*;
import org.springframework.http.HttpEntity;
import web.MainWindowLogic;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JPanel AdminPanel;
    private JPanel RootPanel;
    private JTabbedPane tabbedPane;
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox sortByComboBox;
    private JPanel cartPanel;
    private JPanel profilePanel;
    private JLabel countLabel;
    private JPanel filterPanel;
    private JTextField minPriceField;
    private JTextField maxPriceField;
    private JPanel productsPanel;
    private JPanel categoriesPanel;
    private JScrollPane productsScrollPane;
    private JButton filterButton;
    private JButton clearFiltersButton;
    private JTextField nameField;
    private JTextField countField;
    private JTextField priceField;
    private JTextField weightField;
    private JTextField descriptionField;
    private JLabel imageLabel;
    private JButton imagePathButton;
    private JButton addProductButton;
    private JPanel chooseCategoriesPanel;
    private JTextField addCategoryField;
    private JButton addCategoryButton;
    private JLabel errorLabel;
    private JScrollPane adminPanel;
    private JPanel historyPanel;
    private JTextField usernameTextField;
    private JTextField emailTextField;
    private JTextField firstNameTextField;
    private JTextField lastNameTextField;
    private JTextField phoneNumberTextField;
    private JTextField addressTextField;
    private JTextField passwordTextField;
    private JButton saveButton;
    private JLabel profileErrorLabel;
    private JButton checkoutButton;
    private JLabel cartErrorLabel;
    private JComboBox cityComboBox;
    private JPanel orderHistoryPanel;
    private JScrollPane historyScrollPane;
    private JPanel accountingPanel;
    private JLabel averageTotalPriceLabel;
    private JLabel averageTotalWeightLabel;
    private JComboBox profitCategoriesComboBox;
    private JLabel profitCategoriesLabel;

    private JTable cartTable;
    DefaultTableModel cartDtm;

    User user;

    List<JCheckBox> categoriesCheckBoxes = new ArrayList<>();
    List<JCheckBox> adminCategoriesCheckBoxes = new ArrayList<>();
    List<ProductCountOnly> cart = new ArrayList<>();
    List<Category> categoryList = new ArrayList<>();
    List<City> cities = new ArrayList<>();

    AtomicBoolean threadKiller = new AtomicBoolean(false);

    MainWindowLogic mainWindowLogic;

    public MainWindow(User user, String password) {
        mainWindowLogic = new MainWindowLogic(user.getUsername(), password);
        this.user = user;

        setContentPane(RootPanel);
        productsScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        historyScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        chooseCategoriesPanel.setLayout(new BoxLayout(chooseCategoriesPanel, BoxLayout.Y_AXIS));
        orderHistoryPanel.setLayout(new BoxLayout(orderHistoryPanel, BoxLayout.Y_AXIS));
        setVisible(true);

        // Вкладки админа
        if (user.getRoles().stream().anyMatch(x -> x.getName().equals("ADMIN"))) {
            createAdminPanels();
        } else {
            tabbedPane.remove(5);
            tabbedPane.remove(4);
        }

        // Загружаем города
        loadCities();

        // Табличка корзины
        createCartTable();

        // Добавляем категории
        addCategories();

        // Добавляем список продуктов
        Thread thread = new Thread(() -> {
            addProducts(mainWindowLogic.getProducts());
        });
        thread.start();

        // Загружаем корзину юзера
        loadCart();

        // Загружаем пользовательскую информацию
        loadUserInformation();

        // Варианты сортировки
        List<String> sortList = Arrays.asList("In ascending order", "In descending order", "A-Z", "Z-A");
        sortList.forEach(x -> sortByComboBox.addItem(x));

        sortByComboBox.addActionListener(e -> {
            Thread thread1 = new Thread(() -> {
                threadKiller.set(true);
                List<Product> products = mainWindowLogic.sortProducts(sortByComboBox.getSelectedItem().toString());
                addProducts(products);
            });
            thread1.start();
        });

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        mainWindowLogic.saveCart(user.getId(), cart);
                        super.windowClosing(e);
                    }
                });

        imagePathButton.addActionListener(e -> chooseImage());

        searchButton.addActionListener(e -> {
            Thread thread1 = new Thread(() -> {
                threadKiller.set(true);
                addProducts(mainWindowLogic.getProductsByName(searchField.getText().trim().toUpperCase()));
            });
            thread1.start();
        });

        filterButton.addActionListener(e -> {
            Thread thread1 = new Thread(() -> {
                threadKiller.set(true);
                addProducts(mainWindowLogic.getProductsByFilter(
                        findCheckedCategories(categoriesCheckBoxes),
                        getPrice(minPriceField),
                        getPrice(maxPriceField)
                ));
            });
            thread1.start();
        });

        clearFiltersButton.addActionListener(e -> {
            Thread thread1 = new Thread(() -> {
                threadKiller.set(true);
                addProducts(mainWindowLogic.getProducts());
            });
            thread1.start();
        });

        saveButton.addActionListener(e -> updateUserInformation());

        checkoutButton.addActionListener(e -> checkout());

        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 3:
                    loadOrderHistory();
                case 5:
                    accounting();
            }
        });
    }

    public void addCategories() {
        categoryList.addAll(mainWindowLogic.getCategories());

        for (Category category : categoryList) {
            addCategory(category);
        }
    }

    public void addNewCategory() {
        String categoryName = addCategoryField.getText();
        Category response = mainWindowLogic.addCategory(categoryName);
        categoryList.add(response);
        if (response != null) {
            addCategory(response);
            errorLabel.setText("OK");
        } else {
            errorLabel.setText("Already exists");
        }

    }

    public void addCategory(Category category) {
        JCheckBox checkBox1 = new JCheckBox(category.getName());
        JCheckBox checkBox2 = new JCheckBox(category.getName());
        categoriesPanel.add(checkBox1);
        chooseCategoriesPanel.add(checkBox2);
        categoriesCheckBoxes.add(checkBox1);
        adminCategoriesCheckBoxes.add(checkBox2);

        profitCategoriesComboBox.addItem(category.getName());

        chooseCategoriesPanel.revalidate();
        chooseCategoriesPanel.repaint();
    }

    public List<String> findCheckedCategories(List<JCheckBox> checkBoxes) {
        List<String> checkedCheckBoxes = new ArrayList<>();
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                checkedCheckBoxes.add(checkBox.getText());
            }
        }
        return checkedCheckBoxes;
    }

    public String getPrice(JTextField priceField) {
        String price = priceField.getText();
        try {
            Integer.valueOf(price);
        } catch (NumberFormatException e) {
            price = "";
            priceField.setText(price);
        }
        return price;
    }

    public void addNewProduct() {
        String name = nameField.getText().toUpperCase();
        String description = descriptionField.getText();
        String imagePath = "";
        if (!imageLabel.getText().equals("")) {
            imagePath = "./src/main/resources/images/" + System.currentTimeMillis() + ".jpg";
            try {
                File file = new File(imageLabel.getText());
                BufferedImage img = ImageIO.read(file);
                ImageIO.write(img, "jpg", new File(imagePath));
            } catch (IOException | IllegalArgumentException ex) {

            }
        }
        List<String> categoriesNames = findCheckedCategories(adminCategoriesCheckBoxes);
        try {
            Long count = Long.valueOf(countField.getText());
            Integer price = Integer.valueOf(priceField.getText());
            Float weight = Float.valueOf(weightField.getText());
            Product product = new Product(name, description, count, weight, imagePath, price);
            for (String categoryName : categoriesNames) {
                for (Category category : categoryList) {
                    if (category.getName().equals(categoryName)) {
                        product.getCategories().add(category);
                    }
                }
            }
            //product.getCategories().addAll(categories);
            mainWindowLogic.addProduct(product);
            errorLabel.setText("Product was added");
            // Временно
            addProduct(product);
        } catch (NumberFormatException e) {
            errorLabel.setText("Check fields");
        }

    }

    public void addProducts(List<Product> productList) {
        //List<Product> productList = products;
        if (threadKiller.get()) {
            threadKiller.set(false);
        }
        productsPanel.removeAll();
        System.out.println(Thread.getAllStackTraces().keySet());
        System.out.println(Thread.currentThread().getName());
        for (Product product : productList) {
            if (!threadKiller.get()) {
                addProduct(product);
                productsPanel.revalidate();
                productsPanel.repaint();
            } else {
                System.out.println(Thread.getAllStackTraces().keySet());
                threadKiller.set(false);
                break;
            }
        }
    }

    public void addProduct(Product product) {
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new GridBagLayout());
        productPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        productPanel.setMinimumSize(new Dimension(400, 200));
        productPanel.setMaximumSize(new Dimension(400, 200));
        productPanel.setPreferredSize(new Dimension(400, 200));
        GridBagConstraints c = new GridBagConstraints();

        // Image
        String picPath = !product.getImagePath().equals("") ? product.getImagePath() : "./src/main/resources/images/test.jpg";
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(picPath).getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT));
        JLabel picLabel = new JLabel();
        picLabel.setIcon(imageIcon);
        productPanel.add(picLabel, c);

        // Description
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 1;
        JLabel countLabel = new JLabel();
        Long count = product.getCount();
        if (count > 0) {
            countLabel.setText("Count: " + product.getCount().toString());
        } else {
            countLabel.setText("Not available");
        }

        productPanel.add(countLabel, c);

        // Name
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 0;
        JLabel nameLabel = new JLabel();
        nameLabel.setText(product.getName());
        productPanel.add(nameLabel, c);

        // Price
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 1;
        c.gridy = 1;
        JLabel priceLabel = new JLabel();
        priceLabel.setText("Price: " + product.getPrice().toString());
        productPanel.add(priceLabel, c);

        // Add to cart button
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 1;
        c.gridy = 2;
        JButton addToCartButton = new JButton();
        addToCartButton.setText("Add to cart");
        addToCartButton.addActionListener(e -> addToCartTable(new ProductCountOnly(product, 1L)));
        productPanel.add(addToCartButton, c);

        // Categories
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 2;
        JLabel categoriesLabel = new JLabel();
        final StringBuilder categories = new StringBuilder();
        product.getCategories().forEach(category -> categories.append(category.getName()).append(", "));
        categoriesLabel.setText(categories.toString());
        productPanel.add(categoriesLabel, c);

        productsPanel.add(productPanel);
        productsPanel.add(Box.createVerticalStrut(20));
    }

    public void addToCartTable(ProductCountOnly p) {
        Product product = p.getProduct();
        boolean exists = false;
        if (p.getCount() > product.getCount()) {
            p.setCount(product.getCount());
        } else {
            for (int i = 0; i < cart.size(); i++) {
                ProductCountOnly productInCart = cart.get(i);
                if (productInCart.getProduct().getId().equals(product.getId())) {
                    exists = true;
                    productInCart.increase();
                    cartDtm.setValueAt(productInCart.getCount(), i, 3);
                    cartDtm.setValueAt(productInCart.getCount() * product.getWeight(), i, 2);
                    cartDtm.fireTableDataChanged();
                    break;
                }
            }
            if (!exists) {
                cart.add(p);
                cartDtm.addRow(new Object[]{
                        product.getName(),
                        product.getPrice() * p.getCount(),
                        product.getWeight() * p.getCount(),
                        p.getCount()
                });
            }
        }
    }

    public void addToOrderTable(Order orders, JTable jTable) {
        DefaultTableModel dtm = (DefaultTableModel) jTable.getModel();
        for (ProductCountOnly p : orders.getProducts()) {
            Product product = p.getProduct();
            dtm.addRow(new Object[]{
                    product.getName(),
                    product.getPrice() * p.getCount(),
                    product.getWeight() * p.getCount(),
                    p.getCount(),
                    false
            });
        }
    }

    public JTable createOrderTable(JPanel panel, GridBagConstraints c) {
        String[] columnNames = {"Name", "Total price", "Total weight", "Count", "Check"};
        JTable orderTable = new JTable();
        DefaultTableModel orderDtm = new DefaultTableModel(0, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return getValueAt(0, columnIndex).getClass();
            }
        };
        createTable(c, columnNames, orderDtm, orderTable, panel, 300, 100);
        return orderTable;
    }

    private void createTable(GridBagConstraints c, String[] columnNames, DefaultTableModel dtm, JTable table, JPanel panel, int width, int height) {
        JScrollPane jScrollPane = new JScrollPane();

        table.setPreferredScrollableViewportSize(new Dimension(width, height));
        table.setPreferredSize(new Dimension(width, height));
        table.setMinimumSize(new Dimension(width, height));
        table.setMaximumSize(new Dimension(width, height));

        dtm.setColumnIdentifiers(columnNames);
        table.setModel(dtm);
        for (int i = 0; i < columnNames.length; i++) {
            table.getColumnModel().getColumn(i).setMinWidth(75);
            table.getColumnModel().getColumn(i).setPreferredWidth(75);
            table.getColumnModel().getColumn(i).setMaxWidth(75);
        }
        jScrollPane.setViewportView(table);
        panel.add(jScrollPane, c);
    }

    public void createCartTable() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        String[] columnNames = {"Name", "Total Price", "Total Weight", "Count"};
        cartTable = new JTable();
        cartDtm = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        createTable(c, columnNames, cartDtm, cartTable, cartPanel, 300, 400);

        cartTable.addFocusListener(new FocusAdapter() {
            Long prevVal;
            Long newVal;

            @Override
            public void focusLost(FocusEvent e) {
                TableCellEditor tce = cartTable.getCellEditor();
                if (tce != null) {
                    prevVal = Long.valueOf(cartTable.getValueAt(cartTable.getSelectedRow(), cartTable.getSelectedColumn()).toString());
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                int row = cartTable.getSelectedRow();
                int col = cartTable.getSelectedColumn();
                try {
                    newVal = Long.valueOf(cartTable.getValueAt(row, col).toString());
                } catch (NullPointerException | IndexOutOfBoundsException ignored) {

                } catch (NumberFormatException ex) {
                    newVal = prevVal;
                    cartTable.setValueAt(newVal, row, col);
                } finally {
                    if (prevVal != null && !newVal.equals(prevVal)) {
                        if (newVal == 0) {
                            cart.remove(row);
                            cartDtm.removeRow(row);
                        } else {
                            cart.get(row).setCount(newVal);
                            cartTable.setValueAt(Float.parseFloat(cartTable.getValueAt(row, col - 1).toString()) / prevVal * newVal, row, col - 1);
                            cartTable.setValueAt(Integer.parseInt(cartTable.getValueAt(row, col - 2).toString()) / prevVal * newVal, row, col - 2);
                        }
                    }
                }
            }
        });

    }

    public void loadCart() {
        List<ProductCountOnly> cart = mainWindowLogic.loadCart(user.getId());
        //this.cart.addAll(cart);
        for (ProductCountOnly p : cart) {
            addToCartTable(p);
        }
    }

    private void loadOrderHistory() {
        orderHistoryPanel.removeAll();

        Thread thread = new Thread(() -> {
            List<Order> orderHistory = mainWindowLogic.getOrders(user.getId());
            for (int i = 0; i < orderHistory.size(); i++) {
                Order order = orderHistory.get(i);
                addToOrderHistoryPanel(order);
                orderHistoryPanel.repaint();
                orderHistoryPanel.revalidate();
            }
        });
        thread.start();
    }

    private void addToOrderHistoryPanel(Order order) {
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new GridBagLayout());
        orderPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        orderPanel.setMinimumSize(new Dimension(400, 200));
        orderPanel.setMaximumSize(new Dimension(400, 200));
        orderPanel.setPreferredSize(new Dimension(400, 200));
        GridBagConstraints c = new GridBagConstraints();

        LocalDateTime orderDatetime = order.getOrderDate().toLocalDateTime();
        String orderDate = orderDatetime.getYear() + "/" + orderDatetime.getMonthValue()
                + "/" + orderDatetime.getDayOfMonth();
        String orderTime = orderDatetime.getHour() + ":" + orderDatetime.getMinute();

        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        JLabel orderDateLabel = new JLabel();
        orderDateLabel.setPreferredSize(new Dimension(200, 30));
        orderDateLabel.setText("Order Date: " + orderDate);
        orderPanel.add(orderDateLabel, c);


        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 1;
        JLabel orderTimeLabel = new JLabel();
        orderTimeLabel.setPreferredSize(new Dimension(200, 30));
        orderTimeLabel.setText("Order Time: " + orderTime);
        orderPanel.add(orderTimeLabel, c);


        LocalDateTime deliveryDatetime = order.getDeliveryDate().toLocalDateTime();
        String deliveryDate = deliveryDatetime.getYear() + "/" + deliveryDatetime.getMonthValue()
                + "/" + deliveryDatetime.getDayOfMonth();
        String deliveryTime = deliveryDatetime.getHour() + ":" + deliveryDatetime.getMinute();

        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 2;
        JLabel deliveryDateLabel = new JLabel();
        deliveryDateLabel.setPreferredSize(new Dimension(200, 30));
        deliveryDateLabel.setText("Delivery Date: " + deliveryDate);
        orderPanel.add(deliveryDateLabel, c);

        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 3;
        JLabel deliveryTimeLabel = new JLabel();
        deliveryTimeLabel.setPreferredSize(new Dimension(200, 30));
        deliveryTimeLabel.setText("Delivery Time: " + deliveryTime);
        orderPanel.add(deliveryTimeLabel, c);

        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 4;
        JButton returnButton = new JButton("Return of the\n checked items");
        orderPanel.add(returnButton, c);

        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 5;
        JTable table = createOrderTable(orderPanel, c);
        addToOrderTable(order, table);

        returnButton.addActionListener(e -> {
            DefaultTableModel dtm = (DefaultTableModel) table.getModel();
            int size = table.getRowCount() - 1;
            for (int i = size; i >= 0; i--) {
                if (table.getValueAt(i, 4).equals(Boolean.TRUE)) {
                    Product product = order.getProducts().get(i).getProduct();
                    mainWindowLogic.changeProductCount(product.getId(), product.getCount() + order.getProducts().get(i).getCount());
                    order.getProducts().remove(i);
                    mainWindowLogic.changeOrder(user.getId(), order);
                    dtm.removeRow(i);
                }
            }
        });


        orderHistoryPanel.add(orderPanel);
        orderHistoryPanel.add(Box.createVerticalStrut(20));
    }

    public void loadUserInformation() {
        usernameTextField.setText(user.getUsername());
        firstNameTextField.setText(user.getFirstName());
        lastNameTextField.setText(user.getLastName());
        emailTextField.setText(user.getEmail());
        addressTextField.setText(user.getAddress());
        phoneNumberTextField.setText(user.getPhoneNumber());
    }

    public void updateUserInformation() {
        String username = usernameTextField.getText();
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String email = emailTextField.getText();
        String address = addressTextField.getText();
        String phoneNumber = phoneNumberTextField.getText();
        String password = passwordTextField.getText();
        City city = (City) cityComboBox.getSelectedItem();

        User newUser = new User();
        newUser.setId(user.getId());

        if (!username.equals("")) {
            newUser.setUsername(username);
        } else {
            newUser.setUsername(user.getUsername());
        }
        if (user.getFirstName() == null || !firstName.equals("")) {
            newUser.setFirstName(firstName);
        }
        if (user.getLastName() == null || !lastName.equals("")) {
            newUser.setLastName(lastName);
        }
        if (user.getEmail() == null || !email.equals("")) {
            newUser.setEmail(email);
        }
        if (user.getAddress() == null || !address.equals("")) {
            newUser.setAddress(address);
        }
        if (user.getPhoneNumber() == null || !phoneNumber.equals("")) {
            newUser.setPhoneNumber(phoneNumber);
        }
        if (user.getCity() == null) {
            newUser.setCity(city);
        }
        if (!password.equals("")) {
            newUser.setPassword(password);
        } else {
            newUser.setPassword(null);
        }
        String res = mainWindowLogic.updateUserInformation(newUser);
        profileErrorLabel.setText(res);

        if (res.equals("OK")) {
            user = mainWindowLogic.getUser(user.getId());
        }
    }

    public void checkout() {
        if (user.getAddress() == null || user.getCity() == null) {
            cartErrorLabel.setText("Choose your city and write your address");
        } else {
            for (int i = cartDtm.getRowCount() - 1; i >= 0; i--) {
                cartDtm.removeRow(i);
            }
            mainWindowLogic.addOrder(user.getId(), cart);
            for (ProductCountOnly productCountOnly : cart) {
                mainWindowLogic.changeProductCount(productCountOnly.getProduct().getId(), productCountOnly.getProduct().getCount() - productCountOnly.getCount());
            }
            cart.clear();
        }
    }

    public void loadCities() {
        cities = mainWindowLogic.loadCities();
        List<String> citiesNames = new ArrayList<>();
        cities.forEach(x -> citiesNames.add(x.getName()));
        for (City city : cities) {
            System.out.println(city.getName());
            cityComboBox.addItem(city);
        }
        cityComboBox.setSelectedIndex(0);
    }

    public void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
        chooser.showOpenDialog(null);
        File file = chooser.getSelectedFile();
        imageLabel.setText(file.getAbsolutePath());

    }

    public void createAdminPanels() {

        profitCategoriesComboBox.addActionListener(e -> {
            profitCategoriesLabel.setText(mainWindowLogic.getCategoryProfit(profitCategoriesComboBox.getSelectedItem().toString()).toString());
        });

        addCategoryButton.addActionListener(e -> addNewCategory());

        addProductButton.addActionListener(e -> addNewProduct());
    }

    public void accounting() {
        profitCategoriesComboBox.setSelectedIndex(0);
        Thread thread = new Thread(() -> {
            averageTotalPriceLabel.setText("Average total price: " + mainWindowLogic.getAverageTotalPrice().toString());
            averageTotalWeightLabel.setText("Average total weight: " + mainWindowLogic.getAverageTotalWeight().toString());
            profitCategoriesLabel.setText("Category's profit: " + mainWindowLogic.getCategoryProfit(profitCategoriesComboBox.getSelectedItem().toString()).toString());
        });
        thread.start();
    }
}
