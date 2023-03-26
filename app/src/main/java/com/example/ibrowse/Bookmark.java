package com.example.ibrowse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.List;

public class Bookmark extends AppCompatActivity {

   DatabaseBookmark dbHandlerBook=new DatabaseBookmark(this,null,null,1);
   WebView Web_Views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        final List<String> books=dbHandlerBook.databaseToString();
        if(books.size()>0)
        {
            ArrayAdapter myadapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,books);
            ListView mylist=(ListView)findViewById(R.id.list_view_Bookmarks);
            mylist.setAdapter(myadapter);

            mylist.setOnItemClickListener(
                    new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //*String url=books.get(position);*//*
                            String url=books.get(position);
                            Intent intent = new Intent(view.getContext(),MainActivity.class);

                            intent.putExtra("urls",url);

                            startActivity(intent);
                            finish();


                        }
                    }
            );
        }
    }
}
