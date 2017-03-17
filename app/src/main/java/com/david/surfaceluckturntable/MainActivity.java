package com.david.surfaceluckturntable;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.david.surfaceluckturntable.view.LuckTurntableView;

public class MainActivity extends AppCompatActivity {

    private LuckTurntableView luckTurntableView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        luckTurntableView = (LuckTurntableView) findViewById(R.id.LuckTurntableView);
        imageView = (ImageView) findViewById(R.id.iv_start);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!luckTurntableView.isStart()) {
                    luckTurntableView.clickStart(1);
                    imageView.setImageResource(R.drawable.stop);
                } else {
                    if (!luckTurntableView.isShouldEnd()) {
                        luckTurntableView.clickEnd();
                        imageView.setImageResource(R.drawable.start);
                    }
                }
            }
        });
    }
}
