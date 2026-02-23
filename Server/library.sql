USE library;



create table Category (
	CategoryId int auto_increment primary key,
    CategoryName varchar(45) not null,
    Description text
)comment = 'Các thể loại sách';

create table Book (
    BookId int auto_increment primary key,
    BookCode varchar(20) not null unique comment 'Mã sách logic',
    Title varchar(255) not null,
    CategoryId int not null,
    ISBN varchar(20) not null,
    Author varchar(255) not null,
    Publisher varchar(255) null,
    PublishYear YEAR null,
    Description TEXT,

    constraint FK_Book_Category
        foreign key (CategoryId) references Category(CategoryId)
) comment = 'Thông tin sách';

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
	StaffId int null,
	ReaderId int null,
    
	AccountId int auto_increment primary key,
    AccountCode varchar(20) not null unique comment 'Mã tài khoản',
    Email varchar(100) not null unique,
    Phone varchar(15) not null unique,
    Username varchar(100) not null unique,
    PasswordHash varchar(255),
    Role varchar(10) not null comment 'Reader | Librarian | Admin | Senior',
    
    constraint CK_Account_Role
        check (Role in ('Reader','Librarian','Admin','Senior')),
    
    foreign key (StaffId) references Staff(StaffId),
	foreign key (ReaderId) references Reader(ReaderId)
) comment='Account: đăng nhập vào client chỉ cần username và password - đăng kí cần đầy đủ các trường dữ liệu. 
			Riêng trường [Role] tùy client sẽ tự động truyền chuỗi đăng nhập tích hợp sẵn'; 