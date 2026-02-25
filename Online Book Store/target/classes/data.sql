-- Seed admin user (password: Admin@123)
-- The password here is BCrypt hash for "Admin@123"
INSERT INTO users (id, email, full_name, password, role, created_at)
SELECT 1, 'admin@bookstore.com', 'System Admin', '$2a$10$5Yw0sR2bXg7e8fYj7y2bZOrXouJQ7Qf0WJ1xkS7R0q3v9t0YyVvXy', 'ADMIN', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='admin@bookstore.com');

-- Seed some books (with 15-20% deals)
INSERT INTO books (id, title, author, description, category, price, discount_percent, stock_quantity, availability_status, image_url, created_at, updated_at)
SELECT 1, 'The Intelligent Investor', 'Benjamin Graham', 'Classic value investing principles.', 'BUSINESS_INVESTING', 25.00, 15, 20, 'AVAILABLE', 'https://images.example.com/intelligent-investor.jpg', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id=1);

INSERT INTO books (id, title, author, description, category, price, discount_percent, stock_quantity, availability_status, image_url, created_at, updated_at)
SELECT 2, 'Clean Code', 'Robert C. Martin', 'A Handbook of Agile Software Craftsmanship.', 'ACADEMICS', 30.00, 20, 15, 'AVAILABLE', 'https://images.example.com/clean-code.jpg', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id=2);

INSERT INTO books (id, title, author, description, category, price, discount_percent, stock_quantity, availability_status, image_url, created_at, updated_at)
SELECT 3, '1984', 'George Orwell', 'Dystopian fiction novel.', 'FICTION_LITERATURE', 12.99, NULL, 40, 'AVAILABLE', 'https://images.example.com/1984.jpg', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id=3);

INSERT INTO books (id, title, author, description, category, price, discount_percent, stock_quantity, availability_status, image_url, created_at, updated_at)
SELECT 4, 'Meditations', 'Marcus Aurelius', 'Stoic philosophy notes.', 'SPIRITUALITY_PHILOSOPHY', 10.50, 15, 35, 'AVAILABLE', 'https://images.example.com/meditations.jpg', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id=4);
