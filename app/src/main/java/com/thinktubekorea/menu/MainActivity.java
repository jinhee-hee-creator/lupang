package com.thinktubekorea.menu;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.snatik.storage.Storage;
import com.thinktubekorea.menu.Preferences.CommonPreferences;
import com.thinktubekorea.menu.adapter.CategoryAdapter;
import com.thinktubekorea.menu.adapter.ItemAdapter;
import com.thinktubekorea.menu.model.Category;
import com.thinktubekorea.menu.model.Item;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    String mFilePath = "";
    static final String TAG = "menu";

    CategoryAdapter categoryAdapter;
    ItemAdapter itemAdapter;
    ArrayList<Category> categories;
    HashMap<String, ArrayList<Item>> items;
    @BindView(R.id.textview_menu)
    TextView textviewMenu;
    @BindView(R.id.line1)
    View view1;
    @BindView(R.id.menu_category_header)
    TextView menuCategoryHeader;
    @BindView(R.id.item_name_header)
    TextView itemNameHeader;
    @BindView(R.id.item_expression_header)
    TextView itemExpressionHeader;
    @BindView(R.id.item_glassAmount_header)
    TextView itemGlassAmountHeader;
    @BindView(R.id.item_bottleAmount_header)
    TextView itemBottleAmountHeader;
    @BindView(R.id.menu_category)
    ListView menuCategory;
    @BindView(R.id.menu_item)
    ListView menuItem;

    protected Retrofit retrofit;

    protected ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initRetrofit();
        Log.e("menu", "getResources().getDisplayMetrics().densityDpi : " + getResources().getDisplayMetrics().densityDpi);
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE //기기, 사진, 미디어, 파일 엑세스 권한
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                checkFolder();
                //writeTestItemDataAtStorage();

//                boolean isFirst = CommonPreferences.getIsFirst(MainActivity.this);
//
//                if (isFirst) {

