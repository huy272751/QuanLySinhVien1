package qlsv;

import javax.swing.*;
import java.awt.*;

/** Form chính: mở các form quản lý. */
public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("QUẢN LÝ SINH VIÊN");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 420);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("QUẢN LÝ SINH VIÊN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        JPanel grid = new JPanel(new GridLayout(5, 1, 10, 10));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50));
        grid.add(button("Quản lý Khoa", () -> new KhoaForm().setVisible(true)));
        grid.add(button("Quản lý Lớp", () -> new LopForm().setVisible(true)));
        grid.add(button("Quản lý Sinh viên", () -> new SinhVienForm().setVisible(true)));
        grid.add(button("Quản lý Môn học", () -> new MonHocForm().setVisible(true)));
        grid.add(button("Quản lý Điểm", () -> new DiemForm().setVisible(true)));

        add(title, BorderLayout.NORTH);
        add(grid, BorderLayout.CENTER);
    }

    private JButton button(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        b.addActionListener(e -> action.run());
        return b;
    }
}
