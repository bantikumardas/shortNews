package com.example.myapplication;

import static com.example.myapplication.R.*;
import static com.example.myapplication.SignInActivity.mAuth;
import static com.example.myapplication.SignInActivity.mGoogleSignInClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.Manifest;
import org.json.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    //for navigation menu
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    Button search;
    Button nextbnt;
    TextView example, CurrentPage;
    ListView listView;
    SearchView searchText;
    ProgressBar progressBar;

    Spinner langSpinner;
    Spinner catagorySpinner;
    String nextPageId="";
    String query="";
    String queryLanguage="en";
    String queryCatagory="";
    String name="", email="", phone="", photourl="", tokenId="";
    int pageNo=1;
    String[] language={"English", "Hindi",  "Bengali ", "Nepali", "Tamil", "Urdu "};
    String[] catagoryNews={"Select Category","top","business", "entertainment", "environment", "food", "health", "politics", "science",
            "sports","technology","world"};
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        search = (Button) findViewById(R.id.search);
        nextbnt=(Button)findViewById(id.nextbtn);
        example = (TextView) findViewById(R.id.ex);
        listView=(ListView) findViewById(id.listView);
        searchText=(SearchView) findViewById(R.id.searchText);
        progressBar=(ProgressBar) findViewById(id.progressbar);
        CurrentPage=(TextView) findViewById(id.currentPage);
        langSpinner=(Spinner) findViewById(id.langSpin);
        catagorySpinner=(Spinner) findViewById(id.catagroySpin);


        //for navmenu
        toolbar=(Toolbar)findViewById(id.toolbar);
        drawerLayout=(DrawerLayout)findViewById(id.drawer_layout);
        nav=(NavigationView)findViewById(id.navmenu);
        nav.bringToFront();
        getLoginDetails();
        runTimePermission();
        navigationMenu();
        languageSpinner();
        catagory_spinner();
        changeTheme();

    }

    private void getLoginDetails() {
        try{
            Intent intent=getIntent();
            name=intent.getStringExtra("name");
            email=intent.getStringExtra("email");
            phone=intent.getStringExtra("phone");
            photourl=intent.getStringExtra("photoUrl");
            tokenId=intent.getStringExtra("tokenId");
            View headerView= nav.getHeaderView(0);
            ImageView userPhoto=(ImageView) headerView.findViewById(id.userImage);
            TextView nameView=(TextView)headerView.findViewById(id.userName);
            nameView.setText(name);
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .placeholder(drawable.pngegg)
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT);
            Glide.with(this).load(photourl).apply(requestOptions).into(userPhoto);
        }catch (Exception e){
            Toast.makeText(this, "main"+e+"", Toast.LENGTH_LONG).show();
            Log.e("errorOnMain", e+"");
        }

    }

    private void runTimePermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                        Toast.makeText(MainActivity.this, "permission Granted", Toast.LENGTH_SHORT).show();
                        LocalDataBaseManager dbManager=new LocalDataBaseManager(MainActivity.this);
                        ArrayList<newsClass> array= dbManager.getDatabase();
                        Collections.reverse(array);
                        if(array.size()==0)
                            makeHttpsRequest("&q=india","");
                        else
                           updateUI(array);

                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
    }


    public void changeTheme(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        SwitchCompat switchDarkMode = nav.getMenu().findItem(id.nav_theme_change).getActionView().findViewById(R.id.switch_dark_mode);
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle dark mode switch here
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("isDarkModeOn", true);
                    editor.apply();
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("isDarkModeOn", false);
                    editor.apply();
                }

            }
        });
        if(isDarkModeOn)
         switchDarkMode.setChecked(true);
    }
    public void navigationMenu(){
        setSupportActionBar(toolbar);
        toggle=new ActionBarDrawerToggle(this, drawerLayout, toolbar, string.open, string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch ((item.getItemId())){
                    case id.nav_account:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Toast.makeText(MainActivity.this, "account", Toast.LENGTH_SHORT).show();
                        break;
                    case id.nav_settings:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Toast.makeText(MainActivity.this, "setting", Toast.LENGTH_SHORT).show();
                        break;
                    case id.nav_theme_change:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Toast.makeText(MainActivity.this, "change language", Toast.LENGTH_SHORT).show();
                        break;
                    case id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                        logout();
                        break;


                }
                Log.e("itemId", item.getItemId()+"");
                return true;
            }
        });
    }

    private void logout() {
        mAuth.signOut();
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                       startActivity(new Intent(MainActivity.this, SignInActivity.class));
                       LocalDataBaseManager dbManager=new LocalDataBaseManager(MainActivity.this);
                       dbManager.deleteDataBase();
                    }
                });

    }

    private void catagory_spinner() {
        ArrayAdapter langAdp=new ArrayAdapter(this, android.R.layout.simple_spinner_item, catagoryNews);
        langAdp.setDropDownViewResource(android.R.layout.simple_spinner_item);
        catagorySpinner.setAdapter(langAdp);
        catagorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) queryCatagory="";
                else queryCatagory="&category="+catagoryNews[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void languageSpinner() {
        ArrayAdapter langAdp=new ArrayAdapter(this, android.R.layout.simple_spinner_item, language);
        langAdp.setDropDownViewResource(android.R.layout.simple_spinner_item);
        langSpinner.setAdapter(langAdp);
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1) queryLanguage="hi";
                if(i==2) queryLanguage="bn";
                if(i==3) queryLanguage="ne";
                if(i==4) queryLanguage="ta";
                if(i==5) queryLanguage="ur";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void makeHttpsRequest(String query, String nextPage) {
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            progressBar.setVisibility(View.VISIBLE);
            String url = "https://newsdata.io/api/1/news?apikey=pub_17408f1ee431dbcc05cc71941e2b5a518d045" +
                    ""+query+"&language="+queryLanguage+""+queryCatagory+""+nextPage;

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            try {
                                listView.setVisibility(View.VISIBLE);
                                nextbnt.setVisibility(View.VISIBLE);
                                example.setVisibility(View.INVISIBLE);
                                setNewsInListView(response);
                                Log.d("banti",response);
                                progressBar.setVisibility(View.INVISIBLE);
                                CurrentPage.setText("current page no "+pageNo++);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    example.setText(error+"");
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d("banti",error+"");
                }
            });

          // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } catch (Exception e) {
            example.setText(e + "1");
            Log.d("banti",e+"");
        }


    }

    public void setNewsInListView(String JsonResponse) throws JSONException {
        ArrayList<newsClass> news = new ArrayList<>();
        String examp = "error";
        try {
            JSONObject json =new JSONObject(JsonResponse.toString());
            int totalCount=json.getInt("totalResults");
            LocalDataBaseManager dbManager=new LocalDataBaseManager(MainActivity.this);
            dbManager.deleteDataBase();
            nextPageId=json.getString("nextPage");
            JSONArray jsonArray=json.getJSONArray("results");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject result=jsonArray.getJSONObject(i);
                String title=changeToUtf8(result.getString("title"));
                String link=result.getString("link");
                String keyword=result.getString("keywords");
                JSONArray creator=result.optJSONArray("creator");
                String author="";
                if(creator!=null)
                 author=creator.getString(0);
                String description=changeToUtf8(result.getString("description"));
                String content=changeToUtf8(result.getString("content"));
                String date=result.getString("pubDate");
                String ImageUrl=result.getString("image_url");
                String catagory=result.getJSONArray("category").getString(0);
                String country=result.getJSONArray("country").getString(0);
                newsClass nClass=new newsClass(title,link,keyword,author,description,content,date,ImageUrl,catagory,country,"en");
                int responseCode=dbManager.addResponse(title, link, keyword,author,description,content,date, ImageUrl,catagory,country, "en");
                if(responseCode==0){
                    Toast.makeText(this, "error in get push Database", Toast.LENGTH_SHORT).show();
                }
                news.add(nClass);
            }
            if(totalCount==0){
                example.setText("NOT FOUND");
                listView.setVisibility(View.GONE);
                Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();
            }else{
                updateUI(news);
            }
        } catch (Exception e) {
            example.setText(e + "2");
        }

    }

    public String changeToUtf8(String str) throws UnsupportedEncodingException {
        byte ptext[] = str.getBytes(StandardCharsets.ISO_8859_1);
        String value = new String(ptext, StandardCharsets.UTF_8);
        return value;
    }
    public void updateUI(ArrayList<newsClass> arrayList){
        if(nextPageId=="null"){
            nextbnt.setVisibility(View.INVISIBLE);
        }
        newsAdapter adapter=new newsAdapter(MainActivity.this, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url=arrayList.get(position).getLink();
                Intent intent = new Intent(MainActivity.this, ReadingActivity.class);
                intent.putExtra("link", url);
                startActivity(intent);

            }
        });
    }
    public void search(View view) {
        pageNo=1;
        query=searchText.getQuery().toString();
        if(query!="") query="&q="+query;
        listView.setVisibility(View.INVISIBLE);
        makeHttpsRequest(query, "");
    }

    public void NextPage(View view){
        if(query!="") query="&q="+query;
        listView.setVisibility(View.INVISIBLE);
        makeHttpsRequest(query, "&page="+nextPageId);
    }


}