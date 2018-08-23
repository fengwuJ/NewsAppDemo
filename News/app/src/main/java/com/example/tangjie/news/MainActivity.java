package com.example.tangjie.news;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{

    private List<Contentmoudel> first_page_list = new ArrayList<>();
    private List<Contentmoudel> international_list = new ArrayList<>();
    private List<Contentmoudel> army_list = new ArrayList<>();
    private List<Contentmoudel> society_list = new ArrayList<>();
    private NewsAdapter adapter;

    boolean flag = false;

    String url[] = {"https://news.qq.com/","http://news.qq.com/world_index.shtml",
            "http://mil.qq.com/mil_index.htm","http://society.qq.com/"};

    private TextView textView;

    NavigationView navigationView;
    View headerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent getList = getIntent();
        first_page_list=(List<Contentmoudel>) getList.getSerializableExtra("first_page_list");
        international_list=(List<Contentmoudel>) getList.getSerializableExtra("international_list");
        army_list=(List<Contentmoudel>) getList.getSerializableExtra("army_list");
        society_list=(List<Contentmoudel>) getList.getSerializableExtra("society_list");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerview = navigationView.inflateHeaderView(R.layout.nav_header_main);
        textView = (TextView) headerview.findViewById(R.id.user_name);

        Resources resource=(Resources)getBaseContext().getResources();
        ColorStateList csl=(ColorStateList)resource.getColorStateList(R.color.navigation_menu_item_color);
        navigationView.setItemTextColor(csl);
        /**设置MenuItem默认选中项**/
        navigationView.getMenu().getItem(0).setChecked(true);
        RecyclerViewUI(first_page_list);
    }

    private void RecyclerViewUI(final List<Contentmoudel> newsList){
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent item = new Intent(MainActivity.this,ListcontentActivity.class);
                ImageView newsImage = view.findViewById(R.id.news_image);
                newsImage.setDrawingCacheEnabled(true);
                Bitmap bitmap = newsImage.getDrawingCache();
                Bundle bundle = new Bundle();
                bundle.putParcelable("bitmap",bitmap);
                newsList.get(position).setImage(bitmap);
                item.putExtra("title",newsList.get(position).getTitle());
                item.putExtra("url",newsList.get(position).getLinkTitle());
                item.putExtras(bundle);
                startActivity(item);
            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.about_us) {
            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.first_page:
                RecyclerViewUI(first_page_list);
                break;
            case R.id.international:
                RecyclerViewUI(international_list);
                break;
            case R.id.army_commit:
                RecyclerViewUI(army_list);
                break;
            case R.id.society:
                RecyclerViewUI(society_list);
                break;
            case R.id.log_out:
                textView.setText("");
                Menu munu=navigationView.getMenu();
                munu.findItem(R.id.log_out).setVisible(false);
                Toast.makeText(MainActivity.this,"注销成功",Toast.LENGTH_SHORT).show();
                munu.findItem(R.id.old_login).setVisible(false);
                break;
            case R.id.change_info:
                Intent change = new Intent(MainActivity.this,ChangeActivity.class);
                change.putExtra("changeID",textView.getText().toString());
                startActivityForResult(change,2);
                break;
            case R.id.old_login:
                Intent oldlogin = new Intent(MainActivity.this,LocationActivity.class);
                startActivity(oldlogin);
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivityForResult(intent,1);
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 1:
                if (resultCode == RESULT_OK)
                {
                    String returnedData = data.getStringExtra("username");
                    textView.setText(returnedData);
                    String getname = textView.getText().toString();
                    if(getname!=null){
                        Menu munu=navigationView.getMenu();
                        munu.findItem(R.id.log_out).setVisible(true);
                        munu.findItem(R.id.old_login).setVisible(true);
                    }
                }
                break;

            case 2:
                if(resultCode ==RESULT_FIRST_USER){
                    textView.setText("");
                    Menu munu=navigationView.getMenu();
                    munu.findItem(R.id.log_out).setVisible(false);
                }
            default:
        }
    }
}
