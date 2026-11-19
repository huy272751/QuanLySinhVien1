package qlsv;

import javax.swing.*;
import java.sql.*;

public class LopForm extends CrudForm {
    private final JTextField txtMa = new JTextField(), txtTen = new JTextField(), txtNK = new JTextField();
    private final JComboBox<String> cbKhoa = combo("SELECT MaKhoa, TenKhoa FROM KHOA ORDER BY MaKhoa");

    public LopForm() { init("Quản lý Lớp", 700, 560); }

    @Override protected String[] columns() { return new String[]{"Mã lớp", "Tên lớp", "Niên khóa", "Mã khoa"}; }
    @Override protected String searchSql() {
        return "SELECT MaLop, TenLop, NienKhoa, MaKhoa FROM LOP WHERE MaLop LIKE ? OR TenLop LIKE ? OR MaKhoa LIKE ? ORDER BY MaLop"; }
    @Override protected int searchParams() { return 3; }
    @Override protected JPanel inputPanel() {
        return formPanel(new Object[][]{{"Mã lớp:", txtMa}, {"Tên lớp:", txtTen}, {"Niên khóa:", txtNK}, {"Khoa:", cbKhoa}});
    }
    @Override protected void fill(Object[] r) {
        txtMa.setText(str(r[0])); txtTen.setText(str(r[1])); txtNK.setText(str(r[2])); select(cbKhoa, r[3]); txtMa.setEditable(false);
    }
    @Override protected void clear() { txtMa.setText(""); txtTen.setText(""); txtNK.setText(""); txtMa.setEditable(true); }
    @Override protected String validateInput() {
        if (text(txtMa).isEmpty()) return "Mã lớp không được để trống.";
        if (text(txtTen).isEmpty()) return "Tên lớp không được để trống.";
        if (code(cbKhoa) == null) return "Chưa có khoa. Hãy thêm khoa trước.";
        return null;
    }
    @Override protected void insert(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO LOP(MaLop,TenLop,NienKhoa,MaKhoa) VALUES(?,?,?,?)")) {
            ps.setString(1, text(txtMa)); ps.setString(2, text(txtTen));
            ps.setString(3, text(txtNK).isEmpty() ? null : text(txtNK)); ps.setString(4, code(cbKhoa));
            ps.executeUpdate();
        }
    }
    @Override protected void update(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE LOP SET TenLop=?, NienKhoa=?, MaKhoa=? WHERE MaLop=?")) {
            ps.setString(1, text(txtTen)); ps.setString(2, text(txtNK).isEmpty() ? null : text(txtNK));
            ps.setString(3, code(cbKhoa)); ps.setString(4, text(txtMa));
            ps.executeUpdate();
        }
    }
    @Override protected void delete(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM LOP WHERE MaLop=?")) {
            ps.setString(1, text(txtMa)); ps.executeUpdate();
        }
    }
}
