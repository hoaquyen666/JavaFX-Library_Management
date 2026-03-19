CREATE
DATABASE IF NOT EXISTS library
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

use
library;

create table CategoryGroup
(
    CategoryGroupId   int auto_increment primary key,
    CategoryGroupCode varchar(20) null unique,
    CategoryGroupName varchar(45) null,
    Description       text
) comment = 'Các nhóm thể loại sách';

create table Category
(
    CategoryId      int auto_increment primary key,
    CategoryCode    varchar(20) null unique,
    CategoryGroupId int         null,
    CategoryName    varchar(45) null,
    Description     text,

    constraint fk_category_categorygroup
        foreign key (CategoryGroupId)
            references CategoryGroup (CategoryGroupId)
) comment = 'Các thể loại sách';

create table Author
(
    AuthorId    int auto_increment primary key,
    AuthorCode  varchar(20) unique,
    AuthorName  varchar(255) null,
    DoB         date null,
    Description text
);

create table Book
(
    BookId      int auto_increment primary key,
    BookCode    varchar(20)  null unique comment 'Mã sách logic',
    Title       varchar(255) null,

    ISBN        varchar(20)  null,
    Publisher   varchar(255) null,
    PublishYear YEAR null,
    Description TEXT,
    Price DECIMAL(13,0) null
) comment = 'Thông tin sách';

create table BookAuthor
(
    BookId   int not null,
    AuthorId int not null,

    primary key (BookId, AuthorId),

    constraint fk_ba_book
        foreign key (BookId) references Book (BookId),
    constraint fk_ba_author
        foreign key (AuthorId) references Author (AuthorId)
);

create table BookCategory
(
    BookId     int not null,
    CategoryId int not null,
    primary key (BookId, CategoryId),

    constraint fk_bc_book
        foreign key (BookId) references Book (BookId),
    constraint fk_bc_category
        foreign key (CategoryId) references Category (CategoryId)
) comment = 'Quan hệ many-many, trung gian 1 sách nhiều thể loại - 1 thể loại nhiều sách';

create table BookCopy
(
    CopyId   int auto_increment primary key,
    CopyCode varchar(30) null unique comment 'Mã cuốn vật lý (barcode)',
    BookId   int         null,

    Status   varchar(20) null default 'Available' comment 'Available | Borrowed | Lost | Broken',
    Location varchar(100) null comment 'Vị trí kệ',
    Note     TEXT,

    constraint CK_BookCopy_Status
        check (Status in ('Available', 'Borrowed', 'Lost', 'Broken')),
    constraint FK_BookCopy_Book
        foreign key (BookId) references Book (BookId)
) comment = 'Các bản sao vật lý của sách';

create table Staff
(
    StaffId   int auto_increment primary key,
    StaffCode varchar(20)  null unique comment 'Mã nhân viên',

    Role      varchar(20)  null,

    FullName  varchar(100) null,
    DoB       date         null,
    Email     varchar(100) null unique,
    Phone     varchar(15)  null unique,
    Note      text,

    constraint ck_staff_role
        check (Role in ('Librarian', 'Admin', 'Senior'))

) comment = 'Nhân viên thư viện (Librarian, Senior Manager, Administrator)';

create table Reader
(
    ReaderId   int auto_increment primary key,
    ReaderCode varchar(20)  null unique comment 'Mã độc giả',

    FullName   varchar(100) null,
    DoB        date null,
    Email      varchar(100) null unique,
    Phone      varchar(15)  null unique,

    CreatedAt  datetime              default current_timestamp comment 'Ngày đăng kí',

    Status     varchar(20)  null default 'Active' comment 'Active | Locked | Expired',
    Note       text
) COMMENT = 'Độc giả thư viện, mặc định Status: Active. Nếu: Active hoạt động bình thường - Locked khóa quyền mượn - Expired chưa gia hạn';

