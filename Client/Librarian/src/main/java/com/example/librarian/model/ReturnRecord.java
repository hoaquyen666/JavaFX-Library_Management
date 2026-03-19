package com.example.librarian.model;

public class ReturnRecord extends Borrow {
    // Chúng ta kế thừa Borrow để tận dụng các trường có sẵn
    public ReturnRecord(int borrowId,
                        String borrowCode,
                        String readerCode,
                        String staffCode,
                        String borrowDate,
                        String dueDate,
                        int quantity,
                        String status) {
                super(borrowId,
                borrowCode,
                readerCode,
                staffCode,
                borrowDate,
                dueDate,
                quantity,
                status);
    }

}