# Quản lý sinh viên (Java Swing + SQL Server)

Đồ án lập trình hoàn thiện phần mềm. Ứng dụng desktop Java Swing kết nối Microsoft SQL Server bằng JDBC.

## Chức năng
- 5 form: **Khoa, Lớp, Sinh viên, Môn học, Điểm**
- Mỗi form có: **Thêm, Sửa, Xóa, Tìm kiếm, Làm mới**; kiểm tra dữ liệu đầu vào; thông báo lỗi khóa chính/khóa ngoại/trùng email
- Điểm trung bình môn tự tính trong CSDL: `DiemTB = DiemQT*0.4 + DiemThi*0.6`

## Cơ sở dữ liệu
5 bảng: KHOA, LOP, SINHVIEN, MONHOC, DIEM (script + dữ liệu mẫu: `sql/QuanLySinhVien.sql`).

## Cách chạy
1. Cài JDK 17+, Maven, SQL Server (bật TCP/IP, cổng 1433, bật SQL Server Authentication).
2. Chạy `sql/QuanLySinhVien.sql` trong SSMS.
3. Sửa user/mật khẩu trong `src/main/java/qlsv/DBConnection.java`.
4. Chạy:
   ```
   mvn compile exec:java
   ```
   Hoặc mở bằng IntelliJ / NetBeans / Eclipse (import Maven project) rồi chạy `qlsv.Main`.

## Cấu trúc
```
src/main/java/qlsv/
  Main.java, MainFrame.java, DBConnection.java
  CrudForm.java      (lớp form CRUD dùng chung)
  KhoaForm / LopForm / SinhVienForm / MonHocForm / DiemForm
sql/QuanLySinhVien.sql
```
