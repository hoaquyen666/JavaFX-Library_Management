# JavaFX-Library_Management
Library management project using JavaFX

___
```
1. Project setting
 - SDK: 21
 - Language level: SDK default

2. Database
 - MySQL

3. Luồng ý tưởng
 - Tách riêng bốn form làm bốn modules để xử lý
 + Module 1: Administrator
  -> Chịu trách nhiệm quản lý luồng dữ liệu database, tạo tài khoản mới cho Librarian
 + Module 2: Librarian
  -> Chịu trách nhiệm thêm các thông tin, thuộc tính của sách, bản sao. Hỗ trợ Reader (Nếu cần)
 + Module 3: Reader
  -> Người dùng có thể đăng kí/đăng nhập xem thông tin và tìm kiếm các loại sách, số lượng còn lại của bản sao
 + Module 4: Senior Manager
  -> Chịu trách nhiệm kiểm soát dữ liệu ngoài lề, xem thống kê, báo cáo, hỗ trợ Librarian mới vào làm. 
    Liên hệ Adminstrator tạo tài khoản Librarian
 
 ```
