package com.thinktubekorea.menu;

import android.Manifest;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.thinktubekorea.menu.adapter.CategoryAdapterSub;
import com.thinktubekorea.menu.adapter.ItemAdapter;
import com.thinktubekorea.menu.model.Category;
import com.thinktubekorea.menu.model.Item;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
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

public class SubActivity extends AppCompatActivity {

    String mFilePath = "";
    static final String TAG = "menu";

    // class, instance
    CategoryAdapterSub categoryAdapterSub;
    ItemAdapter itemAdapter;
    ArrayList<Category> categories;
    HashMap<String, ArrayList<Item>> items;
    GridView gridView;
    ItemSubAdapter itemSubAdapter;

    ArrayList<Category> mCategories;
    ArrayList<Item> mItem;

    // 롱클릭 이전 화면 이동 선언  바인딩을 쉽게 하자(ButterKnife 라이브러리 사용)
    @BindView(R.id.text_search)
    TextView textsearch;

    @BindView(R.id.line1)
    View view1;

    @BindView(R.id.menu_category2)
    ListView menuCategory2;

    protected Retrofit retrofit;

    protected ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        // findViewById 메소드를 사용하지 않고 뷰를 쉽게 바인딩 하기 위한 라이브러리
        ButterKnife.bind(this);
        //통신 API , GSON
        initRetrofit();

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
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
                //getDataToServer();
                getGridTodata();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();

        // 술병 클릭시 이미지를 크게 확대 정중앙에 표기 - 5번 미션
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               Toast.makeText(getApplicationContext(),"상품명 : "+ itemSubAdapter.getItem(i).getName().toString() + " , 가격 : "+itemSubAdapter.getItem(i).getTel().toString(),Toast.LENGTH_LONG).show();

                View dialogView = (View) View.inflate(SubActivity.this, R.layout.large_item_row, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(SubActivity.this);
                TextView  itemname = dialogView.findViewById(R.id.item_name);
                ImageView drinkimage = (ImageView) dialogView.findViewById(R.id.drink_Image);
                drinkimage.setImageResource(itemSubAdapter.getItem(i).getImage());
                dlg.setIcon(R.drawable.ic_launcher_foreground);
                dlg.setView(dialogView);
                dlg.setNegativeButton("닫기", null);
                dlg.show();
            }
        });

        // 검색텍스트 롱클릭시 이전 화면으로 이동 - 4번 미션
        textsearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);

                finishActivity(0);
                return false;
            }
        });
    }

    // 그리드뷰 구성
    public void getGridTodata() {
        // 배열추가
        final Integer[] drink = {R.drawable.drink1, R.drawable.drink2, R.drawable.drink3, R.drawable.drink4, R.drawable.drink5,
                R.drawable.drink1, R.drawable.drink2, R.drawable.drink3, R.drawable.drink4, R.drawable.drink5,
                R.drawable.drink1, R.drawable.drink2, R.drawable.drink3, R.drawable.drink4, R.drawable.drink5
        };

        final String[] str_item = {"Brora 34Y", "Dalmore 12Y", "Glenmorange 18Y", "Glenfiddich 12Y", "Bruichladdich Octomore",
                "Brora 34Y", "Dalmore 12Y", "Glenmorange 18Y", "Glenfiddich 12Y", "Bruichladdich Octomore",
                "Brora 34Y", "Dalmore 12Y", "Glenmorange 18Y", "Glenfiddich 12Y", "Bruichladdich Octomore"
        };

        final String[] str_price = {"4,150,000", "350,000", "700,000", "350,000", "640,000",
                "4,150,000", "350,000", "700,000", "350,000", "640,000",
                "4,150,000", "350,000", "700,000", "350,000", "640,000"
        };

        gridView = (GridView) findViewById(R.id.gridView);


        /* 어댑터 객체 생성 */
        itemSubAdapter = new ItemSubAdapter();

        Random rnd = new Random();

        /* 어댑터에 데이터 추가 */
        for (int i = 0; i < 15; i++) {
            int j = rnd.nextInt(15);
            itemSubAdapter.addItem(new ItemSub(str_item[j], str_price[j], drink[j]));

        }

        gridView.setAdapter(itemSubAdapter);
    }

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
                Storage storage = new Storage(SubActivity.this);
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
        CommonPreferences.setBooleanPreferences(SubActivity.this, CommonPreferences.IS_FIRST, false);
    }

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
            Log.e("menu", item);
            Log.e("menu", items.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        if(categories != null){
            setListView(categories, categories.get(0).getCategotyCode());
        }


    }

    public void setListView(ArrayList<Category> categories, String categoryCode) {
        mCategories = categories;
        categoryAdapterSub = new CategoryAdapterSub(SubActivity.this, R.layout.category_sub_row, categories);
        itemAdapter = new ItemAdapter(SubActivity.this, R.layout.item_row, new ArrayList<Item>());
        menuCategory2.setAdapter(categoryAdapterSub);


        // changeItem(categoryCode);
        menuCategory2.setSelection(0);

        menuCategory2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                changeItem(mCategories.get(i).getCategotyCode());

            }
        });
    }

    public void setData(ArrayList<Category> categories, ArrayList<Item> item){
        mCategories = categories;
        mItem =item;
        categoryAdapterSub.clear();
        categoryAdapterSub.addAll(mCategories);
        itemAdapter.clear();
        itemAdapter.addAll(items.get(mCategories.get(0).getCategotyCode()));
    }

    public void changeItem(String code) {
        getGridTodata();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemSubAdapter.notifyDataSetChanged();
            }
        });
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

    private boolean writeResponseBodyToDisk(@NotNull ResponseBody body, String path) {
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

    // 그리드뷰에 담을 adapter 선언
    class ItemSubAdapter extends BaseAdapter {
        ArrayList<ItemSub> items = new ArrayList<ItemSub>();
        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(ItemSub itemSub){
            items.add(itemSub);
        }

        @Override
        public ItemSub getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ItemViewer itemViewer = new ItemViewer(getApplicationContext());
            itemViewer.setItem(items.get(i));

            return itemViewer;
        }
    }
}