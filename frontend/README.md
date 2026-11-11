# Campus Assistant — Frontend

Giao diện quản lý học vụ cho hệ thống Campus Assistant, xây dựng bằng React + TypeScript + Vite và quản lý trạng thái bằng zustand.

## Tính năng

- Đăng nhập bằng email/mật khẩu qua `POST /auth/login`. Hệ thống **không có luồng tự đăng ký**; tài khoản do quản trị viên tạo.
- Điều hướng theo vai trò lấy từ JWT:
  - `STUDENT`: Trợ lý ảo, Tài liệu, Ngành học, Tài khoản.
  - `ADMIN`: Tài liệu, Ngành học, Đơn vị, Sinh viên, Quản trị viên, Tài khoản.
- **Trợ lý ảo**: tạo cuộc trò chuyện, gửi câu hỏi tới agent-service với `student_code`, hiển thị câu trả lời, nguồn tham khảo và dữ liệu công cụ; đổi tên/xoá hội thoại.
- **Tài liệu**: danh sách có phân trang, tìm/xem trước trong ứng dụng, tải xuống; ADMIN có thể tải lên, sửa, xoá.
- **Ngành học**: danh mục ngành, lọc theo mã/tên/đơn vị; ADMIN thêm, sửa (không đổi mã), xoá.
- **Đơn vị**: danh sách khoa/trường và số ngành trực thuộc; ADMIN thêm/xoá.
- **Sinh viên**: tra cứu hồ sơ theo mã sinh viên, cập nhật họ tên/niên khóa/ngành, tạo tài khoản liên kết.
- **Quản trị viên**: danh sách hồ sơ quản trị viên và CRUD.
- **Tài khoản**: thông tin đăng nhập, hồ sơ sinh viên/quản trị viên liên kết, tự cập nhật hồ sơ sinh viên.

## Chạy dự án

```bash
npm install --legacy-peer-deps
npm run dev
```

Mặc định chạy tại `http://localhost:5173`. Dev server proxy:

| Tiền tố | Đích | Ghi chú |
| --- | --- | --- |
| `/api` | `http://localhost:8080` | Bỏ tiền tố trước khi chuyển tiếp |
| `/agent` | `http://localhost:8001` | Bỏ tiền tố trước khi chuyển tiếp |

Cần khởi động sẵn `core-service` (8080) và `agent-service` (8001). Có thể đổi cổng đích trong `vite.config.ts`.

### Build

```bash
npm run build
npm run preview
```

## Cấu trúc

```text
src/
├─ api/            # client.ts (gọi API), types.ts (DTO)
├─ components/     # AppShell, Modal, ConfirmDialog, Toasts, Icon, AccountModal, MessageContent…
├─ pages/          # Login, Chat, Documents, Majors, Units, Students, Admins, Profile
├─ store/          # authStore (phiên đăng nhập), chatStore, uiStore (toast)
├─ utils/          # định dạng ngày, kích thước tệp, chữ viết tắt
├─ navigation.ts   # khai báo menu, phân quyền, hash routing
├─ App.tsx         # cổng đăng nhập + điều hướng trang
└─ index.css       # design system
```

## Ghi chú kỹ thuật

- Token JWT được giữ trong bộ nhớ và phiên đăng nhập lưu ở `localStorage` (`campus-auth`). Response 401 sẽ tự động đăng xuất.
- `student_code` gửi cho agent lấy từ `studentId` của cuộc trò chuyện đang mở.
- API tin nhắn chỉ lưu tin của sinh viên, nên câu trả lời của trợ lý được cache cục bộ (`campus-agent-replies`) để hiển thị lại sau khi tải trang.
- API quản trị viên hiện mở công khai để thử nghiệm; quyền thao tác trên giao diện vẫn dựa trên vai trò trong JWT.
- `npm run lint` hiện không chạy được do `typescript-eslint` chưa hỗ trợ TypeScript 7; `npm run build` vẫn hoạt động bình thường.
