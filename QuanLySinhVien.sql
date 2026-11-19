-- Chạy toàn bộ script này trong SQL Server Management Studio (lưu file dạng Unicode/UTF-8)
IF DB_ID('QuanLySinhVien') IS NOT NULL
BEGIN
    ALTER DATABASE QuanLySinhVien SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLySinhVien;
END
GO
CREATE DATABASE QuanLySinhVien;
GO
USE QuanLySinhVien;
GO

CREATE TABLE KHOA (
    MaKhoa   VARCHAR(10)   PRIMARY KEY,
    TenKhoa  NVARCHAR(100) NOT NULL,
    SoDT     VARCHAR(15)
);

CREATE TABLE LOP (
    MaLop    VARCHAR(10)   PRIMARY KEY,
    TenLop   NVARCHAR(100) NOT NULL,
    NienKhoa VARCHAR(20),
    MaKhoa   VARCHAR(10)   NOT NULL,
    CONSTRAINT FK_LOP_KHOA FOREIGN KEY (MaKhoa) REFERENCES KHOA(MaKhoa)
);

CREATE TABLE SINHVIEN (
    MaSV     VARCHAR(10)   PRIMARY KEY,
    HoTen    NVARCHAR(100) NOT NULL,
    NgaySinh DATE,
    GioiTinh NVARCHAR(5)   CHECK (GioiTinh IN (N'Nam', N'Nữ')),
    DiaChi   NVARCHAR(200),
    Email    VARCHAR(100)  UNIQUE,
    SoDT     VARCHAR(15),
    MaLop    VARCHAR(10)   NOT NULL,
    CONSTRAINT FK_SV_LOP FOREIGN KEY (MaLop) REFERENCES LOP(MaLop)
);

CREATE TABLE MONHOC (
    MaMH     VARCHAR(10)   PRIMARY KEY,
    TenMH    NVARCHAR(100) NOT NULL,
    SoTinChi INT           CHECK (SoTinChi > 0),
    MaKhoa   VARCHAR(10)   NOT NULL,
    CONSTRAINT FK_MH_KHOA FOREIGN KEY (MaKhoa) REFERENCES KHOA(MaKhoa)
);

CREATE TABLE DIEM (
    MaSV     VARCHAR(10),
    MaMH     VARCHAR(10),
    HocKy    INT,
    DiemQT   FLOAT CHECK (DiemQT BETWEEN 0 AND 10),
    DiemThi  FLOAT CHECK (DiemThi BETWEEN 0 AND 10),
    DiemTB   AS (DiemQT * 0.4 + DiemThi * 0.6),
    PRIMARY KEY (MaSV, MaMH, HocKy),
    CONSTRAINT FK_DIEM_SV FOREIGN KEY (MaSV) REFERENCES SINHVIEN(MaSV),
    CONSTRAINT FK_DIEM_MH FOREIGN KEY (MaMH) REFERENCES MONHOC(MaMH)
);
GO

-- ===== DỮ LIỆU MẪU =====
INSERT INTO KHOA VALUES
('CNTT', N'Công nghệ thông tin', '0243111111'),
('KT',   N'Kinh tế',             '0243222222'),
('NN',   N'Ngoại ngữ',           '0243333333');

INSERT INTO LOP VALUES
('CNTT01', N'Công nghệ thông tin 01', '2023-2027', 'CNTT'),
('CNTT02', N'Công nghệ thông tin 02', '2023-2027', 'CNTT'),
('KT01',   N'Kinh tế 01',             '2023-2027', 'KT'),
('NN01',   N'Ngôn ngữ Anh 01',        '2024-2028', 'NN');

INSERT INTO SINHVIEN VALUES
('SV001', N'Nguyễn Văn An',   '2005-03-21', N'Nam', N'Cầu Giấy, Hà Nội',   'an.nv@example.com',   '0901000001', 'CNTT01'),
('SV002', N'Trần Thị Bình',   '2005-07-15', N'Nữ',  N'Đống Đa, Hà Nội',    'binh.tt@example.com', '0901000002', 'CNTT01'),
('SV003', N'Lê Minh Cường',   '2005-11-02', N'Nam', N'Thanh Xuân, Hà Nội', 'cuong.lm@example.com','0901000003', 'CNTT02'),
('SV004', N'Phạm Thu Dung',   '2005-01-30', N'Nữ',  N'Hà Đông, Hà Nội',    'dung.pt@example.com', '0901000004', 'CNTT02'),
('SV005', N'Hoàng Gia Huy',   '2005-09-09', N'Nam', N'Long Biên, Hà Nội',  'huy.hg@example.com',  '0901000005', 'KT01'),
('SV006', N'Vũ Ngọc Lan',     '2005-05-18', N'Nữ',  N'Nam Từ Liêm, Hà Nội','lan.vn@example.com',  '0901000006', 'KT01'),
('SV007', N'Đặng Quốc Khánh', '2006-02-12', N'Nam', N'Hoàng Mai, Hà Nội',  'khanh.dq@example.com','0901000007', 'NN01'),
('SV008', N'Bùi Thảo My',     '2006-06-25', N'Nữ',  N'Tây Hồ, Hà Nội',     'my.bt@example.com',   '0901000008', 'NN01');

INSERT INTO MONHOC VALUES
('LTJAVA', N'Lập trình Java',          3, 'CNTT'),
('CSDL',   N'Cơ sở dữ liệu',           3, 'CNTT'),
('CTDL',   N'Cấu trúc dữ liệu',        3, 'CNTT'),
('KTVM',   N'Kinh tế vi mô',           2, 'KT'),
('NguPhap',N'Ngữ pháp tiếng Anh',      2, 'NN');

INSERT INTO DIEM(MaSV, MaMH, HocKy, DiemQT, DiemThi) VALUES
('SV001','LTJAVA',1,8.0,7.5), ('SV001','CSDL',1,9.0,8.5), ('SV001','CTDL',2,7.0,6.5),
('SV002','LTJAVA',1,9.5,9.0), ('SV002','CSDL',1,8.0,8.0),
('SV003','LTJAVA',1,6.5,7.0), ('SV003','CTDL',2,7.5,8.0),
('SV004','CSDL',1,8.5,9.0),
('SV005','KTVM',1,7.0,7.5), ('SV006','KTVM',1,8.0,8.5),
('SV007','NguPhap',1,9.0,8.0), ('SV008','NguPhap',1,7.5,8.5);
GO

-- Kiểm tra nhanh
SELECT * FROM KHOA; SELECT * FROM LOP; SELECT * FROM SINHVIEN; SELECT * FROM MONHOC; SELECT * FROM DIEM;
