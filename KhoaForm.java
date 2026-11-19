package qlsv;

import javax.swing.*;
import java.sql.*;

public class KhoaForm extends CrudForm {
    private final JTextField txtMa = new JTextField(), txtTen = new JTextField(), txtSdt = new JTextField();

    public KhoaForm() { init("Quản lý Khoa", 700, 520); }

    @Override protected String[] columns() { return new String[]{"Mã khoa", "Tên khoa", "Số ĐT"}; }
    @Override protected String searchSql() {
        return "SELECT MaKhoa, TenKhoa, SoDT FROM KHOA WHERE MaKhoa LIKE ? OR TenKhoa LIKE ? ORDER BY MaKhoa"; }
    @Override protected int searchParams() { return 2; }
    @Override protected JPanel inputPanel() {
        return formPanel(new Object[][]{{"Mã khoa:", txtMa}, {"Tên khoa:", txtTen}, {"Số điện thoại:", txtSdt}});
    }
    @Override protected void fill(Object[] r) { txtMa.setText(str(r[0])); txtTen.setText(str(r[1])); txtSdt.setText(str(r[2])); txtMa.setEditable(false); }
    @Override protected void clear() { txtMa.setText(""); txtTen.setText(""); txtSdt.setText(""); txtMa.setEditable(true); }
    @Override protected String validateInput() {
        if (text(txtMa).isEmpty()) return "Mã khoa không được để trống.";
        if (text(txtTen).isEmpty()) return "Tên khoa không được để trống.";
        return null;
    }
    @Override protected void insert(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO KHOA(MaKhoa,TenKhoa,SoDT) VALUES(?,?,?)")) {
            ps.setString(1, text(txtMa)); ps.setString(2, text(txtTen));
            ps.setString(3, text(txtSdt).isEmpty() ? null : text(txtSdt));
            ps.executeUpdate();
        }
    }
    @Override protected void update(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE KHOA SET TenKhoa=?, SoDT=? WHERE MaKhoa=?")) {
            ps.setString(1, text(txtTen)); ps.setString(2, text(txtSdt).isEmpty() ? null : text(txtSdt));
            ps.setString(3, text(txtMa));
            ps.executeUpdate();
        }
    }
    @Override protected void delete(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM KHOA WHERE MaKhoa=?")) {
            ps.setString(1, text(txtMa)); ps.executeUpdate();
        }
    }
}
