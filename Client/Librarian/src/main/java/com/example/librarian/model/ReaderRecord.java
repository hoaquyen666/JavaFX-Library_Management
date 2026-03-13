package com.example.librarian.model;

public record ReaderRecord(
        String readerCode,
        String username,
        String fullName,
        String phone,
        String email,
        String status
) {
}
