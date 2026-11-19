package qlsv;

import javax.swing.*;
import java.sql.*;

public class SinhVienForm extends CrudForm {
    private final JTextField txtMa = new JTextField(), txtTen = new JTextField(), txtNgay = new JTextField(),
            txtDiaChi = new JTextField(), txtEmail = new JTextField(), txtSdt = new JTextField();
    private final JComboBox<String> cbGT = new JComboBox<>(new String[]{"Nam", "Nữ"});
    private final JComboBox<String> cbLop = combo("SELECT MaLop, TenLop FROM LOP ORDER BY MaLop");

    public SinhVienForm() { init("Quản lý Sinh viên", 950, 650); }

    @Override protected String[] columns() {
        return new String[]{"Mã SV", "Họ tên", "Ngày sinh", "Giới tính", "Địa chỉ", "Email", "Số ĐT", "Mã lớp"}; }
    @Override protected String searchSql() {
        return "SELECT MaSV, HoTen, NgaySinh, GioiTinh, DiaChi, Email, SoDT, MaLop FROM SINHVIEN "
             + "WHERE MaSV LIKE ? OR HoTen LIKE ? OR MaLop LIKE ? OR Email LIKE ? ORDER BY MaSV"; }
    @Override protected int searchParams() { return 4; }
    @Override protected JPanel inputPanel() {
        return formPanel(new Object[][]{
            {"Mã SV:", txtMa}, {"Họ tên:", txtTen}, {"Ngày sinh (yyyy-MM-dd):", txtNgay}, {"Giới tính:", cbGT},
            {"Địa chỉ:", txtDiaChi}, {"Email:", txtEmail}, {"Số điện thoại:", txtSdt}, {"Lớp:", cbLop}});
    }
    @Override protected void fill(Object[] r) {
        txtMa.setText(str(r[0])); txtTen.setText(str(r[1])); txtNgay.setText(str(r[2]));
        cbGT.setSelectedItem(str(r[3])); txtDiaChi.setText(str(r[4])); txtEmail.setText(str(r[5]));
        txtSdt.setText(str(r[6])); select(cbLop, r[7]); txtMa.setEditable(false);
    }
    @Override protected void clear() {
        for (JTextField t : new JTextField[]{txtMa, txtTen, txtNgay, txtDiaChi, txtEmail, txtSdt}) t.setText("");
        txtMa.setEditable(true);
    }
    @Override protected String validateInput() {
        if (text(txtMa).isEmpty()) return "Mã sinh viên không được để trống.";
        if (text(txtTen).isEmpty()) return "Họ tên không được để trống.";
        if (!text(txtNgay).isEmpty()) {
            try { Date.valueOf(text(txtNgay)); }
            catch (IllegalArgumentException e) { return "Ngày sinh phải theo dạng yyyy-MM-dd (VD: 2005-03-21)."; }
        }
        if (!text(txtEmail).isEmpty() && !text(txtEmail).matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$"))
            return "Email không hợp lệ.";
        if (code(cbLop) == null) return "Chưa có lớp. Hãy thêm lớp trước.";
        return null;
    }

    private void bind(PreparedStatement ps, int start) throws SQLException {
        // thứ tự: HoTen, NgaySinh, GioiTinh, DiaChi, Email, SoDT, MaLop
        ps.setString(start, text(txtTen));
        if (text(txtNgay).isEmpty()) ps.setNull(start + 1, Types.DATE); else ps.setDate(start + 1, Date.valueOf(text(txtNgay)));
        ps.setString(start + 2, (String) cbGT.getSelectedItem());
        ps.setString(start + 3, text(txtDiaChi).isEmpty() ? null : text(txtDiaChi));
        ps.setString(start + 4, text(txtEmail).isEmpty() ? null : text(txtEmail));
        ps.setString(start + 5, text(txtSdt).isEmpty() ? null : text(txtSdt));
        ps.setString(start + 6, code(cbLop));
    }

    @Override protected void insert(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO SINHVIEN(HoTen,NgaySinh,GioiTinh,DiaChi,Email,SoDT,MaLop,MaSV) VALUES(?,?,?,?,?,?,?,?)")) {
            bind(ps, 1); ps.setString(8, text(txtMa)); ps.executeUpdate();
        }
    }
    @Override protected void update(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE SINHVIEN SET HoTen=?,NgaySinh=?,GioiTinh=?,DiaChi=?,Email=?,SoDT=?,MaLop=? WHERE MaSV=?")) {
            bind(ps, 1); ps.setString(8, text(txtMa)); ps.executeUpdate();
        }
    }
    @Override protected void delete(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM SINHVIEN WHERE MaSV=?")) {
            ps.setString(1, text(txtMa)); ps.executeUpdate();
        }
    }
}