create table Borrow
(
    BorrowId   int auto_increment primary key,
    BorrowCode varchar(20) null unique comment 'Mã phiếu mượn',

    ReaderId   int         null,
    StaffId    int         null,

    BorrowDate datetime             default current_timestamp comment 'Ngày mượn',
    DueDate    datetime    null comment 'Ngày hẹn trả',

    Status     varchar(20) null default 'Borrowing' comment 'Borrowing | Returned | Overdue - Chỉ nên Returned khi tất cả BorrowDetail đã Returned',
    Note       text,

    constraint FK_Borrow_Reader
        foreign key (ReaderId) references Reader (ReaderId),
    constraint FK_Borrow_Staff
        foreign key (StaffId) references Staff (StaffId)
) comment='Phiếu mượn - Lưu thông tin tổng thể: ai mượn, ngày mượn, ngày hẹn trả, nhân viên xử lý
		STATUS CHỈ NÊN RETURNED KHI TẤT CẢ BorrowDetail ĐÃ RETURNED';

create table BorrowDetail
(
    BorrowDetailId int auto_increment primary key,

    BorrowId       int         null,
    CopyId         int         null,

    ReturnDate     datetime null,
    Status         varchar(20)  null default 'Borrowing' comment 'Borrowing | Returned | Lost',
    FineAmount     decimal(10, 2)       default 0 comment 'Tiền phạt',
    Note           text,
    DepositAmount decimal(13,0) default 0 comment 'Tiền cọc = 1/2 giá sách'

    unique (BorrowId, CopyId),

    constraint FK_Price_Book

    constraint FK_BorrowDetail_Borrow
        foreign key (BorrowId) references Borrow (BorrowId),
    constraint FK_BorrowDetail_Copy
        foreign key (CopyId) references BookCopy (CopyId)
) comment='Chi tiết phiếu mượn - Lưu từng cuốn vật lý được mượn';

create table Account
(
    AccountId    int auto_increment primary key,
    -- Random 36 ký tự siêu bảo mật <(") - xử lý trong client
    AccountCode  varchar(36),

    StaffId      int null,
    ReaderId     int null,

    Username     varchar(100) not null unique,
    PasswordHash varchar(255) not null,

    Role         varchar(20)  not null,

    constraint ck_role
        check (Role in ('Reader', 'Librarian', 'Admin', 'Senior')),
    constraint fk_acc_staff
        foreign key (StaffId) references Staff (StaffId),
    constraint fk_acc_reader
        foreign key (ReaderId) references Reader (ReaderId),
    constraint ck_account_owner
        check (
            (StaffId is not null and ReaderId is null)
                or
            (StaffId is null and ReaderId is not null)
            )
) comment='Account: đăng nhập vào client chỉ cần username và password - đăng kí cần đầy đủ các trường dữ liệu.
			Riêng trường [Role] tùy client sẽ tự động truyền chuỗi đăng nhập tích hợp sẵn';

create table shift
(
    ShiftId    int primary key auto_increment,
    ShiftName  varchar(50) null,
    start_time time        null,
    end_time   time        null
) comment='Định nghĩa ca làm';

CREATE TABLE librarian_shift
(
    Assignment_id int primary key auto_increment,
    Librarian_id  INT  NULL,
    ShiftId       INT  NULL,
    Work_date     DATE NULL,
    Assigned_by   INT  NULL,
    Assigned_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    Attendance_Status BOOLEAN DEFAULT FALSE COMMENT '0: Vắng/Chưa làm, 1: Có mặt',

    unique (Librarian_id, Work_date, ShiftId),

    constraint FK_LS_Librarian
        foreign key (Librarian_id) references Staff (StaffId),

    constraint FK_LS_Shift
        foreign key (ShiftId) references shift (ShiftId),

    constraint FK_LS_AssignedBy
        foreign key (Assigned_by) references Staff (StaffId)
) comment= 'Phân công ca làm';

-- create table Supplier
-- (
--     SupplierId    int auto_increment primary key,
--     SupplierCode  varchar(20)  null unique comment 'Mã nhà cung cấp',
--     SupplierName  varchar(255) null,
--     ContactPerson varchar(100) null,
--     Email         varchar(100) null,
--     Phone         varchar(15) null,
--     Address       varchar(255) null,
--     Status        varchar(20)  null default 'Active',
--     Note          text,
--
--     constraint ck_supplier_status
--         check (Status in ('Active', 'Inactive'))
-- ) comment = 'Nhà cung cấp sách';



