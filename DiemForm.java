package qlsv;

import javax.swing.*;
import java.sql.*;

public class DiemForm extends CrudForm {
    private final JComboBox<String> cbSV = combo("SELECT MaSV, HoTen FROM SINHVIEN ORDER BY MaSV");
    private final JComboBox<String> cbMH = combo("SELECT MaMH, TenMH FROM MONHOC ORDER BY MaMH");
    private final JTextField txtHK = new JTextField(), txtQT = new JTextField(), txtThi = new JTextField(),
            txtTB = new JTextField();

    public DiemForm() { txtTB.setEditable(false); init("Quản lý Điểm", 900, 600); }

    @Override protected String[] columns() {
        return new String[]{"Mã SV", "Họ tên", "Mã MH", "Tên môn", "Học kỳ", "Điểm QT", "Điểm thi", "Điểm TB"}; }
    @Override protected String searchSql() {
        return "SELECT d.MaSV, s.HoTen, d.MaMH, m.TenMH, d.HocKy, d.DiemQT, d.DiemThi, d.DiemTB "
             + "FROM DIEM d JOIN SINHVIEN s ON d.MaSV=s.MaSV JOIN MONHOC m ON d.MaMH=m.MaMH "
             + "WHERE d.MaSV LIKE ? OR s.HoTen LIKE ? OR d.MaMH LIKE ? OR m.TenMH LIKE ? ORDER BY d.MaSV, d.HocKy"; }
    @Override protected int searchParams() { return 4; }
    @Override protected JPanel inputPanel() {
        return formPanel(new Object[][]{
            {"Sinh viên:", cbSV}, {"Môn học:", cbMH}, {"Học kỳ:", txtHK},
            {"Điểm quá trình:", txtQT}, {"Điểm thi:", txtThi}, {"Điểm TB (tự tính 40/60):", txtTB}});
    }
    @Override protected void fill(Object[] r) {
        select(cbSV, r[0]); select(cbMH, r[2]); txtHK.setText(str(r[4]));
        txtQT.setText(str(r[5])); txtThi.setText(str(r[6])); txtTB.setText(str(r[7]));
        cbSV.setEnabled(false); cbMH.setEnabled(false); txtHK.setEditable(false);
    }
    @Override protected void clear() {
        txtHK.setText(""); txtQT.setText(""); txtThi.setText(""); txtTB.setText("");
        cbSV.setEnabled(true); cbMH.setEnabled(true); txtHK.setEditable(true);
    }

    private Double score(JTextField t) { return text(t).isEmpty() ? null : Double.valueOf(text(t).replace(',', '.')); }

    @Override protected String validateInput() {
        if (code(cbSV) == null || code(cbMH) == null) return "Cần có sinh viên và môn học.";
        try { Integer.parseInt(text(txtHK)); } catch (NumberFormatException e) { return "Học kỳ phải là số nguyên."; }
        try {
            for (JTextField t : new JTextField[]{txtQT, txtThi}) {
                Double d = score(t);
                if (d != null && (d < 0 || d > 10)) return "Điểm phải nằm trong khoảng 0 - 10.";
            }
        } catch (NumberFormatException e) { return "Điểm phải là số."; }
        return null;
    }

    private void setScore(PreparedStatement ps, int idx, Double d) throws SQLException {
        if (d == null) ps.setNull(idx, Types.FLOAT); else ps.setDouble(idx, d);
    }

    @Override protected void insert(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO DIEM(MaSV,MaMH,HocKy,DiemQT,DiemThi) VALUES(?,?,?,?,?)")) {
            ps.setString(1, code(cbSV)); ps.setString(2, code(cbMH)); ps.setInt(3, Integer.parseInt(text(txtHK)));
            setScore(ps, 4, score(txtQT)); setScore(ps, 5, score(txtThi));
            ps.executeUpdate();
        }
    }
    @Override protected void update(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE DIEM SET DiemQT=?, DiemThi=? WHERE MaSV=? AND MaMH=? AND HocKy=?")) {
            setScore(ps, 1, score(txtQT)); setScore(ps, 2, score(txtThi));
            ps.setString(3, code(cbSV)); ps.setString(4, code(cbMH)); ps.setInt(5, Integer.parseInt(text(txtHK)));
            ps.executeUpdate();
        }
    }
    @Override protected void delete(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM DIEM WHERE MaSV=? AND MaMH=? AND HocKy=?")) {
            ps.setString(1, code(cbSV)); ps.setString(2, code(cbMH)); ps.setInt(3, Integer.parseInt(text(txtHK)));
            ps.executeUpdate();
        }
    }
}
