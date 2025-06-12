create database Project_IT204;
use Project_IT204;


-- Bảng admin
CREATE TABLE admin (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL
);

-- Bảng customer
CREATE TABLE customer (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          phone VARCHAR(20),
                          email VARCHAR(100) UNIQUE,
                          address VARCHAR(255)
);

-- Bảng product
CREATE TABLE product (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         brand VARCHAR(50) NOT NULL,
                         price DECIMAL(12,2) NOT NULL,
                         stock INT NOT NULL,
                         image VARCHAR(255) NOT NULL
);

-- Bảng invoice
CREATE TABLE invoice (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         customer_id INT DEFAULT NULL,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                         total_amount DECIMAL(12,2) NOT NULL,
                         status ENUM('PENDING', 'CONFIRMED', 'SHIPING', 'COMPLETED', 'CANCELED') DEFAULT 'PENDING',
                         CONSTRAINT fk_invoice_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE SET NULL
);

-- Bảng invoice_details
CREATE TABLE invoice_details (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 invoice_id INT DEFAULT NULL,
                                 product_id INT DEFAULT NULL,
                                 quantity INT NOT NULL,
                                 unit_price DECIMAL(12,2) NOT NULL,
                                 CONSTRAINT fk_invoice_details_invoice FOREIGN KEY (invoice_id) REFERENCES invoice(id) ON DELETE SET NULL,
                                 CONSTRAINT fk_invoice_details_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE SET NULL
);

INSERT INTO admin (username, password) VALUES ('admin1', 'admin123');

