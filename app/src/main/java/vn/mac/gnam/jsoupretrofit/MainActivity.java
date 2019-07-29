package vn.mac.gnam.jsoupretrofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnItemClickListener{
    private RecyclerView rvView;
    private JsoupAdapter jsoupAdapter;
    private ArrayList<ImgItem> imgItems;
    private LinearLayoutManager linearLayoutManager;
    String url = "http://asian.dotplays.com/girl-photo/";
    private SwipeRefreshLayout swipeRv;
    boolean isLoading = false;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvView = (RecyclerView) findViewById(R.id.rvView);
        imgItems = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        rvView.setLayoutManager(linearLayoutManager);
        rvView.setHasFixedSize(true);
        swipeRv = (SwipeRefreshLayout) findViewById(R.id.swipeRv);
        swipeRv.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
                imgItems.clear();
                // To keep animation for 4 seconds
                getData();
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeRv.setRefreshing(false);
                    }
                }, 2000); // Delay in millis
            }
        });

        // Scheme colors for animation
        swipeRv.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
        getData();

    }




    private void getData() {
        swipeRv.setRefreshing(true);
        getHtmlData();
    }


    private void getHtmlData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String img = "";
                        Document document = Jsoup.parse(response);
                        if (document != null){
                                Elements elements = document.select("figure.wp-block-image");
                                for (Element element : elements){
                                    Element elementImg = element.getElementsByTag("img").first();
                                    if (elementImg != null){
                                        img = elementImg.attr("src");
                                    }
                                    imgItems.add(new ImgItem(img));
                                    swipeRv.setRefreshing(false);
                                }
                            }
                        jsoupAdapter = new JsoupAdapter(imgItems, MainActivity.this);
                        rvView.setAdapter(jsoupAdapter);
                        jsoupAdapter.setOnItemClickListener(MainActivity.this);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    public void onItemClick(int position) {
        Intent intent = new Intent(this,DetailPhotoActivity.class);
        ImgItem clickItem = imgItems.get(position);
        intent.putExtra("detail", clickItem.getImg());
        startActivity(intent);
    }

}
