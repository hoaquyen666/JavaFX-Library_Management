package com.example.librarian.dao;

import com.example.librarian.model.Supplier;
import com.example.librarian.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public List<Supplier> getAllSuppliers(){

        List<Supplier> list = new ArrayList<>();

        String sql = "SELECT * FROM supplier ORDER BY SupplierId";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()){

                Supplier s = new Supplier(
                        rs.getInt("SupplierId"),
                        rs.getString("SupplierCode"),
                        rs.getString("SupplierName"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        rs.getString("Address"),
                        rs.getString("Status")
                );

                list.add(s);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public boolean insertSupplier(Supplier supplier) {
        // Defining valid statuses for the 'Status' column
        List<String> validStatuses = List.of("Hoạt động", "Ngừng hoạt động");

        // Validate the supplier's status before proceeding
        if (!validStatuses.contains(supplier.getStatus())) {
            System.err.println("Error: Invalid supplier status: " + supplier.getStatus());
            return false; // Reject the insertion if the status is invalid
        }

        String sql = """
                INSERT INTO supplier (SupplierCode, SupplierName, Email, Phone, Address, Status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, supplier.getCode());
            ps.setString(2, supplier.getName());
            ps.setString(3, supplier.getEmail());
            ps.setString(4, supplier.getPhone());
            ps.setString(5, supplier.getAddress());
            ps.setString(6, supplier.getStatus());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSupplier(Supplier supplier) {
        String sql = """
                UPDATE supplier
                SET SupplierCode = ?, SupplierName = ?, Email = ?, Phone = ?, Address = ?, Status = ?
                WHERE SupplierId = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, supplier.getCode());
            ps.setString(2, supplier.getName());
            ps.setString(3, supplier.getEmail());
            ps.setString(4, supplier.getPhone());
            ps.setString(5, supplier.getAddress());
            ps.setString(6, supplier.getStatus());
            ps.setInt(7, supplier.getId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSupplier(int supplierId) {
        String sql = "DELETE FROM supplier WHERE SupplierId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supplierId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsByCode(String supplierCode, Integer excludeId) {
        String sql = excludeId == null
                ? "SELECT 1 FROM supplier WHERE SupplierCode = ? LIMIT 1"
                : "SELECT 1 FROM supplier WHERE SupplierCode = ? AND SupplierId <> ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, supplierCode);
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}