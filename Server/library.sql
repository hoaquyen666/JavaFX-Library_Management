CREATE DATABASE IF NOT EXISTS library 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

create table CategoryGroup (
    CategoryGroupId int auto_increment primary key,
    CategoryGroupCode varchar(20) not null unique,
    CategoryGroupName varchar(45) not null,
    Description text
) comment = 'Các nhóm thể loại sách';

create table Category (
    CategoryId int auto_increment primary key,
    CategoryCode varchar(20) not null unique,
    CategoryGroupId int not null,
    CategoryName varchar(45) not null,
    Description text,
    
    constraint fk_category_categorygroup
        foreign key (CategoryGroupId)
        references CategoryGroup(CategoryGroupId)
) comment = 'Các thể loại sách';

create table Author (
    AuthorId int auto_increment primary key,
    AuthorCode varchar(20) unique,
    AuthorName varchar(255) not null,
    DoB date null,
    Description text
);

create table Book (
    BookId int auto_increment primary key,
    BookCode varchar(20) not null unique comment 'Mã sách logic',
    Title varchar(255) not null,
    
    ISBN varchar(20) not null,
    Publisher varchar(255) null,
    PublishYear YEAR null,
    Description TEXT
) comment = 'Thông tin sách';

create table BookAuthor (
    BookId int not null,
    AuthorId int not null,

    primary key (BookId, AuthorId),

    constraint fk_ba_book
        foreign key (BookId) references Book(BookId),
    constraint fk_ba_author
        foreign key (AuthorId) references Author(AuthorId)
);

create table BookCategory (
    BookId int not null,
    CategoryId int not null,
    primary key (BookId, CategoryId),

    constraint fk_bc_book
        foreign key (BookId) references Book(BookId),
    constraint fk_bc_category
        foreign key (CategoryId) references Category(CategoryId)
) comment = 'Quan hệ many-many, trung gian 1 sách nhiều thể loại - 1 thể loại nhiều sách';

create table BookCopy (
    CopyId int auto_increment primary key,
    CopyCode varchar(30) not null unique comment 'Mã cuốn vật lý (barcode)',
    BookId int not null,
    
    Status varchar(20) not null default 'Available' comment 'Available | Borrowed | Lost | Broken',
    Location varchar(100) null comment 'Vị trí kệ',
    Note TEXT,

	constraint CK_BookCopy_Status
        check (Status in ('Available','Borrowed','Lost','Broken')),
    constraint FK_BookCopy_Book
        foreign key (BookId) references Book(BookId)
) comment = 'Các bản sao vật lý của sách';

create table Staff (
    StaffId int auto_increment primary key,
    StaffCode varchar(20) not null unique comment 'Mã nhân viên',
    
    FullName varchar(100) not null,
    DoB date not null,
    Email varchar(100) not null unique,
    Phone varchar(15) not null unique,
    Note text
) comment = 'Nhân viên thư viện (Librarian, Senior Manager, Administrator)';

create table Reader (
    ReaderId int auto_increment primary key,
    ReaderCode varchar(20) not null unique comment 'Mã độc giả',
    
    FullName varchar(100) not null,
    DoB date null,
    Email varchar(100) not null unique,
    Phone varchar(15) not null unique,
    
    CreatedAt datetime default current_timestamp comment 'Ngày đăng kí',
    
    Status varchar(20) not null default 'Active' comment 'Active | Locked | Expired',
    Note text
) COMMENT = 'Độc giả thư viện, mặc định Status: Active. Nếu: Active hoạt động bình thường - Locked khóa quyền mượn - Expired chưa gia hạn';

create table Borrow (
    BorrowId int auto_increment primary key,
    BorrowCode varchar(20) not null unique comment 'Mã phiếu mượn',
    
    ReaderId int not null,
    StaffId int not null,
    
    BorrowDate datetime default current_timestamp comment 'Ngày mượn',
    DueDate datetime not null comment 'Ngày hẹn trả',

    Status varchar(20) not null default 'Borrowing' comment 'Borrowing | Returned | Overdue - Chỉ nên Returned khi tất cả BorrowDetail đã Returned',
    Note text,

    constraint FK_Borrow_Reader
        foreign key (ReaderId) references Reader(ReaderId),
    constraint FK_Borrow_Staff
        foreign key (StaffId) references Staff(StaffId)
) comment='Phiếu mượn - Lưu thông tin tổng thể: ai mượn, ngày mượn, ngày hẹn trả, nhân viên xử lý
		STATUS CHỈ NÊN RETURNED KHI TẤT CẢ BorrowDetail ĐÃ RETURNED';

create table BorrowDetail (
    BorrowDetailId int auto_increment primary key,

    BorrowId int not null,
    CopyId int not null,

    ReturnDate datetime null,
    Status varchar(20) not null default 'Borrowing' comment 'Borrowing | Returned | Lost',
    FineAmount decimal(10,2) default 0 comment 'Tiền phạt',
    Note text,
	
    unique (BorrowId, CopyId),
    
    constraint FK_BorrowDetail_Borrow
        foreign key (BorrowId) references Borrow(BorrowId),
    constraint FK_BorrowDetail_Copy
        foreign key (CopyId) references BookCopy(CopyId)
) comment='Chi tiết phiếu mượn - Lưu từng cuốn vật lý được mượn';

