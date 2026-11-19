package qlsv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/** Form CRUD dùng chung: bảng dữ liệu + ô tìm kiếm + nút Thêm/Sửa/Xóa/Làm mới. */
public abstract class CrudForm extends JFrame {
    protected DefaultTableModel model;
    protected JTable table;
    protected JTextField txtSearch = new JTextField(20);

    protected abstract String[] columns();
    /** Câu SELECT có WHERE ... LIKE ? (số tham số = searchParams()). */
    protected abstract String searchSql();
    protected abstract int searchParams();
    protected abstract JPanel inputPanel();
    protected abstract void fill(Object[] row);
    protected abstract void clear();
    /** Trả về thông báo lỗi, hoặc null nếu hợp lệ. */
    protected abstract String validateInput();
    protected abstract void insert(Connection c) throws SQLException;
    protected abstract void update(Connection c) throws SQLException;
    protected abstract void delete(Connection c) throws SQLException;

    /** Gọi ở cuối constructor của lớp con. */
    protected void init(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel lbl = new JLabel(title.toUpperCase(), SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        add(lbl, BorderLayout.NORTH);

        // Giữa: form nhập + nút
        JPanel top = new JPanel(new BorderLayout());
        top.add(inputPanel(), BorderLayout.CENTER);

        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDel = new JButton("Xóa");
        JButton btnClear = new JButton("Làm mới");
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btns.add(btnAdd); btns.add(btnEdit); btns.add(btnDel); btns.add(btnClear);
        top.add(btns, BorderLayout.SOUTH);

        // Tìm kiếm
        JButton btnSearch = new JButton("Tìm kiếm");
        JPanel sp = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        sp.add(new JLabel("Từ khóa:")); sp.add(txtSearch); sp.add(btnSearch);

        model = new DefaultTableModel(columns(), 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && r >= 0) {
                Object[] row = new Object[model.getColumnCount()];
                for (int i = 0; i < row.length; i++) row[i] = model.getValueAt(r, i);
                fill(row);
            }
        });

        JPanel center = new JPanel(new BorderLayout());
        center.add(sp, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout(0, 8));
        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        body.add(top, BorderLayout.NORTH);
        body.add(center, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> run("Thêm thành công!", true, this::insert));
        btnEdit.addActionListener(e -> run("Cập nhật thành công!", true, this::update));
        btnDel.addActionListener(e -> {
            if (table.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng để xóa.");
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                run("Xóa thành công!", false, this::delete);
        });
        btnClear.addActionListener(e -> { clear(); table.clearSelection(); txtSearch.setText(""); loadData(); });
        btnSearch.addActionListener(e -> loadData());
        txtSearch.addActionListener(e -> loadData());

        loadData();
    }

    private interface DbAction { void exec(Connection c) throws SQLException; }

    private void run(String okMsg, boolean validate, DbAction action) {
        if (validate) {
            String err = validateInput();
            if (err != null) { JOptionPane.showMessageDialog(this, err, "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE); return; }
        }
        try (Connection c = DBConnection.getConnection()) {
            action.exec(c);
            JOptionPane.showMessageDialog(this, okMsg);
            clear();
            loadData();
        } catch (SQLException ex) {
            String m = ex.getMessage();
            if (m != null && m.contains("PRIMARY KEY")) m = "Mã/khóa đã tồn tại.";
            else if (m != null && m.contains("UNIQUE")) m = "Giá trị bị trùng (ví dụ Email đã tồn tại).";
            else if (m != null && m.contains("REFERENCE")) m = "Dữ liệu đang được bảng khác sử dụng nên không xóa được.";
            JOptionPane.showMessageDialog(this, m, "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void loadData() {
        model.setRowCount(0);
        String kw = "%" + txtSearch.getText().trim() + "%";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(searchSql())) {
            for (int i = 1; i <= searchParams(); i++) ps.setString(i, kw);
            try (ResultSet rs = ps.executeQuery()) {
                int n = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] row = new Object[n];
                    for (int i = 0; i < n; i++) row[i] = rs.getObject(i + 1);
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi tải dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Tiện ích =====
    protected static JPanel formPanel(Object[][] labelAndField) {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < labelAndField.length; i++) {
            g.gridy = i;
            g.gridx = 0; g.weightx = 0;
            p.add(new JLabel((String) labelAndField[i][0]), g);
            g.gridx = 1; g.weightx = 1;
            p.add((Component) labelAndField[i][1], g);
        }
        return p;
    }

    /** ComboBox nạp từ SQL (2 cột: mã, tên) -> "mã - tên". */
    protected static JComboBox<String> combo(String sql) {
        JComboBox<String> cb = new JComboBox<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) cb.addItem(rs.getString(1) + " - " + rs.getString(2));
        } catch (SQLException ignored) { }
        return cb;
    }

    protected static String code(JComboBox<String> cb) {
        Object o = cb.getSelectedItem();
        return o == null ? null : o.toString().split(" - ")[0].trim();
    }

    protected static void select(JComboBox<String> cb, Object code) {
        if (code == null) return;
        for (int i = 0; i < cb.getItemCount(); i++)
            if (cb.getItemAt(i).startsWith(code + " - ")) { cb.setSelectedIndex(i); return; }
    }

    protected static String text(JTextField t) { return t.getText().trim(); }
    protected static String str(Object o) { return o == null ? "" : o.toString(); }
}
