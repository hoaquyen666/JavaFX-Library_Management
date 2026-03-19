INSERT INTO CategoryGroup (CategoryGroupId, CategoryGroupCode, CategoryGroupName, Description) VALUES
                                                                                                   (1, 'CG_IT', 'Công nghệ thông tin', 'Sách về máy tính, lập trình và phần mềm'),
                                                                                                   (2, 'CG_LIT', 'Văn học', 'Sách văn học trong và ngoài nước'),
                                                                                                   (3, 'CG_SCI', 'Khoa học', 'Sách khoa học tự nhiên và xã hội');

-- 2. Thêm Thể loại (Category)
INSERT INTO Category (CategoryId, CategoryCode, CategoryGroupId, CategoryName, Description) VALUES
                                                                                                (1, 'C_PROG', 1, 'Lập trình', 'Các ngôn ngữ lập trình, thuật toán'),
                                                                                                (2, 'C_DB', 1, 'Cơ sở dữ liệu', 'Thiết kế và quản trị database'),
                                                                                                (3, 'C_NOVEL', 2, 'Tiểu thuyết', 'Tiểu thuyết dài kỳ, truyện ngắn'),
                                                                                                (4, 'C_PHYSICS', 3, 'Vật lý', 'Vật lý lượng tử, vũ trụ học');

INSERT INTO Author (AuthorId, AuthorCode, AuthorName, DoB, Description) VALUES
                                                                            (1, 'AUTH_001', 'Robert C. Martin', '1952-12-05', 'Tác giả nổi tiếng với triết lý Clean Code'),
                                                                            (2, 'AUTH_002', 'Joshua Bloch', '1961-08-28', 'Kỹ sư phần mềm, cựu nhân viên Sun Microsystems'),
                                                                            (3, 'AUTH_003', 'Stephen Hawking', '1942-01-08', 'Nhà vật lý lý thuyết, vũ trụ học'),
                                                                            (4, 'AUTH_004', 'Nam Cao', '1915-10-29', 'Nhà văn hiện thực xuất sắc của Việt Nam'),
                                                                            (5, 'AUTH_005', 'Abraham Silberschatz', '1947-05-01', 'Giáo sư khoa học máy tính tại đại học Yale');

-- 4. Thêm 10 cuốn Sách (Book)
INSERT INTO Book (BookId, BookCode, Title, ISBN, Publisher, PublishYear, Description, Price) VALUES
                                                                                                 (1, 'B001', 'Clean Code', '9780132350884', 'Prentice Hall', 2008, 'Hướng dẫn viết mã sạch cho lập trình viên Agile', 100000),
                                                                                                 (2, 'B002', 'Effective Java', '9780134685991', 'Addison-Wesley', 2018, 'Các best practice khi lập trình Java',200000),
                                                                                                 (3, 'B003', 'Clean Architecture', '9780134494166', 'Prentice Hall', 2017, 'Cẩm nang thiết kế kiến trúc phần mềm', 320000),
                                                                                                 (4, 'B004', 'Database System Concepts', '9780073523323', 'McGraw-Hill', 2010, 'Kiến thức nền tảng về hệ quản trị CSDL', 75000),
                                                                                                 (5, 'B005', 'Lược sử thời gian', '9780553380163', 'Bantam Books', 1988, 'Sách phổ biến khoa học về vũ trụ',275000),
                                                                                                 (6, 'B006', 'Vũ trụ trong vỏ hạt dẻ', '9780553802023', 'Bantam Books', 2001, 'Cuốn sách tiếp nối của Lược sử thời gian', 210000),
                                                                                                 (7, 'B007', 'Chí Phèo', '9786046945678', 'NXB Văn học', 1941, 'Tác phẩm văn học hiện thực phê phán',50000),
                                                                                                 (8, 'B008', 'Sống mòn', '9786046912345', 'NXB Hội Nhà văn', 1944, 'Tiểu thuyết xuất sắc của Nam Cao', 70000),
                                                                                                 (9, 'B009', 'The Agile Samurai', '9781934356586', 'Pragmatic Bookshelf', 2010, 'Làm chủ phương pháp phát triển Agile', 90000),
                                                                                                 (10, 'B010', 'Head First Java', '9780596009205', 'OReilly', 2003, 'Học Java qua cách tiếp cận trực quan', 100000);


