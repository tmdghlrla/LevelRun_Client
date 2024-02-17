package com.qooke.levelrunproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qooke.levelrunproject.api.AutoTagApi;
import com.qooke.levelrunproject.api.NetworkClient;
import com.qooke.levelrunproject.api.PostingApi;
import com.qooke.levelrunproject.config.Config;
import com.qooke.levelrunproject.model.Posting;
import com.qooke.levelrunproject.model.PostingDetail;
import com.qooke.levelrunproject.model.Res;
import com.qooke.levelrunproject.model.TagRes;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostEditActivity extends AppCompatActivity {
    TextView txtCreatedAt;
    EditText editTag, editContent;
    Button btnUpdate;
    ImageView imgBack, imgPhoto, imgPhotoAdd;
    PostingDetail postingDetail;
    File photoFile;
    String createdAt = "";
    String postingUrl = "";
    String fileUrl = "";
    String tags = "";
    String content = "";
    int postingId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);
        editTag = findViewById(R.id.editTag);
        editContent = findViewById(R.id.editContent);
        txtCreatedAt = findViewById(R.id.txtCreatedAt);
        imgBack = findViewById(R.id.imgBack);
        imgPhoto = findViewById(R.id.imgPhoto);
        imgPhotoAdd = findViewById(R.id.imgPhotoAdd);
        btnUpdate = findViewById(R.id.btnUpdate);

        postingDetail = (PostingDetail) getIntent().getSerializableExtra("postingDetail");
        tags = getIntent().getStringExtra("tags");

        postingUrl = postingDetail.item.get(0).postingUrl;
        createdAt = postingDetail.item.get(0).createdAt;
        postingId = postingDetail.item.get(0).postingId;

        if (createdAt.contains("T")) {
            createdAt = createdAt.replace("T", " ");
        }
        content = postingDetail.item.get(0).content;

        Glide.with(PostEditActivity.this).load(postingUrl).into(imgPhoto);
        editTag.setText(tags);
        editContent.setText(postingDetail.item.get(0).content);
        txtCreatedAt.setText(createdAt);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tags = editTag.getText().toString().trim();
                content = editContent.getText().toString().trim();
                if(!(tags.equals("")) && !(tags.contains("#"))) {
                    Toast.makeText(PostEditActivity.this, "해시 태그 형식이 아닙니다. \nex) #근력운동 #운동 #다이어트", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(content.equals("")) {
                    Toast.makeText(PostEditActivity.this, "내용을 입력 하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                editPosting();
            }
        });

        imgPhotoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void editPosting() {
        showProgress();

        Retrofit retrofit = NetworkClient.getRetrofitClient(PostEditActivity.this);
        PostingApi api = retrofit.create(PostingApi.class);

        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        Log.i("PostEditActivity_tag", "postingId : " + postingId);
        Log.i("PostEditActivity_tag", "content : " + content);
        Log.i("PostEditActivity_tag", "tags : " + tags);
        Posting posting = new Posting(postingUrl, content, tags);



        Call<Res> call = api.editPosting(postingId, token, posting);

        // 5. 서버로부터 받은 응답을 처리하는 코드 작성

        call.enqueue(new Callback<Res>() {
            @Override
            public void onResponse(Call<Res> call, Response<Res> response) {
                dismissProgress();

                if(response.isSuccessful()){
                    finish();

                } else{

                }

            }

            @Override
            public void onFailure(Call<Res> call, Throwable t) {
                dismissProgress();
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostEditActivity.this);
        builder.setTitle(R.string.alert_title);
        builder.setItems(R.array.alert_posting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0) {
                    camera();
                } else if (i == 1) {
                    album();
                }
            }
        });
        builder.show();
    }

    // 카메라 실행 함수
    private void camera(){
        int permissionCheck = ContextCompat.checkSelfPermission(
                PostEditActivity.this, android.Manifest.permission.CAMERA);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PostEditActivity.this,
                    new String[]{android.Manifest.permission.CAMERA} ,
                    1000);
            Toast.makeText(PostEditActivity.this, "카메라 권한이 필요합니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(i.resolveActivity(PostEditActivity.this.getPackageManager())  != null  ){

                String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                photoFile = getPhotoFile(fileName);


                // todo : 패키지명을 현재의 프로젝트에 맞게 수정해야함
                Uri fileProvider = FileProvider.getUriForFile(PostEditActivity.this,
                        "com.qooke.levelrunproject.fileprovider", photoFile);
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                startActivityForResult(i, 100);

            } else{
                Toast.makeText(PostEditActivity.this, "휴대폰에 카메라 앱이 없습니다.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 카메라, 앨범 권한 수락 결과 확인하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PostEditActivity.this, "카메라 권한이 승인되었습니다.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostEditActivity.this, "카메라 권한 승인이 필요합니다.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 500: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PostEditActivity.this, "앨범 권한이 승인되었습니다.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostEditActivity.this, "앨범 권한 승인이 필요합니다.",
                            Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    // 사진 파일 저장하는 함수
    private File getPhotoFile(String fileName) {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            return File.createTempFile(fileName, ".jpg", storageDirectory);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    // 카메라에서 찍거나 앨범에서 선택한 사진 보여주는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100 && resultCode == RESULT_OK){

            Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(photoFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            photo = rotateBitmap(photo, orientation);

            // 해상도 낮춰서 압축시킨다.
            OutputStream os;
            try {

                os = new FileOutputStream(photoFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 50, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }

            photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            imgPhoto.setImageBitmap(photo);
            imgPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


            // 네트워크로 데이터 보낼 필요가 있으면 여기에 코드 작성


            //앨범에서 선택했을 때의 코드
        }else if(requestCode == 300 && resultCode == RESULT_OK && data != null &&
                data.getData() != null){

            Uri albumUri = data.getData( );
            String fileName = getFileName( albumUri );
            try {

                ParcelFileDescriptor parcelFileDescriptor = getContentResolver( ).openFileDescriptor( albumUri, "r" );
                if ( parcelFileDescriptor == null ) return;
                FileInputStream inputStream = new FileInputStream( parcelFileDescriptor.getFileDescriptor( ) );
                photoFile = new File( this.getCacheDir( ), fileName );
                FileOutputStream outputStream = new FileOutputStream( photoFile );
                IOUtils.copy( inputStream, outputStream );

//                //임시파일 생성
//                File file = createImgCacheFile( );
//                String cacheFilePath = file.getAbsolutePath( );


                // 압축시킨다. 해상도 낮춰서
                Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                OutputStream os;
                try {
                    os = new FileOutputStream(photoFile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 60, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }
                // todo:
                imgPhoto.setImageBitmap(photo);
                getTag();
//                imgAddPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

//                imageView.setImageBitmap( getBitmapAlbum( imageView, albumUri ) );

            } catch ( Exception e ) {
                e.printStackTrace( );
            }

            // 네트워크로 데이터 보낼 필요가 있으면 여기에 코드 작성
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 사진 찍은거 회전해주는 함수
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    // 앨범 실행 함수
    private void album(){
        if(checkPermission()){
            displayFileChoose();
        }else{
            requestPermission();
        }
    }

    // 앨범에서 권한 확인하는 함수
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(PostEditActivity.this,
                android.Manifest.permission.READ_MEDIA_IMAGES);
        if(result == PackageManager.PERMISSION_DENIED){
            return false;
        }else{
            return true;
        }
    }

    // 앨범 권한 수락되어 있을때 앨범 띄우는 함수
    private void displayFileChoose() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "SELECT IMAGE"), 300);
    }

    // 앨범 권한 수락 여부 확인 함수
    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(PostEditActivity.this,
                android.Manifest.permission.READ_MEDIA_IMAGES)){
            Log.i("DEBUGGING5", "true");
            Toast.makeText(PostEditActivity.this, "앨범 권한 수락이 필요합니다.",
                    Toast.LENGTH_SHORT).show();
        }else{
            Log.i("DEBUGGING6", "false");
            ActivityCompat.requestPermissions(PostEditActivity.this,
                    new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 500);
        }
    }

    // 가져온 이미지 파일 네임 셋팅하는 함수
    public String getFileName( Uri uri ) {
        Cursor cursor = getContentResolver( ).query( uri, null, null, null, null );
        try {
            if ( cursor == null ) return null;
            cursor.moveToFirst( );
            @SuppressLint("Range") String fileName = cursor.getString( cursor.getColumnIndex( OpenableColumns.DISPLAY_NAME ) );
            cursor.close( );
            return fileName;

        } catch ( Exception e ) {
            e.printStackTrace( );
            cursor.close( );
            return null;
        }
    }

    private void getTag() {
        showProgress();

        Retrofit retrofit = NetworkClient.getRetrofitClient(PostEditActivity.this);
        AutoTagApi api = retrofit.create(AutoTagApi.class);

        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        // 보낼 파일
        // 서버에 jpg 형식으로 보낸다. 또는 다른 형식으로 보낸다.
        // 파일은 용량이 커서 쪼개서 보낸다.
        RequestBody fileBody = RequestBody.create(photoFile, MediaType.parse("image/jpg"));
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", photoFile.getName(), fileBody);

        Call<TagRes> call = api.autoTag(token, image);

        // 5. 서버로부터 받은 응답을 처리하는 코드 작성

        call.enqueue(new Callback<TagRes>() {
            @Override
            public void onResponse(Call<TagRes> call, Response<TagRes> response) {
                dismissProgress();

                if(response.isSuccessful()){
                    TagRes tagRes = response.body();
                    postingUrl = tagRes.fileUrl;

                    for(int i = 0; i < tagRes.tagList.size(); i++) {
                        if(i == 0) {
                            tags = tagRes.tagList.get(i);
                        } else {
                            tags = tags + " " + tagRes.tagList.get(i);
                        }

                        if(i == 5) {
                            return;
                        }
                    }
                    editTag.setText(tags);

                }else{

                }

            }

            @Override
            public void onFailure(Call<TagRes> call, Throwable t) {
                dismissProgress();
            }
        });

    }

    Dialog dialog;
    private void showProgress() {
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void dismissProgress() {
        dialog.dismiss();
    }
}