package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.Models.BanBe;
import com.example.appchat.Models.Message;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThongTinChuaKetBanActivity extends AppCompatActivity {

    final int EDIT_CODE_INTENT = 9999;
    NguoiDung nguoi_dung;
    TextView txtHoTen_TaiKhoan, txtContent_GioiTinh_TaiKhoan, txtContent_NgaySinh_TaiKhoan, txtContent_SoDienThoai_TaiKhoan;
    Button btnNhanTin, btnKetBan;
    ImageButton btnBack_ThemBan;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_chua_ket_ban);

        Init_Data();
        Load_Data();
        AddFriend();
        Back();
    }

    protected void Init_Data(){
        preferences = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
        txtHoTen_TaiKhoan = (TextView) findViewById(R.id.txtHoTen_TaiKhoan);
        txtContent_GioiTinh_TaiKhoan = (TextView) findViewById(R.id.txtContent_GioiTinh_TaiKhoan);
        txtContent_NgaySinh_TaiKhoan = (TextView) findViewById(R.id.txtContent_NgaySinh_TaiKhoan);
        txtContent_SoDienThoai_TaiKhoan = (TextView) findViewById(R.id.txtContent_SoDienThoai_TaiKhoan);
        btnNhanTin = (Button) findViewById(R.id.btnNhanTin);
        btnKetBan = (Button) findViewById(R.id.btnKetBan);
        btnBack_ThemBan = (ImageButton) findViewById(R.id.btnBack_ThemBan);
    }

    protected void Load_Data(){
        Bundle bundle = getIntent().getBundleExtra("Infor");
        nguoi_dung = (NguoiDung) bundle.getSerializable("ThongTinNguoiDung");


        if (nguoi_dung.getHoTen() != null) {
            txtHoTen_TaiKhoan.setText(nguoi_dung.getHoTen());
        }

        if (nguoi_dung.getGioiTinh() != null) {
            if (nguoi_dung.getGioiTinh()) {
                txtContent_GioiTinh_TaiKhoan.setText("Nam");
            } else {
                txtContent_GioiTinh_TaiKhoan.setText("Nữ");
            }
        }

        if (nguoi_dung.getNgaySinh() != null) {
            String ngaySinh = nguoi_dung.getNgaySinh();
            ngaySinh = ngaySinh.substring(0, 10);

            String thang = ngaySinh.substring(5, 7);
            String ngay = ngaySinh.substring(8, 10);
            String nam = ngaySinh.substring(0, 4);

            ngaySinh = ngay + "-" + thang + "-" + nam;

            nguoi_dung.setNgaySinh(thang + "-" + ngay + "-" + nam);
            txtContent_NgaySinh_TaiKhoan.setText(ngaySinh);
        }

        if (nguoi_dung.getSoDienThoai() != null) {
            txtContent_SoDienThoai_TaiKhoan.setText(nguoi_dung.getSoDienThoai());
        }
    }

    protected void Back(){
        btnBack_ThemBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void AddFriend(){
        btnKetBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BanBe banBe = new BanBe();
                int maNguoiDung = preferences.getInt("MaNguoiDung", 0);
                banBe.setMaNguoiDung_Mot(maNguoiDung);
                banBe.setMaNguoiDung_Hai(nguoi_dung.getMaNguoiDung());
                DataClient client = APIUtils.getData();
                Call<Message> call = client.SendRequestAddFriend(banBe);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (response.isSuccessful()){
                            if (response.body().getSuccess() == 2){
                                Toast.makeText(ThongTinChuaKetBanActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                Bundle bundle = new Bundle();
                                Intent intent = new Intent(ThongTinChuaKetBanActivity.this, ThongTinDaGuiLoiMoiKetBan.class);
                                bundle.putSerializable("ThongTinNguoiDung", nguoi_dung);
                                intent.putExtra("Infor", bundle);
                                startActivityForResult(intent, EDIT_CODE_INTENT);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });
            }
        });
    }

}