-- 5. Thêm quan hệ Sách - Tác giả (BookAuthor)
INSERT INTO BookAuthor (BookId, AuthorId) VALUES
                                              (1, 1), -- Clean Code - Robert C. Martin
                                              (2, 2), -- Effective Java - Joshua Bloch
                                              (3, 1), -- Clean Architecture - Robert C. Martin
                                              (4, 5), -- Database System Concepts - Abraham Silberschatz
                                              (5, 3), -- Lược sử thời gian - Stephen Hawking
                                              (6, 3), -- Vũ trụ trong vỏ hạt dẻ - Stephen Hawking
                                              (7, 4), -- Chí Phèo - Nam Cao
                                              (8, 4), -- Sống mòn - Nam Cao
                                              (9, 1), -- The Agile Samurai - Robert C. Martin (Giả định để test tìm kiếm)
                                              (10, 2);-- Head First Java - Joshua Bloch (Giả định để test tìm kiếm)

-- 6. Thêm quan hệ Sách - Thể loại (BookCategory)
INSERT INTO BookCategory (BookId, CategoryId) VALUES
                                                  (1, 1), -- Clean Code - Lập trình
                                                  (2, 1), -- Effective Java - Lập trình
                                                  (3, 1), -- Clean Architecture - Lập trình
                                                  (4, 2), -- Database System - Cơ sở dữ liệu
                                                  (5, 4), -- Lược sử thời gian - Vật lý
                                                  (6, 4), -- Vũ trụ trong vỏ hạt dẻ - Vật lý
                                                  (7, 3), -- Chí Phèo - Tiểu thuyết
                                                  (8, 3), -- Sống mòn - Tiểu thuyết
                                                  (9, 1), -- The Agile Samurai - Lập trình
                                                  (10, 1);-- Head First Java - Lập trình

-- insert into BookCopy (CopyCode, BookId, Status, Location) Tự thêm ở app
-- values ('BC0001', 1, 'Available', 'A1-S1'),
--        ('BC0002', 1, 'Available', 'A1-S1'),
--        ('BC0003', 2, 'Available', 'A1-S2'),
--        ('BC0004', 3, 'Available', 'B1-S1'),
--        ('BC0005', 3, 'Available', 'B1-S1'),
--        ('BC0006', 4, 'Available', 'B1-S2');

-- insert into Reader (ReaderCode, FullName, Email, Phone) Tự thêm ở app
-- values ('R1', 'Test Reader', 'reader@gmail.com', '0900000000');
-- insert into Account (Username, PasswordHash, Role, ReaderId)
-- values ('reader1', '123456', 'Reader', 1);


-- Ca làm: Nhân viên tự thêm ở app
insert into shift(ShiftName, start_time, end_time) VALUES
                                                       ("Ca sáng", "8:00", "12:00"),
                                                       ("Ca chiều", "13:00", "17:00")

                                                       --Tự thêm nhân viên ở senior và cả ca làm
-- insert into Staff (StaffCode, Role, FullName, DoB, Email, Phone)
-- values  ('S1','Librarian','Test Librarian','1990-01-01','librarian@gmail.com','0911111111'),
--         ('S2','Admin','Test Admin','1990-01-01','admin@gmail.com','0911111112'),
--         ('S3','Senior','Test Senior','1990-01-01','senior@gmail.com','0911111113');
    insert into Account (Username, PasswordHash, Role, StaffId)
values ('librarian1', '123456', 'Librarian', 1),
    ('admin1', '123456', 'Admin', 2),
    ('senior1', '123456', 'Senior', 3);