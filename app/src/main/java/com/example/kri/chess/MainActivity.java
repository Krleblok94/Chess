package com.example.kri.chess;

import android.opengl.GLSurfaceView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity{

    private GLSurfaceView GLSurfaceView;
    private ChessRenderer chessRenderer = new ChessRenderer(this);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        GLSurfaceView = new ChessSurfaceView(this);
        GLSurfaceView.setRenderer(chessRenderer);
        setContentView(GLSurfaceView);

        GLSurfaceView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event != null){
                    final float normalizedX = (event.getX() / (float)v.getWidth()) * 2 - 1;
                    final float normalizedY = -((event.getY() / (float)v.getHeight()) * 2 - 1);

                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        GLSurfaceView.queueEvent(new Runnable(){
                            @Override
                            public void run(){
                                chessRenderer.handleTouchEvent(normalizedX, normalizedY);
                            }
                        });
                    }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                        GLSurfaceView.queueEvent(new Runnable(){
                            @Override
                            public void run(){chessRenderer.handleDragEvent(normalizedX, normalizedY);}
                        });
                    }
                    return true;
                }else{
                    return false;
                }
            }
        });
    }

    @Override
    protected  void onPause(){
        super.onPause();
        GLSurfaceView.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        GLSurfaceView.onResume();
    }
}
