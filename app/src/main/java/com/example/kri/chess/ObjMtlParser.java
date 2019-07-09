package com.example.kri.chess;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ObjMtlParser{

    public static List<float[]> getMesh(Context context, String file){

        String library;
        List<float[]> meshes = new ArrayList<>();

        BufferedReader reader;
        List<Float> vertices = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<List<String>> facesList = new ArrayList<>();
        int counter = -1;

        String line;
        String parts[];

        try {
            InputStreamReader in = new InputStreamReader(context.getAssets().open(file));
            reader = new BufferedReader(in);
            while((line = reader.readLine()) != null){
                parts = line.split(" ");
                switch(parts[0]){
                    case "mtllib":
                        library = parts[1];
                        break;
                    case "v":
                        vertices.add(Float.valueOf(parts[1]));
                        vertices.add(Float.valueOf(parts[2]));
                        vertices.add(Float.valueOf(parts[3]));
                        break;
                    case "vn":
                        normals.add(Float.valueOf(parts[1]));
                        normals.add(Float.valueOf(parts[2]));
                        normals.add(Float.valueOf(parts[3]));
                        break;
                    case "f":
                        facesList.get(counter).add(parts[1]);
                        facesList.get(counter).add(parts[2]);
                        facesList.get(counter).add(parts[3]);
                        break;
                    case "usemtl":
                        counter++;
                        facesList.add(new ArrayList<String>());
                        break;
                }
            }

        }catch(IOException e){
            // handle error
        }

        int index;
        for(int i = 0; i < facesList.size(); i++){
            index = 0;
            meshes.add(new float[facesList.get(i).size() * 6]);
            for(int j = 0; j < facesList.get(i).size(); j++){
                parts = facesList.get(i).get(j).split("/");
                meshes.get(i)[index++] = vertices.get((Integer.valueOf(parts[0])*3) - 3);
                meshes.get(i)[index++] = vertices.get((Integer.valueOf(parts[0])*3) - 2);
                meshes.get(i)[index++] = vertices.get((Integer.valueOf(parts[0])*3) - 1);
                meshes.get(i)[index++] = normals.get((Integer.valueOf(parts[2])*3) - 3);
                meshes.get(i)[index++] = normals.get((Integer.valueOf(parts[2])*3) - 2);
                meshes.get(i)[index++] = normals.get((Integer.valueOf(parts[2])*3) - 1);
            }
        }

        return meshes;
    }
}