//                }
                getItemDataAtStorage();
                getDataToServer();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();

        textviewMenu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SubActivity.class);
                startActivity(intent);
                //appUpdate();
                return false;
            }
        });
    }

    // 서버와 클라이언트간 Http 통신을 윈한 인터페이스
    protected void initRetrofit() {
        Gson gson = new GsonBuilder()
                .setDateFormat("MM/dd/yyyy HH:mm:ss")
                .create();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl("https://kr.object.ncloudstorage.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    // 파일사용을 위한 것?
    public void assertToEXcard() {
        CopyAssets();
    }

    private void CopyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("category.json");
                Storage storage = new Storage(MainActivity.this);
                String path = storage.getExternalStorageDirectory();
                String newDir = path + File.separator + "menu";
                out = new FileOutputStream(newDir + File.separator + filename);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;

            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
        }
        CommonPreferences.setBooleanPreferences(MainActivity.this, CommonPreferences.IS_FIRST, false);
    }


    //assets 폴더의 파일 복사, 같은 이름으로 sdcard 파일 생성
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void setFilePath(String path) {
        mFilePath = path;
    }

    public void checkFolder() {

        Storage storage = new Storage(getApplicationContext());
        String path = storage.getExternalStorageDirectory();
        String newDir = path + File.separator + "menu";
        setFilePath(newDir);
        storage.createDirectory(mFilePath);
    }

    public void getItemDataAtStorage() {
        Gson gson = new Gson();
        Storage storage = new Storage(getApplicationContext());
        String category = null;
        String item= null;
        try {
              category = storage.readTextFile(mFilePath + File.separator + "category.json");
              item = storage.readTextFile(mFilePath + File.separator + "items.json");

        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            categories = gson.fromJson(category, new TypeToken<List<Category>>() {
            }.getType());
            items = gson.fromJson(item, new TypeToken<HashMap<String, ArrayList<Item>>>() {
            }.getType());
            Log.e("menu1", item);
            Log.e("menu2", items.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        if(categories != null){
            setListView(categories, categories.get(0).getCategotyCode());
        }


    }

    ArrayList<Category> mCategories;
    ArrayList<Item> mItem;

    public void setListView(ArrayList<Category> categories, String categoryCode) {
        mCategories = categories;
        categoryAdapter = new CategoryAdapter(MainActivity.this, R.layout.category_row, categories);
        itemAdapter = new ItemAdapter(MainActivity.this, R.layout.item_row, new ArrayList<Item>());
        menuCategory.setAdapter(categoryAdapter);
        menuItem.setAdapter(itemAdapter);

        changeItem(categoryCode);
        menuCategory.setSelection(0);

        menuCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                changeItem(mCategories.get(i).getCategotyCode());
            }
        });
    }

    public void setData(ArrayList<Category> categories, ArrayList<Item> item){
        mCategories = categories;
        mItem =item;
        categoryAdapter.clear();
        categoryAdapter.addAll(mCategories);
        itemAdapter.clear();
        itemAdapter.addAll(items.get(mCategories.get(0).getCategotyCode()));
    }

    String mNowCode;


    public void changeItem(String code) {
        if(items == null || items.size() < 1){
            return;
        }

         //System.out.println("items : "+ mItem.get(1).toString());

        mNowCode = code;
        itemAdapter.clear();
        for (Item item : items.get(code)) {

            item.setAnimation(false);
        }
        if ("code20".equalsIgnoreCase(code)) {
            itemGlassAmountHeader.setText("");
            itemBottleAmountHeader.setText("Price");
            //menuItem.setDivider(new ColorDrawable(Color.BLUE));
        } else {
            itemGlassAmountHeader.setText("Glass");
            itemBottleAmountHeader.setText("Bottle");
            //menuItem.setDivider(new ColorDrawable(Color.GREEN));
        }
        itemAdapter.addAll(items.get(code));

        itemAdapter.notifyDataSetChanged();
    }

    public  void getDataToServer(){
        getDataToServerCategory();
        getDataToServerItem();
    }

    //데이터 서버 동기화
    public void getDataToServerCategory() {
        Call<ResponseBody> call = apiService.getCategory();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file");

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(), mFilePath + File.separator + "category.json");

                    Log.d(TAG, "file download was a success? " + writtenToDisk);

                } else {
                    Log.d(TAG, "server contact failed");
                }

                //실패 여부와 상관없이 데이터 로드
                getItemDataAtStorage();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                //menuLoadFail();
                getItemDataAtStorage();
            }
        });

    }

    public void getDataToServerItem() {
        Call<ResponseBody> call = apiService.getItem();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file");

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(), mFilePath + File.separator + "items.json");

                    Log.d(TAG, "file download was a success? " + writtenToDisk);
                } else {
                    Log.d(TAG, "server contact failed");
                }
                getItemDataAtStorage();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                getItemDataAtStorage();
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String path) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(path);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public void menuLoadFail() {

        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(MainActivity.this);
        alert.setMessage("데이터 로드 실패");
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("재시도", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alert.show();
    }

    public void appUpdate() {
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "app-release.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        Log.d(TAG, "destination:" + destination);

        final String apkFilePath = Environment.getExternalStorageDirectory() + "/Download/app-release.apk";
        File file = new File(apkFilePath);
        if (file.exists()) {
            file.delete();
            Log.w(TAG, "삭제 됨");
        }
        String downUrl = "https://kr.object.ncloudstorage.com/menu/app-release.apk";

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downUrl));
        request.setDestinationUri(uri);
        final long downloadId = dm.enqueue(request);

        final BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    Uri contentUri = FileProvider.getUriForFile(ctxt, BuildConfig.APPLICATION_ID + ".fileprovider", new File(apkFilePath));
                    Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
                    openFileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    openFileIntent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(openFileIntent);
                    unregisterReceiver(this);
                } else {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setDataAndType(uri,
                            "application/vnd.android.package-archive");
                    startActivity(install);
                    unregisterReceiver(this);
                }
            }
        };
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}
