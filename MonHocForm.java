package qlsv;

import javax.swing.*;
import java.sql.*;

public class MonHocForm extends CrudForm {
    private final JTextField txtMa = new JTextField(), txtTen = new JTextField(), txtTC = new JTextField();
    private final JComboBox<String> cbKhoa = combo("SELECT MaKhoa, TenKhoa FROM KHOA ORDER BY MaKhoa");

    public MonHocForm() { init("Quản lý Môn học", 700, 560); }

    @Override protected String[] columns() { return new String[]{"Mã MH", "Tên môn học", "Số tín chỉ", "Mã khoa"}; }
    @Override protected String searchSql() {
        return "SELECT MaMH, TenMH, SoTinChi, MaKhoa FROM MONHOC WHERE MaMH LIKE ? OR TenMH LIKE ? OR MaKhoa LIKE ? ORDER BY MaMH"; }
    @Override protected int searchParams() { return 3; }
    @Override protected JPanel inputPanel() {
        return formPanel(new Object[][]{{"Mã môn học:", txtMa}, {"Tên môn học:", txtTen}, {"Số tín chỉ:", txtTC}, {"Khoa:", cbKhoa}});
    }
    @Override protected void fill(Object[] r) {
        txtMa.setText(str(r[0])); txtTen.setText(str(r[1])); txtTC.setText(str(r[2])); select(cbKhoa, r[3]); txtMa.setEditable(false);
    }
    @Override protected void clear() { txtMa.setText(""); txtTen.setText(""); txtTC.setText(""); txtMa.setEditable(true); }
    @Override protected String validateInput() {
        if (text(txtMa).isEmpty()) return "Mã môn học không được để trống.";
        if (text(txtTen).isEmpty()) return "Tên môn học không được để trống.";
        try { if (Integer.parseInt(text(txtTC)) <= 0) return "Số tín chỉ phải > 0."; }
        catch (NumberFormatException e) { return "Số tín chỉ phải là số nguyên."; }
        if (code(cbKhoa) == null) return "Chưa có khoa. Hãy thêm khoa trước.";
        return null;
    }
    @Override protected void insert(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO MONHOC(MaMH,TenMH,SoTinChi,MaKhoa) VALUES(?,?,?,?)")) {
            ps.setString(1, text(txtMa)); ps.setString(2, text(txtTen));
            ps.setInt(3, Integer.parseInt(text(txtTC))); ps.setString(4, code(cbKhoa));
            ps.executeUpdate();
        }
    }
    @Override protected void update(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE MONHOC SET TenMH=?, SoTinChi=?, MaKhoa=? WHERE MaMH=?")) {
            ps.setString(1, text(txtTen)); ps.setInt(2, Integer.parseInt(text(txtTC)));
            ps.setString(3, code(cbKhoa)); ps.setString(4, text(txtMa));
            ps.executeUpdate();
        }
    }
    @Override protected void delete(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM MONHOC WHERE MaMH=?")) {
            ps.setString(1, text(txtMa)); ps.executeUpdate();
        }
    }
}
