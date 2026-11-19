package qlsv;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> {
            try (Connection c = DBConnection.getConnection()) {
                new MainFrame().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Không kết nối được SQL Server:\n" + e.getMessage()
                    + "\n\nKiểm tra DBConnection.java (user/pass) và đã chạy sql/QuanLySinhVien.sql chưa.",
                    "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