create table Account (
    AccountId int auto_increment primary key,
    -- Random 36 ký tự siêu bảo mật <(") (Yêu cầu MySQL 8.0+)
    AccountCode varchar(36) default (uuid()) unique,

    StaffId int null,
    ReaderId int null,

    Username varchar(100) not null unique,
    PasswordHash varchar(255) not null,

    Role varchar(20) not null,

    constraint ck_role
        check (Role in ('Reader','Librarian','Admin','Senior')),
    constraint fk_acc_staff
        foreign key (StaffId) references Staff(StaffId),
    constraint fk_acc_reader
        foreign key (ReaderId) references Reader(ReaderId),
    constraint ck_account_owner
        check (
            (StaffId is not null and ReaderId is null)
            or
            (StaffId is null and ReaderId is not null)
        )
) comment='Account: đăng nhập vào client chỉ cần username và password - đăng kí cần đầy đủ các trường dữ liệu. 
			Riêng trường [Role] tùy client sẽ tự động truyền chuỗi đăng nhập tích hợp sẵn';

########
# Dưới đây là các dữ liệu cơ bản được thêm
######## 

insert into CategoryGroup (CategoryGroupCode, CategoryGroupName, Description)
values  ('CG1', 'Fiction', 'Sách hư cấu - Truyện/Tiểu thuyết'),
		('CG2', 'Non-fiction', 'Sách phi hư cấu - Sách tri thức/thực tế'),
		('CG3', 'Target Audience', 'Sách theo đối tượng');

insert into Category (CategoryCode, CategoryGroupId, CategoryName, Description)
values  ('C1', 1,'Romance','Tiểu thuyết tình cảm - Chuyện tình yêu, cảm xúc'),
		('C2', 1, 'Fantasy/Sci-fi', 'Viễn tưởng/giả tưởng - Thế giới hư cấu, khoa học tương lai'),
        ('C3', 1, 'Horror/Thriller', 'Kinh dị/giật gân - Cảm giác mạnh, hồi hộp, bí ẩn'),
        ('C4', 1, 'Mystery/Detective', 'Trinh thám/bí ẩn - Giải mã vụ án'),
        ('C5', 1, 'Short Stories', 'Các tác phẩm ngắn gọn'),
        ('C6', 1, 'Comic Book/Graphic Novels', 'Truyện tranh/sách truyện đồ họa'),
        ('C7', 1, 'Classic Literature', 'Văn học cổ điển'),
        ('C8', 2, 'Self-help', 'Sách kĩ năng/truyền cảm hứng - Phát triển bản thân, tư duy'),
        ('C9', 2, 'Biography/Memoir', 'Tiểu sử/tự truyện'),
        ('C10', 2, 'Business/Finance', 'Sách kinh doanh/tài chính - Quản lý tài chính, kinh tế'),
        ('C11', 2, 'Science/Technology','Sách khoa học/công nghệ - Kiến thức khoa học'),
        ('C12', 2, 'History/Social-cultural', 'Sách lịch sử/văn hóa xã hội - Ghi chép lịch sử, xã hội'),
        ('C13', 2, 'Cookbooks', 'Sách nấu ăn/đời sống - Công thức, kỹ năng đời sống'),
        ('C14', 2, 'Textbooks', 'Sách giáo trình/tài liệu học tập - Sách giáo khoa, chuyên ngành'),
        ('C15', 2, 'Spirituality/Religion', 'Tâm linh/tôn giáo - Sách về đức tin, triết lý'),
        ('C16', 3, 'Children-book', 'Sách thiếu nhi'),
        ('C17', 3, 'Specialized-book', 'Sách chuyên ngành');

insert into Author (AuthorCode, AuthorName)
values
('A1','Nguyễn Nhật Ánh'),
('A2','J. K. Rowling');

insert into Book (BookCode, Title, ISBN)
values
('B1','Tôi thấy hoa vàng trên cỏ xanh', '123456'),
('B2','Hạ đỏ', '123457'),
('B3','Harry Potter','654321'),
('B4', 'Quidditch qua các thời đại', '754321');

insert into BookCategory (BookId, CategoryId)
values
(1,16), -- thiếu nhi
(2,1),  -- romance
(3,2),  -- fantasy
(3,16), -- thiếu nhi
(4,2); -- fantasy

insert into BookAuthor (BookId, AuthorId)
values
(1,1),
(2,1),
(3,2),
(4,2);

insert into BookCopy (CopyCode, BookId, Status, Location)
values
('BC0001', 1, 'Available', 'A1-S1'),
('BC0002', 1, 'Available', 'A1-S1'),
('BC0003', 2, 'Available', 'A1-S2'),
('BC0004', 3, 'Available', 'B1-S1'),
('BC0005', 3, 'Available', 'B1-S1'),
('BC0006', 4, 'Available', 'B1-S2');

insert into Reader (ReaderCode, FullName, Email, Phone)
values  ('R1','Test Reader','reader@gmail.com','0900000000');
insert into Account (Username, PasswordHash, Role, ReaderId) 
values  ('reader1', '123456', 'Reader', 1);

insert into Staff (StaffCode, FullName, DoB, Email, Phone)
values  ('S1','Test Librarian','1990-01-01','librarian@gmail.com','0911111111'),
		('S2','Test Admin','1990-01-01','admin@gmail.com','0911111112'),
        ('S3','Test Senior','1990-01-01','senior@gmail.com','0911111113');
insert into Account (Username, PasswordHash, Role, StaffId) 
values 	('librarian1', '123456', 'Librarian',1),
        ('admin1', '123456', 'Admin', 2),
        ('senior1', '123456', 'Senior', 3);
